package com.cheche365.cheche.pingan.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.parser.service.AThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PINGAN_20000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.WEBPARSER_2
import static com.cheche365.cheche.pingan.flow.Constants._AUTOTYPE_EXTRACTOR
import static com.cheche365.cheche.pingan.flow.Constants._EXTRACT_AUTO_INFO
import static com.cheche365.cheche.pingan.flow.Constants._SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.pingan.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.pingan.flow.FlowMappings._FLOW_CATEGORY_INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.pingan.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPING
import static com.cheche365.cheche.pingan.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.pingan.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.pingan.util.BusinessUtils._GET_VEHICLE_OPTION
import static org.apache.commons.lang3.RandomUtils.nextInt



/**
 * 平安服务实现
 */
@TupleConstructor(
    includeSuperFields = true,
    includeFields = true
)
@Slf4j
class PinganService extends AThirdPartyHandlerService {

    private IThirdPartyDecaptchaService decaptchaService


    PinganService(Environment env) {
        this(env, null, null)
    }

    PinganService(
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        IThirdPartyDecaptchaService decaptchaService) {
        super(env, insuranceCompanyChecker)
        this.decaptchaService = decaptchaService
    }


    @Override
    protected createContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {
        [
            client                           : new RESTClient(env.getProperty('pingan.base_url')).with {
                headers.put('X-Forwarded-For', "111.${nextInt(10, 204)}.${nextInt(10, 137)}.${nextInt(10, 230)}")
                it
            },
            autoTypeExtractor                : _AUTOTYPE_EXTRACTOR,
            insuranceCompany                 : quoteRecord.insuranceCompany,
            cityRhMappings                   : _CITY_RH_MAPPINGS,
            cityRpgMappings                  : _CITY_RPG_MAPPINGS,
            cityQuotingFlowMappings          : _FLOW_CATEGORY_QUOTING_FLOW_MAPPING,
            cityInsuringFlowMappings         : _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            supplementInfoMapping            : _SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                 : _GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            decaptchaService                 : decaptchaService,
            autoInfoExtractor                : _EXTRACT_AUTO_INFO,
            decaptchaInputTopic              : 'decaptcha-in-type02'
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        PINGAN_20000 == conditions.insuranceCompany && (WEBPARSER_2 == conditions.quoteSource)
    }

}
