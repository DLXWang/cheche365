package com.cheche365.cheche.sinosig.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.sinosig.flow.util.CityCodeMappings
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取城市相关的机构信息
 */
@Component
@Slf4j
class TravelCity implements IStep {

    private static final _API_PATH_TRAVEL_CITY = 'Net/travelCity.action'

    @Override
    run(context) {

        log.info '初始套餐：{}',context.insurancePackage
        log.info '初始套餐副本：{}',context.accurateInsurancePackage


        def orgId = CityCodeMappings._AREA_ORG_MAPPING[context.area.id]
        if (orgId) {
            context.orgId = orgId
            log.debug '从mapping中获取机构id：{}', orgId
            return getContinueFSRV(orgId)
        }
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_TRAVEL_CITY,
            body              : [
                q       : context.area.name,
                queryCon: context.area.name
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        def content = result.content[0]
        orgId = content.id
        log.debug '从官网的接口获取机构信息：{},{},{}', orgId, content.ContName, content.cityPlate
        context.orgId = orgId

        getContinueFSRV orgId
    }

}
