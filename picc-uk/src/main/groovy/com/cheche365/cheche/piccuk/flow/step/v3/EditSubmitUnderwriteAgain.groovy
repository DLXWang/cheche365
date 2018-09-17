package com.cheche365.cheche.piccuk.flow.step.v3

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getDoInsuranceFailedFSRV
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.URLENC



/**
 * 上传照片后再次提交核保
 */
@Component
@Slf4j
class EditSubmitUnderwriteAgain implements IStep {

    private static final _URL_PATH_EDIT_SUBMIT_UNDERWRITE = '/prpall/business/editSubmitUndwrt.do'

    @Override
    run(context) {
        RESTClient client = context.client
        client.uri =context.prpall_host
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
            html
        }

        if (result) {
            log.debug'上传照片提交人工审核， 单号为：{}',bsProposalNo
            context.proposalStatus = '影像已上传等待人工审核'
            getDoInsuranceFailedFSRV(null, '人工审核中，请稍后查看结果')
        } else {
            getFatalErrorFSRV result ?: '核保失败'
        }
    }

}
