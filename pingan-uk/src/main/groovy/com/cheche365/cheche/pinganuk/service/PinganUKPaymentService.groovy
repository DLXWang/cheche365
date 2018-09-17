package com.cheche365.cheche.pinganuk.service

import com.cheche365.cheche.core.service.ISuitability
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.parser.service.THttpClientGenerator
import com.cheche365.flow.core.service.TSimpleService
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PINGAN_20000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9
import static com.cheche365.cheche.pinganuk.flow.FlowMappings._FLOW_CATEGORY_GET_PAYMENT_CHANNELS_FLOW_MAPPINGS
import static com.cheche365.cheche.pinganuk.flow.FlowMappings._FLOW_CATEGORY_GET_PAYMENT_INFO_FLOW_MAPPINGS

/**
 * 平安uk获取支付信息服务
 */
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Slf4j
class PinganUKPaymentService implements THttpClientGenerator, IThirdPartyPaymentService, ISuitability<Map>, TSimpleService {

    private IThirdPartyDecaptchaService decaptchaService


    PinganUKPaymentService(
        IThirdPartyDecaptchaService decaptchaService
    ) {
        this.decaptchaService = decaptchaService
    }

    @Override
    getPaymentChannels(Map applyPolicyNos, Map<String, Object> additionalParameters) {
        service createContext(com_cheche365_flow_core_service_TSimpleService__env, applyPolicyNos, additionalParameters), 'paymentChannels', '获取平安uk支付方式'
    }

    @Override
    getPaymentInfo(Map applyPolicyNos, Map<String, Object> additionalParameters) {
        service createContext(com_cheche365_flow_core_service_TSimpleService__env, applyPolicyNos, additionalParameters), 'paymentInfo', '获取平安uk支付信息'
    }

    @Override
    def checkPaymentState(List paymentInfos, Map<String, Object> additionalParameters) {
        return null
    }

    @Override
    def cancelPay(Map applyPolicyNos, Map<String, Object> additionalParameters) {
        return null
    }

    private createContext(env, applyPolicyNos, additionalParameters) {
        [
            client                         : getHttpClient(env.getProperty('pinganuk.pacas_host'), null),
            env                            : env,
            cityPaymentInfoFlowMappings    : _FLOW_CATEGORY_GET_PAYMENT_INFO_FLOW_MAPPINGS,
            cityPaymentChannelsFlowMappings: _FLOW_CATEGORY_GET_PAYMENT_CHANNELS_FLOW_MAPPINGS,
            additionalParameters           : additionalParameters ?: [:],
            decaptchaService               : decaptchaService,
            decaptchaInputTopic            : 'decaptcha-in-type06',
            applyPolicyNos                 : applyPolicyNos
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        PINGAN_20000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)
    }

}
