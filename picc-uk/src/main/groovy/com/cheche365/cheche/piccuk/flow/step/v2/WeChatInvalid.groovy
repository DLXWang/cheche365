package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * 作废微信支付记录
 */
@Component
@Slf4j
class WeChatInvalid implements IStep {

    private static final _PAY_STEP_WECHAT_INVALID = '/cbc/jf/weChatInvalid.do'

    @Override
    Object run(Object context) {
        RESTClient client = context.client



        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _PAY_STEP_WECHAT_INVALID,
            query             : [
                exchangeNo: context.exchangeNo,
                serialNo  : context.createWeChatInfo?.serialNo,
                payType   : context.createWeChatInfo?.payType  // 支付类型 context.createWeChatInfo?.payType
            ]
        ]

        log.debug 'args {}', args
        client.post args
        log.debug '成功作废微信支付记录'
        getContinueFSRV '成功作废'
    }
}
