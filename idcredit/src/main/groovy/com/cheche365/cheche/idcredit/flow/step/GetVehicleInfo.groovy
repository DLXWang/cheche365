package com.cheche365.cheche.idcredit.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.idcredit.util.BusinessUtils.markFailedVehicleInfo
import static com.cheche365.cheche.idcredit.util.BusinessUtils.saveApplicationLog
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取车型信息，需要轮询
 * 无API调用次数的限制
 */
@Component
@Slf4j
class GetVehicleInfo implements IStep {

    private static final _API_PATH_GET_VEHICLE_INFO = '/result/vehicle'

    @Override
    run(context) {

        def token = context.token
        def taskId = context.taskId

        RESTClient client = context.client

        def args = [
            requestContentType  : URLENC,
            contentType         : JSON,
            path                : _API_PATH_GET_VEHICLE_INFO,
            query               : [
                access_token    : context.token,
                tid             : context.taskId
            ]
        ]

        Auto auto = context.auto
        def licensePlateNo = auto.licensePlateNo
        def identity = auto.identity
        def owner = auto.owner
        log.info '以{}、{}参数查询{}、{}、{}的车辆信息', token, taskId, licensePlateNo, identity, owner
        saveApplicationLog context, args as String, this.class.simpleName

        def result = client.get args, { resp, json ->
            json
        }

        saveApplicationLog context, result as String, this.class.simpleName

        log.debug '返回的车型信息结果：{}', result

        if (!result.error && 1 == result.data.result && null == result.data.key) {
            context.vehicleInfo = result.data
            getLoopBreakFSRV result
        } else if (44206 == result.error) {
            Thread.sleep 2000L
            getLoopContinueFSRV result, '尚未完成验证，稍后重试'
        } else {
            if (-1 == result.error) {
                log.warn '绿湾当前系统繁忙，可以稍后重试'
            }
            def errorMsg = '获取车辆信息失败'
            if (0 == result.data.key) {
                markFailedVehicleInfo context
                errorMsg = '车辆信息校验失败，已被标记为失败，当天后继相同请求不再调用查询API'
            }
            getFatalErrorFSRV errorMsg
        }
    }

}
