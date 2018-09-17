package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.*
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.model.PaymentType.Enum.*
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.*


/**
 * Created by zhengwei on 7/31/17.
 */
@Service
class OrderRelatedService {

    PurchaseOrderRepository poRepo

    PaymentRepository paymentRepo

    OrderOperationInfoService ooiService

    InsuranceRepository insuranceRepo

    CompulsoryInsuranceRepository ciRepo

    QuoteRecordRepository qrRepo

    QuoteConfigService quoteConfigService
    PurchaseOrder po
    QuoteRecord qr
    Insurance insurance
    CompulsoryInsurance ci
    List<Payment> payments
    Boolean innerPay

    List<CrudRepository> repos

    OrderRelatedService(PurchaseOrderRepository poRepo, PaymentRepository paymentRepo, InsuranceRepository insuranceRepo,
                        CompulsoryInsuranceRepository ciRepo, QuoteRecordRepository qrRepo,QuoteConfigService quoteConfigService) {
        this.poRepo = poRepo
        this.paymentRepo = paymentRepo
        this.ooiService = ooiService
        this.insuranceRepo = insuranceRepo
        this.ciRepo = ciRepo
        this.qrRepo = qrRepo
        this.quoteConfigService=quoteConfigService
    }

    def initOR = { Closure poFinder ->
        PurchaseOrder po = poFinder(poRepo)
        if(!po){
            return null
        }

        new OrderRelated().with {
            it.po = po
            it.qr = qrRepo.findOne(po.objId)
            it.insurance = insuranceRepo.findFirstByQuoteRecordOrderByIdDesc(qr)
            it.ci = ciRepo.findFirstByQuoteRecordOrderByIdDesc(qr)
            it.payments = paymentRepo.findAllByPurchaseOrder(po)
            it.innerPay=quoteConfigService.isInnerPay(it.qr,it.po)
            it
        }
    }

    OrderRelated initByOrderNo(String orderNo){
        initOR {PurchaseOrderRepository poRepo ->
            poRepo.findFirstByOrderNo(orderNo)
        }
    }

    synchronized allRepos(){
        if(!repos){
            this.repos = this.properties.findAll {it.value instanceof CrudRepository}.collect {it.value}
        }

        this.repos
    }

    class OrderRelated{
        PurchaseOrder po
        QuoteRecord qr
        Insurance insurance
        CompulsoryInsurance ci
        List<Payment> payments
        Boolean innerPay
        List toBePersist = []

        Payment findPending(){
            def pendingPayments = payments.findAll {
                NOTPAYMENT_1 == it.status && PaymentType.Enum.PAY_TYPES.contains(it.paymentType)
            }
            if(pendingPayments.size() > 1){
                throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "存在多余一笔待支付记录");
            }
            pendingPayments.size() == 1 ? pendingPayments.first() : null
        }

        Payment findLastPaid(){
            payments.sort{-it.id}.find {PAYMENTSUCCESS_2 == it.status && [INITIALPAYMENT_1, ADDITIONALPAYMENT_2].contains(it.paymentType)}
        }

        Payment findInitialPaid(){
            payments.find {PAYMENTSUCCESS_2 == it.status && INITIALPAYMENT_1 == it.paymentType}
        }

        Payment findDailyRestart(){
            payments.find {NOTPAYMENT_1 == it.status && PaymentType.Enum.DAILY_RESTART_PAY_7 == it.paymentType}
        }

        Payment findAdditional(){
            def additionalPayments = payments.findAll {
                NOTPAYMENT_1 == it.status && PaymentType.Enum.ADDITIONALPAYMENT_2 == it.paymentType
            }
            if(additionalPayments.size() > 1){
                throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "存在多余一笔待支付记录");
            }
            additionalPayments.size() == 1 ? additionalPayments.first() : null
        }

        //增补支付时，如果首次支付用的非ping++支付渠道
        boolean isPingPlusAdditional(){
            Payment payment = findAdditional()
            if(payment){
                Payment initialPayment = findInitialPaid()
                if(initialPayment){
                    return !PaymentChannel.Enum.isPingPlusPay(initialPayment.channel)
                }else{
                    return false
                }
            }else{
                return false
            }
        }

        @Transactional
        def persist(){
            toBePersist?.findAll {it}?.collectEntries {
                [(it.getClass().simpleName) : allRepos().find {it.respondsTo('save', it)}.save(it)]
            }
            toBePersist.clear()
        }
    }

}
