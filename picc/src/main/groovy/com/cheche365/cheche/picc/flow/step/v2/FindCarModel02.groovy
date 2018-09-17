package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.parser.Constants.get_DATE_FORMAT1



/**
 * 获取车型列表02
 */
@Component
@Slf4j
class FindCarModel02 extends AFindCarModelList {

    @Override
    protected getBodyRequestParameters(context) {
        Auto auto = context.auto
        [
            'carModelQuery.requestType': '02',
            'carModelQuery.areaCode'   : context.areaCode,
            'carModelQuery.uniqueId'   : context.uniqueID,
            'carModelQuery.licenseNo'  : auto.licensePlateNo,
            'carModelQuery.carModel'   : auto.autoType.code ?: auto.engineNo,
            'carModelQuery.frameNo'    : auto.vinNo,
            'carModelQuery.engineNo'   : auto.engineNo,
            'carModelQuery.enrollDate' : auto.enrollDate ? _DATE_FORMAT1.format(auto.enrollDate) : null,
            'carModelQuery.licenseType': '02'
        ]
    }

}
