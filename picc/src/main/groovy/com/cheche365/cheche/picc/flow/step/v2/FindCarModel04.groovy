package com.cheche365.cheche.picc.flow.step.v2

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV



/**
 * 查询车型V204(与车管所校验后查车04)
 */
@Component
@Slf4j
class FindCarModel04 extends AFindCarModel {

    @Override
    protected getBodyRequestParameters(context) {
        [
            'carModelQuery.requestType': '04',
            'carModelQuery.areaCode'   : context.areaCode,
            'carModelQuery.uniqueId'   : context.uniqueID,
            'carModelQuery.carModel'   : context.carModelInfo.vehicleName ?: '1', // 这个1是我们自己测试时发现没问题的值
            'carModelQuery.queryCode'  : context.carModelInfo.vehicleID //与交管平台校验后的车型ID
        ]

    }

    @Override
    protected getFSRV(result) {
        //04在loop循环中
        getLoopContinueFSRV null, result
    }
}
