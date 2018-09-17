package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._PAYMENT_URL_FORMAT_IMAGE_BASE64
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VERIFICATION_CODE_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters



/**
 * 选定支付方式后，进行支付
 * 在支付能顺利进行前，会进行2个前置必要条件判定
 * 1 投保人手机上点击确认
 * 2 校验验证码正确性
 * Created by chukh on 2018/5/22.
 */
@Component
@Slf4j
class Pay implements IStep {

    private static final _API_PATH_PAY = '/ecar/paymentQuery/pay'

    @Override
    run(Object context) {
        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_PAY,
            body              : [
                meta  : [:],
                redata: generateRequestParameters(context, this)
            ]
        ]

        log.info '请求体：\n{}', args.body
        //发送请求获取结果json数据
        def result = client.post args, { resp, json -> json }
        log.debug '提交支付的结果：{}', result
        if (result.message.code == 'success') {
            //支付二维码
            def qrCodeBase64 = result?.result?.twoDimensionCodeLink
            context.qrCodeBase64 = qrCodeBase64
            log.debug '成功获取支付二维码'
            def paymentNo = result.result.payNo
            def checkCode = result.result.paymentShowVos.checkCode
            def twoDimensionCodeLink = result.result.twoDimensionCodeLink
            log.info '支付二维码：{}，支付号:{}，验证码：{}', twoDimensionCodeLink, paymentNo, checkCode
            context.newPaymentInfo = [
                paymentURL: twoDimensionCodeLink,
                metaInfo  : [
                    paymentURLFormat: _PAYMENT_URL_FORMAT_IMAGE_BASE64,
                    paymentNo       : paymentNo,
                    checkCode       : checkCode,
                    //账号，web使用
                    accountId       : context.username
                ]
            ]
            //持久化核保完成的标示
            context.proposal_status = '核保流程完成'
            getContinueFSRV null
        } else {
            def message = result.message.message
            if (message.contains('电子投保单还未确认')) {
                log.error '电子投保单还未确认，请先在手机上点击确认'
                return getValuableHintsFSRV(context, [mergeMaps(_VALUABLE_HINT_VERIFICATION_CODE_TEMPLATE_INSURING.with {
                    it.hints = ['电子投保单还未确认，请先在手机上点击确认']
                    it
                }, [meta: [orderNo: context.order.orderNo]], [originalValue: context.additionalParameters.supplementInfo?.verificationCode])])
            }
            if (message.contains('该承保验证码平台未生成')) {
                log.error '验证码错误,请重新输入'
                return getValuableHintsFSRV(context, [mergeMaps(_VALUABLE_HINT_VERIFICATION_CODE_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]], [originalValue: context.additionalParameters.supplementInfo?.verificationCode])])
            }
            log.info '支付流程失败， 错误原因：{}', message
            getKnownReasonErrorFSRV message
        }
    }
}
