package com.cheche365.cheche.rest.processor.login

import com.cheche365.cheche.core.model.Channel
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * @Author shanxf IOS A端 登陆
 * @Date 2018/4/2  15:48
 */
@Component
@Order(4)
class IosAgentLoginProcessor extends AndroidAgentLoginProcessor{


    @Override
    List<Channel> getSupportClientType() {
        return Collections.singletonList(Channel.Enum.IOS_CHEBAOYI_221)
    }

    @Override
    Channel deviceClientType() {
        return Channel.Enum.IOS_CHEBAOYI_221
    }
}
