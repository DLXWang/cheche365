package com.cheche365.cheche.web.service.payment

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.message.PartnerOrderMessage
import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository
import com.cheche365.cheche.core.repository.PurchaseOrderHistoryRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.BillsGenerator
import com.cheche365.cheche.core.service.PaymentAmountCalculator
import com.cheche365.cheche.core.service.PaymentRefundService
import com.cheche365.cheche.core.util.BigDecimalUtil
import com.cheche365.cheche.web.service.order.ClientOrderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

import static com.cheche365.cheche.core.model.PaymentStatus.Enum.*
import static com.cheche365.cheche.core.model.PaymentType.Enum.*

/**
 * Created by zhengwei on 9/8/16.
 * 处理多次支付以及退款时，订单、支付、优惠、保单相关数据生成／更新。调用支付平台不在本服务职责范围内。 <br>
 * 支持以下情况的处理：<br>
 * 增补：已支付情况下，再次生成待支付记录 <br>
 * 部分退款：已支付情况下，生成部分退款记录  <br>
 * 全额退款：已支付情况，全部退款  <br>
 * 修改报价：未支付情况下修改报价并同步订单等数据 <br>
 * 取消全额退款：订单回退到最近一次已支付状态（正常支付或部分退款）  <br>
 */

@Service
class MultiPaymentService {

    private Logger logger = LoggerFactory.getLogger(MultiPaymentService.class);

    @Autowired
    PaymentAmountCalculator calculator

    @Autowired
    ClientOrderService purchaseOrderService

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository

    @Autowired
    PaymentRefundService refundService

    @Autowired
    BillsGenerator billsGenerator

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    PurchaseOrderAmendRepository orderAmendRepository

    @Autowired
    PurchaseOrderAmendRepository amendRepository

    @Autowired
    private PurchaseOrderHistoryRepository orderHistoryRepository

    @Autowired
    private RedisPublisher redisPublisher

    /**
     * 初始化增补／退款相关数据，包括更新purchase_order, gift, insurance, compulsory_insurance, 生成payment
     * @param amend
     */
    @Transactional
    List<Payment> initMultiPayment(PurchaseOrderAmend amend) {

        def payments = paymentRepository.findByPurchaseOrder(amend.purchaseOrder)
        Object[] insuranceArgs
        preCheck(amend, payments)

        PurchaseOrderHistory history = purchaseOrderService.saveOrderHistory(amend.purchaseOrder, OperationType.Enum.ORDER_AMEND)
        amend.setPurchaseOrderHistory(history)

        if(FULLREFUND_4 == amend.paymentType) {
            applyFullRefundPaymentFlow(amend, payments)
        } else {
            insuranceArgs=applyAdditionalPaymentFlow(amend, payments)
        }

        paymentRepository.save(payments)
        orderAmendRepository.save(amend)

        if (PARTIALREFUND_3 == amend.paymentType) {
            Map<String, Object> syncOrderMessage = assemblePartnerReturnData(payments, amend,insuranceArgs)
            //同步增补数据到第三方合作渠道
            redisPublisher.publish(new PartnerOrderMessage().setKey(amend.purchaseOrder.getOrderNo()).setMessage(syncOrderMessage))
        }else {
            redisPublisher.publish(new PartnerOrderMessage().setKey(amend.purchaseOrder.getOrderNo()).setMessage(amend.purchaseOrder))
        }

        payments
    }

    private Map<String, Object> assemblePartnerReturnData(List<Payment> payments, PurchaseOrderAmend amend,Object[] insuranceArgs) {
        List<Payment> paymentSync = []
        for (Payment payment : payments) {
            paymentSync.add(new Payment().with {
                it.id = payment.id
                it.amount = payment.amount
                it.status = payment.status
                it.paymentType = payment.paymentType
                it
            })
        }
        def syncOrderMessage = ["order"   : amend.purchaseOrder,
                                "payments": paymentSync,
                                "insurance":insuranceArgs[0],
                                "compulsoryInsurance":insuranceArgs[1]]
        syncOrderMessage
    }


    /**
     * [取消全额退款]处理
     */
    @Transactional
    def cancelFullRefundAmend(PurchaseOrder purchaseOrder) {
        def payments = paymentRepository.findByPurchaseOrder(purchaseOrder)
        preCheckCancelRefund(purchaseOrder, payments)

        PurchaseOrderHistory history = purchaseOrderService.saveOrderHistory(purchaseOrder, OperationType.Enum.FULL_REFUND_CANCEL)

        this.purchaseOrderService.resetPaymentStatus(payments, purchaseOrder, history)

        cancelFullRefundAmendFlow(purchaseOrder, payments)

        paymentRepository.save(payments)
        purchaseOrderRepository.save(purchaseOrder)

        //同步增补数据到第三方合作渠道
        redisPublisher.publish(new PartnerOrderMessage().setKey(purchaseOrder.getOrderNo()).setMessage(purchaseOrder))

    }

