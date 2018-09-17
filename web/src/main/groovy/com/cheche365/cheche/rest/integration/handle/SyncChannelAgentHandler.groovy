package com.cheche365.cheche.rest.integration.handle

import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.partner.service.order.PartnerOrderService
import com.cheche365.cheche.web.integration.IIntegrationHandler
import com.cheche365.cheche.web.model.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by liheng on 2018/5/11 0011.
 */
@Service
class SyncChannelAgentHandler implements IIntegrationHandler<Message<ChannelAgent>> {

    @Autowired
    private PartnerOrderService partnerOrderService

    @Override
    Message<ChannelAgent> handle(Message<ChannelAgent> message) {
        partnerOrderService.syncChannelAgent message
        message
    }
}
