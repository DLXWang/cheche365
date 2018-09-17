package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.web.service.order.discount.strategy.DiscountCalculator
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by zhengwei on 12/21/16.
 */

@Component
@Slf4j
class ApplyDiscount implements TPlaceOrderStep {

    @Override
    def run(context){
        DiscountCalculator discountCalculator = context.paymentGenerator
        QuoteRecord quoteRecord = context.quoteRecord
        PurchaseOrder order = context.order
        PurchaseOrderService orderService = context.orderService

        List<Payment> payments = discountCalculator.calculatePurchaseOrder(quoteRecord, order, order.giftId)
        orderService.assambleOrderDescription(payments, order);
        context.payments = payments
        context.toBePersistObjects << payments
        context.toBePersistObjects << order

        getContinueFSRV true
    }

}
