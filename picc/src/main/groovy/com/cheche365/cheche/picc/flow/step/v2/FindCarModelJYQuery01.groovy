package com.cheche365.cheche.picc.flow.step.v2

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.checkVehicleSupplementInfo
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV



/**
 * 专门查车时用的步骤
 * 精友地区根据品牌型号获取车型列表
 */
@Slf4j
class FindCarModelJYQuery01 extends AFindCarModelJYQuery {

    @Override
    protected handleResultFSRV(context, result) {

        def brandModelList = result.body?.queryVehicle

        if (brandModelList) {

            def fsrv = checkVehicleSupplementInfo context, brandModelList, context.getVehicleOption, false, { ctx, item -> item }, true
            if (fsrv) {
                context.newAutoTypes = fsrv[-1]()[0].options
                getContinueFSRV context.newAutoTypes
            } else {
                getValuableHintsFSRV(context, [
                    _VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
                ])
            }
        } else {
            log.error '无法获得车型，通常是车辆/人员信息有误导致的：{}', result
//            getValuableHintsFSRV(context,
//                [
//                    _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING,
//                    _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
//                    _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING,
//                    _VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
//                ])
            getContinueFSRV('车架号查车')
        }
    }

}
