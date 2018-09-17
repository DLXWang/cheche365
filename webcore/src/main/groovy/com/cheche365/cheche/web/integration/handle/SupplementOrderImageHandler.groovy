package com.cheche365.cheche.web.integration.handle

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import com.cheche365.cheche.web.integration.IIntegrationHandler
import com.cheche365.cheche.web.model.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.util.CacheUtil.doJacksonDeserialize
import static com.cheche365.cheche.core.util.CacheUtil.doJacksonSerialize
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW

/**
 * Created by wen on 2018/8/30.
 */
@Service
class SupplementOrderImageHandler implements IIntegrationHandler<Message> {

    @Autowired
    PurchaseOrderImageService purchaseOrderImageService

    @Transactional(propagation = REQUIRES_NEW)
    @Override
    Message handle(Message payload) {
        purchaseOrderImageService.persistOrderFinishedImages(doJacksonDeserialize(doJacksonSerialize(payload.payload), PurchaseOrder.class))
        return payload
    }

}
