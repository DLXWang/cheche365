package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
import static com.cheche365.cheche.parser.Constants._INSURANCE_KIND_NAME_COMPULSORY
import static com.cheche365.cheche.parser.util.BusinessUtils.addQFSMessage
import static com.cheche365.cheche.parser.util.BusinessUtils.getNotQuotedPolicyCauseFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.parser.util.InsuranceUtils.addEffectiveDatesQFSMessage
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getAllKindItems
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static java.math.BigDecimal.ROUND_HALF_UP



/**
 * 获取商业险和交强险报价
 */
@Component
@Slf4j
class CalculatePremium implements IStep {

    private static final _API_PATH_CALCULATE_PREMIUM = '/prpall/business/caculatePremiunForFG.do'

    @Override
    run(context) {

        log.debug 'Start-获取人保报价结果'
        def fsrv = getNotQuotedPolicyCauseFSRV context
        if (fsrv) {
            return fsrv
        }

        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_CALCULATE_PREMIUM,
            body              : generateRequestParameters(context, this)
        ]

        def result = client.post args, { resp, json ->
            json
        }

        def m = result =~ /[\s\S]*车型与平台返回的车型不一致[\s\S]*是否使用平台返回车型,([A-Za-z0-9]*)[\s\S]*/

        if (1 == result.totalRecords) {
            def resultData = result.data.first() // 报价结果
            def errMessage = resultData.errMessage
            if (errMessage) {
                log.error '报价失败：{}', errMessage
                return getKnownReasonErrorFSRV(errMessage)
            }

            def biVoList = resultData.biInsuredemandVoList ? resultData.biInsuredemandVoList.first() : null
            def ciVOList = resultData.ciInsureVOList ? resultData.ciInsureVOList.first() : null
            context.prpCsaless = resultData.prpCsaless
            context.biVoList = biVoList
            context.ciVOList = ciVOList

            // 商业险报价
            if (isCommercialQuoted(context.accurateInsurancePackage)) {
                if ('0000' == biVoList?.errorMessageVo?.errorCode) {
                    def kindItems = biVoList?.prpCitemKinds
                    log.debug '商业险报价结果：{}', kindItems

                    def allKindItems = getAllKindItems kindItems
                    def qr = populateQuoteRecord context, allKindItems, context.kindItemConvertersConfig, 0
                    qr.calculatePremium()
                } else {
                    def remark = biVoList?.ciInsureDemandDAA?.remark
                    log.debug '商业险报价失败：{}', remark
                    return getKnownReasonErrorFSRV(remark)
                }
            }

            // 交强险报价
            if (isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage)) {
                def ciDemand = ciVOList?.ciInsureDemand
                def ciTax = ciVOList?.ciInsureTax
                if ('0000' == ciVOList?.errorMessageVo?.errorCode) {
                    log.debug '交强险报价结果：{}', ciDemand
                    if (ciVOList.errMessage && ciVOList.errMessage?.contains('重复投保')) {
                        log.debug '交强险报价失败： {}', ciVOList?.errMessage
                        addQFSMessage(context, _INSURANCE_KIND_NAME_COMPULSORY, _ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE)
                        return getKnownReasonErrorFSRV(ciVOList.errMessage)
                    }

                    def compulsory = (ciDemand.premium as BigDecimal).setScale(2, ROUND_HALF_UP).doubleValue()
                    def autoTax = ((ciVOList?.ciCarShipTax?.thisPayTax ?: ciTax.sumTax) as BigDecimal).setScale(2, ROUND_HALF_UP).doubleValue()
                    populateQuoteRecordBZ context, compulsory, autoTax
                }
            }

            addEffectiveDatesQFSMessage context
            getLoopBreakFSRV true
        } else if (m.matches()) {
            context.vehicleCode = m[0][1]
            log.warn '您选择的车型与平台返回的车型不一致，平台返回车型为：{}', m[0][1]
            getContinueFSRV context.vehicleCode
        } else {
            log.debug '报价失败, 原因为：{}', result.msg
            getKnownReasonErrorFSRV result.msg
        }
    }

}
