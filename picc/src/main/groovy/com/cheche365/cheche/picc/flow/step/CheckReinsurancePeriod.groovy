package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.DateUtils.getDaysUntil
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT1
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.picc.flow.Constants._STATUS_CODE_PICC_CHECK_PERIOD_FAILURE
import static com.cheche365.cheche.picc.util.BusinessUtils.getCommercialCanInsureDate
import static com.cheche365.cheche.picc.util.BusinessUtils.getDateTextFromReinsurance
import static com.cheche365.cheche.picc.util.BusinessUtils.getEarlyDays4Insuring
import static groovyx.net.http.ContentType.JSON
import static java.time.LocalDate.now as today



/**
 * Created by suyq on 2015/7/9.
 * 检查商业险周期
 * 上海需要同时判断 checkPeriod和checkReinsurance中的时间
 */
@Component
@Slf4j
class CheckReinsurancePeriod implements IStep {

    private static final _API_PATH_CHECK_REINSURANCE = '/ecar/proposal/checkReinsurance'

    @Override
    run(context) {
        if (!context.insurancesCheckList.commercial.first()) {
            log.info '在之前的核保流程中已经得知由于时间问题而无法投保商业险'
            return getContinueFSRV(null)
        }
        RESTClient client = context.client

        def reinsuranceInfo = context.reinsurance
        if (reinsuranceInfo.startdate) {
            //以新的起止日期重新投保
            def (newStartDateText, newEndDateText) = getDateTextFromReinsurance(reinsuranceInfo)
            def newStartDate = getLocalDate(_DATE_FORMAT1.parse(newStartDateText))

            def today = today()
            def earlyDays = getEarlyDays4Insuring context
            if (newStartDate < today) {
                log.info '获取到的起保时间早于今天'
                getContinueFSRV reinsuranceInfo
            } else if (today >= newStartDate.minusDays(earlyDays)) {
                log.info '以新的起止日期{}和{}再次进行非续保商业险到期检查', newStartDateText, newEndDateText
                // 用从错误提示信息中获得的新的起始终止时间再次检查商业险是否到期
                def args = [
                    contentType : JSON,
                    path        : _API_PATH_CHECK_REINSURANCE,
                    body        : generateRequestParameters(context, this)
                                  + ['startDate' : newStartDateText]
                ]
                def result = client.post args, { resp, json ->
                    json
                }

                log.info '新起止日期商业险再保周期检查结果：{}', result

                if (!result.message) {
                    getContinueFSRV result
                } else {
                    log.warn '以新的起止日期进行商业险到期检查时依然报错：{}', result.errorMsg
                    [_ROUTE_FLAG_DONE, _STATUS_CODE_PICC_CHECK_PERIOD_FAILURE, null, result.message]
                }
            } else {
                log.warn '当前日期不在{}天续保期内：终保日期{}、今天{}，二者相差{}天', earlyDays, newStartDate, today, Math.abs(getDaysUntil(newStartDate))
                def canInsureDate = getCommercialCanInsureDate context
                disableCommercial context, _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE, canInsureDate
                getContinueWithIgnorableErrorFSRV null, _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
            }
        } else if (reinsuranceInfo.message?.contains('最早可在商业险到期前N天来投保')) {
            log.info '商业险未到期，但拿不到终保日期'
            disableCommercial context, _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
            getContinueWithIgnorableErrorFSRV null, _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
        } else {
            log.info '非续保客户新商业险通过续保周期reinsurance校验'
            getContinueFSRV reinsuranceInfo
        }
    }
}
