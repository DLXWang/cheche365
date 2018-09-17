package com.cheche365.cheche.rest.service.pingpp

import com.cheche365.cheche.core.service.callback.CallBackType
import com.cheche365.cheche.externalpayment.service.PingPlusCallBackService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChargeRefundService extends PingWebhooksAbstract {

    @Autowired
    private PingPlusCallBackService pingPlusCallBackService

    @Override
    boolean webhooksType(String eventString) {
        return "refund.succeeded" == eventString
    }

    @Override
    void handle(Map event) {
        Map chargeRefundMap = event.data.object
        def param = [
            "thirdpartyPaymentNo"  : chargeRefundMap.id,
            "isChargeRefundSuccess": true
        ]
        pingPlusCallBackService.call(param, CallBackType.REFUNDS)
    }
}
