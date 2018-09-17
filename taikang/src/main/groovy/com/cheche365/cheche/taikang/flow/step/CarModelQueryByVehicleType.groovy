package com.cheche365.cheche.taikang.flow.step

import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_CONTINUE
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getNewSelectedCarModelFSRV
import groovy.util.logging.Slf4j



/**
 * 依据车型查询
 * Created by xuecl on 2018/06/5.
 */
@Slf4j
class CarModelQueryByVehicleType extends ACarModelQuery {

    @Override
    protected vehicleModelConditions(context) {
        log.info '根据品牌型号查车，operateFlag=1'
        [
            operateFlag: '1' //车型查询操作类型
        ]
    }

    @Override
    protected dealResultFSRV(context, result, carModelList) {
        context.optionsByCode = carModelList
        context.resultByCode = result
        // 选出默认值，并向前台推送车型列表
        def fsrv = getNewSelectedCarModelFSRV context, [optionsSource: 'byCode']

        fsrv[0] != _ROUTE_FLAG_CONTINUE ? fsrv : getContinueFSRV(_RESPONSE_FLAG == context.selectedCarModel?.carModel?.responseFlag ? '车型查询成功' : '根据精友编码查车')
    }

}
