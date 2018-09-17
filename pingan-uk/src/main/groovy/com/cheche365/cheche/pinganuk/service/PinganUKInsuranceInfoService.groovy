package com.cheche365.cheche.pinganuk.service

import com.cheche365.cheche.parser.service.AThirdPartyInsuranceInfoService
import com.cheche365.cheche.parser.service.THttpClientGenerator
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT2
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PINGANUK_20500
import static com.cheche365.cheche.parser.util.BusinessUtils._INSURANCE_BASIC_INFO_EXTRACTOR
import static com.cheche365.cheche.pinganuk.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.pinganuk.flow.Constants._PINGANUK_GET_VEHICLE_OPTION
import static com.cheche365.cheche.pinganuk.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.pinganuk.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.pinganuk.flow.FlowMappings._FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.pinganuk.flow.FlowMappings._FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.pinganuk.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.pinganuk.flow.HandlerMappings._CITY_RPG_MAPPINGS



/**
 * 平安UK保险信息
 */
@Service
@Slf4j
class PinganUKInsuranceInfoService extends AThirdPartyInsuranceInfoService implements THttpClientGenerator {

    @Override
    final protected doCreateContext(env, area, auto, additionalParameters) {
        [
            client                            : getHttpClient(getEnvProperty([env: env, area: area], 'pinganuk.pst_host'), null),
            area                              : area,
            insuranceCompany                  : PINGANUK_20500,
            cityRpgMappings                   : _CITY_RPG_MAPPINGS,
            cityRhMappings                    : _CITY_RH_MAPPINGS,
            supplementInfoMapping             : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                  : _PINGANUK_GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping : _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            functionalDone                    : true,
            cityInsuranceBasicInfoFlowMappings: _FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS,
            cityInsuranceInfoFlowMappings     : _FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS,
            insuranceBasicInfoExtractor       : _INSURANCE_BASIC_INFO_EXTRACTOR.curry(_DATETIME_FORMAT2),
            vehicleInfoExtractor              : _VEHICLE_INFO_EXTRACTOR,
            extendedAttributes                : auto.autoType?.supplementInfo,
            supplementInfoSupportList         : [:],
        ]
    }

}
