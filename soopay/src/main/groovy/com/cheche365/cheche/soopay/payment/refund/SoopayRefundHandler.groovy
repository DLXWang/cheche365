package com.cheche365.cheche.soopay.payment.refund

import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.service.UnifiedRefundHandler
import com.cheche365.cheche.externalapi.api.soopay.SoopayRefundAPI
import com.cheche365.cheche.soopay.payment.ISoopayHandler
import com.umpay.api.paygate.v40.Plat2Mer_v40
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.cheche365.cheche.soopay.SoopayConstant._SOOPAY_CALLBACK_REFUND_URL

/**
 * Created by mjg on 2017/6/19.
 */
@Component
@Slf4j
class SoopayRefundHandler extends UnifiedRefundHandler implements ISoopayHandler {

    @Autowired
    SoopayRefundAPI refundAPI

    boolean support(Payment payment) {
        return payment.getChannel() == PaymentChannel.Enum.SOO_PAY_17
    }


    boolean refund(Payment payment) {
        false
    }

    Map<Long, Boolean> callPlatform(String orderNo, Map<Long, Map> sendMap) {

        sendMap.collectEntries { map ->
            try {
                if (sendMap[map.key]) {
                    String respDate = refundAPI.call(map.value)
                    def resultMap = Plat2Mer_v40.getResData(respDate)
                    def status = resultMap.refund_state ?: resultMap.trade_state
                    log.info("联动支付退款请求返回值:{}", Plat2Mer_v40.getResData(respDate))//联动支付的验签
                    [(map.key): _STATE_MAPING[status]]
                }
            } catch (IOException e) {
                log.info("联动支付退款请求失败:{}", e)
                [(map.key): false]
            }
        }

    }

    String name() {
        return "联动优势支付";
    }

    Map<String, String> createMap(Payment payment) {

        log.info '订单支付时间：{}', payment.upstreamId.updateTime
        log.debug '创建联动支付请求参数'
        Map<String, String> request
        log.info '支付日期：{}', payment.upstreamId.updateTime
        if (dateFormat.format(payment.upstreamId.updateTime)== (dateFormat.format(new Date()))) {
            request = [
                amount    : ((payment.amount * 100) as Integer).toString(),
                charset   : SOOPAY_ENCODING,
                mer_date  : dateFormat.format(new Date()),
                mer_id    : SOOPAY_MERCHANT_ID,
                order_id  : payment.upstreamId.outTradeNo,
                res_format: SOOPAY_RES_FORMAT,
                service   : SOOPAY_TXN_TYPE_04,
                sign_type : SOOPAY_SIGN_METHOD,
                version   : SOOPAY_VERSION,
            ]
        } else {
            request = [
                charset      : SOOPAY_ENCODING,
                mer_date     : dateFormat.format(payment.upstreamId.updateTime),
                mer_id       : SOOPAY_MERCHANT_ID,
                order_id     : payment.upstreamId.outTradeNo,
                res_format   : SOOPAY_RES_FORMAT,
                service      : SOOPAY_TXN_TYPE_04,
                sign_type    : SOOPAY_SIGN_METHOD,
                version      : SOOPAY_VERSION,
                notify_url   : _SOOPAY_CALLBACK_REFUND_URL,
                refund_no    : payment.outTradeNo,
                org_amount   : ((payment.purchaseOrder.paidAmount * 100) as Integer).toString(),
                refund_amount: ((payment.amount * 100) as Integer).toString(),
            ]
        }
        log.info '联动支付退款请求参数:{}', request
        return request
    }

    private static _STATE_MAPING = [
        WAIT_BUYER_PAY: false,
        TRADE_SUCCESS : false,
        TRADE_CLOSE   : false,
        TRADE_CANCEL  : true,
        TRADE_FAIL    : false,
        REFUND_SUCCESS: true,
        REFUND_FAIL   : false,
        REFUND_PROCESS: false,
        REFUND_UNKNOWN: false,
    ]
}
