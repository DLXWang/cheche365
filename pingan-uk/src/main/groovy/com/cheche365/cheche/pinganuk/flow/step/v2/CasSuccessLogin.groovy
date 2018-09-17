package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.URLENC

/**
 * 平安uk新版 跳转登陆首页
 */
@Component
@Slf4j
class CasSuccessLogin implements IStep {

    private static final _URL_CAS_SUCCESS_LOGIN = '/ebusiness/CAS_SUCCESS_LOGIN'

    @Override
    run(Object context) {
        RESTClient client = context.client
        client.uri = getEnvProperty(context, 'pinganuk.pst_host')

        def args = [
            requestContentType: URLENC,
            contentType       : HTML,
            path              : _URL_CAS_SUCCESS_LOGIN,
            query             : context.loginResult
        ]

        client.get args, { resp, html ->
            html
        }
        log.info '平安uk新版，跳转登陆首页成功'
        getContinueFSRV '平安uk新版，跳转登陆首页成功'

    }
}
