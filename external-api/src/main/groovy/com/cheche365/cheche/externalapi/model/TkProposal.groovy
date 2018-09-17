package com.cheche365.cheche.externalapi.model

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.DateUtils.*
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey

/**
 * Created by wen on 2018/4/10.
 */
@Slf4j
class TkProposal extends ProposalParameter{

    TkProposal(PurchaseOrder purchaseOrder, QuoteRecordCacheService cacheService){
        super(purchaseOrder,cacheService)
    }

    String getFormId(){
        proposal?.proposalFormId
    }

    String getValidCode(){
        proposal?.verificationCode
    }

    Date insuranceEffectiveDate(){
        proposal?.commercialBeginDate ? getDate(proposal.commercialBeginDate as String,DATE_SHORTDATE_PATTERN) : null
    }

    Date ciEffectiveDate(){
        proposal?.compulsoryBeginDate ? getDate(proposal.compulsoryBeginDate as String,DATE_SHORTDATE_PATTERN) : null
    }

    Date insuranceExpireDate(){
        proposal?.commercialExpireDate ? getDate(proposal.commercialExpireDate as String,DATE_SHORTDATE_PATTERN) : null
    }

    Date ciExpireDate(){
        proposal?.compulsoryExpireDate ? getDate(proposal.compulsoryExpireDate as String,DATE_SHORTDATE_PATTERN) : null
    }
}
