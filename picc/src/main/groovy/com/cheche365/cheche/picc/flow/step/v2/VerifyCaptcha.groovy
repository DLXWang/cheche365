package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 校验续保用户的图片验证码
 */
@Component
@Slf4j
class VerifyCaptcha implements IStep {

    private static final _API_PATH_CHECK_RENEWAL = '/newecar/renewal/checkRenewal'

    @Override
    run(context) {
        def client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_CHECK_RENEWAL,
            body              : [
                random  : context.captchaText,
                tokenNo : context.auto.insuredIdNo ?: context.auto.identity,
                uniqueID: context.uniqueID
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result.resultFlag) {
            log.info '成功校验验证码'
            context.trafficInfo = result

            // 普通续保走true 快速续保走false
            getLoopBreakFSRV result.resultUrl.contains('CommonRenewalInfo')

        } else {
            getLoopContinueFSRV result, '校验验证码失败，稍后重试'
        }
    }

}
