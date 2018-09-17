package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovyx.net.http.RESTClient

import static groovyx.net.http.ContentType.JSON



/**
 * 查询支付订单
 * @author: lp
 * @date: 2018/7/2 10:06
 */
abstract class QueryPaymentRecord implements IStep {

    private static final _API_GET_PAYMENT_RECORD = '/ecar/paymentrecord/query'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_GET_PAYMENT_RECORD,
            body              : [
                meta  : [pageSize: 8],
                redata: getApiParam(context)
            ]
        ]

        log.debug '查询支付订单，请求参数  ：{}', args

        def result = client.post args, { resp, json ->
            json
        }

        log.debug '查询支付订单，返回结果  ：{}', result

        handlePaymentRecord(result, context)
    }

    protected abstract getApiParam(context)

    protected abstract handlePaymentRecord(result, context)

}
