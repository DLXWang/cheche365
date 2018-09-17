package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.parser.Constants._INSURANCE_KIND_NAME_COMMERCIAL
import static com.cheche365.cheche.parser.Constants._INSURANCE_KIND_NAME_COMPULSORY
import static com.cheche365.cheche.parser.Constants._QUOTE_KIND_NAME_COMMERCIAL
import static com.cheche365.cheche.parser.Constants._QUOTE_KIND_NAME_COMPULSORY
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._EFFECTIVE_DATE
import static com.cheche365.cheche.parser.util.BusinessUtils.addQFSMessage
import static com.cheche365.cheche.parser.util.BusinessUtils.checkCompulsoryPackageOptionEnabled
import static com.cheche365.cheche.parser.util.BusinessUtils.countCanInsureDays
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.InsuranceUtils.adjustInsurancePackageFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils._ADVICE_REGULATOR_MAPPINGS
import static com.cheche365.cheche.zhongan.util.BusinessUtils._GET_EFFECTIVE_ADVICES
import static com.cheche365.cheche.zhongan.util.BusinessUtils._QUOTING_PARAM_INSURE_TYPE_MAPPING
import static com.cheche365.cheche.zhongan.util.BusinessUtils.getCustomPremiumParams
import static com.cheche365.cheche.zhongan.util.BusinessUtils.getStandardHintsFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive
import static com.cheche365.cheche.zhongan.util.BusinessUtils.getInstantInsurancePeriodTexts



/**
 * 报价
 */
@Component
@Slf4j
class QuotePrice implements IStep {

    private static final _SERVICE_NAME = 'zhongan.castle.policy.quotePrice'

    @Override
    def run(Object context) {
        //获取计算套餐的参数
        context.coverageInfoList = getCustomPremiumParams context
        def (bsStartDateText, bsEndDateText) = context.businessInstantFlag ? getInstantInsurancePeriodTexts() : getCommercialInsurancePeriodTexts(context, _DATETIME_FORMAT3, false)
        //商业险起保日期，即时投保日期根据当前时间获取
        def (bzStartDateText, bzEndDateText) = getCompulsoryInsurancePeriodTexts(context, _DATETIME_FORMAT3, false)
        //交强险起保日期
        def params = [
            insureFlowCode         : context.insureFlowCode,
            businessEffectiveDate  : bsStartDateText,
            businessExpireDate     : bsEndDateText,
            compelEffectiveDate    : bzStartDateText,
            compelExpireDate       : bzEndDateText,
            isInsureCompelInsurance: checkCompulsoryPackageOptionEnabled(context, context.accurateInsurancePackage) ? '1' : '0',//是否投保交强险 1-是，0-否,
            coverageList           : context.coverageInfoList,                                          //投保险种列表
            businessInstantFlag    : context.businessInstantFlag ?: ''//商业险即时起保标志
        ]

        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)

        log.debug "报价result: {}", result
        if ('0' == result.result) {
            context.quotePriceResult = result
            def quoteRecord = populateQuoteRecord context, result
            log.debug '组装后的新QuoteRecord：{}', quoteRecord
            getLoopBreakFSRV quoteRecord
        } else if ('2' == result.result) {
            context.quotePriceResult = result
            def quoteRecord = populateQuoteRecord context, result
            log.debug '报价有提示的情况下，组装后的新QuoteRecord：{}', quoteRecord
            //注意：此处与众安给出的回复不一致，所选险种不能进行即时起保时，result才为B10513
            if(result.resultMessage.indexOf('商业险已脱保')!=-1) {
                log.debug '脱保车提示信息：{}', result.resultMessage
                addQFSMessage context, _EFFECTIVE_DATE, result.resultMessage
                context.businessInstantFlag = '1'
            } else {
                def cmmercialEarlyDays = bsStartDateText ? countCanInsureDays(
                    context,
                    _INSURANCE_KIND_NAME_COMMERCIAL,
                    _QUOTE_KIND_NAME_COMMERCIAL,
                    getLocalDate(_DATE_FORMAT3.parse(bsStartDateText)),
                    90L) : ''

                def compulsoryEarlyDays = bzStartDateText ? countCanInsureDays(
                    context,
                    _INSURANCE_KIND_NAME_COMPULSORY,
                    _QUOTE_KIND_NAME_COMPULSORY,
                    getLocalDate(_DATE_FORMAT3.parse(bzStartDateText)),
                    90L) : ''

                log.info '重复投保，商业险可能提前: {} 天，交强险可能提前: {} 天', cmmercialEarlyDays, compulsoryEarlyDays
            }
            getContinueFSRV result
        } else if (result.result in _QUOTING_PARAM_INSURE_TYPE_MAPPING) {
            log.info "需要调整套餐 套餐建议 ：{}", result.resultMessage
            adjustInsurancePackageFSRV _ADVICE_REGULATOR_MAPPINGS, _GET_EFFECTIVE_ADVICES, result.result, context
        } else {
            getStandardHintsFSRV result
        }
    }
}
