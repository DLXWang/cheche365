package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.JSON



/**
 * 支付后-缴费完成确认
 */
@Component
@Slf4j
class PaymentCompletion implements IStep {


    private static final _PAY_API_ROPOSAL_TO_POLICY = '/cbc/jf/proposalToPolicy.do'

    @Override
    Object run(Object context) {
        log.debug '缴费完成确认'
        RESTClient client = context.client
        client.uri = context.cbc_host

        def args = [
            requestContentType: JSON,
            contentType       : URLENC,
            path              : _PAY_API_ROPOSAL_TO_POLICY,
            query             : [
                workbenchCertiNo: context.processNo, // 报价单号
                exchangeNo      : context.indexPaymentInfo.paymentNo,// 缴费通知单号
            ]
        ]

        log.debug 'args {}', args
        def result = client.post args, { resp, json ->
            json
        }

        log.debug '缴费完成确认结果： {}', result
        //  TODO 需要完成支付的报文 and 第一次点击后的报文
        //  PDAA  商业   PDZA 交强
        if (result.data?.policyNo &&
            (result.data?.policyNo?.startWith('PDAA') || result.data?.policyNo?.startWith('PDZA'))) {
            log.debug '缴费完成确认成功 ： {}', result
            getContinueFSRV result.data?.policyNo
        } else if ('NotEnough' == result.data?.errorMessage) {
            log.debug '支付未完成'
            getContinueFSRV '支付未完成'
        } else {
            log.debug '缴费完成确认失败'
            getContinueFSRV '缴费完成确认失败'
        }
    }
}
