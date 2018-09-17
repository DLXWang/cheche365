package com.cheche365.cheche.rest.service.pingpp

import com.cheche365.cheche.core.service.callback.CallBackType
import com.cheche365.cheche.externalpayment.service.PingPlusCallBackService
import com.pingplusplus.model.Charge
import com.pingplusplus.model.Event
import com.pingplusplus.model.Order
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @Author shanxf
 * @Date 2018/1/2  9:59
 * 订单支付成功时触发
 */
@Service
class OrderSuccessService extends PingWebhooksAbstract{

    @Autowired
    private PingPlusCallBackService pingPlusCallBackService

    @Override
    boolean webhooksType(String eventString) {
        return "order.succeeded" == eventString
    }

    @Override
    void handle(Map event) {
        Map orderMap = event.data.object
        Map chargeMap = orderMap.charges.data[0]
        def paraMap = [
            "outTradeNo":orderMap.merchant_order_no,
            "thirdpartyPaymentNo":chargeMap.id,
            "itpNo":orderMap.id,
            "isOrderPaySuccess":true
        ]
        pingPlusCallBackService.call(paraMap,CallBackType.PAYMENT)
    }
}
