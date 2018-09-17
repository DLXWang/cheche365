package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.DateUtils.getDaysUntil
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT1
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT1
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.picc.util.BusinessUtils.getCommercialCanInsureDate
import static com.cheche365.cheche.picc.util.BusinessUtils.getDateTextFromPeriod
import static com.cheche365.cheche.picc.util.BusinessUtils.getEarlyDays4Commercial
import static com.cheche365.cheche.picc.util.BusinessUtils.getEarlyDays4Insuring
import static groovyx.net.http.ContentType.JSON
import static java.time.LocalDate.now as today



/**
 * 检查商业险到期
 * 续保和非续保客户的商业险周期校验都用这个接口
 */
@Component
@Slf4j
class CheckPeriod implements IStep {

    private static final _API_PATH_CHECK_PERIOD = '/ecar/proposal/proposalPeriodCheckNew'

    private static final _PATTERN_ERROR_MESSAGE = /.*(\d{4}-\d{2}-\d{2}).*/

    @Override
    run(context) {

        def today = today()
        def renewalInfo = context.renewalInfo

        if (renewalInfo) {
            def startDateText = renewalInfo.prpcmain.startdateStr
            def endDateText = renewalInfo.prpcmain.enddateStr
            def startHour = renewalInfo.prpcmain.starthour ?: 0
            def startDate = getLocalDate(_DATE_FORMAT1.parse(startDateText))
            if (startHour) {
                endDateText = _DATETIME_FORMAT1.format startDate.plusYears(1)
            }
            if (startDateText) {
                def earlyDays = getEarlyDays4Commercial context //续保取earlyDays4Insure
                if (today < startDate.minusDays(earlyDays)) { //续保不在投保期,起保日期改为T+5
                    startDateText = _DATETIME_FORMAT1.format(today.plusDays(5))
                    endDateText = _DATETIME_FORMAT1.format(today.plusYears(1).plusDays(4))
                }
            }

            def result = retrieveResult context, startDateText, endDateText, startHour
            log.info '续保商业险到期检查结果：{}', result

            if (result.message) {
                def m = result.message =~ _PATTERN_ERROR_MESSAGE
                if (m.matches() || 'RE' == result.message) {
                    log.warn '以默认参数进行商业险到期检查时出错，但是含有正确的日期信息：{}，这通常说明不在续保期内', result.message
                    def canInsureDate = getCommercialCanInsureDate context
                    disableCommercial context, _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE, canInsureDate
                    getContinueWithIgnorableErrorFSRV null, _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
                } else {
                    log.warn '以续保RenewalInfo中的起止日期{}、{}进行商业险到期检查时报错：{}', startDateText, endDateText, result.errorMsg
                    getFatalErrorFSRV result.message
                }
            } else {
                log.info '续保客户新商业险通过续保周期校验'
                getContinueFSRV result
            }
        } else {
            def periodInfo = context.period
            if (periodInfo?.message) {
                def (newStartDateText, newEndDateText) = getDateTextFromPeriod(periodInfo)
                if (newStartDateText) {
                    log.warn '以默认参数进行商业险到期检查时出错，但是含有正确的日期信息：{}', periodInfo.message
                    // 从保监会平台返回的正确的的终保日期
                    def lastEndDate = getLocalDate(_DATE_FORMAT1.parse(newStartDateText))

                    def earlyDays = getEarlyDays4Insuring context
                    if (today >= lastEndDate.minusDays(earlyDays)) {

                        log.info '以新的起止日期{}和{}再次进行非续保商业险到期检查', newStartDateText, newEndDateText
                        // 用从错误提示信息中获得的新的起始终止时间再次检查商业险是否到期
                        def newPeriodInfo = retrieveResult context, newStartDateText, newEndDateText

                        log.info '新起止日期非续保商业险到期检查结果：{}', newPeriodInfo

                        if (!newPeriodInfo.message) {
                            getContinueFSRV newPeriodInfo
                        } else {
                            log.warn '以新的起止日期进行商业险到期检查时依然报错：{}', newPeriodInfo.errorMsg
                            getFatalErrorFSRV newPeriodInfo.message
                        }
                    } else {
                        log.warn '当前日期不在{}天续保期内：终保日期{}、今天{}，二者相差{}天', earlyDays, lastEndDate, today, Math.abs(getDaysUntil(lastEndDate))
                        def canInsureDate = getCommercialCanInsureDate context
                        disableCommercial context, _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE, canInsureDate
                        getContinueWithIgnorableErrorFSRV null, _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
                    }
                } else if ('RE' == periodInfo.message) {
                    log.info '未到期，不能获取起保时间'
                    disableCommercial context, _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
                    getContinueWithIgnorableErrorFSRV null, _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
                } else {
                    log.warn '以默认起止日期进行商业险到期检查时出错：{}', periodInfo.errorMsg
                    getFatalErrorFSRV periodInfo.message
                }
            } else {
                log.info '非续保客户新商业险通过续保周期period校验'
                getContinueFSRV periodInfo
            }
        }
    }

    private retrieveResult(context, startDateText, endDateText, startHour = 0) {
        RESTClient client = context.client

        def args = createRequestParams context, startDateText, endDateText, startHour

        // 检查商业险是否到期
        client.post args, { resp, json ->
            json
        }
    }

    private createRequestParams(context, startDateText, endDateText, startHour) {
        [
            contentType: JSON,
            path       : _API_PATH_CHECK_PERIOD,
            body       : generateRequestParameters(context, this)
                + [startDateBI: startDateText]
                + [endDateBI: endDateText]
                + [starthourBI: startHour]
        ]
    }
}
