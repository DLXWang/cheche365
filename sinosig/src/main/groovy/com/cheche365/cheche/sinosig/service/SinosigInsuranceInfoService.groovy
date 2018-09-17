package com.cheche365.cheche.sinosig.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.parser.service.AThirdPartyInsuranceInfoService
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSIG_15000
import static com.cheche365.cheche.parser.util.BusinessUtils._INSURANCE_BASIC_INFO_EXTRACTOR
import static com.cheche365.cheche.sinosig.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.sinosig.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.sinosig.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.sinosig.flow.FlowMappings._FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.sinosig.flow.FlowMappings._FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.sinosig.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.sinosig.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.sinosig.flow.util.BusinessUtils._SINOSIG_GET_VEHICLE_OPTION



/**
 * 阳光保险信息服务
 */
@Service
@Slf4j
class SinosigInsuranceInfoService extends AThirdPartyInsuranceInfoService {

    @Override
    final protected doCreateContext(env, area, auto, additionalParameters) {

        [
            client                            : new RESTClient(env.getProperty('sinosig.base_url')),
            area                              : area,
            insuranceCompany                  : SINOSIG_15000,
            functionalDone                    : true,
            supplementInfoMapping             : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            vehicleModelSupplementInfoMapping : _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            cityRpgMappings                   : _CITY_RPG_MAPPINGS,
            cityRhMappings                    : _CITY_RH_MAPPINGS,
            cityInsuranceBasicInfoFlowMappings: _FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS,
            cityInsuranceInfoFlowMappings     : _FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS,
            insuranceBasicInfoExtractor       : _INSURANCE_BASIC_INFO_EXTRACTOR.curry(_DATETIME_FORMAT3),
            extendedAttributes                : auto.autoType?.supplementInfo,
            supplementInfoSupportList         : [:],
            getVehicleOption                  : _SINOSIG_GET_VEHICLE_OPTION,
            vehicleInfoExtractor              : _VEHICLE_INFO_EXTRACTOR,
        ]
    }

}
