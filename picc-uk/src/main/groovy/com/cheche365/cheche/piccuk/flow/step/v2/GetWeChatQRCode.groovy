package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._PAYMENT_URL_FORMAT_IMAGE_BASE64
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC
import static org.apache.commons.codec.binary.Base64.encodeBase64String
import static org.apache.commons.io.IOUtils.toByteArray
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getPayTypeNo



/**
 * 获取微信支付二维码
 */
@Component
@Slf4j
class GetWeChatQRCode implements IStep {

    private static final _PAY_API_PRINT_TWO_BAR_CODE_SERVLET = '/cbc/PrintTwoBarCodeServlet'

    @Override
    Object run(Object context) {
        log.debug '获取微信支付二维码'

        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : BINARY,
            path              : _PAY_API_PRINT_TWO_BAR_CODE_SERVLET,
            query             : [
                code: URLDecoder.decode(context.wechatCodeUrl,'UTF-8'),
                seed: new Random().nextDouble()
            ]
        ]

        log.debug 'args {}', args
        def result = client.get args, { resp, is ->
            encodeBase64String toByteArray(is)
        }
        if(!result) {
            return getKnownReasonErrorFSRV('获取微信支付二维码图片失败')
        }

        log.debug 'result {}', result
        context.newPaymentInfo = [
            paymentURL: result,
            metaInfo  : [
                paymentURLFormat: _PAYMENT_URL_FORMAT_IMAGE_BASE64,
                paymentNo       : context.exchangeNo, // 交费通知单单号
                orderNo         : context.applyPolicyNos?.orderNo,
                //账号，web使用
                accountId       : context.username,
                //查询支付状态使用
                serialNo        : context.createWeChatInfo?.serialNo, //支付记录序号
                payType         : getPayTypeNo(context.payTypeNo) // wechat 9  wechatpublic  51

            ]
        ]
        getContinueFSRV result
    }
}
