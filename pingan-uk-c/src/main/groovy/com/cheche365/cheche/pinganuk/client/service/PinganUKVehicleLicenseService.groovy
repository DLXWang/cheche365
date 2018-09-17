package com.cheche365.cheche.pinganuk.client.service

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.service.spi.IThirdPartyVehicleLicenseService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED

/**
 * 平安UK车型服务代理
 */
@Slf4j
@Service
class PinganUKVehicleLicenseService implements IThirdPartyVehicleLicenseService {

    @Autowired
    private Environment env

    @Autowired
    private RestTemplate restTemplate

    @Override
    VehicleLicense getVehicleLicense(Area area, Auto auto, Map additionalParameters) {
        def requestParams = [
            areaId        : [area.id as String],
            owner         : [auto.owner],
            identity      : [auto.identity],
            licensePlateNo: [auto.licensePlateNo]
        ] as LinkedMultiValueMap
        log.info '车型查询参数：{}', requestParams

        def gatewayHost = getEnvProperty env: env, 'ms.gateway.url'
        def url = "$gatewayHost/pinganuk/auto/vehiclelicense".toString()

        def entity = new HttpEntity(requestParams, new HttpHeaders(contentType: APPLICATION_FORM_URLENCODED))

        def vehicleLicense = restTemplate.postForEntity(url, entity, VehicleLicense).body
        log.info '车型查询结果：{}', vehicleLicense

        vehicleLicense
    }

}
