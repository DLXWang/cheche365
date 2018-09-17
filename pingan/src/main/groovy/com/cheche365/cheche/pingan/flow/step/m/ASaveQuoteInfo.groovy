package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.common.flow.IStep
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_TRANSFER_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercialTimeCause
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.FlowUtils.getInsurancesNotAllowedFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getNeedSupplementInfoFSRV
import static groovy.json.StringEscapeUtils.escapeJava
import static groovyx.net.http.ContentType.BINARY



/**
 * 平安保存车辆信息基类
 * Created by wangxin on 2016/5/16.
 */
@Component
abstract class ASaveQuoteInfo implements IStep {

    private static final _API_PATH_SAVE_QUOTE_INFO = 'autox/do/api/save-quote-info'

    /**
     * circResult.resultCode的值为C3003，则表明商业险提前投保，所以禁用商业险，返回 商业险的状态 和 商业险提前投保的消息
     */
    private static _RH_C3003 = { context, circResult, log ->
        disableCommercialTimeCause context, 90, true, _DATE_FORMAT3
        log.warn '获取商业险报价失败，原因：{}', circResult.failMsg
        if (_FLOW_TYPE_RENEWAL_CHANNEL == context.flowType) {
            def commercialDate = getCommercialInsurancePeriodTexts(context).first
            getInsurancesNotAllowedFSRV commercialDate
        } else {
            getContinueWithIgnorableErrorFSRV false, circResult.failMsg
        }
    }

    /**
     * circResult.resultCode的值为C3009，表明为过户数据，要重新发送saveQuoteInfo的请求，
     * 并且为context.specialCarDate、context.specialCarFlag重新赋值
     */
    private static _RH_C3009 = { context, circResult, log ->
        log.info '该车辆为过户车辆，推过户日期补充信息'
        def transferDateSupplementInfo = _SUPPLEMENT_INFO_TRANSFER_DATE_TEMPLATE_QUOTING
        getNeedSupplementInfoFSRV { [transferDateSupplementInfo] }
    }

    /**
     * 表示saveQuoteInfo成功，返回商业险成功状态
     */
    private static _RH_C0000 = { context, circResult, log ->
        false
    }

    /**
     * 表示商业险投保查询失败，官网可以跳过
     */
    private static _RH_C3010 = { context, circResult, log ->
        log.warn '获取商业险报价失败，原因：{}', circResult.failMsg
        getContinueWithIgnorableErrorFSRV true, circResult.failMsg
    }

    /**
     * 表示未知code的情况，禁用商业险，返回消息
     */
    private static _RH_DEFAULT = { context, circResult, log ->
        log.warn '获取商业险报价失败，原因：{}', circResult
        getContinueWithIgnorableErrorFSRV false, circResult
    }

    private static _RH_MAPPINGS = [
        C3010  : _RH_C3010,
        C3003  : _RH_C3003,
        C3009  : _RH_C3009,
        C0000  : _RH_C0000,
        default: _RH_DEFAULT
    ]


