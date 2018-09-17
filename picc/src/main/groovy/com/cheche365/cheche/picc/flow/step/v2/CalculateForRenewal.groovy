package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.cheche.parser.util.InsuranceUtils.afterGeneratedRenewalPackage
import static com.cheche365.cheche.picc.flow.step.v2.util.BusinessUtils.getAllKindItems
import static com.cheche365.cheche.picc.util.BusinessUtils.generateRenewalPackage
import static com.cheche365.cheche.picc.util.BusinessUtils.getDefaultStartDateTextBI
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取商业险续保报价
 */
@Component
@Slf4j
class CalculateForRenewal implements IStep {

    private static final _API_PATH_CALCULATE_FOR_RENEWAL = '/newecar/calculate/calculateForRenewal'


    @Override
    run(context) {
        RESTClient client = context.client

        def (startDateText, endDateText) = getDefaultStartDateTextBI(context)
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_CALCULATE_FOR_RENEWAL,
            body              : [
                uniqueID   : context.uniqueID,
                areaCode   : context.areaCode,
                packageName: 'OptionalPackage',
                startDateCI: startDateText,
                endDateCI  : endDateText,
                ciselect   : 1
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        log.info '续保结果：{}', result

        if ('0000' == result.resultCode) {

            def renewalPackage = getAllKindItems result.biviewmodel?.opt

            if (_FLOW_TYPE_RENEWAL_CHANNEL == context.flowType && !context.insurancePackage) {
                context.insurancePackage = generateRenewalPackage(context, renewalPackage)
                afterGeneratedRenewalPackage context
                log.info '获取续保套餐：{}', context.insurancePackage
            }
            getContinueFSRV null
        } else {
            getContinueWithIgnorableErrorFSRV null, result.resultMsg
        }
    }

}
