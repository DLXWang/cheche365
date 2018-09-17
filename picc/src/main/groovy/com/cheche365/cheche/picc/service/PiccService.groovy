package com.cheche365.cheche.picc.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.parser.service.AThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment

import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.WEBPARSER_2
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT1
import static com.cheche365.cheche.parser.Constants._INSURANCE_DATE_EXTRACTOR_BASE
import static com.cheche365.cheche.parser.Constants.get_DATETIME_FORMAT1
import static com.cheche365.cheche.picc.flow.Constants._AUTOTYPE_EXTRACTOR
import static com.cheche365.cheche.picc.flow.Constants._AUTO_INFO_EXTRACTOR
import static com.cheche365.cheche.picc.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.picc.flow.Constants._PICC_GET_VEHICLE_OPTION
import static com.cheche365.cheche.picc.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.picc.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.picc.flow.FlowMappings._FLOW_CATEGORY_INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.picc.flow.FlowMappings._FLOW_CATEGORY_ORDERING_FLOW_MAPPINGS
import static com.cheche365.cheche.picc.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.picc.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.picc.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.picc.flow.step.v2.util.BusinessUtils._CITY_ADVICE_POLICY_MAPPINGS
import static com.cheche365.cheche.picc.util.CityCodeMappings._CITY_CODE_MAPPINGS
import static com.cheche365.flow.core.util.ServiceUtils.persistState
import static java.util.UUID.randomUUID



/**
 * PICC服务实现
 */
@Slf4j
class PiccService extends AThirdPartyHandlerService {

    private IThirdPartyDecaptchaService decaptchaService


    PiccService(Environment env) {
        this(env, null, null)
    }

    PiccService(
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        IThirdPartyDecaptchaService decaptchaService) {
        super(env, insuranceCompanyChecker)
        this.decaptchaService = decaptchaService
    }


    @Override
    protected createContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {

        def area = quoteRecord.area
        def cityCodeMapping = getObjectByCityCode area, _CITY_CODE_MAPPINGS

        def base_url
        try{
            base_url = MockUrlUtil.findBaseUrl(additionalParameters) ?: env.getProperty('picc.base_url')
        } catch (e){
            base_url = env.getProperty('picc.base_url')
        }

        [
            client                            : new RESTClient(base_url),
            areaCode                          : cityCodeMapping.areaCode,
            cityCode                          : cityCodeMapping.cityCode,
            autoTypeExtractor                 : _AUTOTYPE_EXTRACTOR,
            autoInfoExtractor                 : _AUTO_INFO_EXTRACTOR,
            vehicleInfoExtractor              : _VEHICLE_INFO_EXTRACTOR,
            insuranceDateExtractor            : _INSURANCE_DATE_EXTRACTOR_BASE.curry(_DATE_FORMAT1, _DATETIME_FORMAT1),
            insuranceCompany                  : quoteRecord.insuranceCompany,
            cityRpgMappings                   : _CITY_RPG_MAPPINGS,
            cityRhMappings                    : _CITY_RH_MAPPINGS,
            cityQuotingFlowMappings           : _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings          : _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            cityOrderingFlowMappings          : _FLOW_CATEGORY_ORDERING_FLOW_MAPPINGS,
            supplementInfoMapping             : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                  : _PICC_GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping : _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            decaptchaService                  : decaptchaService,
            cityAdvicePolicyMappings          : _CITY_ADVICE_POLICY_MAPPINGS,
            iopAlone                          : true,
            entryId                           : randomUUID().toString(),
            decaptchaInputTopic               : 'decaptcha-in-type02'
        ]

    }

    @Override
    handleException(context, businessObjects, ex) {
        try {
            super.handleException context, businessObjects, ex
        } finally {
            persistState context, ex
        }
    }


    @Override
    boolean isSuitable(Map conditions) {
        PICC_10000 == conditions.insuranceCompany && (WEBPARSER_2 == conditions.quoteSource)
    }

}
