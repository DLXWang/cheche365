package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler
import com.cheche365.cheche.core.service.sms.ConditionTriggerUtil
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by zhengwei on 12/21/16.
 */

@Component
@Slf4j
class SendSms implements TPlaceOrderStep {

    @Override
    def run(Object context) {
        QuoteRecord quoteRecord = context.quoteRecord
        PurchaseOrder order = context.order
        ConditionTriggerHandler conditionTriggerHandler = context.conditionTriggerHandler

        if (!ConditionTriggerUtil.sendMsgNotAllowed(quoteRecord)) {
            ConditionTriggerUtil.sendOrderCommitMessage(conditionTriggerHandler, order, quoteRecord.getInsuranceCompany());
        }

        getContinueFSRV true
    }
}
