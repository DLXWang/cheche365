package com.cheche365.cheche.botpy.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import static com.cheche365.cheche.botpy.util.BusinessUtils.getVehicleName

/**
 * 依据品牌型号车型查询
 */
@Component
@Slf4j
class CreateFindICModelsByAutoModel extends ACreateFindICModels {

    @Override
    protected vehicleModelConditions(context) {
        [
            type : 'vehicle_name', // vehicle_name: 品牌型号
            value: getVehicleName(context), // 根据信息的值
        ]

    }

}
