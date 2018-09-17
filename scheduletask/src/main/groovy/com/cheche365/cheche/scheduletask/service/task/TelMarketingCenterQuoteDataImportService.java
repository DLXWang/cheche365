package com.cheche365.cheche.scheduletask.service.task;

import com.alibaba.fastjson.JSON;
import com.cheche365.cheche.common.math.NumberUtils;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.core.repository.TelMarketingCenterRepeatRepository;
import com.cheche365.cheche.core.repository.UserRepository;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterRepeatInfo;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterTaskType;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepeatInfoRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterChannelFilterService;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterService;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.web.model.Message;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 导入日志中记录的报价用户记录进入电销
 * Created by yinjianbin on 2016/11/10.
 */
@Service
public class TelMarketingCenterQuoteDataImportService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterQuoteDataImportService.class);
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MoApplicationLogRepository applicationLogMongoRepository;
    @Autowired
    private TelMarketingCenterChannelFilterService telMarketingCenterChannelFilterService;
    @Autowired
    private QuoteDataImportHandler quoteDataImportHandler;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BaseService baseService;
    @Autowired
    private TelMarketingCenterService telMarketingCenterService;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private TelMarketingCenterRepeatInfoRepository telMarketingCenterRepeatInfoRepository;
    @Autowired
    private TelMarketingCenterRepeatRepository telMarketingCenterRepeatRepository;

    Date getStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, 17);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 20);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public void importQuoteData() {
        Date date = new Date();
        Date startDate;
        String quoteLogCreateTimeCache = stringRedisTemplate.opsForValue().get(TaskConstants.QUOTE_RECORD_LOG_CREATE_TIME_CACHE);
        if (StringUtils.isEmpty(quoteLogCreateTimeCache)) {
            quoteLogCreateTimeCache = "0";
            startDate = DateUtils.getCustomDate(date, -1, 0, 0, 0);
        } else {
            startDate = new Date(NumberUtils.toLong(quoteLogCreateTimeCache));
        }
        logger.debug("开始重新导入错误的报价数据......");
        startDate = getStartDate();
        Date yesterdayEnd = DateUtils.getCustomDate(date, -1, 23, 59, 59);
        logger.debug("schedule task starting--> import quoteRecord data in the log,start from createTimeMillis --> ({}),startDate:({})", quoteLogCreateTimeCache, startDate);
        List<Channel> excludeChannelList = telMarketingCenterChannelFilterService.findExcludeChannelsByTaskType(TelMarketingCenterTaskType.Enum.QUOTELOG);

        /**
         * 脚本拆分：
         *  1) 按时间、类型、用户不为空、ID大于上次处理ID，查询出日志信息
         *  2) 根据查询出的日志集合，找出用户手机号为空的用户，放入set集合，用以做过滤
         *  剩余的即为有效的数据信息
         * **/
        Pageable pageable = baseService.buildPageable(1, TaskConstants.PAGE_SIZE, Sort.Direction.ASC, "id");
        Page<MoApplicationLog> logPage = applicationLogMongoRepository.findByLogTypeAndCreateTimeBetween(LogType.Enum.Quote_Cache_Record_31.getId(), startDate, yesterdayEnd, pageable);
        while (CollectionUtils.isNotEmpty(logPage.getContent())) {
            List<MoApplicationLog> content = logPage.getContent();
            List<MoApplicationLog> logList = new ArrayList<>();
            logList.addAll(content);
            int size = logList.size();
            logger.debug("timeStamp>{}的数据共{}条,该批次获取到报价数据{}条", NumberUtils.toLong(quoteLogCreateTimeCache), logPage.getTotalElements(), size);

            List<String> orderedMobileList;
            List<MoApplicationLog> noOrderLogList;
            Set<String> mobileSet = new HashSet<>();
            Iterator<MoApplicationLog> iterator = logList.iterator();
            for (; iterator.hasNext(); ) {
                MoApplicationLog mongo = iterator.next();
                boolean isMobileEmpty = mongo.isMobileEmpty();
                if (isMobileEmpty) {
                    iterator.remove();
                } else {
                    String mobile = ((Map) mongo.getLogMessage()).get("mobile").toString();
                    if (StringUtils.isNotBlank(mobile)) {
                        mobileSet.add(mobile);
                    }
                }
            }
            if (CollectionUtils.isEmpty(mobileSet)) {
                mobileSet.add("");
            }
            // mongodb的in条件只接受数据格式的参数，集合形式的也不行；所以将用户ID从list转到了数据中
            orderedMobileList = userRepository.findOrderedMobileList(startDate, yesterdayEnd, new ArrayList<>(mobileSet));
            logger.debug("下过单手机号:[{}]", orderedMobileList);
            if (CollectionUtils.isNotEmpty(orderedMobileList)) {
                mobileSet.removeAll(orderedMobileList);//结果为: 未下单手机号+新报价的手机号
            }
            noOrderLogList = new LinkedList<>();
            Iterator<MoApplicationLog> it = logList.iterator();
            while (it.hasNext()) {
                MoApplicationLog next = it.next();
                String mobile = ((Map) next.getLogMessage()).get("mobile").toString();
                if (mobileSet.contains(mobile)) {
                    noOrderLogList.add(next);
                }
            }
            List<QuoteRecordExt> quoteRecordList = quoteDataImportHandler.convertToQuoteRecord(noOrderLogList, excludeChannelList);
            logger.debug("日志createTime大于({})范围内的申请报价的总数量为{},本次查询数量为{},转换为报价数据的数量为{}", startDate, logPage.getTotalElements(), size, quoteRecordList.size());
            processData(quoteRecordList);

            quoteLogCreateTimeCache = logList.get(logList.size() - 1).getCreateTime().getTime() + "";
            stringRedisTemplate.opsForValue().set(TaskConstants.QUOTE_RECORD_LOG_CREATE_TIME_CACHE, quoteLogCreateTimeCache);
            logger.debug("current quoteLogCreateTimeCache value:{}", quoteLogCreateTimeCache);
            if (size < TaskConstants.PAGE_SIZE) {
                break;
            }
            startDate = new Date(NumberUtils.toLong(quoteLogCreateTimeCache));
            logPage = applicationLogMongoRepository.findByLogTypeAndCreateTimeBetween(LogType.Enum.Quote_Cache_Record_31.getId(), startDate, yesterdayEnd, pageable);
        }
    }


    /**
     * 保存数据到电销
     *
     * @param quoteRecordExtList
     */
    public void processData(List<QuoteRecordExt> quoteRecordExtList) {
        Map<String, List<QuoteRecordExt>> userQuoteRecordListMap = quoteRecordExtList.stream()
                .filter(qr -> StringUtils.isNotEmpty(qr.getMobile()))
                .collect(Collectors.groupingBy(QuoteRecordExt::getMobile));

        for (String mobile : userQuoteRecordListMap.keySet()) {
            List<QuoteRecordExt> quoteRecordExts = userQuoteRecordListMap.get(mobile);
            User user = quoteRecordExts.get(0).getApplicant();
            //保存该用户对应的数据进入电销
            try {
                quoteDataImportHandler.saveToTelMarketingCenterByMobile(mobile, user, quoteRecordExts);
            } catch (Exception e) {
                logger.error("定时任务导入报价日志记录至电销表出错,错误用户手机号-->[{}],跳过该用户继续执行,错误原因-->[{}]", mobile, e.getMessage());
            }

        }
    }

    /******************   MongoDB逻辑    end  ******************/
    /**
     * 报价数据实时进电销
     *
     * @param payload
     */
    @Transactional
    public void realTimeImportQuoteData(Message<MoApplicationLog> payload) {
        TelMarketingCenterSource source = TelMarketingCenterSource.Enum.QUOTE_RECORD;
        MoApplicationLog moApplicationLog =  payload.getPayload();
        List<Channel> excludeChannelList = telMarketingCenterChannelFilterService.findExcludeChannelsByTaskType(TelMarketingCenterTaskType.Enum.QUOTELOG);
        QuoteRecordExt quoteRecordExt = convertToQuoteRecord(moApplicationLog, excludeChannelList);

        if (quoteRecordExt == null) {
            logger.warn("convert MoApplicationLog to QuoteRecordExt return null: logId -->[{}]", moApplicationLog.getId());
        } else {
            User user = quoteRecordExt.getApplicant();
            Date startTime = DateUtils.getDayStartTime(new Date());
            Date endTime = new Date();
            //为避免一个报价用户每天多次进电销 只留报价用户一天当中第一次报价记录进电销
            int count = telMarketingCenterRepeatRepository.countByMobileAndSource(quoteRecordExt.getMobile(), startTime, endTime, source.getId());
            if (count >= 1) {
                logger.info("报价用户实时进电销--该报价用户当天已进电销,手机号:{}", quoteRecordExt.getMobile());
            } else {
                logger.info("报价用户手机号:{}", quoteRecordExt.getMobile());
                Channel channel = channelRepository.findById(quoteRecordExt.getChannel().getId());
                TelMarketingCenterRepeat telMarketingCenterRepeat = telMarketingCenterService.save(null, user, quoteRecordExt.getMobile(), source, null, moApplicationLog.getCreateTime(), null, TelMarketingCenterRepeat.Enum.APPLICATION_LOG, channel, null, null,null);
                this.saveRepeatInfo(quoteRecordExt, telMarketingCenterRepeat);
            }
        }
    }

    private QuoteRecordExt convertToQuoteRecord(MoApplicationLog applicationLog, List<Channel> excludeChannelList) {
        QuoteRecordExt quoteRecordExt;
        try {
            quoteRecordExt = CacheUtil.doJacksonDeserialize(JSON.toJSONString(applicationLog.getLogMessage()), QuoteRecordExt.class);
        } catch (Exception e) {
            logger.error("Json Parse to QuoteRecordExt Exception: logId -->[{}]", applicationLog.getId());
            return null;
        }
        //如果chanel在被过滤渠道中,则丢弃该数据,不导入
        if (quoteRecordExt == null || quoteRecordExt.getChannel() == null || excludeChannelList.contains(quoteRecordExt.getChannel())) {
            return null;
        }
        quoteRecordExt.setCreateTime(applicationLog.getCreateTime());
        quoteRecordExt.setLogId(applicationLog.getId());
        if (applicationLog.getUser() != null) {
            User user = userRepository.findOne(applicationLog.getUser().getId());
            quoteRecordExt.setApplicant(user);
        } else {
            quoteRecordExt.setApplicant(null);
        }
        return quoteRecordExt;
    }


    private void saveRepeatInfo(QuoteRecordExt quoteRecordExt, TelMarketingCenterRepeat telMarketingCenterRepeat) {
        TelMarketingCenterRepeatInfo telMarketingCenterRepeatInfo = new TelMarketingCenterRepeatInfo();
        telMarketingCenterRepeatInfo.setTelMarketingCenterRepeat(telMarketingCenterRepeat);
        telMarketingCenterRepeatInfo.setSourceTable(TelMarketingCenterRepeatInfo.Enum.APPLICATION_LOG);
        telMarketingCenterRepeatInfo.setCreateTime(new Date());
        telMarketingCenterRepeatInfo.setSourceId(String.valueOf(quoteRecordExt.getLogId()));
        telMarketingCenterRepeatInfoRepository.save(telMarketingCenterRepeatInfo);
        logger.debug("实时导入报价数据,保存telMarketingCenterRepeatInfo成功,repeatId:{},repeatInfoId:{}", telMarketingCenterRepeat.getId(), telMarketingCenterRepeatInfo.getId());
    }

}
