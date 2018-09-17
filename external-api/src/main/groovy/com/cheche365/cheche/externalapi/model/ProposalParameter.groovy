package com.cheche365.cheche.externalapi.model

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey

/**
 * Created by wen on 2018/8/8.
 */
@Slf4j
class ProposalParameter {

    Map proposal

    ProposalParameter(PurchaseOrder purchaseOrder, QuoteRecordCacheService cacheService){
        proposal = cacheService.getPersistentState(persistQRParamHashKey(purchaseOrder.objId))?.persistentState
        log.info("订单号 : ${purchaseOrder.orderNo} persistentState恢复 : ${proposal}")
    }

    String getToken(){
        proposal?.token
    }

}
