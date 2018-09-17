package com.cheche365.cheche.piccuk.flow.step.v3

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING
import static com.cheche365.flow.core.util.FlowUtils.getDoInsuranceFailedFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 *
 * 上传影像后查询保单的审核结果 add by chukaihua 2018.08.07
 */
@Slf4j
class SelectProposalForInsureStatus implements IStep {

    private static final _URL_SELECT_PROPOSAL = '/prpall/business/selectProposal.do'

    @Override
    run(context) {

        RESTClient client = context.client
        client.uri = context.prpall_host
        def proposalNo = context.proposalNos?.last()?.first()
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_SELECT_PROPOSAL,
            query             : [
                'prpCproposalVo.proposalNo': proposalNo,
                'prpCproposalVo.riskCode'  : 'DAA,DZA'
            ]
        ]

        log.debug 'args :{}', args
        def result = client.post args, { resp, json ->
            json
        }

        if (result.totalRecords > 0) {
            def underWriteFlag = result.data.underWriteFlag[0]
            // 待审核
            if (underWriteFlag == '待核保') {
                context.proposalStatus = '影像已上传等待人工审核'
                log.debug '投保单：{}状态为待核保', proposalNo
                return getDoInsuranceFailedFSRV([proposalNo: proposalNo, type: this.class.name],
                    '核保中，请稍后查看结果')
            } else if (underWriteFlag == '不通过') {//不通过
                log.debug '投保单：{}审核结果为：不通过，继续传照片', proposalNo
                context.proposalStatus = '需上传影像'
                return getSupplementInfoFSRV([mergeMaps(_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])])
            } else if (underWriteFlag == '见费出单待缴费') {//通过
                log.debug '投保单：{}状态为：见费出单待缴费', proposalNo
                context.proposalStatus = '核保流程完成'
                return getContinueFSRV(null)
            } else {
                log.debug '人工审核结果：{}',underWriteFlag
                getKnownReasonErrorFSRV underWriteFlag
            }
        } else {
            getKnownReasonErrorFSRV '查询不到保单状态'
        }

    }

}
