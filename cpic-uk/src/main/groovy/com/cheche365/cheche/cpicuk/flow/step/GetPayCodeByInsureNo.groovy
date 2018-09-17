package com.cheche365.cheche.cpicuk.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV



/**
 * 根据支付号拉取二维码
 */
@Component
@Slf4j
class GetPayCodeByInsureNo extends QueryPaymentRecord {

    protected getApiParam(context) {
        //投保单号
        def insuranceNo = context.applyPolicyNos.commercial ?: context.applyPolicyNos.compulsory
        [policyNo: insuranceNo]
    }

    protected handlePaymentRecord(result, context) {
        def payNo = result?.result[0]?.payNo
        if (payNo) {
            context.payNo = payNo
            getContinueFSRV result
        } else {
            getKnownReasonErrorFSRV '获取支付单号失败'
        }
    }


}
