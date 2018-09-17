package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_2
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.picc.flow.step.v2.util.BusinessUtils.getAllKindItems
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 按照指定险种的保额获取商业险
 */
@Component
@Slf4j
class CalculateBIForChangeItemKind implements IStep {

    private static final _API_PATH_CALCULATE_BI_FOR_CHANGE_ITEM_KIND = '/newecar/calculate/calculateBIForChangeItemKind'

    @Override
    run(context) {
        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_2

        RESTClient client = context.client

        def args = getRequestParams context

        def changedItemKinds = args.body.changeItemKind

        log.info '变更险种参数：changeItemKind={}', changedItemKinds

        def result = client.post args, { resp, json ->
            json
        }

        if ('成功' == result.resultMsg) {

            log.info '精准报价JSON：{}', result

            def allKindItems = getAllKindItems result.biviewmodel?.opt

            def qr = populateQuoteRecord context, allKindItems, context.kindItemConvertersConfig, 0
            qr.calculatePremium()

            getContinueFSRV qr

        } else {
            getFatalErrorFSRV '自主选择险种报价失败，请与客服联系'
        }
    }


    private getRequestParams(context) {
        [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_CALCULATE_BI_FOR_CHANGE_ITEM_KIND,
            body              : generateRequestParameters(context, this)
        ]
    }

}
