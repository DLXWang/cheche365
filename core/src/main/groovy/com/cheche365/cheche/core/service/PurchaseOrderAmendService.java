package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by xu.yelong on 2016/9/12.
 */
@Service
public class PurchaseOrderAmendService {
    private Logger logger= LoggerFactory.getLogger(PurchaseOrderAmendService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PurchaseOrderAmendRepository purchaseOrderAmendRepository;

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;


    public PurchaseOrderAmend findByQuoteRecord(QuoteRecord quoteRecord){
        return purchaseOrderAmendRepository.findByNewQuoteRecord(quoteRecord);
    }

    public PurchaseOrderAmend save(PurchaseOrderAmend purchaseOrderAmend){
        return purchaseOrderAmendRepository.save(purchaseOrderAmend);
    }

    /**
     * 添加
     */
    public PurchaseOrderAmend addFullRefundAmend(OrderOperationInfo orderOperationInfo){
        PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
        PurchaseOrderAmend amend = new PurchaseOrderAmend();
        amend.setPurchaseOrder(purchaseOrder);
        amend.setOrderOperationInfo(orderOperationInfo);
        amend.setPaymentType(PaymentType.Enum.FULLREFUND_4);
        amend.setPurchaseOrderAmendStatus(PurchaseOrderAmendStatus.Enum.CREATE);
        amend.setCreateTime(new Date());
        amend.setUpdateTime(new Date());
        logger.debug("order refund, orderId ->{}, amendId ->{} " + purchaseOrder.getId(),amend.getId());
        return purchaseOrderAmendRepository.save(amend);

    }

    /**
     * 取消退款/增补申请
     */
    @Transactional
    public void cancelPrevAmend(PurchaseOrder purchaseOrder){
        List<PurchaseOrderAmend> amendList = purchaseOrderAmendRepository.findByPurchaseOrderAndPurchaseOrderAmendStatus(purchaseOrder, PurchaseOrderAmendStatus.Enum.CREATE);
        amendList.forEach(purchaseOrderAmend -> {
            purchaseOrderAmend.setPurchaseOrderAmendStatus(PurchaseOrderAmendStatus.Enum.CANCEL);
            purchaseOrderAmend.setUpdateTime(new Date());
            //取消之后需要重新计算purchaseOrder 的payableAmount 和paidAmount
            logger.debug("cancel order  amend, orderId ->{}, amendId ->{} " + purchaseOrder.getId(),purchaseOrderAmend.getId());
        });
        purchaseOrderAmendRepository.save(amendList);
    }

    public Double getPaidAmountByOrderId(Long orderId){
        Double sum = 0.0;
        //状态为“支付成功”
        List<Long> status = Arrays.asList(PaymentStatus.Enum.PAYMENTSUCCESS_2.getId());
        //type为“首次支付”、“增补”、“部分退款”、“全额退款”
        List<Long> types = Arrays.asList(PaymentType.Enum.INITIALPAYMENT_1.getId(),PaymentType.Enum.ADDITIONALPAYMENT_2.getId(),
                                        PaymentType.Enum.PARTIALREFUND_3.getId(),PaymentType.Enum.FULLREFUND_4.getId());
        List<Payment> payments = paymentRepository.findSuccessPaymentsByOrderId(orderId, status, types);
        if(CollectionUtils.isNotEmpty(payments)){
            for(Payment payment : payments){
                Long type = payment.getPaymentType().getId();
                if(type.equals(PaymentType.Enum.INITIALPAYMENT_1.getId()) || type.equals(PaymentType.Enum.ADDITIONALPAYMENT_2.getId()))
                    sum += payment.getAmount();
                else if(type.equals(PaymentType.Enum.PARTIALREFUND_3.getId()) || type.equals(PaymentType.Enum.FULLREFUND_4.getId()))
                    sum -= payment.getAmount();
            }
        }
        return sum;
    }

    public PurchaseOrderAmend findByOrderOperationInfo(OrderOperationInfo orderOperationInfo){
        return purchaseOrderAmendRepository.findLastByOrderOperationInfo(orderOperationInfo);
    }

    public PurchaseOrderAmend addAmend(QuoteRecord originalQuoteRecord,QuoteRecord newQuoteRecord,PurchaseOrder purchaseOrder,PaymentType paymentType,PurchaseOrderAmendStatus status){
        OrderOperationInfo orderOperationInfo = orderOperationInfoService.getByPurchaseOrder(purchaseOrder);
        PurchaseOrderAmend purchaseOrderAmend = new PurchaseOrderAmend();
        purchaseOrderAmend.setOrderOperationInfo(orderOperationInfo);
        purchaseOrderAmend.setPurchaseOrder(purchaseOrder);
        purchaseOrderAmend.setOriginalQuoteRecord(originalQuoteRecord);
        purchaseOrderAmend.setNewQuoteRecord(newQuoteRecord);
        purchaseOrderAmend.setPaymentType(paymentType);
        purchaseOrderAmend.setPurchaseOrderAmendStatus(status);
        purchaseOrderAmend.setCreateTime(new Date());
        purchaseOrderAmend.setUpdateTime(new Date());
        return purchaseOrderAmendRepository.save(purchaseOrderAmend);
    }
}
