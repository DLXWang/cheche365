package com.cheche365.cheche.externalapi.api.tk

import com.cheche365.cheche.core.model.Payment
import org.springframework.stereotype.Service

/**
 * Created by wen on 2018/4/10.
 */
@Service
class TkPaymentAPI extends TkAPI{

    @Override
    def originBody(Payment payment,Map params){
        [
            platformId : params.platformId,
            proposalNo : payment.purchaseOrder.orderSourceId,
            successNotifyUrl : params.frontUrl,
            failNotifyUrl : params.frontUrl,
            businessType :'0'
        ].findAll {it.key && it.value}
    }

    @Override
    def function(){
        'getSytUrl'
    }

}
