package com.cheche365.cheche.ccint.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.service.DoubleDBService
import com.cheche365.cheche.core.service.IOCRService
import com.cheche365.flow.core.service.TSimpleService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.ccint.flow.Constants._STATUS_CODE_CCINT_API_UPPER_LIMIT_ERROR
import static com.cheche365.cheche.ccint.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.ccint.flow.FlowMappings._FLOW_RECOGNIZE_VEHICLE_LICENSE_FLOW_MAPPINGS



/**
 * 合合行驶证信息识别服务
 * Created by liheng on 2017/3/16 016.
 */
@Service
@Slf4j
class CcintService implements IOCRService, TSimpleService {

    @Autowired
    private DoubleDBService dbService;

    @Override
    VehicleLicense getInformation(uriText, Map additionalParameters) {
        service createContext(com_cheche365_flow_core_service_TSimpleService__env, uriText, additionalParameters), 'vehicleLicense', '合合行驶证识别接口'
    }

    private createContext(env, uriText, additionalParameters) {
        [
            client                        : new RESTClient(env.getProperty('ccint.api_base_url')),
            env                           : env,
            failureStatus                 : _STATUS_CODE_CCINT_API_UPPER_LIMIT_ERROR,
            cityVehicleLicenseFlowMappings: _FLOW_RECOGNIZE_VEHICLE_LICENSE_FLOW_MAPPINGS,
            vehicleInfoExtractor          : _VEHICLE_INFO_EXTRACTOR,
            dbService                     : dbService,
            imageFile                     : new File(new URI(uriText)),
            additionalParameters          : additionalParameters ?: [:]
        ]
    }

}
