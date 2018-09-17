package com.cheche365.cheche.sinosig.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_2
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static groovyx.net.http.ContentType.ANY
import static groovyx.net.http.ContentType.URLENC



/**
 * 计算商业险基类
 */
@Component
@Slf4j
abstract class APremiumBI implements IStep {

    protected static final _API_PATH_PREMIUM_BI = 'Net/netPremiumControl!premiumBI.action'

    @Override
    run(context) {

        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_2
        // 交强险有可能报价成功，此处不能异常结束
        try {
            getResponseResult context
        } catch (e) {
            log.warn '商业险报价异常信息', e
            disableCommercial context, '商业险报价失败'
            getContinueWithIgnorableErrorFSRV false, '商业险报价失败'
        }
    }

    protected static getKindItemQuote(context, postBody) {
        def args = [
            requestContentType: URLENC,
            contentType       : ANY,
            path              : _API_PATH_PREMIUM_BI,
            body              : postBody
        ]

        def result = context.client.post args, { resp, json ->
            json
        }

        if ('0' != result.paraMap.suc) {
            [result.kindList, result.paraMap]
        } else {
            [null, null]
        }
    }

    protected abstract getResponseResult(context);
}
