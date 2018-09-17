package com.cheche365.cheche.web.integration.flow.step.from

import com.cheche365.cheche.web.model.MessageChannel

import static com.cheche365.cheche.web.integration.Constants._POLLER_CONFIGURER
import static com.cheche365.cheche.web.model.MessageChannel.ChannelType.POLLABLE_CHANNEL
import static org.springframework.integration.dsl.IntegrationFlows.from

/**
 * 处理消息渠道消息
 * Created by liheng on 2018/6/14 0014.
 */
class MessageChannelFrom extends AMessageFrom {

    private MessageChannel messageChannel
    private Closure endpointConfigurer

    MessageChannelFrom(MessageChannel messageChannel, Closure endpointConfigurer = null) {
        this.messageChannel = messageChannel
        this.endpointConfigurer = endpointConfigurer ?: this.messageChannel.channelType in POLLABLE_CHANNEL ? _POLLER_CONFIGURER : null
    }

    @Override
    def from(context) {
        from(messageChannel.channelName).with {
            endpointConfigurer ? it.bridge(endpointConfigurer) : it
        }
    }
}
