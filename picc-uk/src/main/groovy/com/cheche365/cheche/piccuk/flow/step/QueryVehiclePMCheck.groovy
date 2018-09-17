package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 南京交管查车
 */
@Component
@Slf4j
class QueryVehiclePMCheck implements IStep {

    private static final _URL_QUERY_VEHICLE_PM_CHECK = '/prpall/business/queryVehiclePMCheck.do'

    @Override
    Object run(Object context) {
        RESTClient client = context.client
        def auto = context.auto

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_QUERY_VEHICLE_PM_CHECK,
            query             : [
                comCode  : context.worekbenchUserComCode,
                frameNo  : auto.vinNo,
                licenseNo: auto.licensePlateNo,
            ]
        ]

        log.debug 'args: {}', args
        def result = client.post args, { resp, json ->
            json
        }

        log.debug 'result:{}', result
        if (result && result.data[0].displacement) { //有可能还需要补充信息选择车型
            log.debug '车辆信息获取成功:{}', result
            context.vehiclePMCheckResult = result.data[0]
            getLoopBreakFSRV '交管车辆信息获取成功'
        } else if (result && result.data[0].checkCode) {
            context.imageBase64 = result.data[0]?.checkCode.replaceAll(' ', '')
            context.checkNo = result.data[0].checkNo
            getContinueFSRV '成功获取验证码'
        } else {
            log.debug '未获取到交管车辆信二维码信息'
            getKnownReasonErrorFSRV '未获取到交管车辆信二维码信息'
        }


    }
}
