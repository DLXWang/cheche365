package com.cheche365.cheche.rest.processor.login

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.rest.web.session.MobileSessionHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * @Author shanxf
 * @Date 2018/4/2  15:42
 */
@Component
@Order(5)
class AndroidAgentLoginProcessor extends ChannelAgentLoginProcessor {

    @Autowired
    private MobileSessionHandler mobileSessionHandler;//App Session持久化处理类

    /**
     * 车保易安卓客户端
     */
    @Override
    List<Channel> getSupportClientType() {
        return Collections.singletonList(Channel.Enum.ANDROID_CHEBAOYI_222)
    }

    @Override
    ChannelAgent register(RegisterInfo registerInfo) {
        ChannelAgent channelAgent = super.register(registerInfo)
        appBind(channelAgent)
        return channelAgent

    }

    @Override
    ChannelAgent login(LoginInfo loginInfo) {
        ChannelAgent channelAgent = super.login(loginInfo)
        appBind(channelAgent)
        return channelAgent
    }

    private void appBind(ChannelAgent channelAgent) {
        if (channelAgent?.user) {
            mobileSessionHandler.doLogin(getRequest(), session, channelAgent.user, deviceClientType())
        }
    }

    Channel deviceClientType() {
        return Channel.Enum.ANDROID_CHEBAOYI_222
    }

    @Override
    protected User getUserInSession() {
        return null
    }
}
