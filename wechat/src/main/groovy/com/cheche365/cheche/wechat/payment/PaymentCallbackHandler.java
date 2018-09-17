package com.cheche365.cheche.wechat.payment;

import com.cheche365.cheche.core.message.RedisPublisher;
import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.model.PaymentStatus;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.service.DoubleDBService;
import com.cheche365.cheche.core.service.OrderCooperationInfoService;
import com.cheche365.cheche.core.service.QuoteConfigService;
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler;
import com.cheche365.cheche.core.service.sms.ConditionTriggerUtil;
import com.cheche365.cheche.wechat.TradeState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by zhengwei on 5/13/15.
 */

@Service
public class PaymentCallbackHandler {

    private Logger logger = LoggerFactory.getLogger(PaymentCallbackHandler.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;

    @Autowired
    private OrderCooperationInfoService orderCooperationInfoService;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private QuoteConfigService quoteConfigService;

    @Autowired
    private DoubleDBService doubleDBService;

    private Map<String,Object> orderQueryResponse;

    private PurchaseOrder purchaseOrder;

    private Payment payment;

    public PaymentCallbackHandler (){

    }

    public void doService(TradeState tradeState){
        switch (tradeState){
            case SUCCESS:
                this.doOnSuccess();
                logger.info("微信支付回调成功outTradeNo:{}",payment.getOutTradeNo());
                break;
            case NOTPAY:
                this.doOnNotPay();
                logger.info("微信支付支付状态为未支付outTradeNo:{}", payment.getOutTradeNo());
                break;
            case USERPAYING:
                this.doOnUserPaying();
                logger.info("微信支付支付状态为户支付中用outTradeNo:{}", payment.getOutTradeNo());
                break;
            case NOPAY:
                this.doOnNoPay();
                logger.info("微信支付支付状态为未支付(输入密码或确认支付超时)outTradeNo:{}", payment.getOutTradeNo());
            case REFUND:
                this.doOnRefund();
                logger.info("微信支付支付状态为转入退款outTradeNo:{}", payment.getOutTradeNo());
                break;
            case PAYERROR:
                this.doOnPayError();
                logger.info("微信支付支付状态为支付失败(其他原因，如银行返回失败)outTradeNo:{}", payment.getOutTradeNo());
                break;
            case CLOSED:
                this.doOnClosed();
                logger.info("微信支付支付状态为已关闭outTradeNo:{}", payment.getOutTradeNo());
                break;
            case REVOKED:
                this.doOnRevoked();
                logger.info("微信支付支付状态为已撤销outTradeNo:[]", payment.getOutTradeNo());
                break;
        }

    }


    public void doOnSuccess(){

        purchaseOrder = payment.getPurchaseOrder();
        payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
        payment.setThirdpartyPaymentNo((String) orderQueryResponse.get("transaction_id"));
        Payment paymentAfterSave = paymentRepository.save(payment);
        purchaseOrder.setStatus(OrderStatus.Enum.PAID_3);
        purchaseOrderRepository.save(purchaseOrder);

        MoApplicationLog log = MoApplicationLog.applicationLogByPurchaseOrder(purchaseOrder);
        log.setLogMessage("wechat payment success for purchase order: "+purchaseOrder.getOrderNo());
        doubleDBService.saveApplicationLog(log);
        logger.debug("wechat payment success for purchase order " + this.purchaseOrder.getOrderNo() + ", the payment id is " + paymentAfterSave);

        orderCooperationInfoService.updatePaySuccessOrderStatus(purchaseOrder);

        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        ConditionTriggerUtil.sendPaymentSuccessMessage(conditionTriggerHandler, quoteRecord, purchaseOrder);
        redisPublisher.publish(payment);
    }

    public void doOnFail(){

    }

    public void doOnNotPay(){
        logger.debug("the purchase order "+purchaseOrder.getOrderNo()+" is under not pay state");
    }

    public void doOnUserPaying(){
        logger.debug("the purchase order "+purchaseOrder.getOrderNo()+" is under paying state");
    }

    public void doOnNoPay(){
        logger.debug("the purchase order " + purchaseOrder.getOrderNo() + " is under no pay state");
    }

    public void doOnRefund(){
        logger.debug("the purchase order "+purchaseOrder.getOrderNo()+" is refunded ");
        this.doOnRevoked();

    }

    public void doOnPayError(){
        logger.debug("the purchase order "+purchaseOrder.getOrderNo()+" is under pay error state");
        this.doOnRevoked();

    }

    public void doOnClosed(){
        logger.debug("the purchase order " + purchaseOrder.getOrderNo() + " is under closed state");
        this.doOnRevoked();

    }

    public void doOnRevoked(){
        if (payment != null){
            payment.setStatus(PaymentStatus.Enum.PAYMENTFAILED_3);
        }
        paymentRepository.save(payment);
        purchaseOrder.setStatus(purchaseOrder.getOperator() == null ? OrderStatus.Enum.PENDING_PAYMENT_1 : OrderStatus.Enum.HANDLING_2);
        purchaseOrder.setUpdateTime(Calendar.getInstance().getTime());
        purchaseOrderRepository.save(purchaseOrder);

        MoApplicationLog log = MoApplicationLog.applicationLogByPurchaseOrder(purchaseOrder);
        log.setLogMessage("wechat payment failed for purchase order: " + purchaseOrder.getOrderNo());
        doubleDBService.saveApplicationLog(log);

    }

    public Map<String, Object> getOrderQueryResponse() {
        return orderQueryResponse;
    }

    public PaymentCallbackHandler setOrderQueryResponse(Map<String, Object> orderQueryResponse) {
        this.orderQueryResponse = orderQueryResponse;
        return this;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public PaymentCallbackHandler setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
        return this;
    }

    public Payment getPayment(){
        return this.payment;
    }

    public PaymentCallbackHandler setPayment(Payment payment){
        this.payment = payment;
        return this;
    }


}
