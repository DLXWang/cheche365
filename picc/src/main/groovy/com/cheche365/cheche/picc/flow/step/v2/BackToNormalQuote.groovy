package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC



/**
 * 续保失败，强制走转保
 */
@Component
@Slf4j
class BackToNormalQuote implements IStep {

    private static final _URL_BACK_TO_CAR_PAGE = '/newecar/proposal/backToCarPage'


    @Override
    run(context) {

        RESTClient client = context.client

        client.request(Method.POST) { req ->
            requestContentType = URLENC
            contentType = BINARY
            uri.path = _URL_BACK_TO_CAR_PAGE
            body = [
                backCarAction : 'changeToUnRenewal',
                uniqueID_sub : context.uniqueID
            ]

            response.success = { resp, stream ->
            }

            response.failure = { resp, reader ->
                log.warn '连接失败，建议重试'
            }
        }

        context.isRenewal = '0'
        context.renewable = false  // 续保标志
        context.reuseFlag = '0'
        context.historical = false  // 历史客户标志
        log.info '续保用户强制走转保流程，修改续保标志位'

        getContinueFSRV true
    }
}
