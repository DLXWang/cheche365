package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 保存保单信息
 */
@Component
@Slf4j
class SaveProposal implements IStep {

    private static
    final _API_PATH_SAVE_PROPOSAL_FOR_START = '/ecar/proposal/saveProposal'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_SAVE_PROPOSAL_FOR_START,
            body              : generateRequestParameters(context, this)
        ]

        def result = client.post args, { resp, json ->
            json
        }
        if (1 == result?.status) {
            log.info '保存订单信息成功，返回结果为：{}', result
            context.proposalInfo = result
            getContinueFSRV true
        } else {
            log.error '保存订单信息失败，错误信息：{}', result.errorMsg
            getFatalErrorFSRV '保存订单信息失败，可能需要补充信息'
        }
    }

}

