package com.cheche365.cheche.unionpay.payment.pay

import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import com.cheche365.cheche.core.service.QuoteConfigService
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler
import com.cheche365.cheche.core.service.sms.ConditionTriggerUtil
import com.cheche365.cheche.unionpay.UnionPayState
import com.cheche365.cheche.unionpay.payment.UnionPayProcessor
import com.cheche365.cheche.unionpay.payment.query.UnionPayQueryTradeHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.service.PaymentSerialNumberGenerator.getPurchaseNo

@Component
class UnionPayHandle {
    private Logger logger = LoggerFactory.getLogger(UnionPayHandle.class);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UnionPayQueryTradeHandler unionPayQueryTradeHandler;

    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private UnionPayProcessor unionPayProcessor;

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private QuoteConfigService quoteConfigService

    @Transactional
    void doPayService(Map<String, String> respMap) {
        logger.info("处理银联支付返回结果开始...");
        String respCode = respMap.get("respCode");
        if (UnionPayState.isPaySuccess(respCode)) {
            this.doOnPaySuccess(respMap);
        } else if (UnionPayState.isUnknown(respCode)) {
            this.doOnPayUnknown(respMap);
        } else {
            this.doOnPayFail(respMap);
        }
        logger.info("处理银联支付返回结果结束...");
    }

    @Transactional
    void doOnPaySuccess(Map<String, String> respMap) {
        String orderId = respMap.get("orderId");
        logger.info("银联支付成功，订单号 -> {}", orderId);
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(getPurchaseNo(orderId));
        Payment payment = paymentRepository.findByOutTradeNo(orderId);
        payment.setChannel(PaymentChannel.Enum.UNIONPAY_3);

        if (PaymentStatus.Enum.PAYMENTSUCCESS_2.getId().equals(payment.getStatus().getId())) {
            logger.warn("注意：银联支付订单{}可能发生重复支付，停止更新支付状态", orderId);
            unionPayProcessor.saveUnionPayLog(orderId,  "注意：银联支付前台类交易，订单已支付成功，可能重复支付，订单号 -> " + orderId);
            return;
        }

        payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
        //交易流水号，退款交易需要
        payment.setThirdpartyPaymentNo(respMap.get("queryId"));
        paymentRepository.save(payment);
        if (!UnionPayProcessor.isUnionPayRefundTrade(respMap.get("txnType"))) {
            purchaseOrder.setStatus(OrderStatus.Enum.PAID_3);
        }
        purchaseOrderRepository.save(purchaseOrder);

        if (!UnionPayProcessor.isUnionPayRefundTrade(respMap.get("txnType"))) {
            QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId())
            ConditionTriggerUtil.sendPaymentSuccessMessage(conditionTriggerHandler, quoteRecord, purchaseOrder);//退款不需要
        }

    }

    @Transactional
    void doOnPayUnknown(Map<String, String> respMap) {
        String orderId = respMap.get("orderId");
        String queryId = respMap.get("queryId");
        String channelId = respMap.get("reqReserved");
        logger.info("银联支付返回未知状态，发起单笔订单查询，订单号 -> {}", orderId);

        Map<String, String> resData = unionPayQueryTradeHandler.sendReqData(orderId, queryId,
            Channel.toChannel(Long.valueOf(channelId)));
        if (resData == null || resData.isEmpty())
            return;

        String origRespCode = resData.get("origRespCode");
        if (UnionPayState.isPaySuccess(origRespCode)) {
            this.doOnPaySuccess(resData);
        } else if (!UnionPayState.isUnknown(origRespCode)) {
            this.doOnPayFail(resData);
        }
    }

    @Transactional
    void doOnPayFail(Map<String, String> respMap) {
        String orderId = respMap.get("orderId");
        logger.info("银联支付失败，订单号 -> {}", orderId);
        Date now = Calendar.getInstance().getTime();
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(getPurchaseNo(orderId));
        Payment payment = paymentRepository.findByOutTradeNo(orderId);

        if (PaymentStatus.Enum.PAYMENTSUCCESS_2.getId().equals(payment.getId())) {
            logger.warn("注意：银联支付订单{}已支付，支付失败不再更新支付状态", orderId);
            return;
        }

        payment.setStatus(PaymentStatus.Enum.PAYMENTFAILED_3);
        payment.setChannel(PaymentChannel.Enum.UNIONPAY_3);
        payment.setUpdateTime(now);
        paymentRepository.save(payment);

        purchaseOrder.setStatus(purchaseOrder.getOperator() == null ? OrderStatus.Enum.PENDING_PAYMENT_1 : OrderStatus.Enum.HANDLING_2);
        purchaseOrder.setUpdateTime(now);
        purchaseOrderRepository.save(purchaseOrder);
    }
}
