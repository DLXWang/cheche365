package com.cheche365.cheche.baoxian.flow.step

import com.cheche365.cheche.baoxian.flow.step.v2.ABaoXianCommonStep
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.baoxian.flow.Handlers._I2O_PREMIUM_CONVERTER
import static com.cheche365.cheche.baoxian.flow.Handlers.getAllKindItems
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getQuoteKindItemParams
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted

/**
 * 修改数据基类：传供应商信息和险种信息，前置规则拦截修改套餐信息，承保政策限制修改套餐信息，报价成功传所有信息
 * @author zhaoym
 */
@Component
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
abstract class AUpdateQuoteInfo extends ABaoXianCommonStep {

    private static final _API_PATH_UPDATE_QUOTE_INFO = ''

    @Override
    run(context) {

        def params = getParams(context)
        log.info '{} 步骤提交请求：{}',this.class.name, params
        def supplementFsrv = checkSupplementInfo(params)

        if (supplementFsrv) {
            supplementFsrv
        } else {
            def result = send context,prefix + _API_PATH_UPDATE_QUOTE_INFO, params
            getResultFSRV result,context
        }


    }

    protected static toFormedParams(context, kindCodeConvertersConfig) {
        context.kindCodeConvertersConfig = kindCodeConvertersConfig
        def ip = context.accurateInsurancePackage
        def (bizStartDateText, bizEndDateText) = getCommercialInsurancePeriodTexts(context, _DATETIME_FORMAT3)
        def (efcStartDateText, efcEndDateText) = getCompulsoryInsurancePeriodTexts(context, _DATETIME_FORMAT3)

        def efcInsureInfo = isCompulsoryOrAutoTaxQuoted(ip) ? [efcInsureInfo: [
            startDate: efcStartDateText,
            endDate  : efcEndDateText
        ]] : [:]
        def taxInsureInfo = [
            taxInsureInfo : [isPaymentTax: isCompulsoryOrAutoTaxQuoted(ip) ? 'Y' : 'N']
        ]
        def bizInsureInfo = isCommercialQuoted(ip) ? [
            bizInsureInfo: [
            startDate: bizStartDateText,
            endDate  : bizEndDateText,
            riskKinds: getQuoteKindItemParams(context, getAllKindItems(kindCodeConvertersConfig), kindCodeConvertersConfig, _I2O_PREMIUM_CONVERTER)
            ]
        ] : [:]
        efcInsureInfo + taxInsureInfo + bizInsureInfo
    }

    protected abstract getParams(context)

    protected abstract getResultFSRV(result,context)

    protected checkSupplementInfo(params) {}
}
