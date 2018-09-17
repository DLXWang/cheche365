package com.cheche365.cheche.rest.service.pingpp

import com.cheche365.cheche.core.service.callback.CallBackType
import com.cheche365.cheche.externalpayment.service.PingPlusCallBackService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class ChargeSuccessService extends PingWebhooksAbstract{

    @Autowired
    private PingPlusCallBackService pingPlusCallBackService

    @Override
    boolean webhooksType(String eventString) {
        return "charge.succeeded" == eventString
    }

    @Override
    void handle(Map event) {
        Map orderMap = event.data.object
        def paraMap = [
            "outTradeNo"         : orderMap.order_no,
            "thirdpartyPaymentNo": orderMap.id,
            "isOrderPaySuccess"  : true
        ]

        if ('wx_pub_qr' != orderMap.channel) {
            log.info("Ping Plus Call Back, event:{}, channel:{}, param:{}, skip handle callback ", event.type, orderMap.channel, paraMap)
            return
        }
        pingPlusCallBackService.call(paraMap, CallBackType.PAYMENT)
    }
}
