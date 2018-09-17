package com.cheche365.cheche.web.integration.flow.step.from

import org.springframework.integration.endpoint.MessageProducerSupport

import static org.springframework.integration.dsl.IntegrationFlows.from

/**
 * 接收消息生产商支持类端点消息，可接入站通道适配器
 * Created by liheng on 2018/6/14 0014.
 */
abstract class AMessageProducerSupportFrom extends AMessageFrom {

    private MessageProducerSupport messageProducerSupport

    AMessageProducerSupportFrom(MessageProducerSupport messageProducerSupport) {
        this.messageProducerSupport = messageProducerSupport
    }

    @Override
    def from(context) {
        from messageProducerSupport
    }
}
