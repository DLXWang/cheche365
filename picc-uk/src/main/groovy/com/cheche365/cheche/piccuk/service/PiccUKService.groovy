package com.cheche365.cheche.piccuk.service

import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.parser.service.AThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.parser.service.THttpClientGenerator
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import groovyx.net.http.EncoderRegistry
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.springframework.core.env.Environment

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9
import static com.cheche365.cheche.parser.Constants._INSURANCE_DATE_EXTRACTOR
import static com.cheche365.cheche.piccuk.flow.Constants._AUTO_TYPE_EXTRACTOR
import static com.cheche365.cheche.piccuk.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.Constants._PICCUK_GET_VEHICLE_OPTION
import static com.cheche365.cheche.piccuk.flow.Constants._PICC_UK_LOAD_PERSISTENT_STATE
import static com.cheche365.cheche.piccuk.flow.Constants._PICC_UK_SAVE_PERSISTENT_STATE
import static com.cheche365.cheche.piccuk.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.FlowMappings._FLOW_CATEGORY_INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.piccuk.util.CityCodeMappings._CITY_CODE_MAPPINGS
import static com.cheche365.flow.core.util.ServiceUtils.persistState
import static org.apache.http.conn.params.ConnRoutePNames.DEFAULT_PROXY



/**
 * PICCUK服务实现
 */
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Slf4j
class PiccUKService extends AThirdPartyHandlerService implements THttpClientGenerator {

    private IConfigService configService
    private IThirdPartyDecaptchaService decaptchaService

    PiccUKService(
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        IThirdPartyDecaptchaService decaptchaService,
        IConfigService configService) {
        super(env, insuranceCompanyChecker)
        this.configService = configService
        this.decaptchaService = decaptchaService
    }

    @Override
    protected createContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {
        def area = quoteRecord.area
        def cityCodeMapping = getObjectByCityCode area, _CITY_CODE_MAPPINGS

        def prefixes = [quoteRecord.channel.id, quoteRecord.insuranceCompany.id, quoteRecord.area.id].toArray()
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
            quoteRecord                      : quoteRecord,
            cityCode                         : cityCodeMapping.cityCode,
            cityName                         : cityCodeMapping.cityName,
            provinceCode                     : cityCodeMapping.provinceCode,
            autoTypeExtractor                : _AUTO_TYPE_EXTRACTOR,
            insuranceDateExtractor           : _INSURANCE_DATE_EXTRACTOR,
            insuranceCompany                 : quoteRecord.insuranceCompany,
            cityRpgMappings                  : _CITY_RPG_MAPPINGS,
            cityRhMappings                   : _CITY_RH_MAPPINGS,
            cityQuotingFlowMappings          : _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings         : _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            supplementInfoMapping            : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                 : _PICCUK_GET_VEHICLE_OPTION,
            iopAlone                         : true,
            portal_host                      : getEnvPropertyNew(newEnv, 'portal_host', null, prefixes),
            prpall_host                      : getEnvPropertyNew(newEnv, 'prpall_host', null, prefixes),
            username                         : getEnvPropertyNew(newEnv, 'username', null, prefixes),
            password                         : getEnvPropertyNew(newEnv, 'password', null, prefixes),
            loadPersistentState              : _PICC_UK_LOAD_PERSISTENT_STATE,
            savePersistentState              : _PICC_UK_SAVE_PERSISTENT_STATE,
            workbenchUserCode                : getEnvPropertyNew(newEnv, 'workbenchUserCode', null, prefixes),
            worekbenchUserComCode            : getEnvPropertyNew(newEnv, 'worekbenchUserComCode', null, prefixes),
            decaptchaService                 : decaptchaService,
            uploadImageUrl                   : getEnvPropertyNew(newEnv, 'uploadImageUrl', null, null),
            sunecm_host                      : getEnvPropertyNew(newEnv, 'sunecm_host', null, prefixes),
            compulsoryAndAutoTaxAllowAlone   : true,
            samCode                          : getEnvPropertyNew(newEnv, 'samCode', null, prefixes),
            batchScanHost                    : getEnvPropertyNew(newEnv, 'batchScanHost', null, prefixes),
            batchScanPort                    : getEnvPropertyNew(newEnv, 'batchScanPort', null, prefixes),
            piccComCode                      : getEnvPropertyNew(newEnv, 'piccComCode', null, prefixes)
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        PICC_10000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)
    }

    @Override
    handleException(context, businessObjects, ex) {
        try {
            super.handleException context, businessObjects, ex
        } finally {
            if (!context.quoting && context.newQuoteRecordAndInsurances && context.updateBusinessObjects) {
                context.updateBusinessObjects context, businessObjects
            }
            persistState context, ex
        }
    }

}
