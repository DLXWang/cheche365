package com.cheche365.cheche.ccint.flow

import com.cheche365.cheche.ccint.flow.step.RecognizeVehicleLicense
import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.flow.business.flow.step.ExtractVehicleInfo

/**
 * 所有流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        识别行驶证信息: RecognizeVehicleLicense,
        抽取车辆信息 : ExtractVehicleInfo
    ]


    private static get_FLOW_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    //<editor-fold defaultstate="collapsed" desc="VehicleLicense Flows">
    static final _RECOGNIZE_VEHICLE_LICENSE_FLOW = _FLOW_BUILDER {
        识别行驶证信息 >> 抽取车辆信息
    }

    //</editor-fold>

}
