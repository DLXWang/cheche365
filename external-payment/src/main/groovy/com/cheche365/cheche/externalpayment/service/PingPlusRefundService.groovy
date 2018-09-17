package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.service.UnifiedRefundHandler
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class PingPlusRefundService extends UnifiedRefundHandler {

    @Autowired
    private PingPlusService pingPlusService

    @Override
    boolean support(Payment payment) {
        return PaymentChannel.Enum.isPingPlusPay(payment.channel)
    }

    @Override
    Map<Long,Boolean> refund(List<Payment> payments) {
        log.info(name()+"调用全额退款开始");
        Map<Long, Boolean> syncCallResult = [:]
        String orderNo = payments.get(0).getPurchaseOrder().getOrderNo();
        for(Payment payment:payments){
            try {
                log.info(name() + "订单:{}调用退款单号为:{}" ,orderNo,payment.getOutTradeNo());
                syncCallResult.put(payment.id,refund(payment))
            }catch(Exception e){
                log.error(name()+"退款失败", e);
                log.info("订单{} 支付单{} 退款失败", payment.getPurchaseOrder().getOrderNo(), payment.getId());
                throw e;
            }
        }
        return syncCallResult;
    }

    @Override
    boolean refund(Payment payment) {
        log.info("调用部分退款开始");
        Map<String, Object> params = [
            payment : payment
        ]
        return pingPlusService.refund(params)
    }

    @Override
    Map<Long, Boolean> callPlatform(String orderNo, Map<Long, Map> sendMap) {
        return null
    }

    @Override
    Map<String, String> createMap(Payment payment) {
        return null
    }

    @Override
    String name() {
        return "PingPlusRefundService"
    }
}
