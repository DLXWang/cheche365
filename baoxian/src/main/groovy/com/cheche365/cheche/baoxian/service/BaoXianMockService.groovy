package com.cheche365.cheche.baoxian.service

import com.cheche365.cheche.core.model.QuoteFieldStatus
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.flow.core.Constants
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.flow.Constants._STATUS_CODE_FATAL_ERROR
import static com.cheche365.cheche.common.flow.Constants._STATUS_CODE_IGNORABLE_ERROR
import static com.cheche365.cheche.common.flow.Constants._STATUS_CODE_OK
import static com.cheche365.cheche.parser.util.BusinessUtils.addQFS
import static com.cheche365.cheche.parser.util.BusinessUtils.resolveNewQuoteRecordInContext
import static java.lang.Boolean.TRUE
import static java.lang.Boolean.valueOf as boolValue



/**
 * 泛华保险伪服务实现
 */
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Slf4j
class BaoXianMockService extends BaoXianService {

    private static final _STATUS_HANDLER_FATAL_ERROR = { context, _businessObjects, fsrv, log ->
        def errorMsg = fsrv.last()
        log.warn '捕获到的非预期异常信息：{}', errorMsg
        addQFS resolveNewQuoteRecordInContext(context),
            new QuoteFieldStatus(description: "【MOCK】-> 捕获到的非预期异常信息：$errorMsg <-【MOCK】")
    }

    private static final _VALID_STATUS_HANDLER_MAPPINGS = [
        (Constants._CHECK_STATUS_BASE.curry(_STATUS_CODE_FATAL_ERROR))  : _STATUS_HANDLER_FATAL_ERROR
    ]


    @Override
    protected doCreateContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {
        super.doCreateContext(quoteRecord, businessSpecificContext, additionalParameters) +
            (boolValue(env.getProperty('baoxian.mock_enabled', TRUE.toString())) ? [
                validStatus: [_STATUS_CODE_OK, _STATUS_CODE_IGNORABLE_ERROR, _STATUS_CODE_FATAL_ERROR]
            ] : [:])
    }

    @Override
    preService(context) {
        super.preService context
        if (boolValue(env.getProperty('baoxian.mock_enabled', TRUE.toString()))) {
            resolveNewQuoteRecordInContext(context).with {
                premium = 0.01d
            }
        }
    }

    @Override
    getValidStatusHandlerMappings() {
        super.validStatusHandlerMappings + (boolValue(env.getProperty('baoxian.mock_enabled', TRUE.toString())) ? _VALID_STATUS_HANDLER_MAPPINGS : [:])
    }

}
