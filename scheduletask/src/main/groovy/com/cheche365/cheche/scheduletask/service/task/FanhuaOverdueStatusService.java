package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.baoxian.model.RefundInfo;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chenxiangyin on 2017/4/11.
 * 此退款针对泛华发起的退款且同步状态至车车的payment
 */
@Service
public class FanhuaOverdueStatusService {
    Logger logger = LoggerFactory.getLogger(FanhuaOverdueStatusService.class);
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;
    @Autowired
    private OrderOperationInfoService orderOperationInfoService;
    @Autowired
    private OrderProcessHistoryService orderProcessHistoryService;
    @Autowired
    private QuoteRecordService quoteRecordService;
    @Autowired
    private IRefundService baoXianRefundService;
    @Autowired
    private QuoteConfigService quoteConfigService;

    private static final String COMMENT = "72小时自动退款成功";
    private static final String QUOTE_VALID_TIME_COMMENT = "定时任务自动退款";
    /**
     * 导入申请退款数据
     */
    @Transactional
    public void chgFullRefundOverdue() {
        Date currentTime = new Date();
        Date overdueTime = DateUtils.calculateDateByDay(currentTime,-3);
        List<PurchaseOrder> fullRefundList = purchaseOrderRepository.findOverdueFanhuaFullRefundByDate(overdueTime);
        logger.debug(" schedule task starting --> full refund size is ", fullRefundList.size());
        List<Payment> paymentSaveList = new ArrayList<>();

        for(PurchaseOrder purchaseOrder : fullRefundList){
            List<Payment> paymentList = paymentRepository.findByPurchaseOrderAndPaymentTypeAndStatus(purchaseOrder.getId(),PaymentType.Enum.FULLREFUND_4,PaymentStatus.Enum.NOTPAYMENT_1);
            OrderOperationInfo orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
            purchaseOrder.setStatus(OrderStatus.Enum.REFUNDED_9);
            purchaseOrder.setUpdateTime(currentTime);
            for(Payment payment : paymentList){
                payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
                payment.setUpdateTime(currentTime);
                payment.setComments(this.COMMENT);
                paymentSaveList.add(payment);
            }
            orderOperationInfoService.updateOrderTransmissionStatus(orderOperationInfo,OrderTransmissionStatus.Enum.REFUNDED);
            orderProcessHistoryService.saveChangeStatusHistory(
                InternalUser.ENUM.SYSTEM,purchaseOrder,OrderTransmissionStatus.Enum.APPLY_FOR_REFUND,OrderTransmissionStatus.Enum.REFUNDED);
        }
        purchaseOrderRepository.save(fullRefundList);
        paymentRepository.save(paymentSaveList);
    }

    @Transactional
    public void chgPartialRefundOverdue() {
        Date currentTime = new Date();
        Date overdueTime = DateUtils.calculateDateByDay(currentTime,-3);
        List<PurchaseOrder> partialRefundList = purchaseOrderRepository.findOverdueFanhuaPartialRefundByDate(overdueTime);
        logger.debug(" schedule task starting --> partial refund size is ", partialRefundList.size());
        List<Payment> paymentSaveList = new ArrayList<>();
        for(PurchaseOrder purchaseOrder : partialRefundList){
            QuoteRecord quoteRecord=quoteRecordService.getById(purchaseOrder.getObjId());
            List<Payment> paymentList = paymentRepository.findByPurchaseOrderAndPaymentTypeAndStatus(purchaseOrder.getId(),PaymentType.Enum.PARTIALREFUND_3,PaymentStatus.Enum.NOTPAYMENT_1);
            for(Payment payment : paymentList) {
                payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
                payment.setUpdateTime(currentTime);
                payment.setComments(this.COMMENT);
                paymentSaveList.add(payment);
            }
        }
        paymentRepository.save(paymentSaveList);
    }

    /**
     * 此退款为泛华出单中状态由车车主动发起退款的任务。
     * */
    @Transactional
    public void chgQuoteValidTimeOverdue(){
        List<PurchaseOrder> quoteValidTimeOverdueList = purchaseOrderRepository.findQuoteValidTimeOverdue();
        logger.debug(" schedule task starting --> quoteValidTimeOverdueList size is ", quoteValidTimeOverdueList.size());
        List<Payment> paymentSaveList = new ArrayList<>();
        for(PurchaseOrder purchaseOrder : quoteValidTimeOverdueList){

            //调用退款服务
            QuoteRecord quoteRecord = quoteRecordService.getById(purchaseOrder.getObjId());
            RefundInfo refundInfo=new RefundInfo();
            refundInfo.setTaskId(purchaseOrder.getOrderSourceId());
            refundInfo.setInsuranceCompany(quoteRecord.getInsuranceCompany());
            refundInfo.setArea(quoteRecord.getArea());
            refundInfo.setAdditionalParameters(new HashMap(){});
            baoXianRefundService.refund(refundInfo);

            List<Payment> paymentList = paymentRepository.findByPurchaseOrderAndPaymentTypeAndStatus(purchaseOrder.getId(),PaymentType.Enum.FULLREFUND_4,PaymentStatus.Enum.NOTPAYMENT_1);
            for(Payment payment : paymentList){
                payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
                payment.setUpdateTime(new Date());
                payment.setComments(this.QUOTE_VALID_TIME_COMMENT);
                paymentSaveList.add(payment);
            }
            OrderOperationInfo orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
            //修改出单中心
            orderOperationInfoService.updateOrderTransmissionStatus(orderOperationInfo,OrderTransmissionStatus.Enum.REFUNDED);
            //出单中心history
            orderProcessHistoryService.saveChangeStatusHistory(
                InternalUser.ENUM.SYSTEM,purchaseOrder,OrderTransmissionStatus.Enum.APPLY_FOR_REFUND,OrderTransmissionStatus.Enum.REFUNDED);

            //修改订单
            purchaseOrder.setStatus(OrderStatus.Enum.REFUNDED_9);
            purchaseOrder.setUpdateTime(new Date());
            purchaseOrder.setStatusDisplay(null);
            purchaseOrder.setOperator(InternalUser.ENUM.SYSTEM);
        }
        purchaseOrderRepository.save(quoteValidTimeOverdueList);
        //payment状态
        paymentRepository.save(paymentSaveList);
    }

}
