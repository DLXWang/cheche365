package com.cheche365.cheche.piccuk.service

import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.parser.service.AThirdPartyPaymentInfoService
import com.cheche365.cheche.parser.service.THttpClientGenerator
import groovy.util.logging.Slf4j
import groovyx.net.http.EncoderRegistry
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9
import static com.cheche365.cheche.piccuk.flow.Constants._PICC_UK_LOAD_PERSISTENT_STATE
import static com.cheche365.cheche.piccuk.flow.Constants._PICC_UK_SAVE_PERSISTENT_STATE
import static com.cheche365.cheche.piccuk.flow.FlowMappings._FLOW_CATEGORY_CHECK_PAYMENT_STATUS_FLOW_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.FlowMappings._FLOW_CATEGORY_GET_PAYMENT_CHANNELS_FLOW_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.FlowMappings._FLOW_CATEGORY_GET_PAYMENT_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static org.apache.http.conn.params.ConnRoutePNames.DEFAULT_PROXY



/**
 * 支付信息服务
 */
@Service
@Slf4j
class PiccUKPaymentInfoService extends AThirdPartyPaymentInfoService implements THttpClientGenerator {

    private IConfigService configService

    PiccUKPaymentInfoService(IConfigService configService) {
        this.configService = configService
    }

    @Override
    protected Object createContext(Environment env, Map applyPolicyNos, List paymentInfos, Map<String, Object> additionalParameters) {

        def prefixes = [additionalParameters?.quoteRecord?.channel?.apiPartner?.code, additionalParameters?.quoteRecord?.insuranceCompany?.id, additionalParameters?.quoteRecord?.area?.id].toArray()
        log.debug 'prefixes: {}', prefixes
        log.debug 'applyPolicyNos: {}', applyPolicyNos
        log.debug 'paymentInfos {}', paymentInfos
        log.debug 'additionalParameters {}', additionalParameters
        def newEnv = [env: env, configService: configService, namespace: 'piccuk']

        [
            client                           : getHttpClient((getEnvPropertyNew(newEnv, 'casserver_host', null, prefixes)), [suptPro: ['TLSv1'] as String[]]
            ).with {
                it.encoderRegistry = new EncoderRegistry(charset: 'GBK')
                if (getEnvPropertyNew(newEnv, 'http_proxy_username', null, prefixes)) {
                    it.client.getCredentialsProvider().setCredentials(
                        new AuthScope(
                            getEnvPropertyNew(newEnv, 'http_proxy_host', null, prefixes),
                            getEnvPropertyNew(newEnv, 'http_proxy_port', null, prefixes) as int),
                        new UsernamePasswordCredentials(
                            getEnvPropertyNew(newEnv, 'http_proxy_username', null, prefixes),
                            getEnvPropertyNew(newEnv, 'http_proxy_password', null, prefixes))
                    )
                }
                it.client.params.setParameter(
                    DEFAULT_PROXY,
                    new HttpHost(
                        getEnvPropertyNew(newEnv, 'http_proxy_host', null, prefixes),
                        getEnvPropertyNew(newEnv, 'http_proxy_port', null, prefixes) as int)
                )
                it
            },
            env                              : env,
            area                             : additionalParameters?.quoteRecord?.area,
            cityRpgMappings                  : _CITY_RPG_MAPPINGS,
            cityRhMappings                   : _CITY_RH_MAPPINGS,
            cityPaymentChannelsFlowMappings  : _FLOW_CATEGORY_GET_PAYMENT_CHANNELS_FLOW_MAPPINGS,
            cityPaymentInfoFlowMappings      : _FLOW_CATEGORY_GET_PAYMENT_INFO_FLOW_MAPPINGS,
            cityCheckPaymentStateFlowMappings: _FLOW_CATEGORY_CHECK_PAYMENT_STATUS_FLOW_MAPPINGS,
            additionalParameters             : additionalParameters ?: [:],
            portal_host                      : getEnvPropertyNew(newEnv, 'portal_host', null, prefixes),
            prpall_host                      : getEnvPropertyNew(newEnv, 'prpall_host', null, prefixes),
            piccsff_host                     : getEnvPropertyNew(newEnv, 'piccsff_host', null, prefixes),
            cbc_host                         : getEnvPropertyNew(newEnv, 'cbc_host', null, prefixes),
            username                         : getEnvPropertyNew(newEnv, 'username', null, prefixes),
            password                         : getEnvPropertyNew(newEnv, 'password', null, prefixes),
            loadPersistentState              : _PICC_UK_LOAD_PERSISTENT_STATE,
            savePersistentState              : _PICC_UK_SAVE_PERSISTENT_STATE,
            applyPolicyNos                   : applyPolicyNos,
            paymentInfos                     : paymentInfos,
            workbenchUserCode                : getEnvPropertyNew(newEnv, 'workbenchUserCode', null, prefixes),
            worekbenchUserComCode            : getEnvPropertyNew(newEnv, 'worekbenchUserComCode', null, prefixes),
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        PICC_10000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)
    }
}
