package com.cheche365.cheche.ordercenter.listener;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository;
import com.cheche365.cheche.core.service.OrderProcessHistoryService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.ordercenter.service.order.OrderTransmissionStatusHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

/**
 * Created by yinJianBin on 2016/11/29.
 */
@Component
public class RedisSyncOrderPayCustomListener {
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    ApplicationContextHolder applicationContextHolder;
    @Autowired
    public OrderOperationInfoRepository orderOperationInfoRepository;
    @Autowired
    public OrderProcessHistoryService orderProcessHistoryService;
    @Autowired
    private OrderTransmissionStatusHandler orderTransmissionStatusHandler;
    @Autowired
    private RefundOrderStatusHandler refundOrderStatusHandler;
    @Autowired
    public InternalUserRepository internalUserRepository;
    @Autowired
    private PurchaseOrderAmendRepository purchaseOrderAmendRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void start() {
        new Thread(new SyncOrderPayStatusThread()).start();
    }

    public String getRedisPaymentString() {
        return stringRedisTemplate.opsForList().rightPop("payment_call_back", 2, TimeUnit.MINUTES);
    }

    class Guardian implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            //重启线程
            logger.debug("5秒后重启线程[SyncOrderPayStatusThread]");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.error("thread sleep over by expection", e);
            }

            new SyncOrderPayStatusThread().run();
        }
    }

    class SyncOrderPayStatusThread extends Observable implements Runnable {

        public void callRestart() {
            super.setChanged();
            super.notifyObservers();
        }

        @Override
        public void run() {
            this.addObserver(new Guardian());
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String paymentJson = getRedisPaymentString();
                if (StringUtils.isBlank(paymentJson)) {
                    continue;
                }
                logger.debug("RedisSyncOrderPayListener run ,output is " + paymentJson);
                Payment payment = null;
                try {
                    payment = CacheUtil.doJacksonDeserialize(paymentJson, Payment.class);
                    if (payment == null) {
                        logger.error("synchronize order error when RedisSyncOrderPayListener, can not deserialize paymentJson ,paymentJson is {}", paymentJson);
                        continue;
                    }
                    if (payment.getPaymentType().equals(PaymentType.Enum.DAILY_RESTART_PAY_7)) {
                        logger.info("pass daily restart pay sync ,paymentJson is {}", paymentJson);
                        continue;
                    }

                    OrderOperationInfo orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(payment.getPurchaseOrder());
                    if (null == orderOperationInfo) {
                        logger.error("没有查询到对应的orderOperationInfo,paymentId-->[{}],orderNo-->[{}]", payment.getId(), payment.getPurchaseOrder().getOrderNo());
                        continue;
                    }

                    this.processResultStatus(payment, orderOperationInfo);
                } catch (Exception e) {
                    if (payment != null) {
                        logger.error("更新payment结果异常,paymentId-->[{}],orderNo-->[{}]", payment.getId(), payment.getPurchaseOrder().getOrderNo(), e);
                    }
                    this.callRestart();
                }
            }


        }

        @Transactional
        public void processResultStatus(Payment payment, OrderOperationInfo orderOperationInfo) {
            if (payment.getPaymentType().getId().equals(PaymentType.Enum.FULLREFUND_4.getId())) {
                logger.debug("监听请求为全额退款结果请求,退款渠道-->[{}],paymentId-->[{}],purchaseOrderNo-->[{}],退款结果-->[{}]", payment.getChannel().getFullDescription(), payment.getId(), payment.getPurchaseOrder().getOrderNo(), payment.getStatus().getStatus());
                //退款类型
                refundOrderStatusHandler.handle(payment, orderOperationInfo);
            } else if (PaymentType.Enum.PAY_TYPES_ID.contains(payment.getPaymentType().getId())) {
                logger.debug("监听请求为支付结果请求,支付渠道-->[{}],paymentId-->[{}],purchaseOrderNo-->[{}],支付结果-->[{}]", payment.getChannel().getFullDescription(), payment.getId(), payment.getPurchaseOrder().getOrderNo(), payment.getStatus().getStatus());
                //首次付款或者增补
                if (payment.getStatus().getId().equals(PaymentStatus.Enum.PAYMENTSUCCESS_2.getId())) {
                    //成功处理
                    this.processPayOrderSuccessStatus(orderOperationInfo, payment);
                } else {
                    //付款失败,状态继续为 等待付款, do nothing.
                    PurchaseOrderAmend purchaseOrderAmend = payment.getPurchaseOrderAmend();
                    if (purchaseOrderAmend != null) { //说明是增补,而非首次退款(首次退款无amend)
                        //增补状态更新为已完成
                        purchaseOrderAmend.setPurchaseOrderAmendStatus(PurchaseOrderAmendStatus.Enum.CANCEL);
                        purchaseOrderAmend.setUpdateTime(new Date());
                        purchaseOrderAmendRepository.save(purchaseOrderAmend);
                    }
                }
            } else {
                //部分退款
                //车车优惠,车车支付
            }
        }


        private void processPayOrderSuccessStatus(OrderOperationInfo orderOperationInfo, Payment payment) {
            //更新出单中心状态
            orderTransmissionStatusHandler.request(orderOperationInfo, OrderTransmissionStatus.Enum.UNCONFIRMED, payment.getOutTradeNo(), null);
            //更新amend状态
            PurchaseOrderAmend purchaseOrderAmend = payment.getPurchaseOrderAmend();
            if (purchaseOrderAmend != null) { //说明是增补,而非首次退款(首次退款无amend)
                //增补状态更新为已完成
                purchaseOrderAmend.setPurchaseOrderAmendStatus(PurchaseOrderAmendStatus.Enum.FINISHED);
                purchaseOrderAmend.setUpdateTime(new Date());
                purchaseOrderAmendRepository.save(purchaseOrderAmend);
            }

        }
    }

}

