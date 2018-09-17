package com.cheche365.cheche.rest.processor.order.step.insure

import com.cheche365.cheche.core.exception.KnownReasonException
import com.cheche365.cheche.core.exception.NonFatalBusinessException
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.rest.processor.order.step.TPlaceOrderStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.rest.processor.quote.QuoteExceptionHandler.checkKnownReasonError

/**
 * Created by zhengwei on 20/03/2018.
 * 核保失败后处理步骤
 */

@Component
@Slf4j
class PostInsureFail implements TPlaceOrderStep {

    @Override
    Object run(Object context) {

        Exception e = context.insureFailException
        PurchaseOrder order = context.order

        if (e instanceof NonFatalBusinessException || e instanceof KnownReasonException) {
            context.toBePersistObjects.with {
                it << order
            }
            context._STATUS__LAZY_END_FLOW_ERROR = e
        } else {
            //对于toA渠道核保失败原因要展示给前台
            if ((order.sourceChannel.isStandardAgent() || order.sourceChannel.isOrderCenterChannel()) && checkKnownReasonError(e)) {
                context.additionalParameters << [doInuranceMessage : e.errorObject?.errorData ?: e.message]
            }
            context.toBePersistObjects.with {
                it << quoteRecord
                it << order
                it << context.insurance
                it << context.compulsoryInsurance
            }

        }
        getContinueFSRV true
    }
}
