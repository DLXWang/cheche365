package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * 车型详细信息
 */
@Component
class VehicleDetailInfo implements IStep {

    @Override
    run(context) {

        context.originalVehicleModels = [
            context.vehicleInfo.subMap([
                'moldCharacterCode',
                'moldName',
                'rMarketDate',
                'seatCount',
                'price'
            ])
        ]
        getContinueFSRV context.originalVehicleModels

    }

}
