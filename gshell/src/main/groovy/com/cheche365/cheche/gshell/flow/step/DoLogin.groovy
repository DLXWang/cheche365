package com.cheche365.cheche.gshell.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.Method.POST



/**
 * 登录
 */
@Component
@Slf4j
class DoLogin implements IStep {

    private static final _API_PATH_DO_LOGIN = '/web/doLogin'

    @Override
    run(context) {
        RESTClient client = context.client

        client.request(POST) { req ->
            uri.path = _API_PATH_DO_LOGIN
            requestContentType = URLENC
            contentType = HTML
            body = [
                name: getEnvProperty(context, 'gshell.login_name'),
                pwd : getEnvProperty(context, 'gshell.password'),
                code: context.captchaText
            ]

            response.success = { resp ->
                if ('302' as int == resp.status) {
                    log.info '悟空采集登录成功'
                    getLoopBreakFSRV null
                } else {
                    log.error '悟空系统验证码识别失败，请稍后重试'
                    getLoopContinueFSRV null, '悟空系统验证码识别失败，请稍后重试'
                }
            }
            response.failure = { resp ->
                log.error '悟空采集系统登录失败，请稍后重试'
                getLoopContinueFSRV null, '系统登录失败, 请稍后重试'
            }
        }
    }

}
