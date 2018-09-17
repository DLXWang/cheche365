package com.cheche365.cheche.rest.service.pingpp

import com.cheche365.cheche.core.service.callback.CallBackType
import com.cheche365.cheche.externalpayment.service.PingPlusCallBackService
import com.pingplusplus.model.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @Author shanxf
 * @Date 2018/1/3  15:47
 */
@Service
class OrderRefundService extends PingWebhooksAbstract{

    @Autowired
    private PingPlusCallBackService pingPlusCallBackService

    @Override
    boolean webhooksType(String eventString) {
        return "order.refunded" == eventString
    }

    @Override
    void handle(Map event) {
        Map orderMap = event.data.object
        Map chargeMap = orderMap.charges.data[0]
        pingPlusCallBackService.call(["thirdpartyPaymentNo":chargeMap.id],CallBackType.REFUNDS)
    }
}
