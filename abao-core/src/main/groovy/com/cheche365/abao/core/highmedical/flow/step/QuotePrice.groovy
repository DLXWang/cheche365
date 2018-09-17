package com.cheche365.abao.core.highmedical.flow.step

import com.cheche365.abao.core.highmedical.model.QuoteResult
import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.abao.InsuranceQuote
import com.cheche365.cheche.core.model.QuoteSource
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV


/**
 * 报价
 * Created by suyaqiang on 2016/11/22.
 */
@Slf4j
class QuotePrice implements IStep {

    @Override
    def run(context) {
        def kieContainer = context.kieContainer
        def kieSession = kieContainer.newStatelessKieSession 'highMedicalKSession'

        def quoteObject = context.quoteObject
        def insuranceQuote = new InsuranceQuote()

        kieSession.execute([quoteObject, insuranceQuote])

        log.info '保费结果：{}', insuranceQuote.premium

        if (insuranceQuote.premium) {
            insuranceQuote.with {
                insuranceProduct     = quoteObject.insuranceProduct
                insuranceCompany     = quoteObject.insuranceProduct?.insuranceCompany
                insuranceQuoteFields = quoteObject.insuranceQuoteFields
                type                 = QuoteSource.Enum.RULEENGINE_1
                it
            }
            context.resultObject = new QuoteResult([insuranceQuote])
            getContinueFSRV insuranceQuote

        } else {
            getKnownReasonErrorFSRV '保费计算失败'
        }

    }

}
