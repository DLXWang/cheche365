package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 查询费率信息
 */
@Component
@Slf4j
class QueryPayFor implements IStep {

    private static final _URL_PATH_QUERY_PAY_FOR = '/prpall/business/queryPayFor.do'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_PATH_QUERY_PAY_FOR,
            body              : generateRequestParameters(context, this)
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result && 1 == result.totalRecords) {
            context.prpDdismantleDetails = result.data[0].prpDdismantleDetails
            context.prpDpayForPolicies = result.data[0].prpDpayForPolicies
            log.info '费率查询成功，结果：{}', context.prpDpayForPolicies
            getContinueFSRV '费率查询成功'
        } else {
            log.error '费率查询失败'
            getKnownReasonErrorFSRV '费率查询失败'
        }
    }

}
