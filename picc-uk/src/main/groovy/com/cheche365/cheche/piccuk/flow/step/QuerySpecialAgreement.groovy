package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getProposalNoForBICI
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.URLENC
import static java.time.LocalDate.now as today



/**
 * 查询特别约定
 */
@Component
@Slf4j
class QuerySpecialAgreement implements IStep {

    private static final _URL_QUERY_SPECIAL_AGREEMENT = '/prpall/common/changeCodeInput.do'

    @Override
    run(context) {
        RESTClient client = context.client
        def comCode = context.piccComCode

        def args = [
            requestContentType: URLENC,
            contentType       : HTML,
            path              : _URL_QUERY_SPECIAL_AGREEMENT,
            query             : [
                actionType    : 'query',
                fieldIndex    : '',
                fieldValue    : context.agreementNo,
                codeMethod    : '',
                codeType      : '',
                codeRelation  : '',
                isClear       : '',
                otherCondition: 'riskCode=DAA/DZA,clauseType=F42,language=1,comCode=' + comCode + ',bizType=PROPOSAL,clauseIssue=2,operateDate=' + today(),
                typeParam     : '',
                getDataMethod : 'getRiskEngage',
                callBackMethod: ''
            ]
        ]

        log.debug 'args {}', args
        def result = client.get args, { resp, html ->
            (html as String).split('_FIELD_SEPARATOR_')
        }

        def index = getCengageIndex(context.insertArgs)
        // update
        if (result) {
            log.debug '增加特别约定 {}', context.agreementNo
            def resultList = result as List
            def (ciNo, biNo) = getProposalNoForBICI(context.proposalNos)
            context.insertArgs.body << [
                ("prpCengageTemps[${index}].id.serialNo".toString()): index + 1,
                ("prpCengageTemps[${index}].clauseCode".toString()) : resultList[0],
                ("prpCengageTemps[${index}].clauseName".toString()) : resultList[1],
                ("clauses[${index}]".toString())                    : resultList[2],
                ("prpCengageTemps[${index}].clauses".toString())    : resultList[2],
                ("prpCengageTemps[${index}].engageFlag".toString()) : resultList[3],
                editType                                            : 'UPDATE',
                'prpCmain.proposalNo'                               : biNo ?: ciNo,
                'prpCmainCI.proposalNo'                             : ciNo,
                'prpCmain.checkFlag'                                : 0,
                'prpCmain.renewalFlag'                              : '01',
                isBICI                                              : '',
                isQueryCarModelFlag                                 : 1,
                operatorCode                                        : '00',
                premiumChangeFlag                                   : 0,
                switchFlag                                          : 1,
                updateQuotation                                     : 1,
                oldPolicyNo                                         : biNo ?: ciNo,
                bizNo                                               : biNo,
            ]
            getContinueFSRV '获取特别约定继续保存订单'
        } else {
            log.error '无法获取特别约定'
            getKnownReasonErrorFSRV '无法获取特别约定'
        }
    }

    private static getCengageIndex(insertArgs) {
        def index = 0
        while (true) {
            if (!(insertArgs.body?.containsKey("prpCengageTemps[${index}].id.serialNo".toString()))) {
                break
            }
            index += 1
        }
        index
    }
}
