package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getHtmlParser
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * 缴费信息页
 */
@Component
@Slf4j
class EditFeeInfor implements IStep {

    private static final _PAY_API_EDIT_FEE_INFOR = '/cbc/jf/editFeeInfor.do'

    @Override
    Object run(Object context) {
        log.debug '缴费信息页'

        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _PAY_API_EDIT_FEE_INFOR,
            query             : [
                exchangeNo: context.exchangeNo,
                pageNo    : 1,
                pageSize  : 10,
                seed      : new Random().nextDouble()
            ]
        ]

        log.debug 'args {}', args
        def result = client.get args, { resp, html ->
            def response = htmlParser.parse(html)
            response
        }

        def index = 0

        // 此处交费记录有四个状态 1 登记 2 交费登记 3 登记确认 4 交易作废  123状态可以继续支付，4状态不可继续支付
        // 未获取到123状态 需要创建交费记录
        result.depthFirst().LABEL.each {
            def m = it.value() =~ /.*登记.*/
            if (m.matches() && !context.checkPayInfoStatus) {
                log.debug '{}', (it.@id =~ /\d/)[0]
                log.debug '{} index {}', it.value(), (it.@id =~ /\d/)[0]
                index = (it.@id =~ /\d/)[0]
                context.checkPayInfoStatus = it.value()[0]
            }
        }

        // 获取参数
        context.createWeChatInfo = [:]
        context.createWeChatInfo.serialNo = result.depthFirst().INPUT.find {
            ('prpJfPayRecords[' + index + '].serialNo') == it.@name
        }?.@value
        context.createWeChatInfo.payNo = result.depthFirst().INPUT.find {
            'prpJfPayRecords[' + index + '].payNo' == it.@name
        }?.@value

        getContinueFSRV context.checkPayInfoStatus ?: '未创建'
    }
}
