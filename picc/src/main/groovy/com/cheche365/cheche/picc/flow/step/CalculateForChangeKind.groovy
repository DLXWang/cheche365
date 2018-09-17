package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_2
import static com.cheche365.cheche.parser.Constants._QUOTE_KIND_NAME_COMMERCIAL
import static com.cheche365.cheche.parser.util.BusinessUtils.countCanInsureDays
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.picc.util.BusinessUtils.getActualStartDate
import static com.cheche365.cheche.picc.util.BusinessUtils.getEarlyDays4Commercial
import static com.cheche365.cheche.picc.util.BusinessUtils.populateQuoteRecord
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 按照指定险种的保额获取商业险
 */
@Component
@Slf4j
class CalculateForChangeKind implements IStep {

    private static final _API_PATH_CALCULATE_FOR_CHANGE_KIND = '/ecar/caculate/caculateForChangeKind'

    @Override
    run(context) {
        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_2

        def (accurateQuote, packageName) = retrieveAccurateQuote(context)

        if ('成功' == accurateQuote.errorMsg) {

            log.info '精准报价JSON：{}', accurateQuote

            def quoteRecord = populateQuoteRecord accurateQuote, packageName, context
            log.info '组装后的新QuoteRecord：{}', quoteRecord

            //未到期且拿到了起保日期，计算距窗口期的时间，并放入quoteFieldStatus中
            countCanInsureDays(
                context,
                'commercial',
                _QUOTE_KIND_NAME_COMMERCIAL,
                getActualStartDate(context),
                getEarlyDays4Commercial(context))

            getContinueFSRV quoteRecord

        } else {
            getFatalErrorFSRV '未通过保险公司审核，请与客服联系'
        }
    }

    private retrieveAccurateQuote(context) {
        RESTClient client = context.client

        def args = createRequestParams context

        def changedItemKinds = args.body.changeItemKind
        def packageName = args.body.'prpcmain.packageName'
        log.debug '变更险种参数：changeItemKind={}', changedItemKinds

        def accurateQuote = client.post args, { resp, json ->
            json
        }

        [accurateQuote, packageName]
    }

    private createRequestParams(context) {
        [
            requestContentType  : URLENC,
            contentType         : JSON,
            path                : _API_PATH_CALCULATE_FOR_CHANGE_KIND,
            body                : generateRequestParameters(context, this)
        ]
    }

}
