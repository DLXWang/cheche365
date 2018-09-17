package com.cheche365.cheche.web.service.order.discount.strategy

import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.web.service.InsurancePurchaseOrderRebateService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by zhengwei on 6/1/15.
 */
@Component
@Slf4j
class DiscountCalculator {

    @Autowired
    List<DiscountStrategy> strategies;

    @Autowired
    GiftStrategy ordinaryStrategy

    @Autowired
    private InsurancePurchaseOrderRebateService insurancePurchaseOrderRebateService;

    def static ALL = {true}
    static Closure NON_USER_INVOLVED_FILTER = {DiscountStrategy strategy -> DiscountStrategy.NON_USER_INVOLVED == strategy.belongsToGroup()}

    def calculate = { QuoteRecord quoteRecord, PurchaseOrder order, giftIds, Closure strategyFilter, Closure discountHandler ->
        strategies.findAll { strategyFilter(it) }.find { it.support(quoteRecord, order, giftIds[0] as Long) }.with { strategy ->
            if (!strategy) {
                log.debug('无可用减免策略 {}', quoteRecord.applicant)
            } else {
                def discountResult = strategy.applyDiscountStrategy(quoteRecord, order, giftIds[0] as Long)
                (giftIds - giftIds[0]).each { giftId ->
                    ordinaryStrategy.applyDiscountStrategy(quoteRecord, order, giftId as Long)
                }
                discountResult.quoteRecord = quoteRecord
                discountResult.order = order
                discountHandler(discountResult)
            }
        }
    }

    def calculateQuoteRecord(QuoteRecord quoteRecord) { //目前有java调用，直接用curry后的结果，java编译报错
        calculateQuoteRecordMethod(quoteRecord)
    }

    def calculateQuoteRecordMethod = calculate.rcurry(null, [null], NON_USER_INVOLVED_FILTER, { discountResult ->
        discountResult.quoteRecord.paidAmount = discountResult.quoteRecord.calculatePaidAmount(discountResult.discountAmount)
    })

    def calculatePurchaseOrder = calculate.rcurry(ALL, {discountResult ->
        def discountPayment, customerPayment
        def result = []
        if(discountResult.discountAmount){
            discountPayment = Payment.getDiscountPaymentTemplate(discountResult.order, discountResult.paymentChannel)
            discountPayment.amount = discountResult.discountAmount
            discountResult.order.paidAmount = discountResult.quoteRecord.calculatePaidAmount(discountResult.discountAmount)
        }

        customerPayment = Payment.getPaymentTemplate(discountResult.order)
        customerPayment.amount = discountResult.order.paidAmount
        result << customerPayment

        insurancePurchaseOrderRebateService.applyRebate(discountResult.quoteRecord,discountResult.order)

        if(discountResult.persistentCallback){
            discountResult.persistentCallback()
        }

        discountPayment && result << discountPayment

        result
    })
}
