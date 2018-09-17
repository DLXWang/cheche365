package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 采集身份发送短信验证码
 * @author: lp
 * @date: 2018/4/23 21:45
 */
@Component
@Slf4j
class PickIdentitySendMsg implements IStep {

    private static final _API_PATH_PICK_IDENTITY_SEND_MSG = '/icore_pnbs/do/app/identityCollection/pickInfo'

    @Override
    run(context) {
        RESTClient client = context.client
        def applyPolicyNo = context.applyPolicyList.applyPolicyNo

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_PICK_IDENTITY_SEND_MSG,
            body              : [
                applyPolicyNos: applyPolicyNo
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result) {
            log.info '采集身份发送短信成功：applyPolicyNo：{}，msg：{}', applyPolicyNo, result.msg
            context.waitIdentityCaptcha = true
            getContinueFSRV result
        } else {
            log.error '采集身份发送短信失败：applyPolicyNo：{}，result：{}', applyPolicyNo, result
            getFatalErrorFSRV '采集身份发送短信失败'
        }

    }
}
