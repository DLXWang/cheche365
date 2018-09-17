package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getHtmlParser
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getPiccFormatter
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC


/**
 * 获取微信扫码支付页
 *
 */
@Component
@Slf4j
class EditPayFeeByWeChat implements IStep {


    private static final _PAY_API_EDIT_PAY_FEE_BY_WECHAT = '/cbc/jf/editPayFeeByWeChat.do'

    @Override
    Object run(Object context) {

        log.debug '获取微信扫码支付页'

        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _PAY_API_EDIT_PAY_FEE_BY_WECHAT,
            query             : [
                exchangeNo: context.exchangeNo,
                serialNo  : context.createWeChatInfo?.serialNo,
                payNo     : context.createWeChatInfo?.payNo,
                flag      : 3,
                ModifyFlag: 'QUEREN',
                seed      : new Random().nextDouble()
            ]
        ]

        log.debug 'args {}', args
        def result = client.get args, { resp, html ->
            htmlParser.parse(html)
        }

        // 获取参数
        result.depthFirst().IMG.each {
            if (it.attributes().get('src').contains('PrintTwoBarCodeServlet')) {
                log.debug '{}', it.attributes().get('src')
                def url = it.attributes().get('src')
                def match = url =~ /.*code=(.*)&seed.*/
                if (match.matches()) {
                    context.wechatCodeUrl = match[0][1]
                }
            }
        }

        result.depthFirst().FONT.each {
            if (it.value() && it.value()[0] && it.value()[0].toString().contains('使用微信扫一扫进行支付')) {
                def deadLineStr = (it.value()[0].toString() =~ /.*请于(.*)之前使用微信扫一扫进行支付.*/)[0][1]
                def timeFormatter= new DateTimeFormatterBuilder().appendPattern('yyyy-M-d HH:mm:ss').toFormatter()
                if (LocalDateTime.now().isAfter(LocalDateTime.parse(deadLineStr, timeFormatter))){
                    log.debug 'now : {} is after deadline :  {}', LocalDateTime.now(), deadLineStr
                    context.needGetNewQRCode = true
                }
            }
        }

        if (context.needGetNewQRCode) {
            log.debug '支付链接已过期，重新生成新的支付记录'
            context.needGetNewQRCode = false
            context.wechatCodeUrl = false
            return getContinueFSRV('链接过期')
        }

        if (!context.wechatCodeUrl) {
            log.debug '未获取到微信支付链接'
            return getKnownReasonErrorFSRV('超过付款期限,请重新报价')
        }
        getContinueFSRV context.wechatCodeUrl

    }
}
