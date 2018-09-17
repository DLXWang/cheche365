package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.Constants._STATUS_CODE_KNOWN_REASON_INSURING_ERROR
import static com.cheche365.cheche.picc.flow.Constants._STATUS_CODE_PICC_CHECK_POLICY_FAILURE
import static groovyx.net.http.ContentType.JSON


/**
 * 检查承保政策
 */
@Component
@Slf4j
class CheckPolicy implements IStep {

    private static final _API_PATH_CHECK_POLICY = '/ecar/underwrite/underwrite/underwriteProposalOrgXML'



    @Override
    run(context) {
        RESTClient client = context.client

        def args = createRequestParams context

        // 检查承保政策
        def result = client.post args, { resp, json ->
            json
        }

        log.info '承保政策检查结果：{}', result

        if ('1' == result.uwStatus as String) {
            getContinueFSRV result
        } else {
            def errorMsg = result.errorMsg
            errorMsg = errorMsg.substring(errorMsg.indexOf('<br/>'), errorMsg.lastIndexOf('<br/>'))
            def errorMsgList = errorMsg.tokenize('<br/>')
            if (errorMsgList.size > 1) {
                def hints = errorMsgList[0..errorMsgList.size() - 2].inject { sum, it -> sum + it }
                [_ROUTE_FLAG_DONE, _STATUS_CODE_KNOWN_REASON_INSURING_ERROR, null, hints]
            } else {
                [_ROUTE_FLAG_DONE, _STATUS_CODE_PICC_CHECK_POLICY_FAILURE, null, result.errorMsg]
            }
        }
    }

    private createRequestParams(context) {
        [
            contentType : JSON,
            path        : _API_PATH_CHECK_POLICY,
            query       : generateRequestParameters(context, this)
        ]
    }

}
