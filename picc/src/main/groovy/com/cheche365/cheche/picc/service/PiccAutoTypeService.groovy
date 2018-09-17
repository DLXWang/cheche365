package com.cheche365.cheche.picc.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.parser.service.AThirdPartyAutoTypeService
import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment

import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLATFORM_BOTPY_11
import static com.cheche365.cheche.picc.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.picc.flow.Constants._PICC_GET_VEHICLE_OPTION
import static com.cheche365.cheche.picc.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.picc.flow.FlowMappings._FLOW_CATEGORY_QUERYVECHILE_FLOW_MAPPINGS
import static com.cheche365.cheche.picc.util.CityCodeMappings._CITY_CODE_MAPPINGS
import static java.util.UUID.randomUUID



@Slf4j
class PiccAutoTypeService extends AThirdPartyAutoTypeService {

    PiccAutoTypeService(Environment env) {
        this.env = env
    }


    @Override
    protected createContext(env, VehicleLicense vehicleLicense, Area area, Map additionalParameters) {
        def cityCodeMapping = getObjectByCityCode area, _CITY_CODE_MAPPINGS

        [
                client                           : new RESTClient(env.getProperty('picc.base_url')),
                area                             : area,
                areaCode                         : cityCodeMapping.areaCode,
                cityCode                         : cityCodeMapping.cityCode,
                cityAutoTypesFlowMappings        : _FLOW_CATEGORY_QUERYVECHILE_FLOW_MAPPINGS,
                supplementInfoMapping            : _CITY_SUPPLEMENT_INFO_MAPPINGS,
                getVehicleOption                 : _PICC_GET_VEHICLE_OPTION,
                vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
                entryId                          : randomUUID().toString(),
                newCarFlag                       : additionalParameters.newCarFlag,
                additionalParameters             : additionalParameters
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        PLATFORM_BOTPY_11 != conditions.quoteSource
    }

    @Override
    String getDescription() {
        '人保车型查询接口'
    }

}
