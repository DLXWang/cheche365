package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.QuoteFieldStatus
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT1
import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.Constants._DATE_FORMAT6
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakWithIgnorableErrorFSRV
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
import static com.cheche365.cheche.parser.Constants._INSURANCE_KIND_NAME_COMMERCIAL
import static com.cheche365.cheche.parser.Constants._INSURANCE_KIND_NAME_COMPULSORY
import static com.cheche365.cheche.parser.util.BusinessUtils.addQFS
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.resolveNewQuoteRecordInContext
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.InsuranceUtils.adjustInsurancePackageFSRV
import static com.cheche365.cheche.picc.flow.step.v2.util.BusinessUtils._ADVICE_REGULATOR_MAPPINGS
import static com.cheche365.cheche.picc.flow.step.v2.util.BusinessUtils._GET_EFFECTIVE_ADVICES
import static com.cheche365.cheche.picc.util.BusinessUtils.getDefaultStartDateTextCI
import static com.cheche365.cheche.picc.util.BusinessUtils.getEarlyDays4Commercial
import static com.cheche365.cheche.picc.util.BusinessUtils.getEarlyDays4Compulsory
import static com.cheche365.cheche.picc.util.BusinessUtils.getNextDays4Commercial
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static java.time.LocalDate.now as today



/**
 * 核保
 */
@Component
@Slf4j
class ToInsurance implements IStep {

    private static final _API_PATH_TOINSURANCE = '/newecar/proposal/toInsurance'

    @Override
    run(context) {
        RESTClient client = context.client

        def (ciStartDateText, ciEndDateText) = getDefaultStartDateTextCI(context, _DATETIME_FORMAT1)
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_TOINSURANCE,
            body              : [
                uniqueID   : context.uniqueID,
                packageName: 'OptionalPackage',
                biselect   : isCommercialQuoted(context.accurateInsurancePackage) ? 1 : 0,
                ciselect   : isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) ? 1 : 0,
                isRenewal  : context.isRenewal,
                cityCode   : context.cityCode,
                startDateCI: ciStartDateText,
                endDateCI  : ciEndDateText,
                startHourCI: getNextDays4Commercial(context) ? 0 : 23,
                endHourCI  : getNextDays4Commercial(context) ? 24 : 23
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        log.info '核保信息：{}', result.resultMsg
        if ('0000' == result.resultCode) {
            getLoopBreakFSRV result.resultMsg
        } else if ('1000_B' == result.resultCode) { // 调整起保时间
            log.info '获取到新的起保时间：{}', result.startDateNew
            setCommercialInsurancePeriodTexts context, result.startDateNew, _DATETIME_FORMAT1, getNextDays4Commercial(context)
            getLoopBreakFSRV true
        } else if ('1000' == result.resultCode) {
            def m = result.resultMsg =~ /.*商业险保单终保日期为(\d{4}-\d{2}-\d{2}).*/
            def m1 = result.resultMsg =~ /.*商业险尚未到期，终保日期为(\d*年\d*月\d*日).*/
            if (m.matches() || m1.matches()) {
                def lastYearEndDate = m.matches() ? _DATE_FORMAT3.parse(m[0][1]) : _DATE_FORMAT6.parse(m1[0][1])
                def startLocalDate = getLocalDate(lastYearEndDate)
                def earlyDays = getEarlyDays4Commercial context

                if (today() >= startLocalDate.minusDays(earlyDays)) {
                    setCommercialInsurancePeriodTexts context, result.startDateNew, _DATETIME_FORMAT1, getNextDays4Commercial(context)
                    getLoopBreakFSRV true
                } else {
                    addQFSWithCommercialPeriodCheck context, null, startLocalDate.minusDays(earlyDays)
                }
            } else {
                addQFSWithCommercialPeriodCheck context
            }
        } else if ('1000_C' == result.resultCode) {
            addQFSWithCommercialPeriodCheck context
        } else if ('1000_D' == result.resultCode) {
            def m = result.resultMsg =~ /.*交强险保单终保日期：(\d*年\d*月\d*日)(\d*)时.*/
            if (m.matches()) {
                def startLocalDate = getLocalDate(_DATE_FORMAT6.parse(m[0][1]))
                if ((m[0][2] as int) > 0 && getNextDays4Commercial(context)) {
                    startLocalDate = startLocalDate.plusDays(1)
                }
                def earlyDays = getEarlyDays4Compulsory context

                if (today() >= startLocalDate.minusDays(earlyDays)) {
                    setCompulsoryInsurancePeriodTexts context, _DATETIME_FORMAT1.format(startLocalDate), _DATETIME_FORMAT1, getNextDays4Commercial(context)
                    getLoopBreakFSRV true
                } else {
                    addQFSWithCompulsoryPeriodCheck context, null, startLocalDate.minusDays(earlyDays)
                }
            }
        } else { // 除了时间原因的套餐建议走既有机制
            adjustInsurancePackageFSRV _ADVICE_REGULATOR_MAPPINGS, _GET_EFFECTIVE_ADVICES, result.resultMsg, context
        }
    }

    private static addQFSWithCommercialPeriodCheck(context, errorMessage = null, canInsureDate = null) {
        if (context.quoting) {
            addQFS(resolveNewQuoteRecordInContext(context),
                new QuoteFieldStatus(
                    filedName: _INSURANCE_KIND_NAME_COMMERCIAL,
                    description: _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE)
            )
            getLoopBreakFSRV _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
        } else {
            disableCommercial context, errorMessage, canInsureDate
            getLoopBreakWithIgnorableErrorFSRV null, _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
        }
    }

    private static addQFSWithCompulsoryPeriodCheck(context, errorMessage = null, canInsureDate = null) {
        if (context.quoting) {
            addQFS(resolveNewQuoteRecordInContext(context),
                new QuoteFieldStatus(
                    filedName: _INSURANCE_KIND_NAME_COMPULSORY,
                    description: _ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE)
            )
            getLoopBreakFSRV _ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
        } else {
            disableCompulsoryAndAutoTax context, null, errorMessage, canInsureDate
            getLoopBreakWithIgnorableErrorFSRV null, _ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
        }
    }

}
