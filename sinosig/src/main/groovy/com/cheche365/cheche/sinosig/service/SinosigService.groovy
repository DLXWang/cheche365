package com.cheche365.cheche.sinosig.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.parser.service.AThirdPartyHandlerService
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSIG_15000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.WEBPARSER_2
import static com.cheche365.cheche.parser.Constants._INSURANCE_DATE_EXTRACTOR
import static com.cheche365.cheche.sinosig.flow.Constants._AUTOTYPE_EXTRACTOR
import static com.cheche365.cheche.sinosig.flow.Constants._AUTO_INFO_EXTRACTOR
import static com.cheche365.cheche.sinosig.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.sinosig.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.sinosig.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.sinosig.flow.FlowMappings._FLOW_CATEGORY_INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.sinosig.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.sinosig.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.sinosig.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.sinosig.flow.util.BusinessUtils._SINOSIG_GET_VEHICLE_OPTION



/**
 * Created by suyq on 2015/8/18.
 * 阳光报价服务
 */
@Service
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Slf4j
class SinosigService extends AThirdPartyHandlerService {

    @Override
    protected createContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {
        [
            client                            : new RESTClient(env.getProperty('sinosig.base_url')),
            agentCode                         : 'W00110002',
            autoTypeExtractor                 : _AUTOTYPE_EXTRACTOR,
            insuranceDateExtractor            : _INSURANCE_DATE_EXTRACTOR,
            insuranceCompany                  : quoteRecord.insuranceCompany,
            cityRpgMappings                   : _CITY_RPG_MAPPINGS,
            cityRhMappings                    : _CITY_RH_MAPPINGS,
            cityQuotingFlowMappings           : _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings          : _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            vehicleInfoExtractor              : _VEHICLE_INFO_EXTRACTOR,
            autoInfoExtractor                 : _AUTO_INFO_EXTRACTOR,
            supplementInfoMapping             : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                  : _SINOSIG_GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping : _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        SINOSIG_15000 == conditions.insuranceCompany && (WEBPARSER_2 == conditions.quoteSource)
    }

}
