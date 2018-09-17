package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.constants.ModelConstants
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 查询微信支付结果
 */
@Component
@Slf4j
class QueryWeChatResult implements IStep {

    private static final _PAY_API_QUERY_WECHAT = '/cbc/jf/queryWeChat.do'

    @Override
    Object run(Object context) {
        log.debug '查询微信支付结果'

        def paymentInfo = context.indexPaymentInfo
        log.debug 'paymentInfo :{}', paymentInfo
        def result = postRequest(context, paymentInfo)
        def paymentResult = context.newCheckPaymentState ?: []
        paymentResult = paymentResult + result
        context.newCheckPaymentState = paymentResult
        log.debug 'index {} 查询完成', context.checkIndex
        context.checkIndex = context.checkIndex + 1
        if(result.payStatus == ModelConstants._PAYMENT_STATUS_SUCCESS) {
            return getContinueFSRV('生成保单号')
        }
        getContinueFSRV context
    }

    /**
     * 发送请求
     * @param context
     * @param paymentInfo
     * @return
     */
    private static postRequest(context, paymentInfo) {
        RESTClient client = context.client
        client.uri = context.cbc_host

        /**
         * 根据报价单号查询支付结果
         */
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _PAY_API_QUERY_WECHAT,
            query             : [
                exchangeNo: paymentInfo?.paymentNo,
                serialNo  : paymentInfo?.serialNo,
                payType   : paymentInfo?.payType,
            ]
        ]

        log.debug 'args {}', args
        def result = client.post args, { resp, json -> json }
        log.debug 'result :{}', result

        dealPaymentResult(paymentInfo, result)
    }

    /**
     * 处理查询返回值
     */
    private static dealPaymentResult(paymentInfo, result) {
        if (result && result.totalRecords == 1) {
            if (result.data[0].SCol2?.contains('未支付')) {
                return _CHECK_PAYMENT_RESULT(paymentInfo, ModelConstants._PAYMENT_STATUS_PROCESSING, '支付中')
            }
            if (result.data[0].SCol2?.contains('付款成功')) {
                return _CHECK_PAYMENT_RESULT(paymentInfo, ModelConstants._PAYMENT_STATUS_SUCCESS, '订单已支付')
            }
            return _CHECK_PAYMENT_RESULT(paymentInfo, ModelConstants._PAYMENT_STATUS_PROCESSING, '支付中')
        } else {
            log.debug '查询微信支付结果失败'
            return _CHECK_PAYMENT_RESULT(paymentInfo, ModelConstants._PAYMENT_STATUS_FAIL, '调用人保接口出错')
        }
    }


    static final _CHECK_PAYMENT_RESULT = { paymentInfo, payStatus, message ->
        [
            orderNo   : paymentInfo.orderNo,
            payStatus : payStatus,
            outTradeNo: paymentInfo.paymentNo,
            message   : message,
        ]
    }

}
