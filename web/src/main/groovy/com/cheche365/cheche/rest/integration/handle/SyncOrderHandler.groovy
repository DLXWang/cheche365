package com.cheche365.cheche.rest.integration.handle

import com.cheche365.cheche.partner.service.order.PartnerOrderService
import com.cheche365.cheche.web.integration.IIntegrationHandler
import com.cheche365.cheche.web.model.Message
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by liheng on 2018/6/28 0028.
 */
@Service
@Slf4j
class SyncOrderHandler implements IIntegrationHandler<Message<Map>> {

    @Autowired
    private PartnerOrderService partnerOrderService

    @Override
    Message<Map> handle(Message<Map> message) {
        log.info '同步订单：{}', message
        partnerOrderService.syncPurchaseOrder message.payload, true
        null
    }
}
