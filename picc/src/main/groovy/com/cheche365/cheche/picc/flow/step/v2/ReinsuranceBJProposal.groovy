package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.parser.Constants.get_DATE_FORMAT1
import static com.cheche365.cheche.parser.util.BusinessUtils.getHtmlParser
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC



/**
 * 重新获取北京流程信息
 * 目前只发现初登日期，查车步骤返回的初登日期为当前日期，从此处获取初登日期较为合理
 * 该步骤放置在查车步骤之后
 */
@Component
@Slf4j
class ReinsuranceBJProposal implements IStep {

    private static final _API_PATH_REINSURANCE_BJ_PROPOSAL = '/newecar/proposal/reinsuranceBJProposal'
    private static final _BJ_PROPOSAL_PROPOSAL_NAMES = [
        'enrollDate'
    ]

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : BINARY,
            path              : _API_PATH_REINSURANCE_BJ_PROPOSAL,
            body              : [
                uniqueID    : context.uniqueID
            ]
        ]

        def result = client.post args, { resp, stream ->
            def inputs = htmlParser.parse(stream).depthFirst().INPUT

            inputs.findResults { input ->
                if (input.@id in _BJ_PROPOSAL_PROPOSAL_NAMES) {
                    [(input.@id): input.@value]
                }
            }.sum()
        }

        if (result?.enrollDate) {
            context.auto.enrollDate = _DATE_FORMAT1.parse(result?.enrollDate)
            log.info '获取初登日期为：{}', result?.enrollDate
            getContinueFSRV result
        } else {
            log.error '获取初登日期失败'
            getContinueWithIgnorableErrorFSRV result, '获取初登日期失败'
        }
    }
}
