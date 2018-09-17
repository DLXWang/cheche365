package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getResponseResult
import static com.cheche365.cheche.cpicuk.flow.Handlers._KIND_ITEM_CONVERTERS_CONFIG
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.getNotQuotedPolicyCauseFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getNeedSupplementInfoFSRV
import static groovyx.net.http.ContentType.JSON
import static java.math.BigDecimal.ROUND_HALF_UP



/**
 * 获取商业险和交强险报价
 */
@Component
@Slf4j
class CalculatePremium implements IStep {


    private static final _API_PATH_CALCULATE_PREMIUM = '/ecar/insure/calculate'

    @Override
    run(context) {

        def fsrv = getNotQuotedPolicyCauseFSRV context
        if (fsrv) {
            return fsrv
        }
        // 太平洋商业险时间 2018-05-20 00:00  2019-05-20 00:00  在handler中转换
        if (isCommercialQuoted(context.accurateInsurancePackage)) {
            def commercialStartDate = _DATE_FORMAT3.format(context.extendedAttributes?.commercialStartDate)
            // 设置商业险起止时间
            setCommercialInsurancePeriodTexts context, commercialStartDate, _DATETIME_FORMAT3, false
        }

        if (isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage)) {
            def compulsoryStartDate = _DATE_FORMAT3.format(context.extendedAttributes?.compulsoryStartDate)
            // 设置交强险险起止时间
            setCompulsoryInsurancePeriodTexts context, compulsoryStartDate, _DATETIME_FORMAT3, false
        }

        RESTClient client = context.client

        def result = client.request(Method.POST) { req ->
            requestContentType = JSON
            contentType = JSON
            uri.path = _API_PATH_CALCULATE_PREMIUM
            body = new JsonBuilder(generateRequestParameters(context, this)).toString()
            log.debug body
            response.success = { resp, json ->
                json
            }

            response.failure = { resp, json ->
                json
            }
        }
        log.debug '报价结果  ：{}', result.result
        def res = getResponseResult result, context, this
        if (res?.state == '1') {
            log.info '报价成功'
            populateQR context, _KIND_ITEM_CONVERTERS_CONFIG, result.result.quoteInsuranceVos,
                result.result.compulsoryInsuransVo, result.result.ecarvo.seatCount, result.result.commercialInsuransVo?.stPremium
            context.calculateResult = result.result
            getContinueFSRV result.result
        } else if (res.state == '0') {
            //要验证码，推验证码
            getNeedSupplementInfoFSRV { context.needSupplementInfos }
        } else if (res.state == '3') {
            log.debug '录入校验码有误，重新获取验证码'
            getContinueFSRV '重新获取报价验证码'
        } else if (res.state == '4') {
            log.debug '所选车型有误，重新报价,调整车型:{}', res.msg
            context.autoModels = res.msg
            getContinueFSRV '重新报价'
        } else {
            getKnownReasonErrorFSRV res?.msg ?: 'res 没有处理结果'
        }
    }


    private static populateQR(context, kindCodeConvertersConfig, bizInfo, forceInfo, seatCount, totalPremium) {

        if (isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage)) {
            populateQuoteRecordBZ(context, str2double(forceInfo.stCipremium), str2double(forceInfo.stTaxAmount))
        } else {
            disableCompulsoryAndAutoTax context
        }

        if (isCommercialQuoted(context.accurateInsurancePackage)) {
            def allKindItems = getQuotedItems(bizInfo, seatCount)
            populateQuoteRecord(context, allKindItems, kindCodeConvertersConfig, str2double(totalPremium), null)
        } else {
            disableCommercial context
        }

    }


    static getQuotedItems(items, seatCount) {
        items.collectEntries {
            [
                (it.insuranceCode): [
                    amount       : str2double(it.amount),
                    premium      : str2double(it.premium),
                    iopPremium   : null,
                    quantity     : (seatCount as int) - 1,//乘客的数量
                    glassType    : it.factorVos == null ?: it.factorVos[0]?.factorValue, //玻璃类型  (默认就是1 国产)
                    nonDeductible: str2double(it.stNonDeductible)
                ]
            ]
        }
    }

    static str2double(value) {
        ((value ?: 0) as BigDecimal).setScale(2, ROUND_HALF_UP).doubleValue()
    }
}

