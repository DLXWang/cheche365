package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getResponseResult
import static groovyx.net.http.ContentType.JSON



/**
 * 该步骤可选，这是对是否通过核保的又一次校验
 * 可进行保费支付步骤的订单
 * 核保之后的保单，判断是否能进行支付，只有支付状态为
 * 起始状态的保单才能查询到结果，表示保单审核通过，可进行下一步
 * Created by chukh on 2018/5/8.
 * 根据条件搜索的分页查询
 */
@Component
@Slf4j
class QueryPayment implements IStep {

    private static final _API_PATH_QUERY_PAYMENT = '/ecar/payment/queryPayment'

    @Override
    run(Object context) {
        RESTClient client = context.client
        def insuredNo = context.applyPolicyNos?.values()?.first() ?: context.commercialInsureNo ?: context
            .compulsoryInsureNo
        log.debug '查询支付订单insuredNo：{}', insuredNo
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_QUERY_PAYMENT,
            body              : [
                meta  : [:],
                redata: [
                    //根据报价单号 或 投保单号 均可
                    insuredNo: insuredNo
                ]
            ]
        ]
        //发送请求获取结果json数据
        def result = client.post args, { resp, json -> json }
        log.debug '查询支付订单结果：{}', result
        //可付费订单结果
        getResponseResult result, context, this
    }
}
