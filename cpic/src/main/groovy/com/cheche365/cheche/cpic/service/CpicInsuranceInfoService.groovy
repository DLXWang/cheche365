package com.cheche365.cheche.cpic.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.parser.service.AThirdPartyInsuranceInfoService
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.common.util.AreaUtils.getProvinceCode
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CPIC_25000
import static com.cheche365.cheche.cpic.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.cpic.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.cpic.flow.FlowMappings._FLOW_INSURANCE_BASIC_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.cpic.flow.FlowMappings._FLOW_INSURANCE_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.cpic.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.cpic.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.cpic.util.BusinessUtils._CPIC_GET_VEHICLE_OPTION
import static com.cheche365.cheche.cpic.util.CityUtils.getCityCode
import static com.cheche365.cheche.parser.util.BusinessUtils._INSURANCE_BASIC_INFO_EXTRACTOR
import static org.apache.commons.lang3.RandomUtils.nextInt as random
import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT



/**
 * 平安保险信息服务
 */
@TupleConstructor(
    includeFields = true
)
@Slf4j
class CpicInsuranceInfoService extends AThirdPartyInsuranceInfoService {

    private IThirdPartyDecaptchaService decaptchaService

    @Override
    final protected doCreateContext(env, area, auto, additionalParameters) {
        def cityCode = getCityCode(area.id) as Long
        def provinceCode = getProvinceCode cityCode
        def fakeIp = "121.40.13${random(0, 9)}.${random(10, 230)}"

        [
            client                              : new RESTClient(env.getProperty('cpic.base_url')).with {
                headers.put('X-Forwarded-For', fakeIp)
                headers.put('X-LocalAddress', fakeIp)
                client.params.setParameter(CONNECTION_TIMEOUT, env.getProperty('cpic.conn_timeout') as Integer)
                client.params.setParameter(SO_TIMEOUT, env.getProperty('cpic.so_timeout') as Integer)
                it
            },
            provinceCode                     : provinceCode as String,
            cityCode                         : cityCode as String,
            insuranceCompany                 : CPIC_25000,
            cityRpgMappings                  : _CITY_RPG_MAPPINGS,
            cityRhMappings                   : _CITY_RH_MAPPINGS,
            functionalDone                    : true,
            getVehicleOption                  : _CPIC_GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping : _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            cityInsuranceBasicInfoFlowMappings: _FLOW_INSURANCE_BASIC_INFO_FLOW_MAPPINGS,
            cityInsuranceInfoFlowMappings     : _FLOW_INSURANCE_INFO_FLOW_MAPPINGS,
            insuranceBasicInfoExtractor       : _INSURANCE_BASIC_INFO_EXTRACTOR.curry(_DATETIME_FORMAT3),
            vehicleInfoExtractor              : _VEHICLE_INFO_EXTRACTOR,
            decaptchaService                  : decaptchaService,
            extendedAttributes                : auto.autoType?.supplementInfo,
            supplementInfoSupportList         : [:],
        ]
    }

}
