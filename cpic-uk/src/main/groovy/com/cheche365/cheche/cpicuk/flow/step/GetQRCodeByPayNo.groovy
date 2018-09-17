package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._PAYMENT_URL_FORMAT_IMAGE_BASE64
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 拉取二维码
 */
@Component
@Slf4j
class GetQRCodeByPayNo implements IStep {

    private static final _URL_GET_QRCODE = '/ecar/payment/paymentTwoDimension'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _URL_GET_QRCODE,
            body              : [
                meta  : [:],
                redata: [
                    payNo: context.payNo,
                ]
            ]
        ]
        log.debug '请求参数  ：{}', args
        def result = client.post args, { resp, is ->
            is
        }
        def rqCode = result?.result?.twoDimensionCodeLink
        if (rqCode) {
            log.debug '成功获取二维码 ：{}', rqCode

            context.newPaymentInfo = [
                paymentURL: rqCode,
                metaInfo  : [
                    paymentURLFormat: _PAYMENT_URL_FORMAT_IMAGE_BASE64,
                    paymentNo       : '',
                    checkCode       : '',
                    //账号，web使用
                    accountId       : context.username
                ]
            ]

            getContinueFSRV rqCode
        } else {
            getKnownReasonErrorFSRV '获取二维码失败'
        }
    }
}
