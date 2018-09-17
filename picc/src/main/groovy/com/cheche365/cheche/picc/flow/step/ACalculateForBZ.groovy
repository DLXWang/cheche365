package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT1
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
import static com.cheche365.cheche.parser.Constants._INSURANCE_KIND_NAME_COMPULSORY
import static com.cheche365.cheche.parser.Constants._QUOTE_KIND_NAME_COMPULSORY
import static com.cheche365.cheche.parser.util.BusinessUtils.checkCompulsoryPackageOptionEnabled
import static com.cheche365.cheche.parser.util.BusinessUtils.countCanInsureDays
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.picc.util.BusinessUtils.getDefaultInsurancePeriodText
import static com.cheche365.cheche.picc.util.BusinessUtils.getEarlyDays4Compulsory
import static com.cheche365.cheche.picc.util.BusinessUtils.getNewCalculateBZDateText
import static com.cheche365.cheche.picc.util.BusinessUtils.populateQuoteRecordBZ
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取交强险报价及核保的基类
 */
@Component
@Slf4j
abstract class ACalculateForBZ implements IStep {

    private static final _API_PATH_CALCULATE_FOR_BZ = '/ecar/caculate/caculateBZ'

    protected static final _BZ_TAX_TYPE_B = 'B' // 补充并纳税
    protected static final _BZ_TAX_TYPE_N = 'N' // 纳税

    @Override
    run(context) {
        if (!checkCompulsoryPackageOptionEnabled(context)) {
            log.warn '未投保交强险，跳过此步骤'
            return getContinueFSRV(null)
        }

        RESTClient client = context.client
        def baseBody = generateRequestParameters(context, this)
        def args = createRequestParams(baseBody)

        def quote = client.post args, { resp, json ->
            json
        }

        def compulsoryEnabled = true            //是否能上交强
        def compulsoryDisabledCauseTime = false //不能上交强是因为投保时间原因
        def finalStartDate, finalEndDate        //真正的投保日期，不能投保则为空

        if (quote.errorMsg) {

            if (taxType) { //上海地区：以补充纳税方式请求，当confirmTaxType返回应以纳税方式时，将尝试以纳税方式请求
                def confirmTaxType = confirmTaxType(quote.errorMsg)
                context.taxType = confirmTaxType
                if (_BZ_TAX_TYPE_B == taxType && _BZ_TAX_TYPE_N == confirmTaxType) {
                    log.info '无法以[补税并纳税]方式投保加强将尝试以[纳税]方式投保'
                    return getContinueFSRV(true)
                }
            }

            log.warn '以默认参数请求交强险报价时出错，但是错误信息中含有正确的日期信息：{}', quote.errorMsg

            def (newStartDateCIText, newEndDateCIText) = getNewCalculateBZDateText(quote.errorMsg)
            if (newStartDateCIText) {
                args = createRequestParams(baseBody)
                args.body += ['prpcmain.startdate': newStartDateCIText, 'prpcmain.enddate': newEndDateCIText]
                log.info '以新的起止日期{}和{}请求交强险报价', newStartDateCIText, newEndDateCIText

                quote = client.post args, { resp, json ->
                    json
                }

                if (!quote.errorMsg) {
                    compulsoryEnabled = true
                    (finalStartDate, finalEndDate) = [newStartDateCIText, newEndDateCIText]
                } else {
                    log.warn '以新的起止日期请求交强险报价时依然报错：{}', quote.errorMsg
                    compulsoryEnabled = false
                    if (!isQuoting()) {
                        compulsoryDisabledCauseTime = true
                    }
                    //未到期且拿到了起保日期，计算距窗口期的时间，并放入quoteFieldStatus中
                    countCanInsureDays(
                        context,
                        _INSURANCE_KIND_NAME_COMPULSORY,
                        _QUOTE_KIND_NAME_COMPULSORY,
                        getLocalDate(_DATE_FORMAT1.parse(newStartDateCIText)),
                        getEarlyDays4Compulsory(context))
                }
            } else {
                log.warn '以默认起保日期交强险报价时出错：{}', quote.errorMsg
                compulsoryEnabled = false
                compulsoryDisabledCauseTime = false
            }
        } else {
            compulsoryEnabled = true
            (finalStartDate, finalEndDate) = getDefaultInsurancePeriodText(context)
        }

        if (compulsoryEnabled) {
            def quoteRecord = populateQuoteRecordBZ(quote, context)
            log.info '合并交强险报价之后的QuoteRecord：{}', quoteRecord
            context.bzStartDateText = finalStartDate
            context.bzEndDateText = finalEndDate
            getContinueFSRV payloadFlag
        } else {
            if (isQuoting()) {
                log.info '禁用交强险及车船税套餐项'
                disableCompulsoryAndAutoTax context
                getContinueWithIgnorableErrorFSRV payloadFlag, quote.errorMsg
            } else {
                if (compulsoryDisabledCauseTime) {
                    disableCompulsoryAndAutoTax context
                    getContinueWithIgnorableErrorFSRV payloadFlag, _ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
                } else {
                    getFatalErrorFSRV quote.errorMsg
                }
            }
        }


    }

    protected createRequestParams(baseBody) {
        [
            requestContentType : URLENC,
            contentType        : JSON,
            path               : _API_PATH_CALCULATE_FOR_BZ,
            body               : baseBody
        ]
    }

    /**
     * 当前step是报价还是核保？
     * @return true意味着是报价操作，false是核保。
     */
    abstract protected isQuoting()

    /**
     * 获取run方法返回值的payload参数控制流程
     */
    abstract protected getPayloadFlag()

    /**
     * 获取纳税方式
     * @return 不需要注明纳税方式的返回null
     */
    abstract protected getTaxType()

    /**
     * 确定纳税方式，补税还是纳税
     * @param quoteErrorMsg
     * @return
     */
    abstract protected confirmTaxType(quoteErrorMsg)


}

