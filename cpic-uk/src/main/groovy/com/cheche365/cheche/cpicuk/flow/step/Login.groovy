package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取登录页面，主要是取cookie
 */
@Component
@Slf4j
class Login implements IStep {

    private static final _URL_GET_LOGIN_HTML = '/ecar/view/portal/page/common/login.html'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : HTML,
            path              : _URL_GET_LOGIN_HTML
        ]

        log.info '太平洋url：{}', client.defaultURI

        def result = client.get args, { resp, html ->
            html
        }

        if (result) {
            log.info '成功获取登录页面html'
            getContinueFSRV null
        } else {
            getLoopContinueFSRV result, '获取登录页面html失败'
        }
    }

}
