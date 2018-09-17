package com.cheche365.cheche.scheduletask.service.task;

import com.alibaba.fastjson.JSON;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.UserRepository;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.core.model.TelMarketingCenterRepeat;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterRepeatInfo;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepeatInfoRepository;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yinJianBin on 2017/7/10.
 */
@Service
public class QuoteDataImportHandler {

    Logger logger = LoggerFactory.getLogger(QuoteDataImportHandler.class);
    @Autowired
    private TelMarketingCenterService telMarketingCenterService;
    @Autowired
    private TelMarketingCenterRepeatInfoRepository telMarketingCenterRepeatInfoRepository;
    @Autowired
    private UserRepository userRepository;


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
                this.saveToTelMarketingCenterByMobile(mobile, user, quoteRecordExts);
            } catch (Exception e) {
                logger.error("定时任务导入报价日志记录至tel_info表出错,错误用户手机号-->[{}],跳过该用户继续执行,错误原因-->[{}]", mobile, e.getMessage());
            }

        }
    }

    @Transactional
    public void saveToTelMarketingCenterByMobile(String mobile, User user, List<QuoteRecordExt> quoteRecordExtList) {
        Map<Channel, List<QuoteRecord>> channelQuoteListMap = quoteRecordExtList.stream().collect(Collectors.groupingBy(QuoteRecord::getChannel));
        Map<Long, TelMarketingCenterRepeat> channelRepeatMap = new HashMap<>();

        TelMarketingCenterSource source = TelMarketingCenterSource.Enum.QUOTE_RECORD;
        for (Channel channel : channelQuoteListMap.keySet()) {
            QuoteRecord firstQuoteRecord = channelQuoteListMap.get(channel).get(0);
            Date sourceCreateTime = firstQuoteRecord.getCreateTime();
            TelMarketingCenterRepeat telMarketingCenterRepeat = telMarketingCenterService.save(null, user, mobile, source, null, sourceCreateTime, null, TelMarketingCenterRepeat.Enum.APPLICATION_LOG, channel, null, null,null);
            channelRepeatMap.put(channel.getId(), telMarketingCenterRepeat);
        }
        //保存info表
        this.saveInfo(quoteRecordExtList, channelRepeatMap);
    }


    private void saveInfo(List<QuoteRecordExt> quoteRecordExtList, Map<Long, TelMarketingCenterRepeat> channelRepeatMap) {
        for (QuoteRecordExt quoteRecordExt : quoteRecordExtList) {
            String sourceId = String.valueOf(quoteRecordExt.getLogId());
            TelMarketingCenterRepeatInfo telMarketingCenterRepeatInfo = telMarketingCenterRepeatInfoRepository.findBySourceId(sourceId);
            if (telMarketingCenterRepeatInfo != null) {
                logger.debug("logId = {}的数据已经存在,跳过该条数据", sourceId);
                continue;
            }
            telMarketingCenterRepeatInfo = new TelMarketingCenterRepeatInfo();
            telMarketingCenterRepeatInfo.setTelMarketingCenterRepeat(channelRepeatMap.get(quoteRecordExt.getChannel().getId()));
            telMarketingCenterRepeatInfo.setSourceTable(TelMarketingCenterRepeatInfo.Enum.APPLICATION_LOG);
            telMarketingCenterRepeatInfo.setCreateTime(new Date());
            telMarketingCenterRepeatInfo.setSourceId(sourceId);
            telMarketingCenterRepeatInfoRepository.save(telMarketingCenterRepeatInfo);
        }
    }


    public List<QuoteRecordExt> convertToQuoteRecord(List<MoApplicationLog> logList, List<Channel> excludeChannelList) {
        List<QuoteRecordExt> quoteRecordList = new ArrayList<>();
        QuoteRecordExt quoteRecordExt;
        for (MoApplicationLog applicationLog : logList) {
            try {
                quoteRecordExt = CacheUtil.doJacksonDeserialize(JSON.toJSONString(applicationLog.getLogMessage()), QuoteRecordExt.class);
            } catch (Exception e) {
                logger.error("Json Parse to QuoteRecordExt Exception: logId -->[{}]", applicationLog.getId());
                continue;
            }
            //如果chanel在被过滤渠道中,则丢弃该数据,不导入
            if (quoteRecordExt.getChannel() == null || excludeChannelList.contains(quoteRecordExt.getChannel())) {
                continue;
            }
            quoteRecordExt.setCreateTime(applicationLog.getCreateTime());
            quoteRecordExt.setLogId(applicationLog.getId());
            if (applicationLog.getUser() != null) {
                User user = userRepository.findOne(applicationLog.getUser().getId());
                quoteRecordExt.setApplicant(user);
            } else {
                quoteRecordExt.setApplicant(null);
            }

            quoteRecordList.add(quoteRecordExt);
        }
        logger.debug("将要被转换的数据量[{}]条,转换之后[{}]条", logList.size(), quoteRecordList.size());
        return quoteRecordList;
    }
    /******************   MongoDB逻辑    end  ******************/
}


class QuoteRecordExt extends QuoteRecord {

    private String logId;
    private Long userId;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
