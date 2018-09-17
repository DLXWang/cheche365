package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 支付人员 - 杭州
 * 提交核保 -> 查询支付 ->  { 支付人员 }->支付
 *
 */
@Component
@Slf4j
class PaymentRecordMan implements IStep {

    private static final _API_PATH_PAYMENT_RECORD_MAN = '/ecar/payment/paymentRecordMan'

    @Override
    run(context) {
        RESTClient client = context.client
        def payments = context.payments
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_PAYMENT_RECORD_MAN,
            body              : [
                meta  : [:],
                redata: [
                    payments: payments
                ]
            ]
        ]
        log.info '请求体：\n{}', args.body
        //发送请求获取结果json数据
        def result = client.post args, { resp, json -> json }
        log.debug '提交RecordMan的结果：{}', result
        if (result.message.code == 'success') {
            log.debug '提交成功'

            getContinueFSRV result
        } else {
            def message = result.message.message
            getKnownReasonErrorFSRV message

        }
    }
}
