package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component



/**
 * 查询车型01V2,北京地区转保和历史客户查车用到01
 */
@Component
@Slf4j
class FindCarModel01 extends AFindCarModelList {

    @Override
    protected getBodyRequestParameters(context) {
        Auto auto = context.auto
        [
            'carModelQuery.requestType': '01',
            'carModelQuery.areaCode'   : context.areaCode,
            'carModelQuery.uniqueId'   : context.uniqueID,
            'carModelQuery.licenseNo'  : auto.licensePlateNo,
            'carModelQuery.frameNo'    : auto.vinNo,
            'carModelQuery.engineNo'   : auto.engineNo
        ]
    }
}
