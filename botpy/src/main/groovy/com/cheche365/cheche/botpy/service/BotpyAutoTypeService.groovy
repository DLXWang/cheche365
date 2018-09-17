package com.cheche365.cheche.botpy.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.parser.service.AThirdPartyAutoTypeService
import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment

import static com.cheche365.cheche.botpy.flow.Constants._BOTPY_GET_VEHICLE_OPTION
import static com.cheche365.cheche.botpy.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.botpy.flow.FlowMappings._FLOW_CATEGORY_QUERY_VEHICLE_FLOW_MAPPINGS
import static com.cheche365.cheche.botpy.util.BusinessUtils.getCityCode
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLATFORM_BOTPY_11



/**
 * 金斗云车型列表服务
 */
@Slf4j
class BotpyAutoTypeService extends AThirdPartyAutoTypeService {

    BotpyAutoTypeService(Environment env) {
        this.env = env
    }


    @Override
    protected createContext(env, VehicleLicense vehicleLicense, Area area, Map additionalParameters) {
        def insuranceCompany = additionalParameters.insuranceCompany
        def newEnv = [env: env, area: area]

        [
            client                           : new RESTClient(env.getProperty('botpy.base_url')),
            cityCodeMappings                 : getCityCode(area),
            insuranceCompany                 : additionalParameters.insuranceCompany,
            appId                            : env.getProperty('botpy.app_id'),
            appKey                           : env.getProperty('botpy.app_key'),
            appVersion                       : env.getProperty('botpy.app_version'),
            outerCode                        : getEnvProperty(newEnv, "${insuranceCompany.id}.code".toString()),
            accountId                        : getEnvProperty(newEnv, "${insuranceCompany.id}.account_id".toString()),
            samCode                          : getEnvProperty(newEnv, "${insuranceCompany.id}.sam_code".toString()),
            cityAutoTypesFlowMappings        : _FLOW_CATEGORY_QUERY_VEHICLE_FLOW_MAPPINGS,
            vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                 : _BOTPY_GET_VEHICLE_OPTION
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        PLATFORM_BOTPY_11 == conditions.quoteSource
    }

    @Override
    String getDescription() {
        '金斗云车型查询接口'
    }

}
