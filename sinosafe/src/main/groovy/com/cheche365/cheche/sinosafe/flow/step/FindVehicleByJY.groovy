package com.cheche365.cheche.sinosafe.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.CollectionUtils.checkAndConvertList
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.createRequestParams
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.sendAndReceive2Map



/**
 * 精友车型查询
 * create by sufc
 */
@Slf4j
class FindVehicleByJY implements IStep {

    private static final _TRAN_CODE = 100001

    @Override
    run(context) {

        def result = sendAndReceive2Map(context, getRequestParams(context), log)

        log.debug "result = {}", result
        def carList = null
        if (result.PACKET.BODY?.VHL_LIST?.VHL_DATA) {
            carList = checkAndConvertList(result.PACKET.BODY.VHL_LIST.VHL_DATA)
        }
        log.debug "车型查询成功，进入选车阶段 VehicleList: {} ", carList
        context.optionsByCode = carList//华安支持品牌型号查询车辆,返回的也是以品牌型号查询出来车辆的列表
        context.resultByCode = result //品牌型号报文
        getContinueFSRV context
    }

    private static getRequestParams(context) {
        Auto auto = context.auto
        def body = [
            VhlTypeQuery: [
                VEHICLE_NO  : context.additionalParameters.supplementInfo.newCarFlag ? '*-*' : auto.licensePlateNo,
                //RACK_NO     : auto.vinNo,//车架号
                VEHICLE_NAME: auto.autoType.code, //品牌型号
                DEPT_NO     : context.area.id,
            ]
        ]
        createRequestParams(context, _TRAN_CODE, body)
    }
}
