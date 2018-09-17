package com.cheche365.cheche.picc.flow.step.v2

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV



/**
 * 查询车型V203
 */
@Component
@Slf4j
class FindCarModel03 extends AFindCarModel {

    @Override
    protected getBodyRequestParameters(context) {
        [
            'carModelQuery.requestType': '03',
            'carModelQuery.areaCode'   : context.areaCode,
            'carModelQuery.uniqueId'   : context.uniqueID,
            'carModelQuery.carModel'   : context.carInfo?.vehicleName ?: '1', // 这个1是我们自己测试时发现没问题的值
            // 以下vehicleFgwCode为其他城市使用，modelCode为上海使用
            'carModelQuery.queryCode'  : context.carInfo?.vehicleFgwCode ?: context.carInfo?.modelCode ?: '1',
            'carModelQuery.parentId'   : context.carInfo?.parentId ?: '1',
            // 北京、上海地区使用
            'carModelQuery.serialno'   : 0
        ]
    }

    @Override
    protected getFSRV(result) {
        //03不在loop循环中
        getContinueFSRV result
    }
}
