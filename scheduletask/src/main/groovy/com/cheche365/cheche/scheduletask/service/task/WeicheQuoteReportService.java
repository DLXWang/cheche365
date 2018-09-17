package com.cheche365.cheche.scheduletask.service.task;

import com.alibaba.fastjson.JSON;
import com.cheche365.cheche.common.math.NumberUtils;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.LogType;
import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository;
import com.cheche365.cheche.core.repository.UserRepository;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.WeicheQuoteEmailInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yinJianBin on 2017/2/10.
 */
@Service
public class WeicheQuoteReportService {
    private Logger logger = LoggerFactory.getLogger(WeicheQuoteReportService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MoApplicationLogRepository applicationLogMongoRepository;
    @Autowired
    private UserRepository userRepository;

    Map<String, Map<String, Integer>> cacheMap = new HashMap<>();//<车牌号,<电话号码,cps>>
    Integer num = 0;

    /******************
     * MongoDB逻辑    start
     ******************/
    public List<WeicheQuoteEmailInfo> getEmailInfoList() {
        Date startTime;
        String logCreateTimeCache = stringRedisTemplate.opsForValue().get(TaskConstants.WEICHE_QUOTE_REPORT_APPLICATION_LOG_CREATE_TIME_CACHE);
        if (StringUtils.isEmpty(logCreateTimeCache)) {
            logCreateTimeCache = "0";
            startTime = DateUtils.getCustomDate(new Date(), -1, 0, 0, 0);
        } else {
            startTime = new Date(NumberUtils.toLong(logCreateTimeCache));
        }
        Date endTime = DateUtils.getCustomDate(new Date(), -1, 23, 59, 59);
        List<WeicheQuoteEmailInfo> emailDataList = new ArrayList<>();
        Pageable pageable = new PageRequest(0, TaskConstants.PAGE_SIZE);
        Long[] channelIds = {52L, 53L, 55L, 57L, 69L, 70L};
        Page<MoApplicationLog> logPage = applicationLogMongoRepository.findByLogTypeAndCreateTimeAndLogMessageAndId(LogType.Enum.Quote_Cache_Record_31.getId(), startTime, endTime, channelIds, pageable);
        List<MoApplicationLog> logList = logPage.getContent();
        if (CollectionUtils.isEmpty(logList))
            if (logger.isDebugEnabled())
                logger.debug("微车邮件数据统计报表没有查询到数据数据,当前缓存日志表createTimeMillis:({}),startTime:({})", logCreateTimeCache, startTime);

        while (CollectionUtils.isNotEmpty(logList)) {
            /**
             * 同一个用户有可能有多条，创建map，key为用户ID，value为报价记录；用以去重；
             * 其中，key转化为Long[]，作为参数查询在昨天之前三个月到昨天的报价记录，如果有，则将用户ID存放到一个有历史报价的Set中，用以排除
             * **/
            Map<Long, MoApplicationLog> distinctMap = new LinkedHashMap<>();
            for (MoApplicationLog log : logList) {
                if (log.getUser() != null) {
                    distinctMap.put(log.getUser().getId(), log);
                }
            }
            List<MoApplicationLog> filterLogList = new ArrayList<>(distinctMap.values());
            Long[] logIdArr = distinctMap.keySet().toArray(new Long[distinctMap.keySet().size()]);

            //昨天三个月之前的时间点
            Date monthsBeforeStartDate = DateUtils.getAroundMonthsDay(startTime, -3);
            List<MoApplicationLog> historyLogList = applicationLogMongoRepository.findByUserIdInAndCreateTimeLessThan(logIdArr, monthsBeforeStartDate, startTime, LogType.Enum.Quote_Cache_Record_31.getId());
            Set<Long> historyUser = historyLogList.stream().map(log -> log.getUser().getId()).collect(Collectors.toSet());

            List<QuoteRecordExt> quoteRecordExts = this.convertToQuoteRecord(filterLogList, historyUser);
            List<WeicheQuoteEmailInfo> dataList = this.buildEmailDataList(quoteRecordExts);
            emailDataList.addAll(dataList);
            logCreateTimeCache = logList.get(logList.size() - 1).getCreateTime().getTime() + "";
            stringRedisTemplate.opsForValue().set(TaskConstants.WEICHE_QUOTE_REPORT_APPLICATION_LOG_CREATE_TIME_CACHE, logCreateTimeCache);
            if (logList.size() < TaskConstants.PAGE_SIZE) {
                break;
            }
            pageable = pageable.next();
            startTime = new Date(NumberUtils.toLong(logCreateTimeCache));
            logPage = applicationLogMongoRepository.findByLogTypeAndCreateTimeAndLogMessageAndId(LogType.Enum.Quote_Cache_Record_31.getId(), startTime, endTime, channelIds, pageable);
            logList = logPage.getContent();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("微车邮件数据统计报表,批量处理数据,该批次处理数量:[{}],查询至日志表createTimeMillis:[{}]", logList.size(), logCreateTimeCache);
        }

        emailDataList.add(new WeicheQuoteEmailInfo());

        //添加统计信息
        WeicheQuoteEmailInfo countNewUser = new WeicheQuoteEmailInfo();
        //count.setId("微车新用户注册统计");
        List<Long> lists = Arrays.asList(52L, 53L, 55L, 57L, 69L, 70L);
        Long quoteCount = userRepository.findCountNewUser(startTime, endTime, lists);
        countNewUser.setId("微车新用户注册统计 ");
        countNewUser.setQuoteTime(quoteCount == null ? "0" : quoteCount.toString());
        emailDataList.add(countNewUser);

//        WeicheQuoteEmailInfo weicheCountQuote = new WeicheQuoteEmailInfo();
//        //count.setId("微车报价统计");
//        weicheCountQuote.setId("微车报价统计 ");
//        weicheCountQuote.setQuoteTime(StringUtil.defaultNullStr(getWeicheCount(startTime, endTime)));
//        emailDataList.add(weicheCountQuote);


        cacheMap.clear();
        num = 0;
        logger.info("微车邮件数据统计报表获取到数据[{}]条,缓存日志表createTimeMillis:[{}]", emailDataList.size(), logCreateTimeCache);
        return emailDataList;
    }

    /******************
     * MongoDB逻辑    end
     ******************/


    private Long getWeicheCount(Date startDate, Date endDate) {
        Pageable pageable = new PageRequest(0, TaskConstants.PAGE_SIZE);
        Long[] channelIds = {52L, 53L, 55L, 57L, 69L, 70L};
        Page<MoApplicationLog> logPage = applicationLogMongoRepository.findByLogTypeAndCreateTimeAndLogMessageAndId(LogType.Enum.Quote_Cache_Record_31.getId(), startDate, endDate, channelIds, pageable);
        return logPage.getTotalElements();
    }

    private List<WeicheQuoteEmailInfo> buildEmailDataList(List<QuoteRecordExt> quoteRecordExts) {
        List<WeicheQuoteEmailInfo> dataList = new ArrayList<>();
        QuoteRecordExt quoteRecordExt;
        WeicheQuoteEmailInfo weicheQuoteEmailInfo;
        for (int i = 0; i < quoteRecordExts.size(); i++) {
            quoteRecordExt = quoteRecordExts.get(i);
            weicheQuoteEmailInfo = new WeicheQuoteEmailInfo();

            int id = i + 1;
            weicheQuoteEmailInfo.setId(id + "");
            weicheQuoteEmailInfo.setQuoteTime(DateUtils.getDateString(quoteRecordExt.getCreateTime(), DateUtils.DATE_SHORTDATE_PATTERN));
            String licensePlateNo = quoteRecordExt.getAuto().getLicensePlateNo();
            weicheQuoteEmailInfo.setLicenseNo(licensePlateNo);
            String mobile = quoteRecordExt.getApplicant().getMobile();
            weicheQuoteEmailInfo.setMobile(mobile);
            weicheQuoteEmailInfo.setCityName(quoteRecordExt.getArea().getName());
            weicheQuoteEmailInfo.setOwnerName(quoteRecordExt.getAuto().getOwner());
            weicheQuoteEmailInfo.setInsuranceCompany(quoteRecordExt.getInsuranceCompany().getName());
            weicheQuoteEmailInfo.setPayableAmount((quoteRecordExt.getTotalPremium() == null || quoteRecordExt.getTotalPremium().equals(0.00)) ? " " : quoteRecordExt.getTotalPremium().toString() + " 元");
            weicheQuoteEmailInfo.setExpireDate(DateUtils.getDateString(quoteRecordExt.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));

            weicheQuoteEmailInfo.setEngineNo(quoteRecordExt.getAuto().getEngineNo());
            weicheQuoteEmailInfo.setVinNo(quoteRecordExt.getAuto().getVinNo());
            weicheQuoteEmailInfo.setEnrollDate(DateUtils.getDateString(quoteRecordExt.getAuto().getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            weicheQuoteEmailInfo.setIdentity(quoteRecordExt.getAuto().getIdentity());
            weicheQuoteEmailInfo.setChannel(quoteRecordExt.getChannel().getDescription());
            Map<String, Integer> mobileMap;
            if (!cacheMap.containsKey(licensePlateNo)) {//不同的车牌号
                num++;
                weicheQuoteEmailInfo.setCpa(num + "");
                mobileMap = new HashMap<>();
                mobileMap.put(mobile, num);
                cacheMap.put(licensePlateNo, mobileMap);
            } else {//同一个车牌号
                mobileMap = cacheMap.get(licensePlateNo);
                Integer numTemp = mobileMap.get(mobile);
                if (numTemp == null) {
                    numTemp = num;
                    mobileMap.put(mobile, numTemp);
                }
                weicheQuoteEmailInfo.setCpa(numTemp + "");
            }

            weicheQuoteEmailInfo.setCpaTime(DateUtils.getDateString(quoteRecordExt.getCreateTime(), DateUtils.DATE_SHORTDATE_PATTERN));

            dataList.add(weicheQuoteEmailInfo);
        }
        return dataList;
    }


    /******************   原MySql逻辑    start  ******************/
    /*public List<QuoteRecordExt> convertToQuoteRecord(List<ApplicationLog> logList) {
        List<QuoteRecordExt> quoteRecordList = new ArrayList<>();
        for (ApplicationLog applicationLog : logList) {
            QuoteRecordExt quoteRecordExt;
            try {
                quoteRecordExt = CacheUtil.doJacksonDeserialize(applicationLog.getLogMessage(), QuoteRecordExt.class);
            } catch (Exception e) {
                continue;
            }
            if (quoteRecordExt.getApplicant() == null || quoteRecordExt.getApplicant().getMobile() == null) {
                continue;
            }
            quoteRecordExt.setCreateTime(applicationLog.getCreateTime());
            quoteRecordExt.setLogId(applicationLog.getId());
            quoteRecordExt.setApplicant(applicationLog.getUser());
            quoteRecordExt.setUserId(applicationLog.getUser().getId());

            quoteRecordList.add(quoteRecordExt);
        }
        return quoteRecordList;
    }*/
    /******************   原MySql逻辑    end  ******************/


    /******************
     * MongoDB逻辑    start
     ******************/
    public List<QuoteRecordExt> convertToQuoteRecord(List<MoApplicationLog> logList, Set<Long> historyUser) {
        List<QuoteRecordExt> quoteRecordList = new ArrayList<>();
        for (MoApplicationLog applicationLog : logList) {
            /**
             * 如果该用户没有历史记录，才进行数据的封装
             * **/
            if (!historyUser.contains(applicationLog.getUser().getId())) {
                QuoteRecordExt quoteRecordExt;
                try {
                    quoteRecordExt = CacheUtil.doJacksonDeserialize(JSON.toJSONString(applicationLog.getLogMessage()), QuoteRecordExt.class);
                } catch (Exception e) {
                    continue;
                }
                if (quoteRecordExt.getApplicant() == null || quoteRecordExt.getApplicant().getMobile() == null) {
                    continue;
                }
                quoteRecordExt.setCreateTime(applicationLog.getCreateTime());
                quoteRecordExt.setLogId(applicationLog.getId());
                if (applicationLog.getUser() != null) {
                    quoteRecordExt.setApplicant(userRepository.findOne(applicationLog.getUser().getId()));
                    quoteRecordExt.setUserId(applicationLog.getUser().getId());
                }

                quoteRecordList.add(quoteRecordExt);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("微车邮件数据统计报表,去除有记录的用户和手机号为空的用户，有效数据条数为【{}】", quoteRecordList.size());
        }
        return quoteRecordList;
    }
    /******************   MongoDB逻辑    end  ******************/
}
