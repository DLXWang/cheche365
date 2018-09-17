package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getResponseResult
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 平安uk新版登陆接口
 */
@Component
@Slf4j
class VerifyLoginCaptcha implements IStep {

    private static final _API_PATH_LOGIN = '/cas/PA003/ICORE_PTS/auth.do'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_LOGIN,
            body              : [
                appId     : getEnvProperty(context, 'pinganuk.appId'),
                code      : context.loginCaptchaText,
                username  : context.username,
                password  : context.password,
                randCodeId: context.captcha.key
            ]
        ]

        def loginResult = client.post args, { resp, json ->
            json
        }

        if ('LOGIN_SUCCESS' == loginResult?.code) {
            getResponseResult loginResult, context, this
            getLoopBreakFSRV '平安uk登陆成功'
        } else {
            getLoopContinueFSRV loginResult, '平安uk登陆失败'
        }
    }

}
