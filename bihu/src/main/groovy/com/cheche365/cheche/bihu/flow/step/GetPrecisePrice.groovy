package com.cheche365.cheche.bihu.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.bihu.Constants._INSURANCE_COMPANY_MAPPING
import static com.cheche365.cheche.bihu.util.BusinessUtils.getCurrentInsuranceCompany
import static com.cheche365.cheche.bihu.util.BusinessUtils.sendAndReceive
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static java.math.BigDecimal.ROUND_HALF_UP

/**
 * 获取报价结果
 */
@Slf4j
class GetPrecisePrice implements IStep {

    private static final _API_PATH_GET_PRECISE_PRICE = '/api/CarInsurance/GetPrecisePrice'

    @Override
    run(context) {
        def currentInsuranceCompany = getCurrentInsuranceCompany(context)

        def queryBody = [
            LicenseNo : context.auto.licensePlateNo,
            QuoteGroup: _INSURANCE_COMPANY_MAPPING[currentInsuranceCompany],
            Agent     : context.agent,
            CustKey   : context.custKey
        ]

        def result
        try {
            result = sendAndReceive context, _API_PATH_GET_PRECISE_PRICE, queryBody, this.class.name
        } catch (ex) {
            log.error("${currentInsuranceCompany}获取报价失败", ex)
            return getContinueFSRV("${currentInsuranceCompany}获取报价失败")
        }

        def businessStatus = result.BusinessStatus
        if (1 == businessStatus && 1 == result.Item.QuoteStatus) {
            def quoteItem = result.Item
            def accurateQuote = context.kindCodeConvertersConfig.collectEntries {
                [(it[0]): [amount: quoteItem[it[0]].BaoE, premium: quoteItem[it[0]].BaoFei]]
            }
            populateQuoteRecord(context, accurateQuote, context.kindCodeConvertersConfig,
                (quoteItem.BizTotal as BigDecimal).setScale(2, ROUND_HALF_UP).doubleValue(), 0)

            // 交强
            def compulsory = quoteItem.ForceTotal as double
            def autoTax = quoteItem.TaxTotal as double
            populateQuoteRecordBZ context, compulsory, autoTax

            log.info '获取保险公司：{}报价完成', currentInsuranceCompany
            getContinueFSRV result
        } else {
            log.error '报价失败：{}', result.Item.QuoteResult ?: result.StatusMessage
            getKnownReasonErrorFSRV(result.Item.QuoteResult ?: result.StatusMessage)
        }
    }

}
