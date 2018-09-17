package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 调整车辆信息
 */
@Component
@Slf4j
class VehicleQueryConfirm implements IStep {

    private static final _URL_VEHICLE_QUERY_CONFIRM = '/ecar/ecar/vehicleQueryConfirm'

    @Override
    run(context) {

        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _URL_VEHICLE_QUERY_CONFIRM,
            body              : [
                meta  : [:],
                redata: [
                    checkCode: context.calculateDaptchaTextKey,
                    checkNo  : context.checkNo
                ]
            ]
        ]
        def result = client.post args, { resp, json ->
            json
        }

        if ('success' == result?.message?.code) {
            //todo  更新车辆信息
            def displacement = result.result[0].displacement as double
            def carVo = [
                emptyWeight        : result.result[0].wholeWeight,   //整备质量
                vehicleStyle       : result.result[0].vehicleStyle,//车辆类型
                seatCount    : result.result[0].limitLoadPerson,//核定载客数
                stRegisterDate: result.result[0].vehicleRegisterDate, //初登日期
                engineCapacity       : displacement ? displacement/1000 : 0.0, //排量

            ]
            log.debug '成功更新车辆信息'
            context.carVo = carVo
            getLoopBreakFSRV '成功更新车辆信息'
        } else {
            getLoopContinueFSRV null, '调整车辆信息失败'
        }
    }

}
