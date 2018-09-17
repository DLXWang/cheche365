package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PlaceOrderResult1_1
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.QuoteConfigService
import com.cheche365.cheche.partner.serializer.APIPlaceOrder
import com.cheche365.cheche.web.response.RestResponseEnvelope
import com.cheche365.cheche.web.service.system.SystemUrlGenerator
import com.cheche365.cheche.web.version.Version
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.common.flow.Constants.get_ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.DateUtils.DATE_LONGTIME24_PATTERN
import static com.cheche365.cheche.common.util.DateUtils.getDateString
import static com.cheche365.cheche.common.util.DoubleUtils.displayDoubleValue
import static com.cheche365.cheche.common.util.DoubleUtils.sub
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PENDING_PAYMENT_1
import static com.cheche365.cheche.core.model.OrderStatus.Enum.isInsureFailure
import static com.cheche365.cheche.rest.util.WebFlowUtil._STATUS_OK
import static com.cheche365.cheche.web.util.PaymentValidationUtil.validateOrderPayable
import static com.cheche365.cheche.web.version.Version.getVersion
import static java.lang.Boolean.TRUE

/**
 * Created by zhengwei on 12/21/16.
 */

@Component
@Slf4j
class GenerateResponse implements TPlaceOrderStep {

    @Autowired
    SystemUrlGenerator systemUrlGenerator

    @Override
    def run(Object context) {
        PurchaseOrder order = context.order
        QuoteRecord quoteRecord = context.quoteRecord
        HttpServletRequest request = context.request

        Boolean pingPlusPay = getVersion(request) >= new Version("1.7")
        validateOrderPayable order.sourceChannel, quoteRecord, order, pingPlusPay, request

        if (order.sourceChannel.isPartnerAPIChannel() && PENDING_PAYMENT_1 == order.status) {
            context.payUrl = systemUrlGenerator.toPaymentUrlOriginal order
        }

        [_ROUTE_FLAG_DONE, _STATUS_OK, new RestResponseEnvelope(toPlaceOrderResult(context), null, context.additionalParameters.doInuranceMessage), null]
    }

    private static toPlaceOrderResult(context,
                                      PurchaseOrder order = context.order,
                                      QuoteRecord quoteRecord = context.quoteRecord,
                                      Insurance insurance = context.insurance,
                                      CompulsoryInsurance compulsoryInsurance = context.compulsoryInsurance,
                                      QuoteConfigService quoteConfigService = context.quoteConfigService) {
        order.sourceChannel.isPartnerAPIChannel() ? new APIPlaceOrder().generatePlaceOrderResult(context) : new PlaceOrderResult1_1(
            id: order.id,
            purchaseOrderNo: order.orderNo,
            status: order.status,
            insurance: insurance,
            compulsoryInsurance: compulsoryInsurance,
            discountAmount: displayDoubleValue(sub(order.payableAmount, order.payableAmount)),
            insureFailure: isInsureFailure(order.status),
            payableAmount: displayDoubleValue(order.payableAmount),
            paidAmount: displayDoubleValue(order.paidAmount),
            createTime: getDateString(order.createTime, DATE_LONGTIME24_PATTERN),
            expireTime: getDateString(order.expireTime, DATE_LONGTIME24_PATTERN),
            deliveryAddress: order.deliveryAddress,
            innerPay: quoteConfigService.isInnerPay(quoteRecord, order),
            reinsure: TRUE == context?.additionalParameters?.reInsure
        )
    }
}
