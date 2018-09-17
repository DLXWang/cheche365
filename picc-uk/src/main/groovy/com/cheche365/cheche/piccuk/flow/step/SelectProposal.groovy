package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 查询投保单
 * Created by liheng on 2016.10.28
 */
@Slf4j
class SelectProposal implements IStep {

    private static final _URL_SELECT_PROPOSAL = '/prpall/business/selectProposal.do'

    @Override
    run(context) {
        if (!context.proposalNos) {
            return context.lastDoneFSRV ?: getLoopBreakFSRV('投保单已清空')
        }

        RESTClient client = context.client

        def (bsProposalNo, bzProposalNo) = context.proposalNos.first()
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_SELECT_PROPOSAL,
            query             : [
                'prpCproposalVo.proposalNo': bsProposalNo ?: bzProposalNo,
                'prpCproposalVo.riskCode'  : 'DAA,DZA'
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result.totalRecords > 0) {
            if (checkUnderWriteFlag(result, '见费出单待缴费')) {
                context.editProposalNo = checkUnderWriteFlag(result, '见费出单待缴费')
                getContinueFSRV 'cancelUnderwrite'
            } else if (checkUnderWriteFlag(result, '待核保')) {
                context.editProposalNo = checkUnderWriteFlag(result, '待核保')
                getContinueFSRV 'cancelProposal'
            } else {
                getContinueFSRV 'deleteProposal'
            }
        } else {
            context.proposalNos.remove 0
            getLoopContinueFSRV result, '投保单已删除'
        }
    }

    private static checkUnderWriteFlag(result, underWriteFlag) {
        result.data.find { proposal ->
            proposal.underWriteFlag == underWriteFlag
        }?.proposalNo
    }
}
