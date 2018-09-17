package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * @author: lp
 * @date: 2018/5/30 9:58
 * 作废支付订单
 */
@Component
@Slf4j
class NullifyPaymentOrder implements IStep {

    private static final _API_NULLIFY_PAYMENT_ORDER = '/ecar/paymentrecord/nullify'

    @Override
    run(Object context) {
        RESTClient client = context.client
        def cpicPaymentInfo = context.cpicPaymentInfo

        if ('2' == cpicPaymentInfo.payStatus)
            cpicPaymentInfo.put('payStatus', '待支付')

        if ('5' == cpicPaymentInfo.status)
            cpicPaymentInfo.put('status', '核保通过')

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_NULLIFY_PAYMENT_ORDER,
            body              : [
                meta  : [:],
                redata: [
                    payments: [cpicPaymentInfo]
                ]
            ]
        ]
        log.info '请求体：\n{}', args.body

        def result = client.post args, { resp, json -> json }

        if ('success' == result.message.code) {
            log.debug '支付号：{}，保单号：{}，作废订单成功，平台返回：{}', context.additionalParameters.paymentNo, context.applyPolicyNos, result
            getContinueFSRV context.message
        } else {
            log.error '支付号：{}，保单号：{}，作废订单失败，平台返回：{}', context.additionalParameters.paymentNo, context.applyPolicyNos, result
            getFatalErrorFSRV '作废订单失败！'
        }
    }


}
