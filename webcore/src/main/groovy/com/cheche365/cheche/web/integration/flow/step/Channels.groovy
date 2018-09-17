package com.cheche365.cheche.web.integration.flow.step

import com.cheche365.cheche.web.integration.flow.TIntegrationStep
import com.cheche365.cheche.web.model.MessageChannel
import org.springframework.integration.channel.AbstractExecutorChannel
import org.springframework.integration.channel.AbstractPollableChannel
import org.springframework.messaging.MessageChannel as SpringMessageChannel

/**
 * outChannel
 * Created by liheng on 2018/6/20 0020.
 */
class Channels implements TIntegrationStep {

    private MessageChannel messageChannel
    private SpringMessageChannel sMessageChannel

    Channels(MessageChannel messageChannel) {
        this.messageChannel = messageChannel
    }

    Channels(SpringMessageChannel sMessageChannel) {
        this.sMessageChannel = sMessageChannel
    }

    @Override
    def build(context) {
        if (sMessageChannel instanceof AbstractPollableChannel || sMessageChannel instanceof AbstractExecutorChannel || messageChannel.needClone()) {
            context.flowBuilder.transform Object, { p -> p.clone() }
        }
        context.flowBuilder.channel sMessageChannel ?: messageChannel.channelName
    }
}