    @Override
    def run(context) {
        def seatFSRV = getVehicleSeatSupplementInfoFSRV(context)
        if (seatFSRV) {
            return seatFSRV
        }

        def params = generateParams(context)
        def args = [
            contentType: BINARY,
            path       : _API_PATH_SAVE_QUOTE_INFO,
            query      : params
        ]

        RESTClient client = context.client

        def result = client.get args, { resp, stream ->

            def text = new StringWriter().with { writer ->
                writer << new InputStreamReader(stream)
            }.toString()
            text = $/$text/$
            new JsonSlurper().with {
                type = JsonParserType.LAX
                it
            }.parseText text.tokenize('\n').collect { line ->
                !line.contains('regex') ? line :
                    line[line.indexOf(':') + 1..-1].with { regex ->
                        def regexStr = regex =~ /"(.*)"/
                        "\"regex\" : \"${escapeJava(regexStr[0][1])}\","
                    }
            }.join('\n')
        }


        def code = result.resultCode
        if ('C0000' == code) {
            if ('C3010' == result.circResult?.resultCode) {
                log.error '商业险查询失败', result.circResult.failMsg
                // 该车续保但是需要强制走转保的标志，在ToQueryInfo步骤中请求参数中使用
                if (context.quoting) {
                    context.forceNonRenewal = true
                    return getContinueFSRV(false)
                } else {
                    return getKnownReasonErrorFSRV(result.circResult.failMsg)
                }
            }
            context.insuranceItemOptions = getInsuranceItemOptions(result)
            log.debug '该车辆所有险种的可选项列表：{}', context.insuranceItemOptions
            //获取必要信息优于商业险是否能投保的判断
            def necessaryInfo = getNecessaryInfo(context, result)
            log.info '报价相关的必要信息，车损、盗抢和自然的保额为：{}', necessaryInfo
            log.info '获取到的商业险起保日期为{}，交强险起保日期为{}', getCommercialInsurancePeriodTexts(context).first as String, getCompulsoryInsurancePeriodTexts(context).first as String

            //修改交强险日期
            def compulsoryStartDate = result.forceConfig.find { rule ->
                rule.name == 'forceInfo.beginDate'
            }?.value
            if (compulsoryStartDate) {
                setCompulsoryInsurancePeriodTexts context, compulsoryStartDate
                log.info '交强险起保日期为：{}', getCompulsoryInsurancePeriodTexts(context)
            }

            //判断商业险是否可投保
            if (result.circResult?.resultCode) {
                def fsrvOrFlag = checkCommercial(context, result.circResult)
                if (fsrvOrFlag) {
                    return fsrvOrFlag
                }
            }

            getContinueFSRV true
        } else if ('C2001' == code) {
            log.error '保存车辆信息失败，原因为：{}', result.resultMessage
            getFatalErrorFSRV '需要确认车型'
        } else if ('C2003' == code) {
            log.error '保存车辆信息失败，原因为：录入的信息无法找到车型，请核对录入信息'
            getFatalErrorFSRV '录入的信息无法找到车型'
        } else {
            log.error '保存车辆信息失败，响应为{}', result
            getFatalErrorFSRV '保存车辆信息失败'
        }
    }

    /**
     * 返回车辆座位数补充信息的FSRV
     * @param context
     */
    protected getVehicleSeatSupplementInfoFSRV(context) {
    }

    /**
     * 生成请求参数
     * @param context
     * @return
     */
    protected abstract generateParams(context)

    /**
     * 获取必要信息，如车座数，商业险和交强险起保日期以及车损、盗抢、自燃的保额
     * @param context
     * @param result
     * @return
     */
    private getNecessaryInfo(context, result) {
        def necessaryElement = ['bizConfig.amount01', 'bizInfo.beginDate', 'bizConfig.amount01Max', 'bizConfig.amount01Min', 'forceInfo.beginDate', 'vehicle.seats']
        def necessaryInfoMap = (result.bizConfig + result.forceConfig).findResults { item ->
            if (item.name in necessaryElement) {
                if (item.name == 'bizConfig.amount01') {
                    def inputAmounts = item.children.findResults { it ->
                        if (it.name == 'bizConfig.inputAmount') {
                            it
                        }
                    }
                    [(item.name): inputAmounts[-1].value]
                } else {
                    [(item.name): item.value]
                }
            }
        }.sum()

        context.passengerCount = necessaryInfoMap.'vehicle.seats' ?: 0
        setCommercialInsurancePeriodTexts(context, necessaryInfoMap.'bizInfo.beginDate')
        setCompulsoryInsurancePeriodTexts(context, necessaryInfoMap.'forceInfo.beginDate')
        //适用于获取费改城市车损，盗抢，自燃的保额，不适用于非费改城市的盗抢和自燃的保额，在bizQuote步骤中会获取自燃和盗抢的保额，并用覆盖的方式来记录。
        context.necessaryInfo = [
            seats                : necessaryInfoMap.'vehicle.seats',
            damageAmount         : necessaryInfoMap.'bizConfig.amount01',
            theftAmount          : necessaryInfoMap.'bizConfig.amount01',
            spontaneousLossAmount: necessaryInfoMap.'bizConfig.amount01'
        ]
    }

    private getInsuranceItemOptions(result) {
        def bizConfig = result.bizConfig
        (bizConfig.collect { it ->
            if (it.option) {
                [
                    (it.name): it.option.collect { option ->
                        option.value
                    }
                ]
            }
        } - null).sum()
    }

    /**
     * 检查商业险是否能是否可投保，并且按照返回数据里面的resultCode对套餐做不同的操作处理
     * @param circResult
     * @param context
     * @return
     */
    private checkCommercial(context, circResult) {
        def resultClosure = circResult.resultCode ? _RH_MAPPINGS[circResult.resultCode] ?: _RH_MAPPINGS.default : _RH_MAPPINGS.C0000
        resultClosure(context, circResult, log)
    }

}
