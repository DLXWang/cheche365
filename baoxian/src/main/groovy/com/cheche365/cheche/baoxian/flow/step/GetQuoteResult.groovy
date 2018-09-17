package com.cheche365.cheche.baoxian.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.baoxian.flow.Constants._COMPANY_I2O_MAPPINGS
import static com.cheche365.cheche.baoxian.util.BusinessUtils._ADVICE_REGULATOR_MAPPINGS
import static com.cheche365.cheche.baoxian.util.BusinessUtils._GET_EFFECTIVE_ADVICES
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_COMMERCIAL_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_COMPULSORY_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.parser.util.InsuranceUtils.adjustInsurancePackageFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getProvideValuableHintsFSRV

/**
 * Created by wangxin on 2017/3/24.
 */
@Component
@Slf4j
class GetQuoteResult extends AGetQuoteResult {

    @Override
    getQuoteResult(context) {
        def result = invokeAndWait _ASYNC_RUNNABLE, [
            timeoutInSeconds: getEnvProperty(context, 'baoxian.quoting_timeout_in_seconds') as long,
            preHandler      : _PRE_HANDLER.curry(_COMPANY_I2O_MAPPINGS[context.insuranceCompany.id], context),
            postHandler     : _POST_HANDLER,
            callbackHandler : context.messageHandler,
        ]
        log.info '伪同步，等待泛华报价异步回调结果，taskId：{}，保险公司：{}', context.taskId, context.insuranceCompany.code, result
        result
    }

    @Override
    resolveResult(context, result) {

        if (result) {
            if ('3' == result.taskState) {
                context.payValidTime = _DATE_FORMAT5.parse result.payValidTime

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
                    getLoopBreakFSRV result
                }
            } else if ('2' == result.taskState || '20' == result.taskState) {
                log.error '获取报价失败：{}', result.msg
                if (result.msg.contains('重复投保') || result.msg.contains('未到期')) {
                    def valuableHintTemplates = []
                    if (isCommercialQuoted(context.accurateInsurancePackage) && result.msg.contains('商业险')) {
                        valuableHintTemplates << _VALUABLE_HINT_COMMERCIAL_START_DATE_TEMPLATE_QUOTING
                    }
                    if (isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) && result.msg.contains('交强险')) {
                        valuableHintTemplates << _VALUABLE_HINT_COMPULSORY_START_DATE_TEMPLATE_QUOTING
                    }
                    getProvideValuableHintsFSRV { valuableHintTemplates }
                } else {
                    getFatalErrorFSRV result.msg
                }
            } else if ('18' == result.taskState) {
                def forbidPolicyAdvice = result.msg - '[' - ']'
                log.info '泛华回调信息:{}', forbidPolicyAdvice
                adjustInsurancePackageFSRV _ADVICE_REGULATOR_MAPPINGS, _GET_EFFECTIVE_ADVICES, result.msg, context
            } else {
                log.error '获取报价结果：{}，原因：{}', result.taskStateDescription, result.msg
                getFatalErrorFSRV result.msg
            }
        } else {
            getFatalErrorFSRV '获取报价失败，可能回调超时'
        }
    }

    private static final _PRE_HANDLER = { prvIdPrefix, context, log ->
        [context.taskId + '_' + prvIdPrefix, null]
    }

    private static final _POST_HANDLER = { result, tid, log ->
        result
    }

    private static final _ASYNC_RUNNABLE = { tid, log ->
        [tid, null]
    }

}
