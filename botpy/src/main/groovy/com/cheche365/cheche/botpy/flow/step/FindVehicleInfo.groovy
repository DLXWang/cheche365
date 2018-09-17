package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.Method.GET

/**
 * 查询车辆信息
 */
@Component
@Slf4j
class FindVehicleInfo implements IStep {

    private static final _API_PATH_FIND_VEHICLE_INFO = '/vehicles'

    @Override
    run(context) {
        def auto = context.auto
        def cityCodeMappings = context.cityCodeMappings

        def body = [
            city_code   : cityCodeMappings.city_code, // 账号所在城市代码
            license_no  : auto.licensePlateNo,
            license_type: '02'
        ]

        def result = sendParamsAndReceive context, _API_PATH_FIND_VEHICLE_INFO, body, GET, log

        if (result.error) {
            log.info("查询结果失败，原因{}，但不影像流程，继续报价！", result.error)
            getContinueFSRV result.error
        } else {
            context.vehicleInfo = result
            getContinueFSRV context.vehicleInfo
        }

    }

}
