package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.EncoderRegistry
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component
import sun.misc.BASE64Encoder

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._PAYMENT_URL_FORMAT_IMAGE_BASE64
import static com.cheche365.cheche.parser.util.BusinessUtils.getHtmlParser
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC

/**
 * 平安uk新版跳转支付页面，并获取二维码
 */
@Component
@Slf4j
class JumpPaymentPage implements IStep {

    private static final _URL_QRCODE_PREFIX = '/epcis_nps'
    private static final _URL_JUMP_PAYMENT_PAGE = _URL_QRCODE_PREFIX + '/newNpsPay.do'

    @Override
    run(context) {
        def domainName = getEnvProperty(context, 'pinganuk.pay_host')
        RESTClient client = context.client.with {
            encoderRegistry = new EncoderRegistry(charset: 'GBK')
            uri = domainName
            it
        }

        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _URL_JUMP_PAYMENT_PAGE,
            body              : buildRequestParam(context)
        ]

        def qrCodeBase64 = client.post args, { resp, stream ->
            def images = htmlParser.parse(stream).depthFirst().IMG
            getImagesBase64(domainName + _URL_QRCODE_PREFIX + images[0].@src.substring(1))
        }

        if (qrCodeBase64) {
            log.info '保单号：{}，成功跳转到支付页面，并获取支付二维码：{}', context.policyInfo, qrCodeBase64
            context.newPaymentInfo = [
                paymentURL: qrCodeBase64,
                metaInfo  : [
                    paymentURLFormat: _PAYMENT_URL_FORMAT_IMAGE_BASE64,
                    paymentNo       : context.paymentInfo.noticeNo
                ]
            ]
            getContinueFSRV qrCodeBase64
        } else {
            log.error '保单号：{}，跳转支付页面失败', context.policyInfo
            getFatalErrorFSRV '跳转支付页面失败'
        }
    }

    /**
     * 构建拉起支付参数
     * @param context
     * @return
     */
    def buildRequestParam(context) {
        def jumpPaymentPageInfo = context.jumpPaymentPageInfo
        jumpPaymentPageInfo.subMap([
            'businessType',
            'businessNo',
            'customerName',
            'currencyNo',
            'amount',
            'regionCode',
            'insuredName',
            'tellerNo',
            'branchNo',
            'documentNo',
            'callBackInfo',
            'certPubKey',
            'signData',
            'callBackURL',
            'applicantName',
            'applicantCertificateType',
            'applicantCertificateNo',
            'ncpInsuredName',
            'insuredCertificateType',
            'insuredCertificateNo',
            'prepaidAccountId',
            'dataSource',
            'payType',
            'prepaidAccountType',
            'applicantTelephone',
            'channelSource',
            'businessTranChnl',
            'businessTranCode',
            'prePayAmount',
            'isContainTax',
            'vehicleNum',
            'bargainNum',
            'departmentName',
            'operationByName',
            'inputByName',
            'undrContractName',
            'payNo',
            'payMode',
            'paBankAccount',
            'paBankAccountName',
            'vehicleTaxCountStr',
            'collectAmountStr',
            'paymentEndDate',
            'circPaymentNo',
            'printFlag',
            'departmentCode',
            'isSocialSecurityPay',
            'socialSecurityPayInfo',
            'customerCertType',
            'customerCertId',
            'isPrintOnly',
            'userName'
        ]) + [
            'networkFlag': '02' //此参数只能是02，如果使用上一步的值，将导致不能获取二维码
        ]
    }


    def getImagesBase64(imgUrl) {
        log.info '影像url：{}', imgUrl
        new BASE64Encoder().encode(new URL(imgUrl).bytes)
    }


}
