package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC


/**
 * 初始化险种信息
 */
@Component
@Slf4j
class InitKindInfo implements IStep {

    private static final _API_PATH_INIT_KIND_INFO = '/newecar/calculate/initKindInfo'

    @Override
    run(context) {
        def client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_INIT_KIND_INFO,
            body              : [
                uniqueID: context.uniqueID
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }
        if ('0000' == result.resultCode) {

            context.initKindInfo = result
            log.info '初始化险种成功'

            context.enableInsurancePackageList = result.items

            getContinueFSRV context.renewable
        } else {
            getFatalErrorFSRV '初始化险种失败，建议重试'
        }
    }

}
