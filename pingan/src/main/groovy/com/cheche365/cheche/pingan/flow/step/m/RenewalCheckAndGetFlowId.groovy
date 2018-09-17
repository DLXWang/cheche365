package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.pingan.util.CityCodeMappings.getCityCode
import static groovyx.net.http.ContentType.TEXT



/**
 * 检查用户是否为续保用户并且获取flowId
 * Created by wangxin on 2015/11/4.
 */
@Component
@Slf4j
class RenewalCheckAndGetFlowId implements IStep {

    private static final _API_PATH_RENEWAL_CHECK = 'autox/do/api/renewal-check'

    @Override
    run(context) {
        def cityCode = getCityCode context.area.id
        Auto auto = context.auto
        RESTClient client = context.client
        def args = [
            contentType: TEXT,
            path       : _API_PATH_RENEWAL_CHECK,
            query      : [
                'department.cityCode' : cityCode,
                'vehicle.licenseNo'   : auto.licensePlateNo,
                'partner.mediaSources': 'SC03-Direct-00001',
                'partner.partnerName' : 'chexie-mobile',
            ]
        ]
        def result = client.get args, { resp, json ->
            context.__xrc = resp.headers.__xrc
            new JsonSlurper().with {
                type = JsonParserType.LAX
                parseText(json.readLines()[0])
            }
        }
        def code = result.resultCode
        if (code in ['C0000', 'C0001']) {
            context.renewable = code == 'C0001' ?: false
            context.flowId = result.flowId
            log.info '是否可以续保：{}，获取flowId：{}', context.renewable, result.flowId
            getContinueFSRV code == 'C0000' ? 1 : 0
        } else {
            log.info '该用户验证失败，响应消息为 {}', result
            getFatalErrorFSRV '用户验证失败'
        }
    }

}
