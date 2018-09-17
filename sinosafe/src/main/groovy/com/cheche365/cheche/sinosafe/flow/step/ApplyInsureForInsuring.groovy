package com.cheche365.cheche.sinosafe.flow.step

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.flow.Constants.get_ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.core.model.OrderStatus.Enum.INSURE_FAILURE_7
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PENDING_PAYMENT_1
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.populateNewQuoteRecordAndInsurances
import static com.cheche365.cheche.sinosafe.flow.Constants._STATUS_CODE_SINOSAFE_CONFIRM_INSURE_FAILURE
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV

/**
 * 核保(核保流程)
 */
@Slf4j
class ApplyInsureForInsuring extends AApplyInsure {

    @Override
    protected doDealInsureSuccess(context, result, errorMsg) {
        context.newQuoteRecordAndInsurances = populateNewQRI(context, result.PACKET.BODY)
        //underwriteStatus为0 转人工核保 3打回修改 7是核保通过(当核保通过后,errorMsg'成功',所以,核保通过分支只判定errorMsg是'成功'即可)
        def jq_base = result.PACKET?.BODY?.JQ_BASE//交强险
        def sy_base = result.PACKET?.BODY?.SY_BASE//商业险
        if ((jq_base != null && jq_base?.underwriteStatus == '0') || (sy_base != null && sy_base?.underwriteStatus == '0')) {
            [_ROUTE_FLAG_DONE, _STATUS_CODE_SINOSAFE_CONFIRM_INSURE_FAILURE, null, '转人工核保原因:' + errorMsg]
        } else if ((jq_base != null && jq_base?.underwriteStatus == '3') || (sy_base != null && sy_base?.underwriteStatus == '3')) {
            getKnownReasonErrorFSRV '打回修改原因:'+errorMsg
        } else if (
        (jq_base != null && jq_base?.underwriteStatus == '7' && sy_base != null && sy_base?.underwriteStatus == '7')
            || (jq_base != null && jq_base?.underwriteStatus == '7' && sy_base == null)
            || (jq_base == null && sy_base != null && sy_base?.underwriteStatus == '7')
        ) {
            getLoopBreakFSRV(result)
        }  else {
            getFatalErrorFSRV errorMsg
        }

    }

    private static populateNewQRI(context, body) {
        def commercialApplyNo = body?.SY_BASE?.PLY_APP_NO
        def compulsoryApplyNo = body?.JQ_BASE?.PLY_APP_NO
        log.info '华安核保返回结果，商业险保单号：{}，交强险保单号：{}', commercialApplyNo, compulsoryApplyNo
        def (_1, newInsurance, newCompulsoryInsurance) = populateNewQuoteRecordAndInsurances(context, commercialApplyNo, null, compulsoryApplyNo, null)

        def insurancePackage = context.accurateInsurancePackage
        def isCompulsoryOrAutoTaxQuoted = isCompulsoryOrAutoTaxQuoted(insurancePackage)
        def isCommercialQuoted = isCommercialQuoted(insurancePackage)

        if (isCommercialQuoted) {
            newInsurance.insuranceStatus = (body?.SY_BASE?.underwriteStatus == '7' && body?.SY_BASE?.IMAGE_REVTYPE != '1') ? PENDING_PAYMENT_1 : INSURE_FAILURE_7
        }
        if (isCompulsoryOrAutoTaxQuoted) {
            newCompulsoryInsurance.ciStatus = (body?.JQ_BASE?.underwriteStatus == '7' && body?.JQ_BASE?.IMAGE_REVTYPE != '1') ? PENDING_PAYMENT_1 : INSURE_FAILURE_7
        }

        [_1, newInsurance, newCompulsoryInsurance]
    }

}
