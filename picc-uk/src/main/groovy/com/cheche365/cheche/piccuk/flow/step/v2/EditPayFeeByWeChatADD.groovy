package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getHtmlParser
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * 编辑二维码支付信息
 *
 */
@Component
@Slf4j
class EditPayFeeByWeChatADD implements IStep {

    private static final _PAY_API_EDIT_PAY_FEE_BY_WECHAT = '/cbc/jf/editPayFeeByWeChat.do'

    @Override
    Object run(Object context) {

        log.debug '编辑二维码支付信息-交费创建'

        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _PAY_API_EDIT_PAY_FEE_BY_WECHAT,
            query             : generateRequestParameters(context, this)
        ]

        log.debug 'args {}', args
        def result = client.get args, { resp, html ->
            htmlParser.parse(html)
        }

        // 获取参数
        def createWeChatInfo = [:]
        result.depthFirst().INPUT.each {
            if ('serverTime' == it.@name) {
                createWeChatInfo.serverTime = it.@value
            }
            if ('initFlag' == it.@name) {
                createWeChatInfo.initFlag = it.@value
            }
            if ('prpJfWechat.id.serialNo' == it.@name) {
                createWeChatInfo.prpJfWechatSerialNo = it.@value
            }
            if ('prpJfPosRecord.id.serialNo' == it.@name) {
                createWeChatInfo.serialNo = it.@value
            }
            if ('prpJfPosRecord.payType' == it.@name) {
                createWeChatInfo.payType = it.@value
            }
            if ('prpJfPosRecord.sumFee' == it.@name) {
                createWeChatInfo.sumFee = it.@value
            }
            if ('prpJfPosRecord.payNo' == it.@name) {
                createWeChatInfo.payNo = it.@value
            }
        }
        context.createWeChatInfo = createWeChatInfo

        getContinueFSRV '创建交费信息成功'
    }
}