    /**
     * 类增补处理流程，包括增补，部分退款和修改报价。
     */
    def applyAdditionalPaymentFlow(PurchaseOrderAmend amend, List<Payment> payments){
        prepareNewOrder(amend)

        switchGift(amend.purchaseOrder, payments, amend.purchaseOrderHistory)

        applyAmountDiff(amend, payments)

        prePersistCheck(amend.purchaseOrder, payments, amend.paymentType)

        return updateBills(amend.originalQuoteRecord,amend.newQuoteRecord, amend.purchaseOrder)
    }

    /**
     * 全额退款处理流程。不同于类增补流程，全额退款时没有传修改后的报价，所以不能用同一种流程处理
     */
    def applyFullRefundPaymentFlow(PurchaseOrderAmend amend, List<Payment> payments) {
        handleFullRefundOrder(amend, payments)
        applyAmountDiff(amend, payments)
        prePersistCheck(amend.purchaseOrder, payments, amend.paymentType)
    }

    /**
     * [取消全额退款]处理流程，订单回退至最近一次已支付状态
     */
    def cancelFullRefundAmendFlow(PurchaseOrder purchaseOrder, List<Payment> payments) {
        def index = payments.findLastIndexOf {
            it.status == PAYMENTSUCCESS_2 && it.purchaseOrderHistory
        }
        if (index < 0) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, '非法操作，未找到最近一次支付成功的支付记录')
        }

        Payment latestPaidPayment = payments[index]
        purchaseOrderService.revertGiftByOrderHistory(latestPaidPayment.purchaseOrderHistory)
        revertPaymentsByOrderHistory(payments, latestPaidPayment.purchaseOrderHistory)
        revertOrderByOrderHistory(purchaseOrder, latestPaidPayment.purchaseOrderHistory)
        prePersistCheck(purchaseOrder, payments, latestPaidPayment.paymentType)
    }

    def handleFullRefundOrder(PurchaseOrderAmend amend, List<Payment> payments) {

        this.purchaseOrderService.releaseOldOrderGift(amend.purchaseOrder, payments, true, amend.purchaseOrderHistory)
        this.purchaseOrderService.resetPaymentStatus(payments, amend.purchaseOrder, amend.purchaseOrderHistory)
        if (PAYMENT_TYPE_TO_ORDER_TYPE.containsKey(amend.paymentType)) {
            amend.purchaseOrder.status = PAYMENT_TYPE_TO_ORDER_TYPE.get(amend.paymentType)
        }
        amend.purchaseOrder.paidAmount = amend.purchaseOrder.payableAmount
    }

    def revertOrderByOrderHistory(PurchaseOrder purchaseOrder, PurchaseOrderHistory orderHistory) {
        purchaseOrder.objId = orderHistory.objId
        purchaseOrder.payableAmount = orderHistory.payableAmount
        purchaseOrder.paidAmount = orderHistory.paidAmount
        purchaseOrder.status = OrderStatus.Enum.PAID_3
        purchaseOrder.updateTime = Calendar.getInstance().getTime()
    }

    def revertPaymentsByOrderHistory(List<Payment> payments, PurchaseOrderHistory orderHistory) {
        payments.findAll {
            it.status == PaymentStatus.Enum.CANCEL_4 && orderHistory == it.purchaseOrderHistory
        }.each {
            it.status = ([DISCOUNT_5, CHECHEPAY_6, BAOXIANPAY_8].contains(it.paymentType)) ? PaymentStatus.Enum.PAYMENTSUCCESS_2 : PaymentStatus.Enum.NOTPAYMENT_1
        }
    }

    /**
     * 校验数据是否允许增补／退款操作
     */
    def preCheck(PurchaseOrderAmend amend, List<Payment> payments) {

        if (null != amend.getPurchaseOrderHistory()) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, '非法操作，当前交易已经提交不可重复提交')
        }

        if (FULLREFUND_4 != amend.paymentType && !DoubleUtils.equalsDouble(amend.newQuoteRecord.calculatePremiumNoSetPremium(), amend.newQuoteRecord.premium)) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "非法操作，当前订单金额不匹配")
        }

        def unPaidPayment = payments.find { (PARTIALREFUND_3 == it.paymentType) && (NOTPAYMENT_1 == it.status) }
        if (PARTIALREFUND_3 == amend.paymentType && unPaidPayment) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "非法操作，存在待支付的部分退款，退款成功后方可再次操作增补")
        }

        def checkPayment = payments.findAll {
            PAYMENTSUCCESS_2 == it.status && ![CHECHEPAY_6, DISCOUNT_5, BAOXIANPAY_8].contains(it.paymentType);
        }
        if (!checkPayment && [CHECHEPAY_6, BAOXIANPAY_8, FULLREFUND_4, PARTIALREFUND_3].contains(amend.paymentType)) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "非法操作，车车承担，泛华承担，全额退款，部分退款必须有一笔已经支付的payment！");
        }

        if (![OrderStatus.Enum.PENDING_PAYMENT_1, OrderStatus.Enum.INSURE_FAILURE_7, OrderStatus.Enum.PAID_3, OrderStatus.Enum.REFUNDING_10].contains(amend.purchaseOrder.status)) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "非法操作，订单状态${amend.purchaseOrder.status.description}不允许${amend.paymentType.description}")
        }

        def amends = amendRepository.findByPurchaseOrder(amend.purchaseOrder)
        if (amends.any { it.id != amend.id && it.purchaseOrderAmendStatus == PurchaseOrderAmendStatus.Enum.CREATE }) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, '非法操作，存在未完成的订单修改记录')
        }
    }

    /**
     * 持久化前校验订单、支付记录数据的一致性
     */
    def prePersistCheck(PurchaseOrder purchaseOrder, List<Payment> payments, PaymentType paymentType) {
        def paidAmount = payments.findAll {
            [INITIALPAYMENT_1, ADDITIONALPAYMENT_2].contains(it.paymentType) && it.status != CANCEL_4
        }?.amount.sum()

        def refundAmount = payments.findAll {
            [PARTIALREFUND_3, FULLREFUND_4].contains(it.paymentType) && it.status != CANCEL_4
        }?.amount.sum()

        if (FULLREFUND_4 != paymentType && !DoubleUtils.equalsDouble(purchaseOrder.paidAmount, BigDecimalUtil.subtract(DoubleUtils.doubleValue(paidAmount), DoubleUtils.doubleValue(refundAmount)))) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "非法操作，该订单非取消状态的支付总数不匹配！")
        }

        def cheChePayment = payments.findAll {
            [DISCOUNT_5, CHECHEPAY_6, BAOXIANPAY_8].contains(it.paymentType) && it.status != CANCEL_4
        }
        def cheCheAmount = cheChePayment.inject(0.0) { result, payment -> BigDecimalUtil.add(result,payment.amount) }
        if (!DoubleUtils.equalsDouble(cheCheAmount, DoubleUtils.displayDoubleValue(BigDecimalUtil.subtract(purchaseOrder.payableAmount, purchaseOrder.paidAmount)))) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "非法操作，该订单应付金额不等于实付金额加上车车支付加上车车优惠");
        }

        def checkPaymentType = payments.findAll {
            [INITIALPAYMENT_1, ADDITIONALPAYMENT_2, PARTIALREFUND_3, FULLREFUND_4].contains(it.paymentType) && it.status == NOTPAYMENT_1;
        }

        if (checkPaymentType && checkPaymentType[0].channel != purchaseOrder.channel) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "非法操作，该订单的支付方式与原支付方式不一致！");
        }
        return true;
    }

    def preCheckCancelRefund(PurchaseOrder purchaseOrder, List<Payment> payments) {

        PurchaseOrderAmend orderAmend = orderAmendRepository.findLatestAmendByPurchaseOrder(purchaseOrder);
        if (FULLREFUND_4 != orderAmend.paymentType || orderAmend.purchaseOrderAmendStatus != PurchaseOrderAmendStatus.Enum.CREATE) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, '非法操作，订单最新的增补记录必须为[全额退款类型],[创建状态]')
        }

        def refundPayments = payments.findAll { it.status == PAYMENTSUCCESS_2 && [FULLREFUND_4].contains(it.paymentType) }
        if (refundPayments) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, '非法操作，全额退款已支付成功')
        }
    }

    /**
     * 根据Amend记录修改订单相关信息
     */
    def prepareNewOrder(PurchaseOrderAmend amend) {
        def order = amend.purchaseOrder
        def quoteRecord = amend.newQuoteRecord

        if (PAYMENT_TYPE_TO_ORDER_TYPE.containsKey(amend.paymentType)) {
            order.status = PAYMENT_TYPE_TO_ORDER_TYPE.get(amend.paymentType)
        }

        if (OrderStatus.Enum.PENDING_PAYMENT_1 == order.status) {
            order.expireTime = DateUtils.calculateDate(order.createTime, WebConstants.ORDER_EXPIRE_INTERVAL * 10, WebConstants.ORDER_EXPIRE_INTERVAL_TIMEUNIT)
        }

        if (quoteRecord!=null){
            order.objId = quoteRecord.id
            order.paidAmount = quoteRecord.getTotalPremium()
            order.payableAmount = order.paidAmount
        }
    }

    def applyAmountDiff(PurchaseOrderAmend amend, List<Payment> payments) {

        if ([ADDITIONALPAYMENT_2, INITIALPAYMENT_1].contains(amend.paymentType)) {
            handleAdditionalPayment(amend, payments)
        } else if ([FULLREFUND_4, PARTIALREFUND_3].contains(amend.paymentType)) {
            def refundAmount = PARTIALREFUND_3==amend.paymentType ? calculatePartialRefund(amend, payments) : calculator.customerNetPaid(payments)
            refundService.getRefund(payments, refundAmount, amend.purchaseOrder)?.each {
                it.purchaseOrderAmend = amend
                it.paymentType = amend.paymentType
                payments.add(it)
            }
        } else if ([CHECHEPAY_6, BAOXIANPAY_8].contains(amend.paymentType)) {
            handleCheChePay(amend, payments)
        } else {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "非预期支付类型: ${amend.paymentType}")
        }
    }

    private void handleCheChePay(PurchaseOrderAmend amend, List<Payment> payments) {
        handleAdditionalPayment(amend, payments)
        def cheChePayment = payments.find { it.status == PAYMENTSUCCESS_2 && [CHECHEPAY_6, BAOXIANPAY_8].contains(it.paymentType) }
        if (cheChePayment) {
            amend.purchaseOrder.paidAmount = BigDecimalUtil.subtract(amend.purchaseOrder.paidAmount,cheChePayment.amount)
        }
        payments
    }

    private switchGift(PurchaseOrder order, List<Payment> payments, PurchaseOrderHistory orderHistory) {
        this.purchaseOrderService.switchGift(order, payments, order.giftId, true, orderHistory)
    }

    /**
     * 计算增补差价
     */
    def handleAdditionalPayment(PurchaseOrderAmend amend, List<Payment> payments) {

        def order = amend.purchaseOrder
        def netPaid = calculator.customerNetPaid(payments)
        def diffAmount = BigDecimalUtil.subtract(order.paidAmount, netPaid)
        logger.debug("${amend.paymentType.description}差价：${diffAmount}")

        if (diffAmount < 0) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, '非法操作，增补金额小于0')
        }

        if (diffAmount == 0) {
            order.status = OrderStatus.Enum.PAID_3
            order.appendDescription("增补金额为0，订单状态不做修改，目前状态为$order.status.status")
            return payments
        }

        Payment payment = Payment.getPaymentTemplate(amend)
        payment.amount = DoubleUtils.doubleValue(diffAmount)
        payment.purchaseOrder.appendDescription("增补$order.paidAmount-$netPaid=$diffAmount")
        if (![CHECHEPAY_6, BAOXIANPAY_8].contains(amend.paymentType)) {
            def paidExisted = payments.any { INITIALPAYMENT_1 == it.paymentType && PAYMENTSUCCESS_2 == it.status };
            payment.setPaymentType(paidExisted ? ADDITIONALPAYMENT_2 : INITIALPAYMENT_1)
        }
        payments.add(payment)
    }

    /**
     * 计算部分退款差价
     */
    def calculatePartialRefund(PurchaseOrderAmend amend, List<Payment> payments) {

        def diffAmount = BigDecimalUtil.subtract(calculator.customerNetPaid(payments), amend.purchaseOrder.paidAmount)
        if (diffAmount < 0) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, '退款金额大于已付款金额')
        }
        diffAmount
    }

    private updateBills(QuoteRecord originalQuoteRecord,QuoteRecord quoteRecord, PurchaseOrder purchaseOrder) {
        purchaseOrder.updateTime = Calendar.getInstance().getTime()
        purchaseOrderRepository.save(purchaseOrder)
        if (quoteRecord!=null){
           return billsGenerator.generateBills(originalQuoteRecord,quoteRecord, purchaseOrder)
        }
    }

}
