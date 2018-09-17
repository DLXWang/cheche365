package com.cheche365.cheche.idcredit.flow

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.common.flow.FlowChain
import com.cheche365.cheche.idcredit.flow.step.FindVehicleInfo
import com.cheche365.cheche.idcredit.flow.step.GetToken
import com.cheche365.cheche.idcredit.flow.step.GetVehicleInfo
import com.cheche365.flow.business.flow.step.ExtractVehicleInfo

/**
 * 所有流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        获取Token : GetToken,
        查找车型信息 : FindVehicleInfo,
        获取车型信息 : GetVehicleInfo,
        抽取车辆信息 : ExtractVehicleInfo
    ]


    private static get_FLOW_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }


    //<editor-fold defaultstate="collapsed" desc="VehicleLicense Flows">
    private static final _VEHICLE_LICENSE_FLOW_DEFAULT = _FLOW_BUILDER {
        loop { 获取Token } >> 查找车型信息 >> loop { 获取车型信息 } >> 抽取车辆信息
    }

    static final _VEHICLE_LICENSE_FLOW = new FlowChain(flows: [_VEHICLE_LICENSE_FLOW_DEFAULT])

    //</editor-fold>

}
