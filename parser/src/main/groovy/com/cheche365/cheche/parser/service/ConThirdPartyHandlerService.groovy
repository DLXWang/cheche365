package com.cheche365.cheche.parser.service

import com.cheche365.cheche.common.util.CollectionUtils
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService2
import com.cheche365.flow.core.service.TSimpleConcurrentService
import groovy.util.logging.Slf4j
import groovyx.gpars.group.PGroup
import org.slf4j.MDC

import static com.cheche365.cheche.parser.ArtificialPolicyConstants._QUOTE_EXCEPTION_CODE
import static com.cheche365.cheche.parser.ArtificialPolicyConstants._QUOTE_SUCCESS_CODE

/**
 * 报价的并发包装服务
 */
@Slf4j
class ConThirdPartyHandlerService implements IThirdPartyHandlerService2, TSimpleConcurrentService {

    private static final _SERVICE_CONFIG_TEMPLATE = [
        0L     : [
            priorityMappings: [:]
        ],
        default: [
            options: [
                timeout       : 100L,   // 等待报价超时最多50秒
                maxResultCount: 0L
            ]
        ]
    ]

    private static final _JOB_BLUEPRINT_QUOTE = { mdcContext, company, service, quoteRecord, additionalParameters ->
        MDC.contextMap = mdcContext
        def qr = quoteRecord.clone()
        def addParams = additionalParameters.clone()
        try {
            qr.insuranceCompany = company
            service.quote(qr, addParams)
            [company, qr, addParams, null]
        } catch (ex) {
            [company, null, addParams, ex]
        }
    }

    private Map<InsuranceCompany, IThirdPartyHandlerService> companyServiceMappings
    private PGroup parserTaskPGroup

    /**
     * 并发报价服务
     * @param companyServiceMappings
     * @param parserTaskPGroup
     */
    ConThirdPartyHandlerService(
        Map<String, IThirdPartyHandlerService> companyServiceMappings,
        PGroup parserTaskPGroup) {
        this.companyServiceMappings = companyServiceMappings
        this.parserTaskPGroup = parserTaskPGroup
    }

    @Override
    Map<InsuranceCompany, Map> quotes(QuoteRecord quoteRecord, Map<String, Object> additionalParameters) {
        log.debug '将要执行并发调取报价服务：{}，{}，{}', companyServiceMappings, quoteRecord, additionalParameters
        def mdcContext = MDC.copyOfContextMap
        def priorityMappings = companyServiceMappings.keySet().withIndex().collectEntries { insuranceCompany, index ->
            [(insuranceCompany): index]
        }
        def serviceConfig = CollectionUtils.mergeMaps(_SERVICE_CONFIG_TEMPLATE.clone(),
            [
                0L     : [priorityMappings: priorityMappings],
                default: [options: [
                    timeout       : quoteRecord.channel.isStandardAgent() ? 120L : 90L,
                    maxResultCount: companyServiceMappings.keySet().size()
                ]]
            ]
        )
        service(
            companyServiceMappings,
            serviceConfig,
            _JOB_BLUEPRINT_QUOTE.curry(mdcContext),
            [quoteRecord, additionalParameters],
            parserTaskPGroup
        ).with { results ->
            results.collectEntries { company, qr, addParams, exception ->
                if (qr) {
                    log.debug '并发调取报价服务结果 qr：{}', qr
                    log.debug '并发调取报价服务结果 input additional parameters ：{}', additionalParameters
                    log.debug '并发调取报价服务结果 output additional parameters ：{}', addParams
                    [(company): [code: _QUOTE_SUCCESS_CODE, data: [quoteRecord: qr, additionalParameters: addParams]]]
                } else {
                    log.debug '并发调取报价服务异常，company:{}, error：{}', company.code, exception

                    [(company): [code: _QUOTE_EXCEPTION_CODE, error: exception, data: [additionalParameters: addParams]]]
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
