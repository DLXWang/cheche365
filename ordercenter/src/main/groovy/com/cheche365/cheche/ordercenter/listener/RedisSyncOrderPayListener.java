package com.cheche365.cheche.ordercenter.listener;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository;
import com.cheche365.cheche.core.service.IInternalUserService;
import com.cheche365.cheche.core.service.OrderProcessHistoryService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.ordercenter.service.order.OrderTransmissionStatusHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


/**
 * Created by chenxy on 2016/9/12.
 * 监听需要同步的订单
 */
@Component
public class RedisSyncOrderPayListener implements MessageListener {

    Logger logger = LoggerFactory.getLogger(RedisSyncOrderPayListener.class);

    @Autowired
    public OrderOperationInfoRepository orderOperationInfoRepository;
    @Autowired
    public OrderProcessHistoryService orderProcessHistoryService;
    @Autowired
    private OrderTransmissionStatusHandler orderTransmissionStatusHandler;
    @Autowired
    private IInternalUserService internalUserService;
    @Autowired
    private RefundOrderStatusHandler refundOrderStatusHandler;
    @Autowired
    public InternalUserRepository internalUserRepository;
    @Autowired
    private PurchaseOrderAmendRepository purchaseOrderAmendRepository;

    public void onMessage(Message message, byte[] pattern) {

        String paymentJson = String.valueOf(message.toString());
        logger.debug("RedisSyncOrderPayListener run ,output is " + paymentJson);

        if (StringUtils.isBlank(paymentJson)) {
            return;
        }

        Payment payment = CacheUtil.doJacksonDeserialize(paymentJson, Payment.class);
        if (payment == null) {
            logger.error("synchronize order error when RedisSyncOrderPayListener, can not deserialize paymentJson ,paymentJson is {}", paymentJson);
            return;
        }

        OrderOperationInfo orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(payment.getPurchaseOrder());
        if (null == orderOperationInfo) {
            logger.error("没有查询到对应的orderOperationInfo,paymentId-->[{}],orderNo-->[{}]", payment.getId(), payment.getPurchaseOrder().getOrderNo());
            return;
        }

        this.processResultStatus(payment, orderOperationInfo);
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
        orderTransmissionStatusHandler.request(orderOperationInfo, OrderTransmissionStatus.Enum.UNCONFIRMED);
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
