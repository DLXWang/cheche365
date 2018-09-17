package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.math.NumberUtils;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.LogType;
import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository;
import com.cheche365.cheche.core.repository.UserRepository;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterTaskType;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterChannelFilterService;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
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

import java.util.*;

/**
 * 导入日志中记录的(ToA渠道的)报价用户记录进入电销
 * Created by yinjianbin on 2017/7/10.
 */
@Service
public class TelMarketingCenterToAQuoteDataImportService {

    Logger logger = LoggerFactory.getLogger(TelMarketingCenterToAQuoteDataImportService.class);
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



    /******************   MongoDB逻辑    start  ******************/
    /**
     * 导入日志中记录的报价用户记录进入电销
     */
    public void importQuoteData() {
        Date startDate;
        String quoteLogCreateTimeCache = stringRedisTemplate.opsForValue().get(TaskConstants.TOA_QUOTE_RECORD_LOG_CREATE_TIME_CACHE);
        if (StringUtils.isEmpty(quoteLogCreateTimeCache)) {
            quoteLogCreateTimeCache = "0";
            startDate = DateUtils.getCustomDate(new Date(), 0, 0, 0, 0);
        } else {
            startDate = new Date(NumberUtils.toLong(quoteLogCreateTimeCache));
        }
        Date endDate = new Date();
        logger.debug("schedule task starting--> import (ToA channel) quoteRecord data in the log,start from createTimeMillis --> [{}]", quoteLogCreateTimeCache);
        List<Channel> excludeChannelList = telMarketingCenterChannelFilterService.findExcludeChannelsByTaskType(TelMarketingCenterTaskType.Enum.QUOTELOG);

        /**
         * 脚本拆分：
         *  1) 按时间、类型、用户不为空、ID大于上次处理ID，查询出日志信息
         *  2) 根据查询出的日志集合，找出用户手机号为空的用户，放入set集合，用以做过滤
         *  剩余的即为有效的数据信息
         * **/
        Pageable pageable = baseService.buildPageable(1, TaskConstants.PAGE_SIZE, Sort.Direction.ASC, "id");
        Long[] channelIds = Channel.standardAgents().id
        Page<MoApplicationLog> logPage = applicationLogMongoRepository.findByLogTypeAndCreateTimeAndLogMessageAndId(LogType.Enum.Quote_Cache_Record_31.getId(), startDate, endDate, channelIds, pageable);

        while (CollectionUtils.isNotEmpty(logPage.getContent())) {
            List<MoApplicationLog> content = logPage.getContent();
            List<MoApplicationLog> logList = new ArrayList<>(content.size());
            logList.addAll(content);

            List<String> orderedMobileList;
            List<MoApplicationLog> noOrderLogList;
            int size = logList.size();
            Set<String> mobileSet = new HashSet<>();
            Iterator<MoApplicationLog> iterator = logList.iterator();
            for (; iterator.hasNext(); ) {
                MoApplicationLog mongo = iterator.next();
                boolean isMobileEmpty = mongo.isMobileEmpty();
                if (isMobileEmpty) {
                    iterator.remove();
                } else {
                    String mobile = ((Map) mongo.getLogMessage()).get("mobile").toString();
                    mobileSet.add(mobile);
                }
            }
            if (CollectionUtils.isEmpty(mobileSet)) {
                mobileSet.add("");
            }
            // mongodb的in条件只接受数据格式的参数，集合形式的也不行；所以将用户ID从list转到了数据中
            orderedMobileList = userRepository.findOrderedMobileList(startDate, endDate, new ArrayList<>(mobileSet));
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
            logger.debug("日志id大于范围内的(ToA渠道)申请报价的总数量为{},本次查询数量为{},转换为报价数据的数量为{}", logPage.getTotalElements(), size, quoteRecordList.size());
            quoteDataImportHandler.processData(quoteRecordList);

            quoteLogCreateTimeCache = logList.get(logList.size() - 1).getCreateTime().getTime() + "";
            stringRedisTemplate.opsForValue().set(TaskConstants.TOA_QUOTE_RECORD_LOG_CREATE_TIME_CACHE, quoteLogCreateTimeCache);
            if (size < TaskConstants.PAGE_SIZE) {
                break;
            }
            pageable = pageable.next();
            startDate = new Date(NumberUtils.toLong(quoteLogCreateTimeCache));
            logPage = applicationLogMongoRepository.findByLogTypeAndCreateTimeAndLogMessageAndId(LogType.Enum.Quote_Cache_Record_31.getId(), startDate, endDate, channelIds, pageable);
        }
    }
    /******************   MongoDB逻辑    end  ******************/


}
