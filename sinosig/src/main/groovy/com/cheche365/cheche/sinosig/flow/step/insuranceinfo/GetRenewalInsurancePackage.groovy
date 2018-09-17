package com.cheche365.cheche.sinosig.flow.step.insuranceinfo

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.cheche.parser.util.InsuranceUtils.afterGeneratedRenewalPackage
import static com.cheche365.cheche.sinosig.flow.util.BusinessUtils.getCommercialRequestParameters
import static com.cheche365.cheche.sinosig.flow.util.BusinessUtils.generateRenewalPackage
import static groovyx.net.http.ContentType.ANY
import static groovyx.net.http.ContentType.URLENC

/**
 * 计算商业险
 */
@Component
@Slf4j
class GetRenewalInsurancePackage implements IStep {

    private static final _API_PATH_PREMIUM_BI = 'Net/netPremiumControl!premiumBI.action'

    @Override
    run(context) {

        try {
            def (kindItemListInit, paraMap) = getKindItemQuote(context, getCommercialRequestParameters(context, 1, 2))

            //续保套餐检查
            if (_FLOW_TYPE_RENEWAL_CHANNEL == context.flowType && !context.insurancePackage && kindItemListInit && paraMap) {
                //刷新六要素，因为之前步骤拿到的六要素里面可能有*号
                context.selectedCarModel.frameNo = paraMap.FrameNo_ ?: context.selectedCarModel.frameNo
                context.selectedCarModel.engineNo = paraMap.EngineNo_ ?: context.selectedCarModel.engineNo
                context.insurancePackage = generateRenewalPackage(kindItemListInit, paraMap)
                log.info "续保车辆上年套餐：{}", context.insurancePackage
                afterGeneratedRenewalPackage context
            } else {
                getContinueWithIgnorableErrorFSRV null, '获取续保套餐失败'
            }
            getContinueFSRV null
        } catch (e) {
            getContinueWithIgnorableErrorFSRV null, '获取续保套餐非预期异常'
        }
    }

    private static getKindItemQuote(context, postBody) {
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

}
