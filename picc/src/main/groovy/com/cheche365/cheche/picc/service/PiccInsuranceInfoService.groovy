package com.cheche365.cheche.picc.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.parser.service.AThirdPartyInsuranceInfoService
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT1
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT1
import static com.cheche365.cheche.parser.Constants._INSURANCE_DATE_EXTRACTOR_BASE
import static com.cheche365.cheche.picc.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.picc.flow.Constants._INSURANCE_BASIC_INFO_EXTRACTOR
import static com.cheche365.cheche.picc.flow.Constants._PICC_GET_VEHICLE_OPTION
import static com.cheche365.cheche.picc.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.picc.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.picc.flow.FlowMappings._FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.picc.flow.FlowMappings._FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.picc.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.picc.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.picc.util.CityCodeMappings._CITY_CODE_MAPPINGS



/**
 * 获取续保车辆信息
 */
@TupleConstructor(
    includeFields = true
)
@Slf4j
class PiccInsuranceInfoService extends AThirdPartyInsuranceInfoService {

    private IThirdPartyDecaptchaService decaptchaService

    @Override
    protected doCreateContext(env, area, auto, additionalParameters) {
        def cityCodeMapping = getObjectByCityCode area, _CITY_CODE_MAPPINGS

        [
            client                            : new RESTClient(env.getProperty('picc.base_url')),
            areaCode                          : cityCodeMapping.areaCode,
            cityCode                          : cityCodeMapping.cityCode,
            insuranceCompany                  : PICC_10000,
            qfsMessageMappings                : [:],
            cityRpgMappings                   : _CITY_RPG_MAPPINGS,
            cityRhMappings                    : _CITY_RH_MAPPINGS,
            cityInsuranceBasicInfoFlowMappings: _FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS,
            cityInsuranceInfoFlowMappings     : _FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS,
            insuranceBasicInfoExtractor       : _INSURANCE_BASIC_INFO_EXTRACTOR,
            supplementInfoSupportList         : [],
            vehicleInfoExtractor              : _VEHICLE_INFO_EXTRACTOR,
            insuranceDateExtractor            : _INSURANCE_DATE_EXTRACTOR_BASE.curry(_DATE_FORMAT1, _DATETIME_FORMAT1),
            supplementInfoMapping             : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                  : _PICC_GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping : _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            decaptchaService                  : decaptchaService,
            flowType                          : _FLOW_TYPE_RENEWAL_CHANNEL
        ]
    }

}
