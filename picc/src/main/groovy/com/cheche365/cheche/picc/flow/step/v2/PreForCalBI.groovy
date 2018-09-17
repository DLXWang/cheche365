package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT1
import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_COMMERCIAL_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENROLL_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.picc.util.BusinessUtils.getNextDays4Commercial
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getProvideValuableHintsFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 计算商业险之前校验
 */
@Component
@Slf4j
class PreForCalBI implements IStep {

    private static final _API_PATH_PRE_FOR_CAL_BI = '/newecar/proposal/preForCalBI'

    @Override
    run(context) {

        RESTClient client = context.client

        log.info '{}，计算保费前校验', context.auto.licensePlateNo

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_PRE_FOR_CAL_BI,
            body              : generateRequestParameters(context, this)
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result.resultCode) {
            if ('0000' == result.resultCode) {
                log.info '{}，计算保费前校验成功：{}', context.auto.licensePlateNo, result.resultMsg
                getLoopBreakFSRV result.resultMsg
            } else if ('1000_E' == result.resultCode) {
                log.warn '车型校验失败：{}', result.resultMsg
                context.platFormModelCode = result.platFormModelCode
                getContinueFSRV 2
            } else if ('1000_B' == result.resultCode) { // 调整起保时间
                def m = result.resultUrl =~ /.*(\d{4}\/\d{2}\/\d{2}).*/
                if (m.matches()) {
                    def startDateNew = m[0][1]
                    log.info '获取到新的起保时间：{}', startDateNew
                    setCommercialInsurancePeriodTexts context, startDateNew, _DATETIME_FORMAT1, getNextDays4Commercial(context)
                    getLoopContinueFSRV null, result.resultMsg
                } else {
                    log.error '查询失败：{}', result.resultMsg
                    getKnownReasonErrorFSRV '起保时间过期'
                }
            } else if ('1000' == result.resultCode) {
                if (result.resultMsg.contains('请选择正确的起保日期')) {
                    log.error '起保日期校验失败：{}', result.resultMsg
                    getProvideValuableHintsFSRV { [_VALUABLE_HINT_COMMERCIAL_START_DATE_TEMPLATE_QUOTING] }
                } else if (result.resultMsg.contains('车架号与车型查询时上传的不一致')) {
                    log.error '车架号校验失败：{}', result.resultMsg
                    def hints = [
                        _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING.with {
                            it.originalValue = context.auto.vinNo
                            it
                        }
                    ]
                    getProvideValuableHintsFSRV { hints }
                } else if (result.resultMsg.contains('初始登记日期')) {
                    def m = result =~ /.*商业险连接平台失败车辆初始登记日期与交管信息不一致，交管信息为：(\d{4}-\d{2}-\d{2}).*/
                    if (m.matches()) {
                        def enrollDate = _DATE_FORMAT3.parse(m[0][1])
                        context.auto.enrollDate = enrollDate
                        log.info '获取到新的初登日期：{}', enrollDate
                        getLoopContinueFSRV null, result.resultMsg
                    } else {
                        def hints = [
                                _VALUABLE_HINT_ENROLL_DATE_TEMPLATE_QUOTING.with {
                                    it.originalValue = context.auto.enrollDate
                                    it
                                }
                        ]
                        getProvideValuableHintsFSRV { hints }
                    }
                } else if (result.resultMsg.contains('可明天继续访问投保')){
                    log.error '{}，计算保费前校验失败：您的操作信息存在异常，可明天继续访问投保', context.auto.licensePlateNo
                    getFatalErrorFSRV '操作信息异常，可明天继续访问投保'
                } else {
                    log.error '校验失败：{}', result.resultMsg
                    getFatalErrorFSRV '商业险平台查询失败'
                }
            } else if ('1001' == result.resultCode) {
                if (result.resultMsg.contains('车架号')) {
                    log.error '车架号校验失败：{}', result.resultMsg
                    def hints = [
                        _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING.with {
                            it.originalValue = context.auto.vinNo
                            it
                        }
                    ]
                    getProvideValuableHintsFSRV { hints }
                } else if(result.resultMsg.contains('初登日期')){
                    log.error '初登日期校验失败：{}', result.resultMsg
                    def hints = [
                        _VALUABLE_HINT_ENROLL_DATE_TEMPLATE_QUOTING.with {
                            it.originalValue = context.auto.enrollDate
                            it
                        }
                    ]
                    getProvideValuableHintsFSRV { hints }
                } /*
                // “核定载客量必须在4-5之间” 这个错误提示只有在测试的时候出现，我们的座位数是从官网上查出来的，不能填写，如果测试时人为改变座位数可能出现此错误，所以先暂时不处理
                else if (result.resultMsg.contains('核定载客量')) {
                    log.error '座位数校验失败：{}', result.resultMsg
                    getProvideValuableHintsFSRV { [_SUPPLEMENT_INFO_CAR_SEAT_COUNT_TEMPLATE_QUOTING] }
                } */
                else {
                    log.error '校验失败：{}', result.resultMsg
                    getFatalErrorFSRV '商业险平台查询失败'
                }
            } else if ('1000_CHECK' == result.resultCode) {
                log.info "获取到验证码"
                context.imageBase64 = result.checkCode
                getContinueFSRV 4
            } else {
                log.error '校验失败：{}', result.resultMsg
                getFatalErrorFSRV '商业险平台查询失败'
            }
        } else {
            getFatalErrorFSRV '算费前校验失败, 不明原因业务异常'
        }

    }

}
