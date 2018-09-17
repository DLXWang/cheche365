package com.cheche365.cheche.piccuk.flow.step

import groovy.util.logging.Slf4j
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * 行驶本服务获取车型，使用获取到一个车型的品牌型号
 */
@Slf4j
class GetCarModelInfoVL extends AGetCarModelInfo {

    @Override
    protected handleCarModelInfoResult(context, carModelInfo) {
        if (carModelInfo && carModelInfo[0].id) {
            context.carModelInfo = carModelInfo[0]
            log.info '获取车辆模型信息：{}', carModelInfo
        }

        getContinueFSRV carModelInfo
    }
}
