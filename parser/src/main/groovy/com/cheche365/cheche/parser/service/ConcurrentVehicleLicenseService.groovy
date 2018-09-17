package com.cheche365.cheche.parser.service

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.service.spi.IThirdPartyVehicleLicenseService
import com.cheche365.flow.core.service.TSimpleConcurrentService
import groovy.util.logging.Slf4j
import groovyx.gpars.group.PGroup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * 行驶证的并发包装服务
 * 用于异步执行所有真正的行驶证服务，这些服务被分类并赋予分类内部不同的优先级
 * TODO：重点关注高并发量时大量任务堆积导致无数超时及非预期抛出异常时的情况
 */
@Service
@Slf4j
class ConcurrentVehicleLicenseService implements IThirdPartyVehicleLicenseService, TSimpleConcurrentService {

    private static final _SERVICE_CONFIG_TEMPLATE = [
        // 免费服务
        0L: [
            priorityMappings: [
                piccUKVehicleLicenseService   : 0L,
                piccVehicleLicenseService     : 5L,
                pinganUKVehicleLicenseService : 10L,
                cpicVehicleLicenseService     : 20L,
                chinalifeVehicleLicenseService: 25L
            ]
        ],

        // 付费服务
        10L: [
            priorityMappings: [
                idcreditVehicleLicenseService: 0L,
            ]
        ],

        default: [
            options: [
                timeout: 5L // 获取行驶证的超时最长5秒
            ]
        ]
    ]

    private static final _JOB_BLUEPRINT_GET_VEHICLE_LICENSE = { name, service, area, auto, additionalParams ->
        [name, service.getVehicleLicense(area, auto, additionalParams)]
    }


    @Autowired(required = false)
    private Map<String, IThirdPartyVehicleLicenseService> services

    @Autowired
    @Qualifier('parserTaskPGroup')
    private PGroup parserTaskPGroup



    @Override
    VehicleLicense getVehicleLicense(Area area, Auto auto, Map additionalParameters) {
        log.debug '将要执行并发获取行驶证服务：{}，{}，{}，{}', services, area, auto, additionalParameters
        service(
            services,
            _SERVICE_CONFIG_TEMPLATE,
            _JOB_BLUEPRINT_GET_VEHICLE_LICENSE,
            [area, auto, additionalParameters],
            parserTaskPGroup
        ).with { results ->
            def vl = (results ? results[0][1] : null) as VehicleLicense
            log.debug '从并发服务中获取到的行驶证信息为：{}', vl
            vl
        }
    }

}
