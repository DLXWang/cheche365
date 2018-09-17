package com.cheche365.cheche.web.service.payment

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.service.OrderOperationInfoService
import com.cheche365.cheche.core.service.PurchaseOrderAmendService
import com.cheche365.cheche.core.service.QuoteConfigService
import com.cheche365.cheche.core.util.BigDecimalUtil
import groovy.util.logging.Slf4j
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import static com.cheche365.cheche.core.model.PaymentType.Enum.*
import static com.cheche365.cheche.core.model.OrderStatus.Enum.*
import static com.cheche365.cheche.core.model.PurchaseOrderAmendStatus.Enum.CREATE

/**
 * Created by zhengwei on 4/12/17.
 */

@Service
@Slf4j
class PaymentService {

    PurchaseOrderRepository poRepo

    PaymentRepository paymentRepo

    OrderOperationInfoService ooiService

    InsuranceRepository insuranceRepo

    CompulsoryInsuranceRepository ciRepo

    QuoteRecordRepository qrRepo

    PurchaseOrderAmendService orderAmendService

    MultiPaymentService multiPaymentService

    RedisPublisher redisPublisher

    QuoteConfigService quoteConfigService

    List<CrudRepository> repos

    PaymentService(PurchaseOrderRepository poRepo, PaymentRepository paymentRepo, OrderOperationInfoService ooiService, InsuranceRepository insuranceRepo,
                   CompulsoryInsuranceRepository ciRepo, QuoteRecordRepository qrRepo, PurchaseOrderAmendService orderAmendService,
                   MultiPaymentService multiPaymentService, QuoteConfigService quoteConfigService, RedisPublisher redisPublisher) {
        this.poRepo = poRepo
        this.paymentRepo = paymentRepo
        this.ooiService = ooiService
        this.insuranceRepo = insuranceRepo
        this.ciRepo = ciRepo
        this.qrRepo = qrRepo
        this.redisPublisher = redisPublisher
        this.orderAmendService = orderAmendService
        this.multiPaymentService = multiPaymentService
        this.quoteConfigService = quoteConfigService
    }

    @Transactional
    def onPaySuccess(OrderRelated or, Closure beforeSave){

        Payment payment = or.payments.find {it.paymentType == INITIALPAYMENT_1}
        beforeSave?.call(payment)

        or.po.status = OrderStatus.Enum.PAID_3
        or.toBePersist << or.po

        payment.status = PaymentStatus.Enum.PAYMENTSUCCESS_2
        payment.comments = payment.channel.name

        def result = or.persist()
        def afterSave = paymentRepo.save(payment) //为了保证payment save redis发消息线程再执行，不能用or.persist方法
        redisPublisher.publish(afterSave)
        result.payments = [afterSave]

        return result
    }

    def onPayFail(OrderRelated or, Closure beforeSave){

        Payment payment = or.payments.find {it.paymentType == INITIALPAYMENT_1}
        beforeSave?.call(payment)

        payment.status =  PaymentStatus.Enum.PAYMENTFAILED_3
        payment.comments = payment.channel.name

        def result = or.persist()
        def afterSave = paymentRepo.save(payment)
        result.payments = [afterSave]

        return result
    }

    @Transactional
    def onRefund(OrderRelated or, PaymentType paymentType, Double refundAmount) {
        log.info("泛华订单退款处理，修改payment和purchaseOrder,订单号为:{}, paymentType为：{}, statusDisplay:{}", or.po.orderNo, paymentType?.name, or.po.statusDisplay)
        updateRefundOrder(or, paymentType, refundAmount)
        createRefundPayment(or, paymentType, refundAmount)
        or.persist()
    }

    @Transactional
    def onAdditionalPayment(OrderRelated or, Payment additionalPayment){
        or.po.payableAmount = BigDecimalUtil.add(or.po.payableAmount, additionalPayment.amount).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()
        additionalPayment.purchaseOrder = or.po
        additionalPayment.paymentType = BAOXIANPAY_8

        or.toBePersist << or.po
        or.toBePersist << additionalPayment

        or.persist()
    }

    @Transactional
    void onOrder(OrderRelated or, Closure beforeSave){
        ooiService.updatePurchaseOrderStatusForServiceSuccess(or.po)

        beforeSave?.call()
        or.persist()
    }

    def initOR = { Closure poFinder ->
        PurchaseOrder po = poFinder(poRepo)
        if(!po){
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, '订单不存在')
        }

        new OrderRelated().with {
            it.po = po
            it.qr = qrRepo.findOne(po.objId)
            it.insurance = insuranceRepo.findFirstByQuoteRecordOrderByIdDesc(qr)
            it.ci = ciRepo.findFirstByQuoteRecordOrderByIdDesc(qr)
            it.payments = paymentRepo.findByPurchaseOrder(po)
            it
        }
    }

    def initPayment = { Payment template, Payment newOne ->
        newOne.user = template.user
        newOne.channel = template.channel
        newOne.clientType = template.clientType
        newOne.comments = template.channel.channel
    }

    def createRefundPayment = { OrderRelated or, PaymentType paymentType, Double refundAmount ->
        Payment refundPayment = new Payment(paymentType: paymentType)
        refundPayment.amount = refundAmount
        refundPayment.purchaseOrder = or.po
        refundPayment.status = PaymentStatus.Enum.NOTPAYMENT_1
        or.payments.find { INITIALPAYMENT_1 == it.paymentType }.with {
            initPayment(it, refundPayment)
            refundPayment.upstreamId = it
        }
        or.toBePersist << refundPayment
        refundPayment
    }

    def updateRefundOrder = { OrderRelated or, PaymentType paymentType, Double refundAmount ->
        if (PARTIALREFUND_3 == paymentType) {
            or.po.paidAmount = BigDecimalUtil.subtract(or.po.paidAmount, refundAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
            or.po.payableAmount = BigDecimalUtil.subtract(or.po.payableAmount, refundAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
        } else if (FULLREFUND_4 == paymentType) {
            or.po.status = REFUNDING_10
        } else {
            throw new BusinessException(BusinessException.Code.UNIMPLEMENTED_METHOD, "非预期退款类型 ${paymentType}")
        }
        or.toBePersist << or.po
        or.po
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
        List toBePersist = []

        @Transactional
        def persist(){
            toBePersist?.findAll {it}?.collectEntries {
                [(it.getClass().simpleName) : allRepos().find {it.respondsTo('save', it)}.save(it)]
            }
        }

    }
}
