package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * 获取车辆使用年限
 */
@Component
@Slf4j
class GetUseYears implements IStep {

    private static final _API_PATH_GET_USE_YEARS = '/ecar/car/carModel/getUseYears'


    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            path        : _API_PATH_GET_USE_YEARS,
            query       : generateRequestParameters(context, this)
        ]

        def autoUseYears = client.post args, { resp, years ->
            years
        }

        log.debug '车辆使用年数：{}', autoUseYears
        context.autoUseYears = autoUseYears
        getContinueFSRV autoUseYears
    }

}
