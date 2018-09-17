package com.cheche365.cheche.externalapi.model

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.service.QuoteRecordCacheService

/**
 * Created by wen on 2018/9/10.
 */
class AiBaoProposal extends ProposalParameter{

    AiBaoProposal(PurchaseOrder purchaseOrder, QuoteRecordCacheService cacheService){
        super(purchaseOrder,cacheService)
    }

    String payUrl(){
        proposal?.payUrl
    }
}
