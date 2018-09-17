package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.picc.flow.Constants._STATUS_CODE_PICC_CHECK_SUPPORT_NEW_CAR_FAILURE

/**
 * 获取车辆使用年限
 */
@Component
@Slf4j
class GetNewUseYears implements IStep {

    private static final _API_PATH_GET_USE_YEARS = '/ecar/car/carModel/getNewUseYears'


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

        log.info '车辆使用年数：{}', autoUseYears
        if ('0' == context.newCarEnableFlag_stepone && autoUseYears < 1) {
            [_ROUTE_FLAG_DONE, _STATUS_CODE_PICC_CHECK_SUPPORT_NEW_CAR_FAILURE, null, '网上投保暂不支持新车']
        } else {
            context.autoUseYears = autoUseYears
            getContinueFSRV autoUseYears
        }
    }

}
