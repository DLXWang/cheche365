package com.cheche365.cheche.botpy.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * 依据车架号车型查询
 */
@Component
@Slf4j
class CreateFindICModelsByVinNo extends ACreateFindICModels {

    @Override
    protected vehicleModelConditions(context) {
        [
            type : 'frame_no', // 依据信息frame_no:车架号
            value: context.auto.vinNo
        ]
    }

}
