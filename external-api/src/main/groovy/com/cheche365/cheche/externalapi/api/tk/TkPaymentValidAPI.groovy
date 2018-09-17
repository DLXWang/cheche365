package com.cheche365.cheche.externalapi.api.tk

import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.externalapi.model.TkProposal
import org.springframework.stereotype.Service

/**
 * Created by wen on 2018/6/25.
 */
@Service
class TkPaymentValidAPI extends TkAPI{

    @Override
    def originBody(Payment payment, Map params){
        [
            proposalNo : payment.purchaseOrder.orderSourceId,
            issueCode : new TkProposal(payment.purchaseOrder,cacheService).getValidCode(),  //北京地区必传
            businessType :'0'
        ].findAll {it.key && it.value}
    }


    @Override
    def function(){
        'checkIssueCode'
    }
}
