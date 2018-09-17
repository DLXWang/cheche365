package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.cpic.flow.Constants._DATETIME_FORMAT_NEW as _DATETIME_FORMAT_NEW
import static com.cheche365.cheche.cpic.util.BusinessUtils._KIND_CODE_CONVERTERS_V3
import static com.cheche365.cheche.cpic.util.BusinessUtils.getChangedKindItems
import static com.cheche365.cheche.cpic.util.BusinessUtils.loopAnswer
import static com.cheche365.cheche.cpic.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.decodeValidationCode
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static groovyx.net.http.ContentType.JSON



/**
 * 获取商业险报价 新步骤，目前只有重庆使用 TODO
 * Created by xushao on 2015/7/21.
 */
@Component
@Slf4j
class CalcPremium implements IStep {

    private static final _API_PATH_CALCPREMIUM = 'cpiccar/salesNew/quotation/calcPremium'

    private static final _INSURANCE_COVERAGES = [
        damage              : [['车辆损失险', 'DamageLossCoverage', 'damageIop', "11024101"],
                               ['DamageLossExemptDeductibleSpecialClause', "11024119"]
                               , ['glass', 'engine', 'scratchAmount', 'spontaneousLoss']],
        scratchAmount       : [['划痕险', 'CarBodyPaintCoverage', 'scratchIop', "11024101"],
                               ['CarBodyPaintExemptDeductibleSpecialClause', "11024101"]],
        engine              : [['涉水损失险', 'PaddleDamageCoverage', 'engineIop', "11024103"],
                               ['PaddleDamageExemptDeductibleSpecialClause', "11024101"]],
        spontaneousLoss     : [['自燃损失险', 'SelfIgniteCoverage', "spontaneousLossIop", "11024107"],
                               ['SelfIgniteExemptDeductibleSpecialClause', "11024418"]],
        unableFindThirdParty: [['机动车损失保险无法找到第三方特约险', 'DamageLossCannotFindThirdSpecialCoverage', "", "11024422"], []],
        glass               : [['玻璃破碎险', 'GlassBrokenCoverage', "", "11024108"], []], //context.glassManufacturer
        thirdPartyAmount    : [['第三者责任险', 'ThirdPartyLiabilityCoverage', 'thirdPartyIop', "11024103"],
                               ['ThirdPartyLiabilityExemptDeductibleSpecialClause', "11024120"]],
        driverAmount        : [['车上司机责任险', 'InCarDriverLiabilityCoverage', 'driverIop', "11024105"],
                               ['InCarDriverLiabilityExemptDeductibleSpecialClause', "11024440"]],
        passengerAmount     : [['车上乘客责任险', 'InCarPassengerLiabilityCoverage', 'passengerIop', "11024106"],
                               ['InCarPassengerLiabilityExemptDeductibleSpecialClause', "11024441"]],
        theft               : [['全车盗抢损失险', 'TheftCoverage', 'theftIop', "11024102"],
                               ['TheftCoverageExemptDeductibleSpecialClause', "11024101"]]
    ]

    @Override
    run(context) {
        RESTClient client = context.client
        def coverageInfo = createCoverageInfo(context)
        context.coverageInfo = coverageInfo

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_CALCPREMIUM,
            body              : generateRequestParameters(context, this)
        ]

        def result
        try {
            result = client.post args, { resp, json ->
                json
            }
        } catch (ex) {
            log.warn '计算保费异常：{}，尝试重试', ex.message
            return getLoopContinueFSRV(null, '计算保费异常')
        }

        /**太平洋官网针对提交信息时的问题验证，至少发送3次请求，第1次请求参数无答案，响应问题；
         * 第2次请求参数包含答案，响应中得到商业险的起保和终保日期并作为第三次请求参数；
         * 第3次请求参数包含答案以及商业险起保日期*/
        5.times { time ->
            if (result.question) {
                context.questionAnswer = decodeValidationCode result.question
                setCommercialInsurancePeriodTexts context, result.commecialStartDateAnswer, _DATETIME_FORMAT_NEW
                result = loopAnswer(context)
            }
        }

        if (result) {
            if (result.total) {
                if ('T' == result.iLogQuoteFlag) {
                    getFatalErrorFSRV '报价失败'
                } else {
                    log.info '获取商业险报价成功，报价为{}', result
                    if (result.startDate) {
                        log.info '商业险投保日期修改为{}', result.startDate
                        setCommercialInsurancePeriodTexts context, result.startDate, _DATETIME_FORMAT_NEW
                    }
                    context.premiumInfo = result
                    context.newQuoteRecord = populateQuoteRecord(context)
                    getLoopBreakFSRV result
                }
            } else if (result.checkCode) {
                log.info '成功获得商业险转保验证码'
                context.imageBase64 = result.checkCode
                getContinueFSRV result.checkCode
            } else {
                def msg = result.totalBlockDescrtion ?: result.webBlockDescrtion
                log.error "获取商业险报价失败，${msg}"
                getFatalErrorFSRV "报价失败，${msg}"
            }
        } else {
            log.error '商业险报价返回结果为null'
            getFatalErrorFSRV '商业险报价未知原因失败'
        }
    }

    private loopAnswer(context) {
        loopAnswer(context, _API_PATH_CALCPREMIUM, this)
    }

    /**
     * TODO 确定这些参数对不对
     * 根据套餐来确定请求
     * @param context
     * @return
     */
    private createCoverageInfo(context) {

        def (quoteFieldStatus, coverageInfoList, amounts) = getChangedKindItems(_KIND_CODE_CONVERTERS_V3, context, _INSURANCE_COVERAGES)
        context.quoteFieldStatus = quoteFieldStatus
        context.amount = amounts.findAll { key, value ->
            !key.contains('engine') && !key.contains('unableFindThirdParty')
        }

        JsonBuilder builder = new JsonBuilder(coverageInfoList)
        builder.toString()
    }

}
