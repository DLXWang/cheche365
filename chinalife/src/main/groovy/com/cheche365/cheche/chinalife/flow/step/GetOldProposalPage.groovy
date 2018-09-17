package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.chinalife.flow.Constants._STATUS_CODE_CHINALIFE_RENEW_FAILURE
import static com.cheche365.cheche.chinalife.util.BusinessUtils.generateRenewalPackage
import static com.cheche365.cheche.chinalife.util.BusinessUtils.validateCheckCode
import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.cheche.parser.util.InsuranceUtils.afterGeneratedRenewalPackage
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取续保套餐
 */
@Component
@Slf4j
class GetOldProposalPage implements IStep {
    private static final _GET_RENEWAL_PACKAGE = '/online/saleNewCar/carProposalshowOldProposalPage.do'

    @Override
    run(Object context) {
        RESTClient client = context.client
        def args = getRequestParams context

        def result = client.post args, { resp, json ->
            json
        }
        /*
         * 当返回的result.temporary.resultType等于4时表示需要前端计算校验码并返回.
         * 国寿财官网的网页未校验验证码直接报错,故将此段代码也放到破解验证码的前边.
         * 只要在获取续保套餐节点返回校验码,就不破解校验码直接返回'获取续保套餐resultType不成功'
         */
        if (!(result?.temporary?.resultType in ['0','2','3'])) { // 0 表示成功. 2 表示交强险保单重复,但仍能报价.
            return [_ROUTE_FLAG_DONE, _STATUS_CODE_CHINALIFE_RENEW_FAILURE, null, '获取续保套餐resultType不成功']
        }

        //破解验证码
        def getRequestParamsClosure = { answer, bsDemandNo, bzDemandNo ->
            args = getRequestParams context
            args.body += [
                'temporary.quoteMain.busChangeCheckCodeFlag' : 0,
                'temporary.quoteMain.busRenewalFlag'         : '1',
                'temporary.quoteMain.demandNo'               : bsDemandNo,
                'temporary.quoteMain.busCheckCode'           : answer
            ]
            args
        }
        result = validateCheckCode(result, getRequestParamsClosure, { Map requestParams ->
            client.post requestParams, { resp, json ->
                json
            }
        })

        def quoteItemKinds = result.temporary?.quoteMain?.geQuoteItemkinds

        if (quoteItemKinds) {

            if (_FLOW_TYPE_RENEWAL_CHANNEL == context.flowType && !context.insurancePackage) {
                context.insurancePackage = generateRenewalPackage quoteItemKinds
                log.info '续保套餐 insurancePackage：{}', context.insurancePackage
                afterGeneratedRenewalPackage context
            }

            getContinueFSRV true
        } else {
            getContinueWithIgnorableErrorFSRV true, '获取续保套餐失败'
        }
    }

    private getRequestParams(context) {
        [
            requestContentType : URLENC,
            contentType        : JSON,
            path               : _GET_RENEWAL_PACKAGE,
            body               : generateRequestParameters(context, this)
        ]
    }
}
