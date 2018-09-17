package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._PAYMENT_STATUS_FAIL
import static com.cheche365.cheche.core.constants.ModelConstants._PAYMENT_STATUS_PROCESSING
import static com.cheche365.cheche.core.constants.ModelConstants._PAYMENT_STATUS_SUCCESS
import static groovyx.net.http.ContentType.JSON



/**
 * @author: lp
 * @date: 2018/5/30 9:58
 * 查询多个支付信息
 */
@Component
@Slf4j
class CheckPaymentState implements IStep {

    private static final _API_CHECK_PAYMENT_STATE = '/ecar/paymentrecord/query'

    @Override
    run(Object context) {
        def paymentInfos = context.paymentInfos
        def paymentResult = paymentInfos.collect {
            postRequest(context, it)
        }
        context.newCheckPaymentState = paymentResult
        getContinueFSRV paymentResult
    }

    /**
     * 发送请求
     * @param context
     * @param paymentInfo
     * @return
     */
    private static postRequest(context, paymentInfo) {
        if (!paymentInfo.paymentNo) {
            return _CHECK_PAYMENT_RESULT(paymentInfo.orderNo, _PAYMENT_STATUS_FAIL, '支付号为空', null, null)
        }
        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_CHECK_PAYMENT_STATE,
            body              : [
                meta  : [:],
                redata: [
                    payNo: paymentInfo.paymentNo
                ]
            ]
        ]
        log.info '请求体：\n{}', args.body

        def result = client.post args, { resp, json -> json }

        if ('success' == result.message.code) {
            log.info '支付号：{}，查询太平洋支付接口成功，平台返回：{}', paymentInfo.paymentNo, result
            return buildResult(paymentInfo, result.result)
        } else {
            log.error '支付号：{}，查询太平洋支付接口失败，平台返回：{}', paymentInfo.paymentNo, result
            return _CHECK_PAYMENT_RESULT(paymentInfo.orderNo, _PAYMENT_STATUS_FAIL, '调用太平洋接口出错', null, null)
        }
    }

    /**
     * 构建return给web
     * @param context
     * @param respPaymentInfo
     * @return
     */
    private static buildResult(paymentInfo, respPaymentInfo) {
        def commercialInsurance = getInsuranceByPolicyNo(paymentInfo.commercial, respPaymentInfo)
        def compulsoryInsurance = getInsuranceByPolicyNo(paymentInfo.compulsory, respPaymentInfo)

        if (!respPaymentInfo) {
            return _CHECK_PAYMENT_RESULT(paymentInfo.orderNo, _PAYMENT_STATUS_FAIL, '根据支付号未查询到订单', null, null)
        }
        if ('3' == (commercialInsurance?.payStatus ?: compulsoryInsurance.payStatus)) { // 3 表示支付成功
            return _CHECK_PAYMENT_RESULT(paymentInfo.orderNo, _PAYMENT_STATUS_SUCCESS, '订单已支付', compulsoryInsurance, commercialInsurance)
        }
        return _CHECK_PAYMENT_RESULT(paymentInfo.orderNo, _PAYMENT_STATUS_PROCESSING, '让支付飞一会~~', null, null)
    }

    /**
     * 根据保单号 获取险种信息
     * @param policyNo insurances
     * @return
     */
    def static getInsuranceByPolicyNo(policyNo, insurances) {
        if (policyNo && insurances) {
            return insurances.find { insurance -> insurance.insuredNo == policyNo }
        }
    }

    static final _CHECK_PAYMENT_RESULT = { orderNo, payStatus, message, compulsoryInsurance, commercialInsurance ->
        [
            orderNo            : orderNo,
            payStatus          : payStatus,
            outTradeNo         : commercialInsurance?.businessId ?: compulsoryInsurance?.businessId,
            message            : message,
            compulsoryInsurance: [
                ProposalNo   : compulsoryInsurance?.insuredNo,
                PolicyNo     : compulsoryInsurance?.policyNo,
                EffectiveDate: compulsoryInsurance?.beginDate,
                ExpireDate   : compulsoryInsurance?.endDate
            ],
            commercialInsurance: [
                ProposalNo   : commercialInsurance?.insuredNo,
                PolicyNo     : commercialInsurance?.policyNo,
                EffectiveDate: commercialInsurance?.beginDate,
                ExpireDate   : commercialInsurance?.endDate
            ]
        ]
    }

}
