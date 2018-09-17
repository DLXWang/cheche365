package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import java.time.LocalDate

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static java.util.Calendar.DATE
import static java.util.Calendar.MONTH
import static java.util.Calendar.YEAR





/**
 * 查询投保单
 * 已经有一个投保单查询了，此步骤是用来判断支付完成后是否生成保单号
 * 如果未生成保单号。下一步调用{缴费完成确认}* Created by yujingtai 2018.07.31
 */
@Component
@Slf4j
class SelectProposalForCheckStatus implements IStep {

    private static final _URL_SELECT_PROPOSAL = '/prpall/business/selectProposal.do'

    @Override
    run(context) {

        RESTClient client = context.client
        client.uri = context.prpall_host
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_SELECT_PROPOSAL,
            query             : [
                'prpCproposalVo.proposalNo': context.processNo,
                'prpCproposalVo.riskCode'  : 'DAA,DZA'
            ]
        ]

        log.debug 'args :{}', args
        def result = client.post args, { resp, json ->
            json
        }
        def paymentInfo = context.indexPaymentInfo   // 当前查询的投保单
        if (result.totalRecords > 0) {
            def commercial = paymentInfo?.commercial
            def compulsory = paymentInfo?.compulsory
            def (commercialPolicyNo, commercialStartTimestampString) = getPolicyNo(result, commercial)
            def (compulsoryPolicyNo, compulsoryStartTimestampString) = getPolicyNo(result, compulsory)

            // policyNo json中会返回 '   ' 。
            if ((!commercialPolicyNo || commercialPolicyNo.startsWith(' '))
                && (!compulsoryPolicyNo || compulsoryPolicyNo.startsWith(' '))) {
                log.debug '{}未生成保单号', context.processNo
                return getContinueFSRV('创建保单号')
            } else {
                log.debug '{}已生成保单号', context.processNo
                def newCheckPaymentState = context.newCheckPaymentState  // 上一步保存的结果

                if (newCheckPaymentState && newCheckPaymentState.size() > 0) {
                    def index = 0
                    for (Map map : newCheckPaymentState) {
                        if (map.orderNo == paymentInfo.orderNo) {
                            break
                        }
                        index += 1
                    }
                    if (index < newCheckPaymentState.size()) {
                        newCheckPaymentState[index].compulsoryInsurance = [
                            ProposalNo   : compulsory,
                            PolicyNo     : compulsoryPolicyNo,
                            EffectiveDate: getEffectiveOrExpireDate(compulsoryStartTimestampString, true),
                            ExpireDate   : getEffectiveOrExpireDate(compulsoryStartTimestampString, false)
                        ]
                        newCheckPaymentState[index].commercialInsurance = [
                            ProposalNo   : commercial,
                            PolicyNo     : commercialPolicyNo,
                            EffectiveDate: getEffectiveOrExpireDate(commercialStartTimestampString, true),
                            ExpireDate   : getEffectiveOrExpireDate(commercialStartTimestampString, false)
                        ]
                    }
                }

                context.newCheckPaymentState = newCheckPaymentState

                getContinueFSRV '已生成保单号'
            }
        } else {
            getContinueWithIgnorableErrorFSRV result, '查询失败，执行下一循环'
        }
    }

    private static getPolicyNo(result, processNo) {
        def item = result.data.find { proposal ->
            proposal.proposalNo == processNo
        }
        new Tuple2(item?.policyNo, item?.startDate?.time)
    }

    private static getEffectiveOrExpireDate(startTimeString, isEffective) {
        if (startTimeString) {
            def date = new Date((startTimeString as Long))
            _DATETIME_FORMAT3.format(LocalDate.of(date[YEAR] + (isEffective ? 0 : 1), date[MONTH] + 1, date[DATE]))
        } else {
            ''
        }
    }
}
