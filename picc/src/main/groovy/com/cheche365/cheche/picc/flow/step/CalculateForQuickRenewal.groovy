package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.cheche.parser.util.InsuranceUtils.afterGeneratedRenewalPackage
import static com.cheche365.cheche.picc.util.BusinessUtils.generateRenewalPackage
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取商业险续保报价
 */
@Component
@Slf4j
class CalculateForQuickRenewal implements IStep {

    private static final _API_PATH_CALCULATE_FOR_QUICK_RENEWAL = '/ecar/caculate/checkForQuickRenewal'


    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType  : URLENC,
            contentType         : JSON,
            path                : _API_PATH_CALCULATE_FOR_QUICK_RENEWAL,
            query               : [
                uniqueID  : context.uniqueID
            ]
        ]

        def accurateQuote = client.get args, { resp, json ->
            json
        }

        log.info '快速续保结果：{}', accurateQuote

        if ('成功' == accurateQuote.errorMsg) {
            context.defaultPackageJson = accurateQuote

            if (_FLOW_TYPE_RENEWAL_CHANNEL == context.flowType && !context.insurancePackage) {
                context.insurancePackage = generateRenewalPackage(context, accurateQuote.OptionalPackage)
                afterGeneratedRenewalPackage context
            }

            if (!context.selectedCarModel && accurateQuote.carModelInfo.size() > 0) {
                if (accurateQuote.carModelInfo[0].body?.carModels?.size() > 0) {
                    context.selectedCarModel = accurateQuote.carModelInfo[0].body.carModels[0]
                } else {
                    context.selectedCarModel = accurateQuote.carModelInfo[0].body
                }
            }
            getContinueFSRV true
        } else {
            getContinueWithIgnorableErrorFSRV false, accurateQuote.errorMsg
        }

    }

}
