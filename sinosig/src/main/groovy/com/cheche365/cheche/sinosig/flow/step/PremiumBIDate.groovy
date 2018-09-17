package com.cheche365.cheche.sinosig.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static groovyx.net.http.ContentType.ANY
import static groovyx.net.http.ContentType.URLENC

/**
 * 校验商业险起保日期
 */
@Component
@Slf4j
class PremiumBIDate implements IStep {

    private static final _API_PATH_SAVE_BI_DATE = 'Net/netPremiumControl!premiumSaveBIDate.action'

    @Override
    run(context) {
        def (startDateText) = getCommercialInsurancePeriodTexts(context)
        def (effectiveDate, errorMsg) = getRequestResult(context, startDateText)
        if (effectiveDate) {
            log.info '从保险公司获取商业险起保日期为{}', effectiveDate
            if (effectiveDate != startDateText) {
                log.info '以{}为起保日期，重新调用本接口', effectiveDate
                (effectiveDate) = getRequestResult(context, effectiveDate)
            }
            setCommercialInsurancePeriodTexts context, effectiveDate
            getContinueFSRV effectiveDate
        } else {
            log.error '商业险起保时间保存失败：{}', errorMsg
            getFatalErrorFSRV '商业险起保日期保存失败，通常意味着无法进行商业险报价'
        }

    }

    private getRequestResult(context, startDateText) {
        def args = getRequestParams(context, startDateText)
        RESTClient client = context.client
        def result = client.post args, { resp, json ->
            json
        }

        if ('1' == result.paraMap.suc) {
            [result.paraMap.insuApp]
        } else {
            [null, result.paraMap.message]
        }
    }

    private getRequestParams(context, startDateText) {
        [
            requestContentType: URLENC,
            contentType       : ANY,
            path              : _API_PATH_SAVE_BI_DATE,
            body              : [
                'paraMap.id'         : context.token,
                'paraMap.insuApp'    : startDateText,
                'paraMap.calcFlag'   : '2',//参考handlers文件里_PREMIUM_BI_RPG_BASE闭包的注释
                'paraMap.ifTra'      : context.accurateInsurancePackage?.compulsory ? '1' : '0',
                'paraMap.renewalFlag': context.renewable ? '1' : '',
            ]
        ]
    }
}
