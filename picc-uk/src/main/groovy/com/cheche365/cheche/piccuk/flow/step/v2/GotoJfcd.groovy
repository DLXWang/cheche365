package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * 申请检查保单支付状态
 */
@Component
@Slf4j
class GotoJfcd implements IStep {

    private static final _PAY_API_GOTOJFCD = '/prpall/workbench/gotoJfcd.do'

    @Override
    Object run(Object context) {
        RESTClient client = context.client
        client.uri = context.prpall_host
        context.processNo = context.processNo ?: context.applyPolicyNos?.values()?.first() ?: context.applyPolicyNos.commercial ?: context.applyPolicyNos.compulsory

        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _PAY_API_GOTOJFCD,
            query             : [
                applyNo: context.processNo,
                ticket : context.ticket
            ]

        ]
        log.debug 'args: {}', args
        client.get args
        getContinueFSRV '登录JF成功'

    }
}
