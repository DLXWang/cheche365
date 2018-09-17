package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.JSON

/**
 * 获取风险数据
 */
@Component
@Slf4j
class GetERiskData implements IStep {

    private static final _API_PATH_GET_ERISK_DATA = '/ecar/renewal/getERiskData'


    @Override
    run(context) {
        RESTClient client = context.client
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def renewalInfo = context.renewalInfo
        def args = [
            contentType : JSON,
            path        : _API_PATH_GET_ERISK_DATA,
            query       : [
                areaCode    : areaCode,
                cityCode    : cityCode,
                carPolicyNo : renewalInfo.renewalPolicyNo
            ]
        ]

        def result = client.get args, { resp, json ->
            json
        }

        log.info '风险数据：{}', result
        context.risk = result

        getContinueFSRV result

    }

}
