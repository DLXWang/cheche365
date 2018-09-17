package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取订单号
 */
@Component
@Slf4j
class CancelSafeDefray implements IStep {

    private static
    final _API_PATH_CANCEL_SAFE_DEFRAY_PROPOSAL_FOR_START = '/ecar/payment/cancelSafeDefray'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = getRequestParameters(context)

        def result = client.post args, { resp, json ->
            json
        }

        if (1 == result?.status) {
            context.orderInfo = result
            log.info '获取订单号和秘钥成功：orderID：{}，key：{}', context.orderInfo?.orderId, context.orderInfo?.key
            getContinueFSRV true
        } else {
            log.error '获取订单号和秘钥失败，错误信息：{}', result
            getFatalErrorFSRV '获取订单号和秘钥失败'
        }

    }

    private static getRequestParameters(context) {
        [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_CANCEL_SAFE_DEFRAY_PROPOSAL_FOR_START,
            body              : [
                proposalno         : context.proposalInfo.ProposalnoBI,
                renewalflagForOM   : context.renewable,
                isrenewalForInsight: context.renewable,
                identifyNumber     : context.proposalInfo.identifyNumber
            ]
        ]
    }

}

