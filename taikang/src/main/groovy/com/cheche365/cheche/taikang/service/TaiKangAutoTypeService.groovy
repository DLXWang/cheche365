package com.cheche365.cheche.taikang.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.parser.service.AThirdPartyAutoTypeService
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty

@Slf4j
class TaiKangAutoTypeService extends AThirdPartyAutoTypeService {

    @Override
    protected Object createContext(Object env, VehicleLicense vehicleLicense, Area area, Map additionalParameters) {
        def insuranceCompany = additionalParameters.insuranceCompany
        def newEnv = [env: env, area: area]
        [
            client                           : new RESTClient(env.getProperty('taikang.base_url')),
            cityCodeMappings                 : getCityCode(area),
            insuranceCompany                 : additionalParameters.insuranceCompany,
            outerCode                        : getEnvProperty(newEnv, "${insuranceCompany.id}.code".toString()),
            accountId                        : getEnvProperty(newEnv, "${insuranceCompany.id}.account_id".toString()),
            samCode                          : getEnvProperty(newEnv, "${insuranceCompany.id}.sam_code".toString()),
            cityAutoTypesFlowMappings        : _FLOW_CATEGORY_QUERY_VEHICLE_FLOW_MAPPINGS,
            vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                 : ''
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        return false
    }
}
