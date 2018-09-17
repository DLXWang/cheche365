package com.cheche365.cheche.chinalife.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.parser.service.AThirdPartyInsuranceInfoService
import groovy.util.logging.Slf4j
import groovyx.net.http.EncoderRegistry
import org.springframework.stereotype.Service

import static com.cheche365.cheche.chinalife.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.chinalife.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.chinalife.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.chinalife.flow.FlowMappings._FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.chinalife.flow.FlowMappings._FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.chinalife.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.chinalife.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.chinalife.util.BusinessUtils._CHINA_LIFE_GET_VEHICLE_OPTION
import static com.cheche365.cheche.chinalife.util.CityCodeMappings._CITY_CODE_MAPPINGS
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CHINALIFE_40000
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.util.BusinessUtils._INSURANCE_BASIC_INFO_EXTRACTOR



/**
 * 国寿财保险信息
 */
@Service
@Slf4j
class ChinalifeInsuranceInfoService extends AThirdPartyInsuranceInfoService {

    @Override
    final protected doCreateContext(env, area, auto, additionalParameters) {
        def cityCodeMapping = _CITY_CODE_MAPPINGS[area.id]
        [
            client                            : new RESTClient(env.getProperty('chinalife.base_url')).with {
                encoderRegistry = new EncoderRegistry(charset: 'GBK')
                it
            },
            deptId                            : cityCodeMapping.deptId,
            parentId                          : cityCodeMapping.parentId,
            comCode                           : cityCodeMapping.comCode,
            insuranceCompany                  : CHINALIFE_40000,
            cityRpgMappings                   : _CITY_RPG_MAPPINGS,
            cityRhMappings                    : _CITY_RH_MAPPINGS,
            supplementInfoMapping             : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                  : _CHINA_LIFE_GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping : _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            functionalDone                    : true,
            vehicleInfoExtractor              : _VEHICLE_INFO_EXTRACTOR,
            cityInsuranceBasicInfoFlowMappings: _FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS,
            cityInsuranceInfoFlowMappings     : _FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS,
            insuranceBasicInfoExtractor       : _INSURANCE_BASIC_INFO_EXTRACTOR.curry(_DATETIME_FORMAT3),
            extendedAttributes                : auto.autoType?.supplementInfo,
            supplementInfoSupportList         : [:],
            flowType                          : _FLOW_TYPE_RENEWAL_CHANNEL
        ]
    }

}
