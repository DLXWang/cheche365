package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.populateNewQuoteRecordAndInsurances
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.URLENC



/**
 * 核保
 */
@Component
@Slf4j
class EditSubmitUnderwrite implements IStep {

    private static final _URL_PATH_EDIT_SUBMIT_UNDERWRITE = '/prpall/business/editSubmitUndwrt.do'

    @Override
    run(context) {
        RESTClient client = context.client

        def (bsProposalNo, bzProposalNo) = context.proposalNos.first()
        def args = [
            requestContentType: URLENC,
            contentType       : HTML,
            path              : _URL_PATH_EDIT_SUBMIT_UNDERWRITE,
            query             : [
                bizNo: bsProposalNo ?: bzProposalNo
            ]
        ]

        def result = client.get args, { resp, html ->
            def pattern = ''
            pattern += bsProposalNo ? "投保单：$bsProposalNo(.*)[!|。](.*)" : '()()'
            pattern += bzProposalNo ? "投保单：$bzProposalNo(.*)[!|。]" : '()'
            def m = html =~ /$pattern/
            if (m.find()) {
                log.info '核保结果：{}', m[0][0]
                m[0][1..3] // 商业险不投保时添加占位，固定位置获取意见信息    商业核保信息|核保意见|交强核保信息
            }
        }

        if (result) {
            def (bsMsg, bsOpinion, bzMsg) = result
            if ((!bsMsg || bsMsg.contains('提交核保成功，自动核保通过'))
                && (!bzMsg || bzMsg.contains('提交核保成功，自动核保通过'))
                && (!bsMsg || !bsMsg.contains('承保处理失败'))
                && (!bzMsg || !bzMsg.contains('承保处理失败'))) {
                log.info '核保通过'
                context.proposalStatus = '核保流程完成'
                context.newQuoteRecordAndInsurances = populateNewQuoteRecordAndInsurances(context,
                    bsProposalNo, null, bzProposalNo, null)
                context.insertArgs = '' // 清除因特别约定增加的缓存， add by yujt 20180903
                getContinueFSRV '核保通过'
            } else if (bsMsg == '重复提交核保' || bsOpinion.contains('核保意见')) {
                getContinueFSRV '查看核保意见'
            } else {
                getKnownReasonErrorFSRV bsMsg ?: '核保失败'
            }
        } else {
            getFatalErrorFSRV '核保失败'
        }
    }

}
