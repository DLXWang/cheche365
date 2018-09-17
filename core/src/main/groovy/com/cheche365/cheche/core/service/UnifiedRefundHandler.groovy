package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentStatus
import com.cheche365.cheche.core.repository.PaymentRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Created by Administrator on 2016/10/18 0018.
 * 统一退款服务，支持微信，支付宝，银联
 */
@Component
public abstract class UnifiedRefundHandler {

    private Logger logger = LoggerFactory.getLogger(UnifiedRefundHandler.class);
    @Autowired
    private PaymentSerialNumberGenerator paymentSerialNumberGenerator;

    @Autowired
    private PaymentRepository paymentRepository;

    abstract boolean support(Payment payment);

    @Transactional
    Map<Long,Boolean> refund(List<Payment> payments) {
        logger.info(name()+"调用退款开始");
        Map<Long,Map> sendMap = new HashMap<Long,Map>();
        String orderNo = payments.get(0).getPurchaseOrder().getOrderNo();
        for(Payment payment:payments){
            try {
                paymentSerialNumberGenerator.next(payment);
                logger.info(name() + "订单:{}调用退款单号为:{}" ,orderNo,payment.getOutTradeNo());
                Map<String, String> reqMap = createMap(payment);
                sendMap.put(payment.getId(),reqMap);
            }catch(Exception e){
                logger.error(name()+"退款失败", e);
                logger.info("订单{} 支付单{} 退款失败", payment.getPurchaseOrder().getOrderNo(), payment.getId());
                throw e;
            }
        }
        Map<Long, Boolean> syncCallResult = callPlatform(orderNo, sendMap);
        syncCallResult.entrySet().findAll { !it.getValue() }.each { entry ->
            payments.find { it.id == entry.getKey() }.setStatus(PaymentStatus.Enum.CANCEL_4);
        }
        paymentRepository.save(payments);
        return syncCallResult;
    }

    abstract boolean refund(Payment payment);

    abstract Map<Long,Boolean> callPlatform(String orderNo,Map<Long,Map> sendMap);

    abstract Map<String, String> createMap(Payment payment);

    abstract String name();


}
