package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC


/**
 * 南京交管查车
 */
@Component
@Slf4j
class QueryVehiclePMConfirm implements IStep {

    private static final _URL_QUERY_VEHICLE_PM_CONFIRM = '/prpall/business/queryVehiclePMConfirm.do'

    @Override
    Object run(Object context) {
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_QUERY_VEHICLE_PM_CONFIRM,
            query             : [
                comCode  : context.worekbenchUserComCode,
                checkNo  : context.checkNo,
                checkCode: context.queryVehicleDaptchaTextKey,
            ]
        ]

        log.debug 'args: {}', args
        def result = client.post args, { resp, json ->
            json
        }

        if (!result) {
            log.debug '未获取到交管车辆信息'
            return getLoopContinueFSRV('未获取到交管车辆信息')
        }

        context.vehiclePMCheckResult = result.data[0]

        log.debug '交管查车结果：{}', result
        getLoopBreakFSRV result

    }
}
