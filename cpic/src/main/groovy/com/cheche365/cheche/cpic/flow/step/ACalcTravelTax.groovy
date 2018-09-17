package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.parser.Constants
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.Constants._DATE_FORMAT5
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakWithIgnorableErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.cpic.util.BusinessUtils.loopAnswer
import static com.cheche365.cheche.parser.Constants._AUTO_TAX
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._INSURANCE_KIND_NAME_COMPULSORY
import static com.cheche365.cheche.parser.Constants._QUOTE_KIND_NAME_COMPULSORY
import static com.cheche365.cheche.parser.util.BusinessUtils.addQFSMessage
import static com.cheche365.cheche.parser.util.BusinessUtils.checkCompulsoryPackageOptionEnabled
import static com.cheche365.cheche.parser.util.BusinessUtils.countCanInsureDays
import static com.cheche365.cheche.parser.util.BusinessUtils.decodeValidationCode
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.InsuranceUtils.disableInsurancePackageOption
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 计算交强险报价
 * Created by xushao on 2015/7/22.
 */
@Component
@Slf4j
abstract class ACalcTravelTax implements IStep {

    private static final _PATTERN_ERROR_MESSAGE = /.*(\d{4}-\d{2}-\d{2}).*/

    private static final _HANDLE_ERROR_MESSAGE_ENROLL_DATE = { context, errorMessage ->
        def matcher = errorMessage =~ _PATTERN_ERROR_MESSAGE
        if (matcher.matches()) {
            def enrollDate = matcher[0][1]
            log.error '车辆初登日期与交管信息不一致，交管信息为：{}', enrollDate
            getKnownReasonErrorFSRV '初登日期与交管信息不一致'
        } else {
            log.error '错误信息与正则表达式不匹配：{}', errorMessage
        }
    }

    private static final _HANDLE_ERROR_MESSAGE_START_DATE = { context, errorMessage ->
        def matcher = errorMessage =~ _PATTERN_ERROR_MESSAGE
        if (matcher.matches()) {
            def compulsoryEndDate = matcher[0][1]
            log.warn '已投保交强险，保险期限至：{}', compulsoryEndDate
            def compulsoryStartDate = Constants.get_DATE_FORMAT3.format(Constants.get_DATE_FORMAT3.parse(compulsoryEndDate).plus(1))
            setCompulsoryInsurancePeriodTexts context, compulsoryStartDate
            getLoopContinueFSRV null, '交强险未到投保期'
        } else {
            log.error '错误信息与正则表达式不匹配：{}', errorMessage
        }
    }

    private static final _HANDLE_ERROR_MESSAGE_EARLY_INSURED_DAY = { context, errorMessage ->
        def matcher = errorMessage =~ /.*不能晚于当前日期＋(.*)天.*/
        if (matcher.matches()) {
            def earlyInsuredDay = matcher[0][1]
            def compulsoryStartDate = context.compulsoryInfo?.complusoryStartDate ?
                _DATE_FORMAT5.parse(context.compulsoryInfo.complusoryStartDate) :
                _DATE_FORMAT3.parse(context.travelTaxInfo.startDate)
            countCanInsureDays(
                context,
                _INSURANCE_KIND_NAME_COMPULSORY,
                _QUOTE_KIND_NAME_COMPULSORY,
                getLocalDate(compulsoryStartDate),
                earlyInsuredDay as long)
            log.warn '交强险最早可提前{}天投保', earlyInsuredDay
            disableCompulsoryAndAutoTax context
            getLoopBreakWithIgnorableErrorFSRV null, '交强险未到投保期'
        } else {
            log.error '错误信息与正则表达式不匹配：{}', errorMessage
        }
    }

    private static final _HANDLE_ERROR_MESSAGE_REPEAT_DEFAULT = { context, errorMessage ->
        log.warn '交强险重复投保，但未取得投保期'
    }

    private static final _HANDLE_ERROR_MESSAGE_DEFAULT = { context, errorMessage ->
        log.error '其他未知情况导致的交强险不可投：{}', errorMessage
    }

    private static final _ERROR_MESSAGE_HANDLER_MAPPINGS = [
        初始登记日期  : _HANDLE_ERROR_MESSAGE_ENROLL_DATE,
        交强险     : _HANDLE_ERROR_MESSAGE_START_DATE,
        重复投保    : _HANDLE_ERROR_MESSAGE_REPEAT_DEFAULT,
        不能晚于当前日期: _HANDLE_ERROR_MESSAGE_EARLY_INSURED_DAY,
        default : _HANDLE_ERROR_MESSAGE_DEFAULT
    ]


    @Override
    run(context) {
        if (!checkCompulsoryPackageOptionEnabled(context)) {
            return getLoopBreakWithIgnorableErrorFSRV(null, '交强险已禁用')
        }
        if (context.travelTaxInfo.payTaxStatus != '1') {
            disableInsurancePackageOption context, _AUTO_TAX
            addQFSMessage context, _AUTO_TAX, '所在的地区暂不支持网上代缴车船税'
        }

        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : apiPath,
            body              : generateRequestParameters(context, this)
        ]

        def result
        try {
            result = send context, client, args
        } catch (ex) {
            log.warn '计算交强险异常：{}，尝试重试', ex.message
            return getLoopContinueFSRV(null, '计算交强险异常')
        }

        log.info '交强险报价结果，{}', result
        if (result) {
            if (result.webBlockDescrtion) {
                def fsrv = processErrorMessage context, result.webBlockDescrtion
                if (fsrv) {
                    fsrv
                } else {
                    disableCompulsoryAndAutoTax context
                    getLoopBreakFSRV result
                }
            } else {
                if (result.checkCode) {
                    log.info '成功获取交强险转保验证码'
                    context.imageBase64 = result.checkCode
                    getContinueFSRV result.checkCode
                }
                setCompulsoryInsurancePeriodTexts context, result.startDate
                log.debug '获取交强险报价成功，报价为{}', result
                populateQuoteRecordBZ context, result.complusoryPreimum, result.vehicleTaxAmount
                getLoopBreakFSRV result
            }
        } else {
            log.error '交强险报价返回结果为null'
            getFatalErrorFSRV '交强险报价未知原因失败'
        }

    }

    abstract protected getApiPath()

    private static processErrorMessage(context, errorMessage) {
        def handleErrorMessage = _ERROR_MESSAGE_HANDLER_MAPPINGS.find { message, value ->
            errorMessage.contains message
        }?.value ?: _ERROR_MESSAGE_HANDLER_MAPPINGS.default
        handleErrorMessage context, errorMessage
    }

    private send(context, client, args) {
        def result = client.post args, { resp, data ->
            data
        }
        if (result.question) {
            context.questionAnswer = decodeValidationCode result.question
            result = loopAnswer context, apiPath, this
            log.info '需要验证问题返回结果，响应为{}', result
        }
        result
    }

}
