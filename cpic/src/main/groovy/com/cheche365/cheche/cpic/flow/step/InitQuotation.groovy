package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.InsurancePackage
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.cheche.cpic.util.BusinessUtils._CHECHE_COVERAGES_V3
import static com.cheche365.cheche.cpic.util.BusinessUtils.addForcedInsurance
import static com.cheche365.cheche.cpic.util.BusinessUtils.generateCustInsurancePackage
import static com.cheche365.cheche.parser.util.InsuranceUtils.afterGeneratedRenewalPackage
import static com.cheche365.flow.core.util.FlowUtils.getFlowNotSupportFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 初始化报价 新步骤，目前只有重庆使用
 * Created by xushao on 2015/7/21.
 */
@Component
@Slf4j
class InitQuotation implements IStep {

    private static final _URL_PATH_INIT_QUOTATION = 'cpiccar/salesNew/quotation/initQuotation'

    @Override
    run(context) {
        RESTClient client = context.client
        def bodyContent = [
            random: context.baseInfoResult.random,
            orderNo:context.orderNo,
            otherSource:"02"
        ]
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _URL_PATH_INIT_QUOTATION,
            body              : bodyContent
        ]

        def result
        try {
            result = client.post args, { resp, json ->
                json
            }
        } catch (ex) {
            log.warn '初始化交强险异常：{}，尝试重试', ex.message
            return getLoopContinueFSRV (null, '初始化交强险异常')
        }

        if (result) {
            context.allCoverageInfo = result.allCoverageInfo
            context.preRules = result.preRules

            if (result.custCoverage) {
                context.renewable = true
                if (context.flowType == _FLOW_TYPE_RENEWAL_CHANNEL && !context.insurancePackage) {
                    context.insurancePackage = generateCustInsurancePackage context, _CHECHE_COVERAGES_V3, result.custCoverage
                    if (!context.insurancePackage) {
                        context.insurancePackage = new InsurancePackage(compulsory: true, autoTax: true, thirdPartyAmount: 100000, thirdPartyIop: true, damage: true, damageIop: true)
                    }
                    afterGeneratedRenewalPackage context
                    addForcedInsurance context
                }
            }

            if (!context.renewable && context.flowType == _FLOW_TYPE_RENEWAL_CHANNEL) {
                getFlowNotSupportFSRV '续保通道不支持非续保客户'
            } else {
                getLoopBreakFSRV result
            }
        } else {
            log.error '获取盗抢险保额失败，请稍后重试'
            getFatalErrorFSRV '获取盗抢险保额失败，请稍后重试'
        }
    }

}
