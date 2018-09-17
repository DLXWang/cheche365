package com.cheche365.cheche.piccuk.flow.step

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV



/**
 * 获取车辆真实价格
 */
@Slf4j
class GetCarActualValue extends AGetCarActualValue {

    @Override
    protected returnFSRV(context) {
        getContinueFSRV context.carActualValue
    }
}
