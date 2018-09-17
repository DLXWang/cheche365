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
 * 用户最近一次更新超过一周,则短信通知该用户(一天一次)
 * Created by yinJianBin on 2018/5/8.
 */
@Component
class TideStatusNoChangeSmsRemindByDayTask extends BaseTask {

    @Autowired
    ConditionTriggerHandler conditionTriggerHandler
    @Autowired
    InternalUserManageService internalUserManageService
    @Autowired
    TidePlatformRepository tidePlatformRepository


    void doProcess() {
        def list = tidePlatformRepository.findRebateNoChange(7, 14)
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
