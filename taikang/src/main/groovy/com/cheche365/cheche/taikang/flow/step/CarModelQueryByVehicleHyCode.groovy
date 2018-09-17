package com.cheche365.cheche.taikang.flow.step

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import groovy.util.logging.Slf4j



/**
 * 依据行业车型编码查询
 * Created by xuecl on 2018/06/5.
 */
@Slf4j
class CarModelQueryByVehicleHyCode extends ACarModelQuery {

    @Override
    protected vehicleModelConditions(context) {
        log.info '根据行业车型编码查车，operateFlag=4'
        [
            operateFlag     : '4', //车型查询操作类型
            vehicleHyCode   : context.vehicleHyCode, // 行业车型编码
            seatCount       : context.seatCount, // 座位数
            replacementValue: context.replacementValue, // 新车购置价
        ]
    }

    @Override
    protected dealResultFSRV(context, result, carModelList) {
        // 更新 context.selectedCarModel
        updateCarModelList(context, carModelList) ?:
            getContinueFSRV(_RESPONSE_FLAG == context.selectedCarModel?.carModel?.responseFlag)
    }

}
