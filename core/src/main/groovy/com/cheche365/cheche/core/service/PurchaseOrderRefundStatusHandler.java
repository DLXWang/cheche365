package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2016/10/20 0020.
 */

@Service
public class PurchaseOrderRefundStatusHandler {

    private final Logger logger = LoggerFactory.getLogger(PurchaseOrderRefundStatusHandler.class);

    @Autowired
    PaymentAmountCalculator amountCalculator;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Transactional
    public Payment changeOrderStatus(List<Payment> childPayments,PurchaseOrderAmend purchaseOrderAmend,PurchaseOrder purchaseOrder){
        //部分退款不通知
        if(purchaseOrderAmend.getPaymentType().equals(PaymentType.Enum.PARTIALREFUND_3)){
            return null;
        }
        //全额退款判断是否所有退款已经全部完成，如果全部完成则修改订单状态
        logger.info("调用changeOrderStatus方法orderNo:"+purchaseOrder.getOrderNo());
        Payment payment = null;
        List<Payment> paymentNot = childPayments.stream().filter(p -> p.getStatus().equals(PaymentStatus.Enum.NOTPAYMENT_1)).collect(Collectors.toList());
        logger.info("调用changeOrderStatus方法paymentNot:"+paymentNot.size()+"orderNo:"+purchaseOrder.getOrderNo());
        //不存在未完成的payment全部回调已经完毕
        if(paymentNot.size()==0){
            logger.info("调用changeOrderStatus方法size为0 orderNo:"+purchaseOrder.getOrderNo());
            //全额退款或者部分退款把所有金额退款完毕
            if (0 == amountCalculator.customerNetPaid(childPayments).compareTo(BigDecimal.ZERO)) {
                logger.info("调用changeOrderStatus开始修改订单状态 orderNo:"+purchaseOrder.getOrderNo());
                purchaseOrder.setStatus(OrderStatus.Enum.REFUNDED_9);
                purchaseOrderRepository.save(purchaseOrder);
                payment = childPayments.stream().filter(p -> purchaseOrderAmend.equals(p.getPurchaseOrderAmend())).collect(Collectors.toList()).get(0);
            }else{//存在退款失败或取消
                logger.info("调用changeOrderStatus状态未确认 orderNo:" + purchaseOrder.getOrderNo());
                List<Payment> paymentFail = childPayments.stream().filter(p -> purchaseOrderAmend.equals(p.getPurchaseOrderAmend())&&p.getStatus().equals(PaymentStatus.Enum.PAYMENTFAILED_3)).collect(Collectors.toList());
                List<Payment> paymentCancel = childPayments.stream().filter(p -> purchaseOrderAmend.equals(p.getPurchaseOrderAmend())&&p.getStatus().equals(PaymentStatus.Enum.CANCEL_4)).collect(Collectors.toList());
                //
                if(paymentCancel.size()>0){
                    logger.info("调用changeOrderStatus存在取消的payment orderNo:"+purchaseOrder.getOrderNo());
                    payment = paymentCancel.get(0);
                }
                if(paymentFail.size()>0){
                    logger.info("调用changeOrderStatus存在失败的payment orderNo:"+purchaseOrder.getOrderNo());
                    payment = paymentFail.get(0);
                }
            }
        }else{
            logger.info("订单: {} 有未完成退款不能修改订单状态，已付款 {}，已退款 {}", purchaseOrder.getOrderNo(), amountCalculator.customerPaid(childPayments), amountCalculator.customerRefunded(childPayments), amountCalculator.customerRefunded(childPayments));
            purchaseOrder.setDescription("退款未全部完成");
        }
        return payment;
    }
}
