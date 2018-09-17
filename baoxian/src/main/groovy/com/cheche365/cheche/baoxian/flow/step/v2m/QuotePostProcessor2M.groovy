package com.cheche365.cheche.baoxian.flow.step.v2m

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.parser.util.InsuranceUtils.anyQuotePostProcess
import static com.cheche365.cheche.parser.util.InsuranceUtils.quotePostProcess

/**
 * 报价后处理器2M
 */
@Component
@Slf4j
class QuotePostProcessor2M implements IStep {

    @Override
    def run(Object context) {
        log.debug '报价以后多家QuoteRecord：{}', context.quoteRecordMappings
        anyQuotePostProcess(context)
    }
}
