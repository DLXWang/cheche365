package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.picc.util.BusinessUtils.getDefaultStartDateTextBI
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取非续保客户商业险默认套餐报价
 * 包含经济、综合保障两种套餐， 并且在该步骤默认选择交强险和人身意外险
 * 因此，交强险、车船税的保费在该步骤已能拿到
 *
 */
@Component
@Slf4j
class CalculateForBatch implements IStep {

    private static final _API_PATH_CALCULATE_FOR_BATCH = '/newecar/calculate/calculateForBatch'

    @Override
    run(context) {
        RESTClient client = context.client

        def (startDateText, endDateText) = getDefaultStartDateTextBI(context)
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_CALCULATE_FOR_BATCH,
            body              : [
                uniqueID    : context.uniqueID,
                packageName : 'EconomyPackage',
                startDateCI : startDateText,
                endDateCI   : endDateText
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if ('成功' == result.resultMsg) {

            log.info '全险JSON：{}', result

            context.defaultPackageJson = result

            getContinueFSRV result

        } else {
            getFatalErrorFSRV result.resultMsg
        }
    }
}
