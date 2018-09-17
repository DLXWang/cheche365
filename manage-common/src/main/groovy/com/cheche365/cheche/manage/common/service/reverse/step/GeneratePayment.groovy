package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.PurchaseOrderGiftService
import groovy.util.logging.Slf4j
import org.apache.commons.collections.CollectionUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by yellow on 2017/11/6.
 */
@Service
@Slf4j
class GeneratePayment implements  TPlaceInsuranceStep{

    @Transactional
    @Override
    Object run(Object context) {
        log.debug("------生成支付------")
        PurchaseOrder purchaseOrder=context.purchaseOrder
        PurchaseOrderGiftService purchaseOrderGiftService=context.purchaseOrderGiftService
        PaymentRepository paymentRepository=context.paymentRepository
        List<Payment> paymentList=paymentRepository.findByPurchaseOrder(purchaseOrder)
        List<Gift> gifts = purchaseOrderGiftService.findGiftByPurchaseOrder(purchaseOrder)
        //重新生成所有payment
        for(Payment payment :paymentList){
            payment.setStatus(PaymentStatus.Enum.CANCEL_4)
            payment.setUpdateTime(new Date())
        }
        paymentRepository.save(paymentList)
        Double giftAmount=0
        if(CollectionUtils.isNotEmpty(gifts)){
            for(Gift gift:gifts){
                if(gift.getGiftType() ==GiftType.Enum.TEL_MARKETING_DISCOUNT_27){
                    giftAmount+=gift.getGiftAmount()
                }
            }
        }
        purchaseOrder.setPaidAmount(purchaseOrder.getPayableAmount() - giftAmount)
        createPayment(purchaseOrder.getPaidAmount(),PaymentType.Enum.INITIALPAYMENT_1,purchaseOrder,paymentRepository)
        createPayment(giftAmount,PaymentType.Enum.DISCOUNT_5,purchaseOrder,paymentRepository)
        getContinueFSRV true
    }

    private void createPayment(Double amount,PaymentType paymentType,PurchaseOrder purchaseOrder,PaymentRepository paymentRepository){
        if(amount == 0){
            return
        }
        Payment payment = new Payment()
        payment.setPurchaseOrder(purchaseOrder)
        payment.setChannel(PaymentChannel.Enum.OFFLINE_PAY_19)
        payment.setClientType(Channel.Enum.WAP_8)
        payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2)
        payment.setComments("该订单是由出单中心录入保单功能反向生成的订单，状态置为已支付")
        payment.setPaymentType(paymentType)
        payment.setCreateTime(purchaseOrder.getCreateTime())
        payment.setUser(purchaseOrder.getApplicant())
        payment.setAmount(amount)
        payment.setOperator(purchaseOrder.getOperator())
        paymentRepository.save(payment)
    }
}
