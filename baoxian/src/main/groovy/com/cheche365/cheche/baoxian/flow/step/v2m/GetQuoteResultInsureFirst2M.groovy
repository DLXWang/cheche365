package com.cheche365.cheche.baoxian.flow.step.v2m

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_COMMERCIAL_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_COMPULSORY_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getProvideValuableHintsFSRV



/**
 * 对应先核保后支付流程的报价回调
 */
@Component
@Slf4j
class GetQuoteResultInsureFirst2M extends AConQuoteResult {

    @Override
    resolveResult(context, result) {
        //商业险报价成功
        if ('14' == result.taskState) {
            dealQuoteSuccessResult context, result
        } else if (result.taskState in ['13', '30', '33'] ) {
            dealReInsureResult context, result
        } else if ('51' == result.taskState) {
            def forbidPolicyAdvice = result.errorMsg - '[' - ']'
            log.info '泛华回调信息:{}', forbidPolicyAdvice
            getKnownReasonErrorFSRV result.errorMsg
        } else {
            log.error '获取报价结果：{}，原因：{}', result.taskStateDescription, result.errorMsg
            getFatalErrorFSRV result.taskStateDescription
        }
    }

    private dealQuoteSuccessResult(context, result) {
        context.imageInfos = result.imageInfos //获取回调消息中的影像信息
        context.payValidTime = _DATE_FORMAT5.parse result.quoteValidTime
        context.insureSupplys = result.insureSupplys

        /**
         * 处理商业险
         */
        def bizInsureInfo = result.insureInfo.bizInsureInfo
        if (bizInsureInfo) {
            def transformedQuote = bizInsureInfo.riskKinds.inject [:], { output, item ->
                output + [(item.riskCode): item]
            }
            def commercialPremium = (bizInsureInfo.premium ?: 0) as double
            def iopPremium = (bizInsureInfo.nfcPremium ?: 0) as double
            populateQuoteRecord context, transformedQuote, context.kindCodeConvertersConfig, commercialPremium, iopPremium
        } else {
            disableCommercial context
        }

        /**
         * 处理交强险
         */
        def efcInsureInfo = result.insureInfo.efcInsureInfo
        if (efcInsureInfo) {
            def compulsoryPremium = (efcInsureInfo?.premium ?: 0) as double
            def autoTax = ((result.insureInfo.taxInsureInfo?.taxFee ?: 0) as double) + ((result.insureInfo.taxInsureInfo?.lateFee ?: 0) as double)
            populateQuoteRecordBZ context, compulsoryPremium, autoTax
        } else {
            disableCompulsoryAndAutoTax context
        }


        if (!bizInsureInfo && !efcInsureInfo) {
            getKnownReasonErrorFSRV '商业险和交强险全部报价失败'
        } else {
            context.quoteResult = result
            getContinueFSRV result
        }
    }

    private dealReInsureResult(context, result) {
        log.error '获取报价失败：{}', result.errorMsg
        if (result.errorMsg?.contains('重复投保') || result.errorMsg?.contains('未到期')) {
            def valuableHintTemplates = []
            if (isCommercialQuoted(context.accurateInsurancePackage) && result.errorMsg?.contains('商业险')) {
                valuableHintTemplates << _VALUABLE_HINT_COMMERCIAL_START_DATE_TEMPLATE_QUOTING
            }
            if (isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) && result.errorMsg?.contains('交强险')) {
                valuableHintTemplates << _VALUABLE_HINT_COMPULSORY_START_DATE_TEMPLATE_QUOTING
            }
            getProvideValuableHintsFSRV { valuableHintTemplates }
        } else {
            getFatalErrorFSRV result.errorMsg ?: result.taskStateDescription
        }
    }

}
