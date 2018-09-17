package com.cheche365.cheche.scheduletask.task;


import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.repository.DailyInsuranceRepository;
import com.cheche365.cheche.core.repository.DailyRestartInsuranceRepository;
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 停复驶开始时间前12小时短信提醒
 * Created by Luly on 2017/02/15.
 */
@Service
public class StopAndRestartTwelveHoursSendSMSTask extends BaseTask {
    private Logger logger = LoggerFactory.getLogger(StopAndRestartTwelveHoursSendSMSTask.class);

    @Autowired
    private DailyInsuranceRepository dailyInsuranceRepository;

    @Autowired
    private DailyRestartInsuranceRepository dailyRestartInsuranceRepository;

    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;

    @Override
    protected void doProcess() throws Exception {
        List<Object[]> stopList = dailyInsuranceRepository.findStopDataByTime();
        logger.debug("停驶开始日前12小时需发送信息数据共{}条", CollectionUtils.isEmpty(stopList) ? 0 : stopList.size());

        List<Object[]> restartList = dailyRestartInsuranceRepository.findRestartDataByTime();
        logger.debug("复驶开始日前12小时需发送信息数据共{}条", CollectionUtils.isEmpty(stopList) ? 0 : stopList.size());
        this.processData(stopList, ScheduleCondition.Enum.STOP_BEGIN_TWELVE_HOUR.getId().toString());
        this.processData(restartList, ScheduleCondition.Enum.RESTART_BEGIN_TWELVE_HOUR.getId().toString());
    }

    private void processData(List<Object[]> dataList, String type) {
        if (!CollectionUtils.isEmpty(dataList)) {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put(SmsCodeConstant.TYPE, type);
            for (Object[] obj : dataList) {
                paramMap.put(SmsCodeConstant.AUTO_LICENSEPLATE_NO, String.valueOf(obj[0]));//车牌号
                paramMap.put(SmsCodeConstant.ORDER_EFFECTIVE_DATE, String.valueOf(obj[1]));//起止日期
                paramMap.put(SmsCodeConstant.MOBILE, String.valueOf(obj[2]));//手机号
//                paramMap.put(SmsCodeConstant.ORDER_ORDER_NO, String.valueOf(obj[3]));
                conditionTriggerHandler.process(paramMap);
            }
        } else {
            logger.debug("今日无符合条件的数据");
        }
    }
}
