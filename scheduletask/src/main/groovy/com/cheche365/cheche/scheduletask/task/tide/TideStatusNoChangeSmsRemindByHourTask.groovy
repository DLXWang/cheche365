package com.cheche365.cheche.scheduletask.task.tide

import com.cheche365.cheche.core.model.ScheduleCondition
import com.cheche365.cheche.core.model.tide.TidePlatform
import com.cheche365.cheche.core.repository.tide.TidePlatformRepository
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler
import com.cheche365.cheche.core.service.sms.SmsCodeConstant
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.scheduletask.task.BaseTask
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * 如果最近的一次点位更新超过2周,则1小时通知一次
 * Created by yinJianBin on 2018/5/8.
 */
@Component
class TideStatusNoChangeSmsRemindByHourTask extends BaseTask {

    @Autowired
    ConditionTriggerHandler conditionTriggerHandler
    @Autowired
    InternalUserManageService internalUserManageService
    @Autowired
    TidePlatformRepository tidePlatformRepository


    void doProcess() {
        def list = tidePlatformRepository.findRebateNoChange(14, 15)
        list.each {
            sendSms(it)
        }
    }

    def sendSms(TidePlatform tidePlatform) {
        def paramMap = [
                (SmsCodeConstant.TYPE)              : ScheduleCondition.Enum.REBATE_NO_CHANGE.getId().toString(),
                (SmsCodeConstant.MOBILE)            : tidePlatform.mobile,
                (SmsCodeConstant.INTERNAL_USER_NAME): tidePlatform.userName,
                (SmsCodeConstant.PLATFORM_NAME)     : tidePlatform.name,
                (SmsCodeConstant.NUMBER)            : tidePlatform.status as String
        ]
        conditionTriggerHandler.process(paramMap)
    }

}
