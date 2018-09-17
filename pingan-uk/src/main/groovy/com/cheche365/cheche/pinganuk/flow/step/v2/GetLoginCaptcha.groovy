package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_1
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.getImageBase64
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 平安uk获取验证码
 */
@Component
@Slf4j
class GetLoginCaptcha implements IStep {

    private static final _API_PATH_GET_RAND_CODE = '/cas/genRandCode'

    @Override
    run(context) {
        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_1
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_GET_RAND_CODE,
            body              : [
                appId: getEnvProperty(context, 'pinganuk.appId')
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result) {
            log.info '平安uk新版，获取验证码成功'
            context.captcha = result.content
            context.imageBase64 = getImageBase64(result.content.data)
            getContinueFSRV result.content
        } else {
            getFatalErrorFSRV '平安uk新版，获取验证码失败'
        }

    }

}
