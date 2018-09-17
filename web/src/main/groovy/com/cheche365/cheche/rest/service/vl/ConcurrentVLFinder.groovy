package com.cheche365.cheche.rest.service.vl

import com.cheche365.cheche.common.util.CollectionUtils
import com.cheche365.cheche.core.model.InsuranceInfo
import com.cheche365.cheche.core.service.spi.IVehicleLicenseFinder
import com.cheche365.flow.core.service.TSimpleConcurrentService
import groovy.util.logging.Slf4j
import groovyx.gpars.group.PGroup
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
@Slf4j
class ConcurrentVLFinder implements TSimpleConcurrentService {

    private static final _SERVICE_CONFIG_TEMPLATE = [
        0L     : [
            priorityMappings: [:]
        ],
        default: [
            options: [
                timeout       : 30L,   // 获取到阶段性的行驶证信息超时最多20秒
                maxResultCount: 0L
            ]
        ]
    ]

    private static final _JOB_BLUEPRINT_GET_VL = { mdcContext, name, service, licensePlateNo, owner ->
        MDC.contextMap = mdcContext
        [name, service.find(licensePlateNo, owner)]
    }

    @Autowired(required = false)
    List<IVehicleLicenseFinder> finders

    @Autowired
    @Qualifier('parserTaskPGroup')
    private PGroup parserTaskPGroup

    Map<String, InsuranceInfo> find(String licensePlateNo, String owner) {

        def finderServices = finders.collectEntries{[(it.getClass().getSimpleName()):it]}
        log.debug '将要执行并发调取行驶证查询服务：{}，{}', licensePlateNo, owner
        def mdcContext = MDC.copyOfContextMap
        def priorityMappings = finderServices.keySet().withIndex().collectEntries { name, index -> [(name): index] }
        def serviceConfig = CollectionUtils.mergeMaps(_SERVICE_CONFIG_TEMPLATE.clone(),
            [
                0L     : [priorityMappings: priorityMappings],
                default: [options: [maxResultCount: finders.size()]]
            ]
        )
        service(
            finderServices,
            serviceConfig,
            _JOB_BLUEPRINT_GET_VL.curry(mdcContext),
            [licensePlateNo, owner],
            parserTaskPGroup
        ).with { results ->
            results.collectEntries { [(it[0]): (it[1] as InsuranceInfo)] }
        }
    }
}
