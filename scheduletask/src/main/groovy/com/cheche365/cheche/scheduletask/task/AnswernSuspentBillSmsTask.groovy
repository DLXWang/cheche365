package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.core.model.ScheduleCondition
import com.cheche365.cheche.core.repository.DailyInsuranceRepository
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler
import com.cheche365.cheche.core.service.sms.SmsCodeConstant
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils

/**
 * 停复驶开始时间前12小时短信提醒
 * Created by yinjianbin on 2017/12/27
 */
@Service
public class AnswernSuspentBillSmsTask extends BaseTask {
    private Logger logger = LoggerFactory.getLogger(AnswernSuspentBillSmsTask.class);

    @Autowired
    private DailyInsuranceRepository dailyInsuranceRepository;

    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;

    @Override
    protected void doProcess() throws Exception {
        List<Object[]> dataList = dailyInsuranceRepository.findAnswernExpire90DaysData();
        logger.debug("安心停驶返钱账单短信通知数据{}条", dataList.size());
        processData(dataList)
    }

    private void processData(List<Object[]> dataList) {
        if (!CollectionUtils.isEmpty(dataList)) {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.ANSWERN_SUSPEND_BILL.id.toString());
            for (Object[] obj : dataList) {
                paramMap.put(SmsCodeConstant.SUSPEND_BILL_LINK, String.valueOf(obj[0]));//订单号
                paramMap.put(SmsCodeConstant.MOBILE, String.valueOf(obj[1]));//手机号
                conditionTriggerHandler.process(paramMap);
            }
        } else {
            logger.debug("今日无符合条件的数据");
        }
    }
}
