package com.cheche365.cheche.baoxian.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.service.TPsuedoSync
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.springframework.stereotype.Component

import static com.cheche365.cheche.baoxian.flow.Constants._COMPANY_I2O_MAPPINGS
import static com.cheche365.cheche.baoxian.flow.Constants._TASKID_TTL
import static com.cheche365.cheche.baoxian.util.BusinessUtils._ADVICE_REGULATOR_MAPPINGS
import static com.cheche365.cheche.baoxian.util.BusinessUtils._GET_EFFECTIVE_ADVICES
import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.core.model.LogType.Enum.BAOXIAN_35
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.parser.util.BusinessUtils.saveAppLog
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.InsuranceUtils.adjustInsurancePackageFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static java.util.concurrent.TimeUnit.HOURS

/**
 * Created by wangxin on 2017/3/24.
 */
@Component
abstract class AGetQuoteResult implements IStep, TPsuedoSync {

    @Override
    run(context) {
        //从redis中获取报价，如果没有对应的报价，则伪同步等待泛华回调。
        def globalContext = context.globalContext
        def failedResultKey = context.taskId + '_' + _COMPANY_I2O_MAPPINGS[context.insuranceCompany.id]
        def result = null

        if (globalContext.exists(failedResultKey)) {
            result = new JsonSlurper().parseText(globalContext.get(failedResultKey))
            log.info '{}获取缓存的报价失败结果：{}', failedResultKey, result
        } else {
            result = getQuoteResult(context)
        }

        def licensePlateNo = context.auto.licensePlateNo
        saveAppLog(context.logRepo, BAOXIAN_35, context.taskId, context.insuranceCompany?.name, result ? new JsonBuilder(result).toString() : '可能由于超时导致无法获得响应结果', this.class.name, "$licensePlateNo:response")

        log.info '{}的报价回调结果：{}', context.taskId, result

        if (result) {
            resolveResult(context, result)
        } else {
            getFatalErrorFSRV "${context.taskId}， 异步回调超时"
        }

    }

    protected resolveResult(context, result) {
        if (result) {
            //商业险报价成功
            if ('14' == result.taskState) {
                context.imageInfos = result.imageInfos //获取回调消息中的影像信息
                context.payValidTime = _DATE_FORMAT5.parse result.quoteValidTime
                context.insureSupplys = result.insureSupplys

                // 检查商业交强起保日期
                def fsrv = checkStartDate(context, result)
                if (fsrv) {
                    return fsrv
                }

                /**
                 * 处理商业险
                 */
                def bizInsureInfo = result.insureInfo.bizInsureInfo
                if (bizInsureInfo) {
                    def transformedQuote = bizInsureInfo.riskKinds.inject [:], { output, item ->
                        output + [(item.riskCode): item]
                    }
                    def commercialPremium = (bizInsureInfo.premium ?: 0) as double
                    def iopPremium = (bizInsureInfo.nfcPremium ?: 0) as double
                    populateQuoteRecord context, transformedQuote, context.kindCodeConvertersConfig, commercialPremium, iopPremium
                } else {
                    disableCommercial context
                }

                /**
                 * 处理交强险
                 */
                def efcInsureInfo = result.insureInfo.efcInsureInfo
                if (efcInsureInfo) {
                    def compulsoryPremium = (efcInsureInfo?.premium ?: 0) as double
                    def autoTax = ((result.insureInfo.taxInsureInfo?.taxFee ?: 0) as double) + ((result.insureInfo.taxInsureInfo?.lateFee ?: 0) as double)
                    populateQuoteRecordBZ context, compulsoryPremium, autoTax
                } else {
                    disableCompulsoryAndAutoTax context
                }

                if (!bizInsureInfo && !efcInsureInfo) {
                    getKnownReasonErrorFSRV '商业险和交强险全部报价失败'
                } else {
                    context.quoteResult = result
                    getLoopBreakFSRV result
                }
            } else if ('13' == result.taskState || '30' == result.taskState || '33' == result.taskState) {
                log.error '获取报价失败：{}', result.errorMsg
                def failedResultKey = context.taskId + '_' + _COMPANY_I2O_MAPPINGS[context.insuranceCompany.id]
                if (!context.globalContext.exists(failedResultKey)) {
                    context.globalContext.bindWithTTL(failedResultKey, new JsonBuilder(result).toString(), _TASKID_TTL, HOURS)
                    log.info '{} 首次报价，缓存的报价失败数据：{}', failedResultKey, context.globalContext.get(failedResultKey)
                } else {
                    log.info '{} 再次报价，缓存的报价失败数据：{}', failedResultKey, context.globalContext.get(failedResultKey)
                }
                getFatalErrorFSRV result.errorMsg ?: result.taskStateDescription
            } else if ('51' == result.taskState) {
                def forbidPolicyAdvice = result.errorMsg - '[' - ']'
                log.info '泛华回调信息:{}', forbidPolicyAdvice
                def fsrv = adjustInsurancePackageFSRV _ADVICE_REGULATOR_MAPPINGS, _GET_EFFECTIVE_ADVICES, result.errorMsg, context
                log.info '泛华taskId：{}，调整套餐后FSRV：{}', context.taskId, fsrv
                fsrv
            } else {
                log.error '获取报价结果：{}，原因：{}', result.taskStateDescription, result.errorMsg
                getFatalErrorFSRV result.taskStateDescription
            }
        } else {
            getFatalErrorFSRV '获取报价失败，可能回调超时'
        }
    }

    private checkStartDate(context, result) {
        def efcStartDateText = getCompulsoryInsurancePeriodTexts(context).first()
        def bizStartDateText = getCommercialInsurancePeriodTexts(context).first()
        log.info '商业起保日期：{},交强起保日期{}', bizStartDateText, efcStartDateText
        def efcStartDate = result.insureInfo?.efcInsureInfo?.startDate
        def bizStartDate = result.insureInfo?.bizInsureInfo?.startDate
        def isEfcStartDateChanged = efcStartDate && (efcStartDateText != efcStartDate.split(' ').first())
        def isBizStartDateChanged = bizStartDate && (bizStartDateText != bizStartDate.split(' ').first())
        if (isBizStartDateChanged || isEfcStartDateChanged) {
            if (isEfcStartDateChanged) {
                log.info '交强险变更日期为： {}', efcStartDate
                setCompulsoryInsurancePeriodTexts context, efcStartDate, _DATETIME_FORMAT3
            }
            if (isBizStartDateChanged) {
                log.info '商业险变更日期为：{}', bizStartDate
                setCommercialInsurancePeriodTexts context, bizStartDate, _DATETIME_FORMAT3
            }
            getContinueFSRV false
        }
    }

    abstract getQuoteResult(context)
}
