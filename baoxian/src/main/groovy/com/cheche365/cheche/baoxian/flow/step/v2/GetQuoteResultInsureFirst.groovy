package com.cheche365.cheche.baoxian.flow.step.v2

import com.cheche365.cheche.baoxian.flow.step.AGetQuoteResult
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.baoxian.flow.Constants._COMPANY_I2O_MAPPINGS
import static com.cheche365.cheche.baoxian.flow.Constants._TASKID_TTL
import static com.cheche365.cheche.baoxian.util.BusinessUtils._ADVICE_REGULATOR_MAPPINGS
import static com.cheche365.cheche.baoxian.util.BusinessUtils._GET_EFFECTIVE_ADVICES
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.InsuranceUtils.adjustInsurancePackageFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static java.util.concurrent.TimeUnit.HOURS

/**
 * 对应先核保后支付流程的报价回调
 */
@Component
@Slf4j
class GetQuoteResultInsureFirst extends AGetQuoteResult {

    @Override
    getQuoteResult(context) {
        def result = invokeAndWait _ASYNC_RUNNABLE, [
            timeoutInSeconds: getEnvProperty(context, 'baoxian.quoting_timeout_in_seconds') as long,
            preHandler      : _PRE_HANDLER.curry(_COMPANY_I2O_MAPPINGS[context.insuranceCompany.id], context),
            postHandler     : _POST_HANDLER,
            callbackHandler : context.messageHandler,
        ]
        log.info '伪同步，等待泛华报价异步回调结果，taskId：{}，保险公司：{}', context.taskId, context.insuranceCompany.code, result
        result
    }

    private static final _PRE_HANDLER = { prvIdPrefix, context, log ->
        [context.taskId + '_' + prvIdPrefix, null]
    }

    private static final _POST_HANDLER = { result, tid, log ->
        result
    }

    private static final _ASYNC_RUNNABLE = { tid, log ->
        [tid, null]
    }

}
