package com.cheche365.cheche.cpicuk.service

import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.parser.service.AThirdPartyPaymentInfoService
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CPIC_25000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9
import static com.cheche365.cheche.cpicuk.flow.Constants._CPIC_UK_LOAD_PERSISTENT_STATE
import static com.cheche365.cheche.cpicuk.flow.Constants._CPIC_UK_SAVE_PERSISTENT_STATE
import static com.cheche365.cheche.cpicuk.flow.FlowMappings._FLOW_CATEGORY_CHECK_PAYMENT_STATUS_FLOW_MAPPINGS
import static com.cheche365.cheche.cpicuk.flow.FlowMappings._FLOW_CATEGORY_GET_PAYMENT_CHANNELS_FLOW_MAPPINGS
import static com.cheche365.cheche.cpicuk.flow.FlowMappings._FLOW_CATEGORY_GET_PAYMENT_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.cpicuk.flow.FlowMappings._FLOW_CATEGORY_CANCEL_PAY_FLOW_MAPPINGS
import static com.cheche365.cheche.cpicuk.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.cpicuk.flow.HandlerMappings._CITY_RPG_MAPPINGS



/**
 * cpicuk支付相关service
 */
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Slf4j
class CpicUKPaymentInfoService extends AThirdPartyPaymentInfoService {

    private IThirdPartyDecaptchaService decaptchaService
    private IConfigService configService

    CpicUKPaymentInfoService(decaptchaService, configService) {
        this.decaptchaService = decaptchaService
        this.configService = configService
    }

    @Override
    protected createContext(Environment env, Map applyPolicyNos, List paymentInfos, Map<String, Object> additionalParameters) {

        def prefixes = [additionalParameters.quoteRecord?.channel?.apiPartner?.code, additionalParameters.quoteRecord?.insuranceCompany?.id, additionalParameters.quoteRecord?.area?.id].toArray()

        log.debug 'prefixes: {}', prefixes
        def newEnv = [env: env, configService: configService, namespace: 'cpicuk']

        def base_url = MockUrlUtil.findBaseUrl(additionalParameters) ?: getEnvPropertyNew(newEnv, 'base_url', null, prefixes)
        [
            client                           : getHttpClient(base_url, null),
            env                              : env,
            area                             : additionalParameters.quoteRecord?.area,
            cityRpgMappings                  : _CITY_RPG_MAPPINGS,
            cityRhMappings                   : _CITY_RH_MAPPINGS,
            cityPaymentInfoFlowMappings      : _FLOW_CATEGORY_GET_PAYMENT_INFO_FLOW_MAPPINGS,
            cityPaymentChannelsFlowMappings  : _FLOW_CATEGORY_GET_PAYMENT_CHANNELS_FLOW_MAPPINGS,
            cityCheckPaymentStateFlowMappings: _FLOW_CATEGORY_CHECK_PAYMENT_STATUS_FLOW_MAPPINGS,
            cityCancelPayFlowMappings        : _FLOW_CATEGORY_CANCEL_PAY_FLOW_MAPPINGS,
            additionalParameters             : additionalParameters ?: [:],
            loadPersistentState              : _CPIC_UK_LOAD_PERSISTENT_STATE,
            savePersistentState              : _CPIC_UK_SAVE_PERSISTENT_STATE,
            decaptchaService                 : decaptchaService,
            decaptchaInputTopic              : 'decaptcha-in-type07',
            applyPolicyNos                   : applyPolicyNos,
            paymentInfos                     : paymentInfos,
            username                         : getEnvPropertyNew(newEnv, 'username', null, prefixes),
            password                         : getEnvPropertyNew(newEnv, 'password', null, prefixes),
            partnerCode                      : getEnvPropertyNew(newEnv, 'partnerCode', null, prefixes),
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        CPIC_25000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)
    }

}
