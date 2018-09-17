package com.cheche365.cheche.cpicuk.service

import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.parser.service.AThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.core.env.Environment

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CPIC_25000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9
import static com.cheche365.cheche.cpicuk.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.cpicuk.flow.Constants._CPIC_GET_VEHICLE_OPTION
import static com.cheche365.cheche.cpicuk.flow.Constants._CPIC_UK_LOAD_PERSISTENT_STATE
import static com.cheche365.cheche.cpicuk.flow.Constants._CPIC_UK_SAVE_PERSISTENT_STATE
import static com.cheche365.cheche.cpicuk.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.cpicuk.flow.FlowMappings._FLOW_CATEGORY_INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.cpicuk.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.cpicuk.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.cpicuk.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.parser.Constants._INSURANCE_DATE_EXTRACTOR
import static com.cheche365.flow.core.util.ServiceUtils.persistState



/**
 * 太平洋UK服务实现
 */
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Slf4j
class CpicUKService extends AThirdPartyHandlerService {

    private IThirdPartyDecaptchaService decaptchaService
    private IConfigService configService


    CpicUKService(
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        IThirdPartyDecaptchaService decaptchaService,
        IConfigService configService) {
        super(env, insuranceCompanyChecker)
        this.decaptchaService = decaptchaService
        this.configService = configService
    }

    @Override
    protected createContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {

        def prefixes = [quoteRecord.channel.apiPartner?.code, quoteRecord.insuranceCompany.id, quoteRecord.area.id].toArray()
        def newEnv = [env: env, configService: configService, namespace: 'cpicuk']
        def base_url = MockUrlUtil.findBaseUrl(additionalParameters) ?: getEnvPropertyNew(newEnv, 'base_url', null, prefixes)
        log.info 'base_url is {}', base_url
        [
            client                           : new RESTClient(base_url),
            quoteRecord                      : quoteRecord,
            insuranceDateExtractor           : _INSURANCE_DATE_EXTRACTOR,
            insuranceCompany                 : quoteRecord.insuranceCompany,
            cityRpgMappings                  : _CITY_RPG_MAPPINGS,
            cityRhMappings                   : _CITY_RH_MAPPINGS,
            cityQuotingFlowMappings          : _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings         : _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            supplementInfoMapping            : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                 : _CPIC_GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            iopAlone                         : true,
            decaptchaService                 : decaptchaService,
            configService                    : configService,
            namespace                        : 'cpicuk',
            decaptchaInputTopic              : 'decaptcha-in-type07',
            loadPersistentState              : _CPIC_UK_LOAD_PERSISTENT_STATE,
            savePersistentState              : _CPIC_UK_SAVE_PERSISTENT_STATE,
            compulsoryAndAutoTaxAllowAlone   : true,
            samCode                          : getEnvPropertyNew(newEnv, 'samCode', null, prefixes),
            username                         : getEnvPropertyNew(newEnv, 'username', null, prefixes),
            password                         : getEnvPropertyNew(newEnv, 'password', null, prefixes),
            partnerCode                      : getEnvPropertyNew(newEnv, 'partnerCode', null, prefixes),
            macAddress                       : getEnvPropertyNew(newEnv, 'macAddress', null, prefixes),
            sellerVo                         : [
                [
                    branchCode: getEnvPropertyNew(newEnv, 'branchCode', null, prefixes),
                    certNo  : getEnvPropertyNew(newEnv, 'sellerCertNo', null, prefixes),
                    name    : getEnvPropertyNew(newEnv, 'sellerName', null, prefixes),
                    sellerId: getEnvPropertyNew(newEnv, 'sellerId', null, prefixes)
                ]
            ]

        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        CPIC_25000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)
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
