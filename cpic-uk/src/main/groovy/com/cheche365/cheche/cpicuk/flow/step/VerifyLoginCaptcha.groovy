package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getFailedNoticeFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 登录
 */
@Component
@Slf4j
class VerifyLoginCaptcha implements IStep {

    private static final _API_PATH_J_SPRING_SECURITY_CHECK = '/ecar/j_spring_security_check'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_J_SPRING_SECURITY_CHECK,
            body              : [
                j_password : context.password,
                j_username : context.username,
                verify_code: context.captchaText
            ],
            headers           : [
                'X-Requested-With': 'XMLHttpRequest'
            ]
        ]
        log.debug '登录请求参数 {}', args
        def result = client.post args, { resp, json ->
            json
        }

        if ('true' == result?.authentication) {
            log.info '成功校验登录验证码'
            getLoopBreakFSRV result
        } else if ('false' == result?.authentication && '1' == result?.errCode) {
            //errCode = 1，表示账号或密码错误；errCode = 3，表示验证码错误。
            log.info '登录失败，太平洋返回：{}', result.errMsg
            def errorMsg = '小鳄鱼太平洋登录失败，账号或密码错误，返回信息：' + result.errMsg + ',地区：' +
                context.area?.name + ',username:' + context.username + ',password:' + context.password
            getFailedNoticeFSRV 'crocodile', errorMsg
        } else {
            getLoopContinueFSRV result, '没有获取验证码'
        }
    }

}
