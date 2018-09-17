package com.cheche365.cheche.cpicuk.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV



/**
 * 根据报价单号获取支付单号
 */
@Component
@Slf4j
class GetPaymentRecord extends QueryPaymentRecord {

    private quotationNo;

    protected getApiParam(context) {
        quotationNo = context.quotationNo
        [quotationNo: quotationNo]
    }

    protected handlePaymentRecord(result, context) {
        def paymentInfo = result?.result.find {
            it.quotationNo == quotationNo
        }

        def payNo = paymentInfo?.payNo
        if (payNo) {
            context.payNo = payNo
            getContinueFSRV result
        } else {
            getKnownReasonErrorFSRV '获取支付单号失败'
        }
    }


}
