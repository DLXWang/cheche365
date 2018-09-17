package com.cheche365.cheche.rest.integration.handle

import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler
import com.cheche365.cheche.web.integration.IIntegrationHandler
import com.cheche365.cheche.web.model.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.CHANNEL_AGENT_REGISTER
import static com.cheche365.cheche.core.model.agent.AgentLevel.Enum.SALE_DIRECTOR_1
import static com.cheche365.cheche.core.service.sms.ConditionTriggerUtil.sendMessage
import static com.cheche365.cheche.core.service.sms.SmsCodeConstant.AGENT_LEVEL
import static com.cheche365.cheche.core.service.sms.SmsCodeConstant.AGENT_MOBILE
import static com.cheche365.cheche.core.service.sms.SmsCodeConstant.AGENT_NAME
import static com.cheche365.cheche.core.service.sms.SmsCodeConstant.MOBILE

/**
 * Created by liheng on 2018/8/31 0031.
 */
@Service
class SendSmsHandler implements IIntegrationHandler<Message<ChannelAgent>> {

    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler

    @Override
    Message<ChannelAgent> handle(Message<ChannelAgent> message) {
        def channelAgent = message.payload
        // 发送代理人注册短信
        if (SALE_DIRECTOR_1 != channelAgent.agentLevel) {
            sendMessage conditionTriggerHandler, [
                (MOBILE)      : channelAgent.parent.user.mobile,
                (AGENT_MOBILE): channelAgent.user.mobile,
                (AGENT_NAME)  : channelAgent.user.name,
                (AGENT_LEVEL) : channelAgent.agentLevel.description,
                type          : CHANNEL_AGENT_REGISTER.id as String
            ]
        }
        return null
    }
}
