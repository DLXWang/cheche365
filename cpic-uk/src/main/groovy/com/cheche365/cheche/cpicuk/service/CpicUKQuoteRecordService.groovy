package com.cheche365.cheche.cpicuk.service

import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.parser.service.AThirdPartyQuoteRecordService
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CPIC_25000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9
import static com.cheche365.cheche.cpicuk.flow.FlowMappings._FLOW_CATEGORY_GET_QUOTE_RECORD_STATUS_FLOW_MAPPINGS



/**
 * cpicuk报价单相关service
 */
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Slf4j
class CpicUKQuoteRecordService extends AThirdPartyQuoteRecordService {

    private IThirdPartyDecaptchaService decaptchaService
    private IConfigService configService

    CpicUKQuoteRecordService(decaptchaService, configService) {
        this.decaptchaService = decaptchaService
        this.configService = configService
    }

    @Override
    protected Object createContext(Environment environment, List numbers, Map<String, Object> additionalParameters) {
        def prefixes = [additionalParameters.quoteRecord?.channel?.apiPartner?.code, additionalParameters.quoteRecord?.insuranceCompany?.id, additionalParameters.quoteRecord?.area?.id].toArray()

        log.debug 'prefixes: {}', prefixes
        def newEnv = [env: env, configService: configService, namespace: 'cpicuk']

        def base_url = getEnvPropertyNew(newEnv, 'base_url', null, prefixes)
        [
            client                             : getHttpClient(base_url, null),
            env                                : env,
            area                               : additionalParameters.quoteRecord?.area,
            cityGetQuoteRecordStateFlowMappings: _FLOW_CATEGORY_GET_QUOTE_RECORD_STATUS_FLOW_MAPPINGS,
            additionalParameters               : additionalParameters ?: [:],
            decaptchaService                   : decaptchaService,
            decaptchaInputTopic                : 'decaptcha-in-type07',
            numbers                            : numbers,
            username                           : getEnvPropertyNew(newEnv, 'username', null, prefixes),
            password                           : getEnvPropertyNew(newEnv, 'password', null, prefixes),
            partnerCode                        : getEnvPropertyNew(newEnv, 'partnerCode', null, prefixes),
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        CPIC_25000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)
    }

}
