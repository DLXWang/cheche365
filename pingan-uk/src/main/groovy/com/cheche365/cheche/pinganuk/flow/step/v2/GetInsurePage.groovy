package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.URLENC



/**
 * 跳转到新版投保页，必须首先到投保中心
 */
@Component
@Slf4j
class GetInsurePage implements IStep {

    private static final _URL_GET_INSURE_PAGE = '/ebusiness/frames/main_02.jsp'

    @Override
    run(Object context) {
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : HTML,
            path              : _URL_GET_INSURE_PAGE,
            query             : context.loginResult
        ]

        client.get args, { resp, html ->
            html
        }

        log.info '平安uk新版，获取投保中心页成功'
        getContinueFSRV '平安uk新版，获取投保中心页成功'

    }

}
