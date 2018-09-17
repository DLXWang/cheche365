package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 人保官网在结算前会调用此API，其返回的信息中会带有正确的终保日期。
 * 所以，对于转报用户而言是需要调用的。
 */
@Component
@Slf4j
class GetCheckReinsurance implements IStep {

    private static final _API_PATH_GET_CHECK_REINSURANCE = '/ecar/proposal/checkReinsurance'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType  : URLENC,
            contentType         : JSON,
            path                : _API_PATH_GET_CHECK_REINSURANCE,
            body                : generateRequestParameters(context, this)
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result.startdate) {
            context.requiredRefreshYearAndPrice = true
        }

        log.debug '获取再保险周期结果：{}', result
        context.reinsurance = result

        //根据GetCheckPeriod&GetCheckReinsurance是否获取到新的起保日期从而决定是否重走GetNewUseYears&CheckPriceForCar
        def requiredRefreshYearAndPrice = context.requiredRefreshYearAndPrice ?: false
        getContinueFSRV requiredRefreshYearAndPrice
    }

}
