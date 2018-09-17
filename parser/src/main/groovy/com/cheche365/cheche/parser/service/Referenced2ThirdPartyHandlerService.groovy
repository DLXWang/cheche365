package com.cheche365.cheche.parser.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService2
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.parser.ArtificialPolicyConstants._MISSING_REFERRED_RESULT_CODE
import static com.cheche365.cheche.parser.ArtificialPolicyConstants._NEED_SUPPLY_AND_BAD_PARAMETER
import static com.cheche365.cheche.parser.ArtificialPolicyConstants._QUOTE_EXCEPTION_CODE
import static com.cheche365.cheche.parser.ArtificialPolicyConstants._QUOTE_SUCCESS_CODE
import static com.cheche365.cheche.parser.ArtificialPolicyConstants._TWO_TIMES_THROW_SUPPLY_CODE

/**
 * 报价的并发包装服务
 */
@Slf4j
class Referenced2ThirdPartyHandlerService implements IThirdPartyHandlerService2 {

    private IThirdPartyHandlerService2 referred
    private Object postFailedRule
    private Map<Object, List<Closure>> quoteRuleMappings
    private Map<String, IThirdPartyHandlerService> services
    private def getQuotingService

    Referenced2ThirdPartyHandlerService(
        IThirdPartyHandlerService2 referred,
        Object postFailedRule,
        Map<Object, List<Closure>> quoteRuleMappings,
        Object getQuotingService,
        Map<String, IThirdPartyHandlerService> services) {
        this.referred = referred
        this.postFailedRule = postFailedRule
        this.quoteRuleMappings = quoteRuleMappings
        this.getQuotingService = getQuotingService
        this.services = services
    }


    @Override
    Map<InsuranceCompany, Map> quotes(QuoteRecord quoteRecord, Map<String, Object> additionalParameters) {
        def quoteSourceMap = additionalParameters.quoteSourceMap
        def quoteCompanies = additionalParameters.quoteCompanies
        def referredCompanies = additionalParameters.referredCompanies
        def referResults
        def service = getQuotingService(referred)
        if (service instanceof IThirdPartyHandlerService2) {
            log.info '参考多家保险公司，走并发报价'
            referResults = referred.quotes(quoteRecord, additionalParameters)
            // 预处理之前补齐一次
            referredCompanies.each { insuranceCompany ->
                def isExistReferred = referResults[insuranceCompany]
                if (!isExistReferred) {
                    referResults << [(insuranceCompany): [code: _MISSING_REFERRED_RESULT_CODE]]
                }
            }
            preDeal(postFailedRule, referResults, additionalParameters, quoteRecord)
        } else {
            def (company, ser) = service
            log.info '参考唯一保险公司为：{}', company.code
            def qr = quoteRecord.clone()
            def addParams = additionalParameters.clone()
            try {
                qr.insuranceCompany = company
                ser.quote(qr, addParams)
                referResults = [(company): [code: _QUOTE_SUCCESS_CODE, data: [quoteRecord: qr, additionalParameters: addParams]]]
            } catch (ex) {
                log.info '参考唯一保险公司报价时异常：{}', ex.message
                referResults = [(company): [code: _QUOTE_EXCEPTION_CODE, error: ex, data: [additionalParameters: addParams]]]
                preDeal(postFailedRule, referResults, additionalParameters, quoteRecord)
            }
        }

        def deal = quoteRuleMappings.find { rule, deal ->
            rule(referResults, quoteSourceMap)
        }?.value
        deal.with { rule1, rule2 ->
            rule2(quoteSourceMap, quoteCompanies, rule1(referResults, quoteSourceMap)) ?: [:]
        }
    }

    private static preDeal(postFailedRule, referResults, additionalParameters, quoteRecord) {
        referResults.each { key, value ->
            if (value.code != 0) {
                def exception = value.error
                def code = additionalParameters.flowState?.hasThrowLackOfSupplementInfo ? _TWO_TIMES_THROW_SUPPLY_CODE : (exception instanceof BusinessException) ? exception.code.codeValue : _QUOTE_EXCEPTION_CODE
                value.code = code
                if (!(code in _NEED_SUPPLY_AND_BAD_PARAMETER)) {
                    def qr = quoteRecord.clone()
                    def addParams = (value.data?.additionalParameters) ?: additionalParameters.clone()
                    postFailedRule 0.0, qr, addParams, exception
                    value << [data: [quoteRecord: qr, additionalParameters: addParams]]
                }
            }
        }

    }

    @Override
    void quote(QuoteRecord quoteRecord, Map<String, Object> additionalParameters) {
        throw new UnsupportedOperationException('当前报价方式不支持单个公司报价')
    }

    @Override
    void insure(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Map<String, Object> additionalParameters) {
        throw new UnsupportedOperationException('当前报价方式不支持核保')
    }

    @Override
    void order(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Map<String, Object> additionalParameters) {
        throw new UnsupportedOperationException('当前报价方式不支持下单')
    }
}
