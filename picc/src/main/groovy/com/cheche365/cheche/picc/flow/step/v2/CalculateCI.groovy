package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.Constants._DATE_FORMAT6
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getResponseResult
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT1
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.picc.util.BusinessUtils.getDefaultStartDateTextCI
import static com.cheche365.cheche.picc.util.BusinessUtils.getEarlyDays4Compulsory
import static com.cheche365.cheche.picc.util.BusinessUtils.getNextDays4Commercial
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static java.time.LocalDate.now as today



/**
 * 计算交强险
 */
@Component
@Slf4j
class CalculateCI implements IStep {

    private static final _API_PATH_CALCULATE_CI = '/newecar/calculate/calculateCI'


    @Override
    run(context) {
        if (!isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage)) {
            log.warn '未投保交强险，跳过此步骤'
            return getContinueFSRV(null)
        }

        RESTClient client = context.client

        def (ciStartDateText, ciEndDateText) = getDefaultStartDateTextCI(context, _DATETIME_FORMAT1)
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_CALCULATE_CI,
            body              : [
                uniqueID   : context.uniqueID,
                areaCode   : context.areaCode,
                cityCode   : context.cityCode,
                ciselect   : 1,
                packageName: 'OptionalPackage',
                startDateCI: ciStartDateText,
                endDateCI  : ciEndDateText
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if ('成功' == result.resultMsg) {
            // 天津暂不支持投保车船税
           getResponseResult(result, context, this)
        } else if ('1000_B' == result.resultCode) {
            def m = result.resultMsg =~ /.*交强险保险期间与上张保单有重复.*>(\d*年\d*月\d*日)(\d*)时.*/
            if (m.matches()) {
                def startLocalDate = getLocalDate(_DATE_FORMAT6.parse(m[0][1]))
                if ((m[0][2] as int) > 0 && getNextDays4Commercial(context)) {
                    startLocalDate = startLocalDate.plusDays(1)
                }
                def earlyDays = getEarlyDays4Compulsory context
                if (today() >= startLocalDate.minusDays(earlyDays)) {
                    setCompulsoryInsurancePeriodTexts context, _DATETIME_FORMAT1.format(startLocalDate), _DATETIME_FORMAT1, getNextDays4Commercial(context)
                    getContinueFSRV(true)
                } else {
                    disableCompulsoryAndAutoTax context, null, null, startLocalDate.minusDays(earlyDays)
                    log.info '交强险报价失败，官网提示信息：{}', result.resultMsg
                    getContinueWithIgnorableErrorFSRV result, _ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
                }
            }
        } else if ('1000_C' == result.resultCode) {
            def m = result.resultMsg =~ /.*您当前的交强险尚未到期，终保日期为(\d*年\d*月\d*日).*/
            if (m.matches()) {
                def startLocalDate = getLocalDate(_DATE_FORMAT6.parse(m[0][1]))
                if (getNextDays4Commercial(context)) {
                    startLocalDate = startLocalDate.plusDays(1)
                }
                def earlyDays = getEarlyDays4Compulsory context
                if (today() >= startLocalDate.minusDays(earlyDays)) {
                    setCompulsoryInsurancePeriodTexts context, _DATETIME_FORMAT1.format(startLocalDate), _DATETIME_FORMAT1, getNextDays4Commercial(context)
                    getContinueFSRV true
                } else {
                    disableCompulsoryAndAutoTax context, null, null, startLocalDate.minusDays(earlyDays)
                    log.info '交强险报价失败，官网提示信息：{}', result.resultMsg
                    getContinueWithIgnorableErrorFSRV result, _ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
                }
            } else {
                disableCompulsoryAndAutoTax context
                log.info '交强险报价失败，官网提示信息：{}', result.resultMsg
                getContinueWithIgnorableErrorFSRV result, _ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
            }
        } else {
            disableCompulsoryAndAutoTax context
            log.info '交强险报价失败，官网提示信息：{}', result.resultMsg
            getContinueWithIgnorableErrorFSRV result, _ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
        }
    }

}
