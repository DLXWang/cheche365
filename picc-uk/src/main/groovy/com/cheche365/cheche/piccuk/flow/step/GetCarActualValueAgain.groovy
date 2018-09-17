package com.cheche365.cheche.piccuk.flow.step

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV



/**
 * 车型校验后查车后继续
 * 获取车辆真实价格
 */
@Slf4j
class GetCarActualValueAgain extends AGetCarActualValue {

    @Override
    protected returnFSRV(context) {
        getLoopContinueFSRV context.carActualValue, '再次获取车辆真实价格'
    }

}
