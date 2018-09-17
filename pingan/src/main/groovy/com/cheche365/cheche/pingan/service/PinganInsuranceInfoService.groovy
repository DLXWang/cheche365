package com.cheche365.cheche.pingan.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.parser.service.AThirdPartyInsuranceInfoService
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PINGAN_20000
import static com.cheche365.cheche.parser.util.BusinessUtils._INSURANCE_BASIC_INFO_EXTRACTOR
import static com.cheche365.cheche.pingan.flow.Constants._SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.pingan.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.pingan.flow.FlowMappings._FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.pingan.flow.FlowMappings._FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.pingan.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.pingan.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.pingan.util.BusinessUtils._GET_VEHICLE_OPTION
import static org.apache.commons.lang3.RandomUtils.nextInt



/**
 * 平安保险信息服务
 */
@TupleConstructor(
    includeFields = true
)
@Slf4j
class PinganInsuranceInfoService extends AThirdPartyInsuranceInfoService {

    private IThirdPartyDecaptchaService decaptchaService


    @Override
    final protected doCreateContext(env, area, auto, additionalParameters) {

        [
            client                           : new RESTClient(env.getProperty('pingan.base_url')).with {
                headers.put('X-Forwarded-For', "122.${nextInt(10, 204)}.${nextInt(10, 137)}.${nextInt(10, 230)}")
                it
            },
            area                              : area,
            insuranceCompany                  : PINGAN_20000,
            functionalDone                    : true,
            supplementInfoMapping             : _SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                  : _GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping : _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            cityInsuranceBasicInfoFlowMappings: _FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS,
            cityInsuranceInfoFlowMappings     : _FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS,
            insuranceBasicInfoExtractor       : _INSURANCE_BASIC_INFO_EXTRACTOR.curry(_DATETIME_FORMAT3),
            cityRhMappings                    : _CITY_RH_MAPPINGS,
            cityRpgMappings                   : _CITY_RPG_MAPPINGS,
            decaptchaService                  : decaptchaService,
            extendedAttributes                : auto.autoType?.supplementInfo,
            supplementInfoSupportList         : [:],
        ]
    }

}
