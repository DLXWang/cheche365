package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * 创建微信交费记录
 */
@Component
@Slf4j
class SaveByWeChatDENGJI implements IStep {

    private static final _PAY_API_SAVE_BY_WECHAT = '/cbc/jf/saveByWeChat.do'

    @Override
    Object run(Object context) {
        log.debug '登记微信缴费记录'
        RESTClient client = context.client



        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _PAY_API_SAVE_BY_WECHAT,
            query             : [
                payType: context.createWeChatInfo?.payType,
                type   : 3,
            ],
            body              : [
                'prpJfWechat.id.exchangeNo'   : context.exchangeNo,
                'prpJfWechat.id.serialNo'     : context.createWeChatInfo?.prpJfWechatSerialNo,
                'prpJfPosRecord.id.exchangeNo': context.exchangeNo,
                'prpJfPosRecord.id.serialNo'  : context.createWeChatInfo?.serialNo,
                'prpJfPosRecord.payNo'        : context.createWeChatInfo?.payNo,
                'initFlag'                    : context.createWeChatInfo?.initFlag,
                'prpJfPosRecord.payType'      : context.createWeChatInfo?.payType,
                'serverTime'                  : context.createWeChatInfo?.serverTime,
                'exchangeNo'                  : context.exchangeNo,
                'prpJfPayExch.latestPayDate'  : context.createWeChatInfo?.serverTime,
                'prpJfPosRecord.sumFee'       : context.createWeChatInfo?.sumFee,
                'prpJfPosRecord.currency'     : 'CNY'

            ]
        ]

        log.debug 'args {}', args
        client.post args

        getContinueFSRV '登记微信缴费记录'
    }
}
