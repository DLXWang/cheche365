package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC

/**
 * 删除投保单
 * Created by liheng on 2016.10.28
 */
@Slf4j
class DeleteProposal implements IStep {

    private static final _URL_DELETE_PROPOSAL = '/prpall/business/deleteProposal.do'

    @Override
    run(context) {
        RESTClient client = context.client

        def (bsProposalNo, bzProposalNo) = context.proposalNos.first()
        def bizNo = bsProposalNo && bzProposalNo ? bsProposalNo + ',' + bzProposalNo : bsProposalNo ?: bzProposalNo
        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _URL_DELETE_PROPOSAL,
            query             : [
                bizNo: bizNo
            ]
        ]

        log.info '删除投保单：{}', bizNo
        client.get args
        context.proposalNos.remove 0
        getContinueFSRV bizNo

    }
}
