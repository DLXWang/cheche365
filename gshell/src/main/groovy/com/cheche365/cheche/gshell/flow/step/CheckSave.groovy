package com.cheche365.cheche.gshell.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC


/**
 * 上传身份证照片
 */
@Component
@Slf4j
class CheckSave implements IStep {

    private static final _API_PATH_SF_CHECK_SAVE = '/web/checkSave'

    @Override
    run(context) {
        RESTClient client = context.client
        def data = context.data
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_SF_CHECK_SAVE,
            body              : [
                name      : data?.name ?: context.additionalParameters.owner,
                cardnumber: data?.cardNumber ?: context.additionalParameters.identity,
                pingtai   : getEnvProperty(context, 'gshell.pingtai_pingan')
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }


        if (result?.status) {
            log.info '成功采集身份证信息'
            getContinueFSRV result
        } else {
            getFatalErrorFSRV '采集身份证信息失败'
        }
    }

}
