package com.cheche365.cheche.pinganuk.service

import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IOCRService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.parser.service.AThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.parser.service.THttpClientGenerator
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment

import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PINGAN_20000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9
import static com.cheche365.cheche.parser.Constants._INSURANCE_DATE_EXTRACTOR
import static com.cheche365.cheche.pinganuk.flow.Constants._AUTO_TYPE_EXTRACTOR
import static com.cheche365.cheche.pinganuk.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.pinganuk.flow.Constants._PINGANUK_GET_VEHICLE_OPTION
import static com.cheche365.cheche.pinganuk.flow.Constants._PINGANUK_LOAD_PERSISTENT_STATE
import static com.cheche365.cheche.pinganuk.flow.Constants._PINGANUK_SAVE_PERSISTENT_STATE
import static com.cheche365.cheche.pinganuk.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.pinganuk.flow.FlowMappings._FLOW_CATEGORY_INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.pinganuk.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.pinganuk.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.pinganuk.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.pinganuk.util.BusinessUtils._CITY_ADVICE_POLICY_MAPPINGS



/**
 * 平安UK服务实现
 */
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Slf4j
class PinganUKService extends AThirdPartyHandlerService implements THttpClientGenerator {

    private IThirdPartyDecaptchaService decaptchaService

    private IOCRService getInformationService

    PinganUKService(
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        IThirdPartyDecaptchaService decaptchaService,
        IOCRService getInformationService) {
        super(env, insuranceCompanyChecker)
        this.decaptchaService = decaptchaService
        this.getInformationService = getInformationService
    }

    @Override
    protected createContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {

        def newEnv = [env: env, area: quoteRecord.area]

        [
            client                           : getHttpClient(getEnvProperty([env: env, area: quoteRecord.area], 'pinganuk.pacas_host'), null),
            username                         : getEnvProperty(newEnv, 'username'),
            password                         : getEnvProperty(newEnv, 'password'),
            autoTypeExtractor                : _AUTO_TYPE_EXTRACTOR,
            insuranceDateExtractor           : _INSURANCE_DATE_EXTRACTOR,
            insuranceCompany                 : quoteRecord.insuranceCompany,
            cityRpgMappings                  : _CITY_RPG_MAPPINGS,
            cityRhMappings                   : _CITY_RH_MAPPINGS,
            cityQuotingFlowMappings          : _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings         : _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            supplementInfoMapping            : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                 : _PINGANUK_GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            iopAlone                         : true,
            cityAdvicePolicyMappings         : _CITY_ADVICE_POLICY_MAPPINGS,
            decaptchaService                 : decaptchaService,
            getInformationService            : getInformationService,
            decaptchaInputTopic              : 'decaptcha-in-type06',
            loadPersistentState              : _PINGANUK_LOAD_PERSISTENT_STATE,
            savePersistentState              : _PINGANUK_SAVE_PERSISTENT_STATE,
        ]
    }


    @Override
    boolean isSuitable(Map conditions) {
        PINGAN_20000 == conditions.insuranceCompany && (AGENTPARSER_9 == conditions.quoteSource)
    }

}