@Component
class RefundOrderStatusHandler {
    private Logger logger = LoggerFactory.getLogger(RefundOrderStatusHandler.class);

    @Autowired
    private PurchaseOrderAmendRepository purchaseOrderAmendRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private OrderTransmissionStatusHandler orderTransmissionStatusHandler;


    public void handle(Payment payment, OrderOperationInfo orderOperationInfo) {
        PurchaseOrderAmend purchaseOrderAmend = payment.getPurchaseOrderAmend();
        if (payment.getStatus().getId().equals(PaymentStatus.Enum.PAYMENTSUCCESS_2.getId())) {
            //退款成功
            this.processToRefunded(payment, orderOperationInfo);
        } else if (payment.getStatus().getId().equals(PaymentStatus.Enum.PAYMENTFAILED_3.getId())) {
            //退款失败
            this.processToRefundfailed(payment, orderOperationInfo);
        }

        //退款状态更新为已完成
        purchaseOrderAmend.setPurchaseOrderAmendStatus(PurchaseOrderAmendStatus.Enum.FINISHED);
        purchaseOrderAmend.setUpdateTime(new Date());
        purchaseOrderAmendRepository.save(purchaseOrderAmend);

        stringRedisTemplate.opsForSet().remove("syn.refunding.order.id", purchaseOrderAmend.getOrderOperationInfo().getPurchaseOrder().getOrderNo());
    }

    /**
     * 退款成功处理
     *
     * @param payment
     * @param orderOperationInfo
     */
    private void processToRefunded(Payment payment, OrderOperationInfo orderOperationInfo) {
        logger.debug("退款成功处理,purchaseOrderNo-->[{}],paymentId-->[{}]", payment.getPurchaseOrder().getOrderNo(), payment.getId());
        orderTransmissionStatusHandler.request(orderOperationInfo, OrderTransmissionStatus.Enum.REFUNDED);
    }


    /**
     * 退款失败处理
     *
     * @param payment
     * @param orderOperationInfo
     */
    private void processToRefundfailed(Payment payment, OrderOperationInfo orderOperationInfo) {
        logger.debug("退款失败处理,purchaseOrderNo-->[{}],paymentId-->[{}]", payment.getPurchaseOrder().getOrderNo(), payment.getId());
        if (payment.getPaymentType().getId().equals(PaymentType.Enum.FULLREFUND_4.getId())) {
            orderTransmissionStatusHandler.request(orderOperationInfo, OrderTransmissionStatus.Enum.REFUND_FAILED);
        }
    }

}


