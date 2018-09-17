package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getHtmlParser
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.cheche.piccuk.util.BusinessUtils._GET_EFFECTIVE_ADVICES
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getEffectiveAdvice
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * 核保意见
 */
@Component
@Slf4j
class ShowUnderwriteMessage implements IStep {

    private static final _URL_PATH_SHOW_UNDERWRITE_MESSAGE = '/prpall/business/showUndwrtMsg.do'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _URL_PATH_SHOW_UNDERWRITE_MESSAGE,
            query             : [
                bizNo  : context.proposalNos?.last()?.first(),
                bizType: context.baseInfo?.bizType ?: 'PROPOSAL'
            ]
        ]
        def response = client.post args, { resp, html ->
            htmlParser.parse(html)
        }
        // 多条时取最新的一条 只有一条的情况下返回值不一样 此时取全部内容
        def result = !response.depthFirst().TEXTAREA.isEmpty() ? response.depthFirst().TEXTAREA.last().value()[0] : response.text()
        def advice = getEffectiveAdvice(_GET_EFFECTIVE_ADVICES, result)
        log.debug '自动核保失败，人保给出的提示为：{}', advice
        def m = advice.find {
            it =~ /.*(验车照片|提供购车发票).*/
        }
        if (m) {
            //推补充信息 上传影像
            context.proposalStatus = '需上传影像'
            return getSupplementInfoFSRV([mergeMaps(_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])])
        }

        def specialNoMatcher = advice =~ /(请录入).*?(特别约定)/
        if (specialNoMatcher.find()) {
            def specialNo = (specialNoMatcher[0][0] =~ /(\d+)/)[0][1]
            log.debug '需要录入特别约定： {}', specialNo
            context.agreementNo = specialNo
            return getContinueFSRV('增加特别约定')
        }

        getKnownReasonErrorFSRV advice
    }

}
