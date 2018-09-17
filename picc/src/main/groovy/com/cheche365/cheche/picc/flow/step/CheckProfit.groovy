package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.ANY
import static groovyx.net.http.ContentType.URLENC

/**
 * 注册UniqueID到核保
 */
@Component
@Slf4j
class CheckProfit implements IStep {

    private static final _API_PATH_CHECK_PROFIT = '/ecar/underwrite/underwrite/underwriteCheckProfit'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType  : URLENC,
            contentType         : ANY,
            path                : _API_PATH_CHECK_PROFIT,
            body                : generateRequestParameters(context, this)
        ]

        log.debug '用下列请求参数注册UniqueID到核保流程：{}', args

        def result = client.post args, { resp, data ->
            data
        }
        log.info '注册UniqueID到核保流程的结果：{}', result

        if ('3' == result[1]) {
            getContinueFSRV result
        } else {
            getFatalErrorFSRV '自动核保未通过'
        }
    }

}
