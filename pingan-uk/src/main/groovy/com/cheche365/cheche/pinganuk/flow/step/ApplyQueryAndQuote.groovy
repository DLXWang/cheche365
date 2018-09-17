package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT2
import static com.cheche365.cheche.common.Constants._DATE_FORMAT5
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.DateUtils.getDaysUntil
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getNotQuotedPolicyCauseFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.htmlParser
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.parser.util.InsuranceUtils.addEffectiveDatesQFSMessage
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.getAllKindItems
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.POST



/**
 * 报价
 * Created by liheng on 2016-08-30 0030.
 */
@Component
@Slf4j
class ApplyQueryAndQuote implements IStep {

    private static final _URL_APPLY_QUERY_AND_QUOTE = '/icore_pnbs/do/app/quotation/applyQueryAndQuote'

    @Override
    run(context) {

        def fsrv = getNotQuotedPolicyCauseFSRV context
        if (fsrv) {
            return fsrv
        }

        RESTClient client = context.client

        client.request(POST, TEXT) { req ->
            uri.path = _URL_APPLY_QUERY_AND_QUOTE
            headers = [
                dataSource: context.baseInfo?.systemId,
                userId    : context.baseInfo?.umCode
            ]
            body = new JsonBuilder(generateRequestParameters(context, this)).toString()

            response.success = { resp, reader ->
                def result = new JsonSlurper().parse reader
                def applyQueryResult = result.applyQueryResult
                updateContext context, applyQueryResult

                return quotedResult(context, result, applyQueryResult)
            }

            response.failure = { resp, reader ->
                def errorMsg = htmlParser.parse(reader).depthFirst().with { html ->
                    html.ERRORMSG ? html.ERRORMSG.first().text() : html.BODY.text().tokenize('\n').first()
                }

                log.error '报价非预期异常，状态码：{}，错误信息：{}', resp.status, errorMsg ?: '非业务异常'

                getKnownReasonErrorFSRV errorMsg
            }
        }

    }

    /**
     * 成功报价
     * @param context
     * @param result
     */
    private static quotedSuccess(context, result) {
        def c01ResultDTO = result.c01CaculateResult?.c01ResultDTO
        def c51ResultDTO = result.c51CaculateResult?.c51ResultDTO
        if (isCommercialQuoted(context.accurateInsurancePackage)) {
            def allKindPremium = getAllKindItems result.voucher.c01DutyList
            log.debug 'allKindPremium：{}', allKindPremium

            populateQuoteRecord(context, allKindPremium, context.kindItemConvertersConfig,
                getDoubleValue(c01ResultDTO?.totalAgreePremium))
        }

        def compulsory = getDoubleValue c51ResultDTO?.totalAgreePremium
        def autoTax = getDoubleValue result.voucher?.vehicleTaxInfo?.totalTaxMoney
        populateQuoteRecordBZ context, compulsory, autoTax
        context.c51CircInfoDTO = c51ResultDTO?.circInfoDTO
        context.circInfoDTO = context.applyQueryResult.circInfoDTO
        context.c01DutyList = result.voucher.c01DutyList
        context.c51DutyList = result.voucher.c51DutyList
        context.voucher = mergeMaps true, context.voucher, result.voucher
        addEffectiveDatesQFSMessage context
        getLoopBreakFSRV context.voucher
    }

    /**
     * 报价结果处理
     * @param context
     * @param result
     * @return
     */
    private static quotedResult(context, result, applyQueryResult) {
        def voucherNo = result.voucher?.quotationNo
        context.voucherNo = voucherNo
        log.info 'quotationNo：{}', voucherNo
        def bsLastYearEndDateText = applyQueryResult.circInfoDTO?.thirdVehicleInfoDTO?.busilastyearenddate
        if (bsLastYearEndDateText) {
            /**
             * 二次报价前，上送lastPolicyNo，commercialClaimRecord，crossCommissionRate参数，如有问题，可改成无voucherNo时，再上送。
             * liuyue
             * 20180515
             */
            context.c01LastPolicyNo = result.c01PolicyList?.lastPolicyNo?.get(0)
            context.c01CommercialClaimRecord = result.voucher?.c01ExtendInfo?.commercialClaimRecord
            context.c01CrossCommissionRate = result.voucher?.c01ExtendInfo?.crossCommissionRate

            context.busilastyearenddate = bsLastYearEndDateText
            def beginDate = getLocalDate(_DATE_FORMAT5.parse(bsLastYearEndDateText)).plusDays(1)
            def untilNowDay = getDaysUntil(beginDate)
            if (untilNowDay < 0 && untilNowDay >= -90) { // untilNowDay商业险到期时间到今天的天数
                def beginDateText = _DATETIME_FORMAT2.format beginDate.atTime(0, 0, 0)
                if (beginDateText != result.voucher?.c01BaseInfo?.insuranceBeginTime) {
                    setCommercialInsurancePeriodTexts context, beginDateText
                    log.debug '商业险起保日期： {} {}', context.dateInfo.bsStartDateText, context.dateInfo.bsEndDateText
                    return getContinueFSRV(true)  // 修正商业起保日期，重新计算折损价
                }
            }
        }

        def errorCode = applyQueryResult?.errorCode
        def decisionTreeResult = result.c01CaculateResult?.c01ResultDTO?.decisionTreeResult

        if (voucherNo && (result.isAutoSaveFlag || '0' == errorCode)) {
            context.applyQueryResult = applyQueryResult
            quotedSuccess context, result
        } else if ('-400' == errorCode || '0' == decisionTreeResult?.isQuote) {
            def advices = '-400' == errorCode ? applyQueryResult.errorMessage : decisionTreeResult.modifyAdvise
            log.error '商业险报价失败：{}', advices

            getKnownReasonErrorFSRV advices
        } else {
            /**
             * afterForceQuote、renewalAndC51DisTax、showCompareList状态需要第二次报价
             * renewalAndC51DisTax、showCompareList 出示机动车出险相关的费率告知单
             * allDone、showNotice状态表示报价成功
             * chooseVehType出现时商业险起期超过提前投保最大天数
             */
            if ('allDone' == context.processType) {
                getFatalErrorFSRV '报价失败：未知业务异常'
            } else {
                context.processType = result.processType
                getLoopContinueFSRV result, "processType：${result.processType}，再次报价"
            }
        }
    }

    private static updateContext(context, applyQueryResult) {
        def coveragePremiumList = applyQueryResult?.coveragePremiumList
        context.pureRiskPremium01 = coveragePremiumList ? coveragePremiumList?.first()?.pureRiskPremium : null
        context.nonClaimAdjust = applyQueryResult?.circInfoDTO?.thirdVehicleInfoDTO?.nonclaimAdjust // 非索赔调整
        context.claimRecordList = applyQueryResult?.circInfoDTO?.claimRecordList ?: context.claimRecordList
        context.commercialClaimRecord = applyQueryResult?.circCommercialClaimRecord ?: context.commercialClaimRecord
        context.stdVehicleInfoDTO = applyQueryResult?.circInfoDTO?.stdVehicleInfoDTO ?: context.stdVehicleInfoDTO
    }

    private static getDoubleValue(textNum) {
        textNum ? textNum as double : 0
    }

}
