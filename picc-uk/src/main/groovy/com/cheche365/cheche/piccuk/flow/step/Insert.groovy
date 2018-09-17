package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.URLENC



/**
 * 保存
 */
@Component
@Slf4j
class Insert implements IStep {

    def _INSERT_URL_PATH_BY_CITY_CODE = [
        110000L: '/prpall/business/insert4S.do',
        default: '/prpall/business/insert.do'
    ]

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : HTML,
            path              : getObjectByCityCode (context.area, _INSERT_URL_PATH_BY_CITY_CODE),
            body              : generateRequestParameters(context, this)
        ]

        def insertArgs = args.clone()
        def result = client.post args, { resp, html ->
            html
        }

        if (!result.toString().contains('errorMessage')) {
            log.info '投保单号：{}', result
            def proposalNos = result.toString().split(',') as List
            context.proposalNos = context.proposalNos ?: []
            context.proposalNos << proposalNos
            context.proposalStatus = '去核保'
            context.insertArgs = insertArgs  // TODO 设置一下超时时间，这参数有点多
            getContinueFSRV result
        } else {
            log.error '保存报价失败：{}', result
            getFatalErrorFSRV result.text().tokenize('=').last()
        }
    }

}
