package com.cheche365.cheche.taikang.flow.step

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import groovy.util.logging.Slf4j



/**
 * 依据精友车型编码查询
 * Created by xuecl on 2018/06/5.
 */
@Slf4j
class CarModelQueryByVehicleJYCode extends ACarModelQuery {

    @Override
    protected vehicleModelConditions(context) {
        log.info '根据精友车型编码查车，operateFlag=2'
        [
            operateFlag: '2', //车型查询操作类型
            vehicleCode: context.selectedCarModel?.carModel?.vehicleCode // 精友车型编码
        ]
    }

    @Override
    protected dealResultFSRV(context, result, carModelList) {
        // 更新 context.selectedCarModel
        def fsrv = updateCarModelList(context, carModelList)

        if (fsrv) {
            return fsrv
        }

        def carModel = context.selectedCarModel?.carModel

        if (_RESPONSE_FLAG == carModel?.responseFlag) {
            getContinueFSRV(true)
        } else {
            context.replacementValue = carModel?.replacementValue
            context.vehicleHyCode = carModel?.vehicleHyCode
            context.seatCount = carModel?.seatCount
            getContinueFSRV('根据行业编码查车')
        }
    }
}
