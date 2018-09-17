package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 校验新验证码
 * @author wangxiaofei
 */
@Component
@Slf4j
class VerifyNewCaptcha implements IStep {

    private static final _API_PATH_GET_TRAFFIC_INFORMATION = 'ecar/car/carModel/getrafficinformation'

    @Override
    run(context) {
        def client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_GET_TRAFFIC_INFORMATION,
            body              : [
                uniqueId : context.uniqueID,
                checkcode: context.captchaText,
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if ('0000' == result.errorCode) {
            log.info '成功校验验证码'
            context.trafficInfo = result
            getLoopBreakFSRV result
        } else {
            getLoopContinueFSRV result, '校验验证码失败，稍后重试'
        }
    }
}
