package com.cheche365.cheche.sinosig.flow.step

import groovy.util.logging.Slf4j
import org.apache.commons.lang3.time.DateUtils

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.isDefaultStartDate
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.InsuranceUtils.afterGeneratedRenewalPackage
import static com.cheche365.cheche.sinosig.flow.util.BusinessUtils.generateRenewalPackage
import static com.cheche365.cheche.sinosig.flow.util.BusinessUtils.getCommercialRequestParameters
import static com.cheche365.cheche.sinosig.flow.util.ComboUtils.adjustAccurateInsurancePackageAmount
import static com.cheche365.cheche.sinosig.flow.util.ComboUtils.adjustAmountRangeMapping
import static com.cheche365.cheche.sinosig.flow.util.ComboUtils.getAmountRangeMappingsFromList

/**
 * 获取报价套餐、起保日期
 */
@Slf4j
class PremiumBIKindList extends APremiumBI {

    @Override
    protected getResponseResult(context) {
        def (kindItemListInit, paraMap) = getKindItemQuote(context, getCommercialRequestParameters(context, 1, 2, 0))
        //续保套餐检查
        if (_FLOW_TYPE_RENEWAL_CHANNEL == context.flowType && !context.insurancePackage && kindItemListInit && paraMap) {
            context.insurancePackage = generateRenewalPackage(kindItemListInit, paraMap)
            log.info "续保车辆上年套餐：{}", context.insurancePackage
            afterGeneratedRenewalPackage context
        } else {
            getContinueWithIgnorableErrorFSRV null, '获取续保套餐失败'
        }
        def amountRangeMappings = getAmountRangeMappingsFromList kindItemListInit
        amountRangeMappings = adjustAmountRangeMapping amountRangeMappings
        adjustAccurateInsurancePackageAmount context.accurateInsurancePackage, amountRangeMappings

        log.info '初始套餐：{}', context.insurancePackage
        log.info '初始套餐副本：{}', context.accurateInsurancePackage

        context.kindItemList = kindItemListInit
        context.paraMap = paraMap
        def repeatInsuMessage = paraMap.repeat_insu_com
        // 核保时未在投保期结束流程
        if (!context.quoting && repeatInsuMessage?.contains('商业险重复投保')) {
            return getKnownReasonErrorFSRV(repeatInsuMessage)
        }
        def startDateText = (repeatInsuMessage =~ /.*商业险保单终保日期为：(\d{4}-\d{2}-\d{2})。.*/).with { m ->
            if (m.find()) {
                _DATE_FORMAT3.format(DateUtils.addDays(_DATE_FORMAT3.parse(m[0][1]), 1))
            }
        } ?: paraMap.insuApp
        // 修正商业险起保日期后修正保额
        if (!isDefaultStartDate(startDateText) && getCommercialInsurancePeriodTexts(context).first != startDateText) {
            setCommercialInsurancePeriodTexts context, startDateText
            log.info "商业险起保日期：{}", context.dateInfo.bsStartDateText
            getContinueFSRV startDateText
        } else {
            getLoopBreakFSRV null
        }
    }

}
