package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 校验验证码
 * Created by sufc on 2017/8/4.
 */
@Component
@Slf4j
class VerificationForInsureQuery implements IStep {

    private static final _API_PATH_PROPOSAL_VERIFICATION_FOR_INSURE_QUERY = '/newecar/proposal/verificationForInsureQuery'

    @Override
    def run(Object context) {
        def client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_PROPOSAL_VERIFICATION_FOR_INSURE_QUERY,
            body              : [
                checkcodebi: context.captchaText,
                uniqueID   : context.uniqueID
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if ('0000' == result.resultCode) {
            log.info '成功校验验证码'
            getLoopBreakFSRV true
        } else if ('1000_E' == result.resultCode) {
            log.info '成功校验验证码,但与交管所校验车型信息自动修正！'
            context.platFormModelCode = result.platFormModelCode
            getLoopBreakFSRV false
        } else if ('1000_CHECK' == result.resultCode) {
            context.imageBase64 = result.checkCode
            getLoopContinueFSRV result, '校验验证码失败，稍后重试'
        }

    }

}
