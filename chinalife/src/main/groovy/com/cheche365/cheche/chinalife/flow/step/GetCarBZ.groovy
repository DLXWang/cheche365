package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.chinalife.util.BusinessUtils.getAutoTaxPremium
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getNewCalculateBZDateText
import static com.cheche365.cheche.chinalife.util.BusinessUtils.startDateInPeriod
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakWithIgnorableErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._INSURANCE_KIND_NAME_COMPULSORY
import static com.cheche365.cheche.parser.Constants._INSURANCE_PACKAGE_FIELD_NAME_DESCRIPTION_MAPPINGS
import static com.cheche365.cheche.parser.Constants._QUOTE_KIND_NAME_COMPULSORY
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
import static com.cheche365.cheche.parser.util.BusinessUtils.addQFSMessage
import static com.cheche365.cheche.parser.util.BusinessUtils.checkCompulsoryPackageOptionEnabled
import static com.cheche365.cheche.parser.util.BusinessUtils.countCanInsureDays
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 交强险报价
 */
@Component
@Slf4j
class GetCarBZ implements IStep {

    private static final _URL_CAR_BZ = '/online/saleNewCar/carProposalproposalBZ.do'

    @Override
    run(context) {
        if (!checkCompulsoryPackageOptionEnabled(context)) {
            return getContinueFSRV(null)
        }

        def client = context.client
        def args = [
            requestContentType : URLENC,
            contentType        : JSON,
            path               : _URL_CAR_BZ,
            body               : generateRequestParameters(context, this)
        ]

        def quote = client.post args, { resp, json ->
            json
        }

        def compulsoryEnabled = true            //是否能上交强
        def compulsoryDisabledCauseTime = false //不能上交强是因为投保时间原因

        def compulsoryPremium, autoTaxPremium //交强险和车船税金额
        compulsoryPremium = quote.temporary.quoteMain.geQuoteRisks?.get(1)?.sumPremium
        autoTaxPremium = getAutoTaxPremium quote.temporary.quoteMain.geQuoteCars?.get(0)?.geQuoteCarTax

        if (!compulsoryPremium) {
            log.warn '以默认参数请求交强险报价时出错，但是错误信息中含有正确的日期信息：{}', quote.temporary.resultMessage

            def (newBZStartDateText, newBZEndDateText) = getNewCalculateBZDateText(quote.temporary.resultMessage)
            if (newBZStartDateText) {
                // 判断如果新日期不在投保期，认为不能报价
                if (!startDateInPeriod(context, newBZStartDateText, 'UIBZStartDateMaxMessage')) {
                    log.info '获取的新起保日期{}不在{}天投保期内', newBZStartDateText, context.carVerify.UIBZStartDateMaxMessage
                    compulsoryEnabled = false
                    compulsoryDisabledCauseTime = true
                } else {
                    setCompulsoryInsurancePeriodTexts context, newBZStartDateText
                    log.info '以新的起止日期{}和{}请求交强险报价', newBZStartDateText, newBZEndDateText
                    return getLoopContinueFSRV(true, '调整起保日期，交强险重新报价')
                }
            } else {
                log.warn '以默认或获取的起保日期交强险报价时出错：{}', quote.temporary.resultMessage
                compulsoryEnabled = false
                compulsoryDisabledCauseTime = false
            }
        }

        //获取到交强险报价而没获取到车船税报价 compulsoryPremium && !autoTaxPremium
        if (compulsoryPremium && '1' != quote.area) {
            context.needGetAutoTax = true
        }

        if (compulsoryEnabled) {
            context.compulsoryEnabled = true
            context.compulsoryPremium = compulsoryPremium
            context.autoTaxPremium = autoTaxPremium
            getLoopBreakFSRV quote
        } else {
            if (context.quoting) {
                disableCompulsoryAndAutoTax context
                log.info '禁用交强险及车船税套餐项'
                if (context.dateInfo?.bzStartDateText) {
                    //未到期且拿到了起保日期，计算距窗口期的时间，并放入quoteFieldStatus中
                    countCanInsureDays(context,
                        _INSURANCE_KIND_NAME_COMPULSORY,
                        _QUOTE_KIND_NAME_COMPULSORY,
                        getLocalDate(_DATE_FORMAT3.parse(context.dateInfo?.bzStartDateText)),
                        context.carVerify.UIBZStartDateMaxMessage as int)
                } else {
                    addQFSMessage(context, _INSURANCE_KIND_NAME_COMPULSORY, "${_INSURANCE_PACKAGE_FIELD_NAME_DESCRIPTION_MAPPINGS.compulsory}不在投保期内")
                }

                getLoopBreakWithIgnorableErrorFSRV null, '交强险不在投保期内'
            } else {
                if (compulsoryDisabledCauseTime) {
                    disableCompulsoryAndAutoTax context, _ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
                    getLoopBreakWithIgnorableErrorFSRV null, _ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
                } else {
                    disableCompulsoryAndAutoTax context
                    getLoopBreakWithIgnorableErrorFSRV null, '计算交强险失败'
                }
            }
        }
    }

}
