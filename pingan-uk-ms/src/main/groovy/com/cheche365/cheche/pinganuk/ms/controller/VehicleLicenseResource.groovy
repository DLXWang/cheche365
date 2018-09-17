package com.cheche365.cheche.pinganuk.ms.controller

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.service.spi.IThirdPartyVehicleLicenseService
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST API
 * Created by Huabin on 2016/9/11.
 */

@RestController('/auto/vehiclelicense')
@Slf4j
class VehicleLicenseResource {

    /**
     * 由于项目依赖关系，此处应该只能注入Pingan UK对应的行驶证服务
     */
    @Autowired
    @Qualifier('UNKNOWN_1')
//    @Qualifier('pinganUKService')
    private IThirdPartyVehicleLicenseService service


    @PostMapping
    @HystrixCommand(fallbackMethod = 'fallback')
    def vehicleLicense(@RequestParam Map formData) {
        log.info '根据：{} 查询车辆信息', formData

        def auto = new Auto(
            licensePlateNo: formData.licensePlateNo,
            identity: formData.identity,
            owner: formData.owner
        )
        def area = new Area(id: formData.areaId as int)

        def vehicleLicense = service.getVehicleLicense area, auto, [:]
        log.info '车辆信息：{}', vehicleLicense

        vehicleLicense
    }


    private fallback(Map formData) {
        log.warn 'VEHICLE LICENSE 服务不可用'

        null
    }

}
