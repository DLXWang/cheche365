package com.cheche365.cheche.externalapi.model

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.service.QuoteRecordCacheService

/**
 * Created by wen on 2018/8/8.
 */
class HuanongProposal extends ProposalParameter{

    HuanongProposal(PurchaseOrder purchaseOrder, QuoteRecordCacheService cacheService){
        super(purchaseOrder,cacheService)
    }

    def phone(){
        proposal?.phoneNo
    }

    def verificationCode(){
        proposal?.verificationCode
    }
}
