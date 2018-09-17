package com.cheche365.cheche.web.integration.flow.step.from

import org.springframework.integration.gateway.MessagingGatewaySupport

import static org.springframework.integration.dsl.IntegrationFlows.from

/**
 * 处理消息网关
 * Created by liheng on 2018/6/14 0014.
 */
abstract class AMessagingGatewaySupportFrom extends AMessageFrom {

    private MessagingGatewaySupport inboundGateway

    AMessagingGatewaySupportFrom(MessagingGatewaySupport inboundGateway) {
        this.inboundGateway = inboundGateway
    }

    @Override
    def from(context) {
        from inboundGateway
    }
}
