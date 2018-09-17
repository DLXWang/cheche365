package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取保险周期，通常和GetReubsurance获取的周期同时使用
 */
@Component
@Slf4j
class GetCheckPeriod implements IStep {

    private static final _API_PATH_GET_CHECK_PERIOD = '/ecar/proposal/proposalPeriodCheckNew'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType  : URLENC,
            contentType         : JSON,
            path                : _API_PATH_GET_CHECK_PERIOD,
            body                : generateRequestParameters(context, this)
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result.message) {
            context.requiredRefreshYearAndPrice = true
        }

        log.debug '获取保险周期结果：{}', result
        context.period = result
        getContinueFSRV result
    }
}
