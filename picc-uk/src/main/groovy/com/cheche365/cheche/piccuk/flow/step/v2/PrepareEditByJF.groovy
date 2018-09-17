package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getHtmlParser
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.TEXT



/**
 * 检查保单支付状态,获取下一步执行步骤
 */
@Component
@Slf4j
class PrepareEditByJF implements IStep {

    private static final _PAY_API_PREPARE_EDITBYJF = '/cbc/jf/prepareEditByJF.do'

    @Override
    Object run(Object context) {
        log.debug '检查保单支付状态,获取下一步执行步骤'
        RESTClient client = context.client
        client.uri = context.cbc_host

        def args = [
            requestContentType: HTML,
            contentType       : TEXT,
            path              : _PAY_API_PREPARE_EDITBYJF,
            query             : [
                workbenchUserCode    : context.workbenchUserCode,
                workbenchCertiNo     : context.processNo,
                worekbenchUserComCode: context.worekbenchUserComCode,
            ]
        ]

        if (context.strCertiNoMap) {
            args.query.ticket = context.ticket
        }

        log.debug 'args {}', args
        def result = client.get args, { resp, html ->
            def response = htmlParser.parse(html)
            response
        }
        // 获取执行步骤  是否需要 工作平台见费处理
        def resultNodeList = result.depthFirst()
        /**
         * 流程修改  add by yujingtai 20180716
         */
        if ('工作平台见费处理' == resultNodeList.TITLE?.text()) {
            //获取支付请求参数
            context.strCertiNoMap = resultNodeList.INPUT.find {
                it.@name == 'strCertiNoMap'
            }?.@value
            //获取支付exchangeNo
            def exchangeNo = resultNodeList.INPUT.find {
                it.@name == 'exchangeNo'
            }?.@value
            if (exchangeNo) {
                log.debug '已经生成缴费通知单号，前往交费信息页'
                context.exchangeNo = exchangeNo
                getContinueFSRV '缴费信息页'
            } else {
                log.debug '未生成缴费通知单号'
                getContinueFSRV '生成缴费通知单号'
            }
        } else {
            log.debug '检查保单支付状态失败'
            getKnownReasonErrorFSRV '检查保单支付状态失败'
        }

    }
}
