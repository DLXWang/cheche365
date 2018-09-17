package com.cheche365.cheche.sinosig.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_COMPULSORY_COMMON_CHECK_FAILURE
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE
import static com.cheche365.cheche.parser.util.BusinessUtils.checkCompulsoryPackageOptionEnabled
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.resolveNewQuoteRecordInContext
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 计算交强险
 */
@Component
@Slf4j
class PremiumCI implements IStep {

    private static final _API_PATH_PREMIUM_CI = 'Net/netPremiumControl!premiumCI.action'

    @Override
    run(context) {
        if (!checkCompulsoryPackageOptionEnabled(context)) {
            return getContinueFSRV(null)
        }

        RESTClient client = context.client
        def (startDateText) = getCompulsoryInsurancePeriodTexts(context)
        def args = [
            requestContentType : URLENC,
            contentType        : JSON,
            path               : _API_PATH_PREMIUM_CI,
            body               : [
                'paraMap.id'             : context.token,
                'paraMap.insuAppTra'     : startDateText,
                'paraMap.calcFlag'       : '1',
                'paraMap.billOpenDate'   : '',
                'paraMap.hasKind'        : '0',
                'paraMap.renewalFlag'    : '',
                'paraMap.initCalcTraFlag': '1',
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if ('0' != result.paraMap.suc) {
            if (checkCompulsoryEnabled(result)) {
                def newQuoteRecord = resolveNewQuoteRecordInContext(context).with {
                    autoTax = result.paraMap?.sumPremium_tax as double ?: 0
                    compulsoryPremium = result.paraMap.sumPremium_tra as double ?: 0
                    it
                }
                log.info '交强险：{}，车船税：{}', newQuoteRecord.compulsoryPremium, newQuoteRecord.autoTax

                def compulsoryEffectiveDate = result.paraMap.insuAppTra
                setCompulsoryInsurancePeriodTexts context, compulsoryEffectiveDate
                log.info '交强险起保日期：{}', compulsoryEffectiveDate
                getContinueFSRV result
            } else {
                log.warn '交强险不可投保原因：{}', result.paraMap.repeat_insu_tra
                //TODO:需要处理时间原因导致的不可投保
                def errorMsg = result.paraMap.repeat_insu_tra?.contains('交强险未到投保期') ?
                    _ERROR_MESSAGE_COMPULSORY_PERIOD_CHECK_FAILURE : _ERROR_MESSAGE_COMPULSORY_COMMON_CHECK_FAILURE
                disableAndGetFSRV context, errorMsg
            }
        } else {
            log.error '交强险投保失败：{}', result.paraMap.message
            disableAndGetFSRV context, _ERROR_MESSAGE_COMPULSORY_COMMON_CHECK_FAILURE
        }
    }

    /**
     * 根据返回的交强险保费是否为空来判断交强险是否有效
     */
    private static checkCompulsoryEnabled(result) {
        result.paraMap.sumPremium_tra && (result.paraMap.sumPremium_tra as double)
    }

    private static disableAndGetFSRV(context, errorMsg) {
        disableCompulsoryAndAutoTax context, errorMsg
        getContinueWithIgnorableErrorFSRV false, errorMsg
    }
}
