package com.cheche365.cheche.sinosig.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 核保
 * @author wangmz
 */
@Component
@Slf4j
class SavePremium implements IStep {
    private static final _API_PATH_SAVE_PREMIUM = 'Net/netPremiumControl!savePremium.action'

    @Override
    run(context) {
        RESTClient client = context.client

        def (startDateText) = getCompulsoryInsurancePeriodTexts(context)

        def args = [
            requestContentType : URLENC,
            contentType        : JSON,
            path               : _API_PATH_SAVE_PREMIUM,
            body               : [
                'paraMap.id'                : context.token,
                'paraMap.planType'          : context.renewable ? '2' : '-1', // 1:大众热门;2:经济实惠;-1:自由选择
                'paraMap.isSubPre'          : '1',
                'paraMap.policyType'        : '2',
                'paraMap.policyType_tra'    : '2',
                'paraMap.ifTra'             : context.accurateInsurancePackage.compulsory ? '1' : '0', //'0' : 不投交强险； '1' : 投交强险
                'paraMap.taxFlag'           : context.insurancesCheckList.compulsory.first() ? '1' : '', //'1' ： 交强险可以投； '' ： 表示表示交强险不可投
                'paraMap.insuApp'           : startDateText,
                'paraMap.packageFlag'       : '2'

            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result.paraMap.suc != '1') {
            def message = result.paraMap?.result ?: result.paraMap?.message
            log.error '车辆核保失败：{}', message
            getFatalErrorFSRV message
        } else {
            log.info '车辆核保成功'
            getContinueFSRV result
        }
    }

}
