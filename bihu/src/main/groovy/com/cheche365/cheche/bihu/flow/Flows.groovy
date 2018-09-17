package com.cheche365.cheche.bihu.flow

import com.cheche365.cheche.bihu.flow.step.GetPrecisePrice
import com.cheche365.cheche.bihu.flow.step.GetReInfo
import com.cheche365.cheche.bihu.flow.step.GetSubmitInfo
import com.cheche365.cheche.bihu.flow.step.PrecisePrice
import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.parser.flow.steps.CheckInsurancesCheckList
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceBasicInfo
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceInfo
import com.cheche365.cheche.parser.flow.steps.InsurePostProcessor
import com.cheche365.cheche.parser.flow.steps.ProcessVehicleLicenseStagedResult
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.flow.business.flow.step.ExtractVehicleInfo

/**
 * 所有流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        查找车型信息    : GetReInfo,
        抽取车辆信息    : ExtractVehicleInfo,
        处理行驶证阶段性结果: ProcessVehicleLicenseStagedResult,
        抽取保险基本信息  : ExtractInsuranceBasicInfo,
        抽取保险信息    : ExtractInsuranceInfo,

        精准报价      : PrecisePrice,
        获取报价      : GetPrecisePrice,
        报价后处理器    : QuotePostProcessor,

        检查险种清单    : CheckInsurancesCheckList,
        获取核保信息    : GetSubmitInfo,
        核保后处理器    : InsurePostProcessor
    ]

    private static get_FLOW_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    //<editor-fold defaultstate="collapsed" desc="VehicleLicense Flows">
    static final _VEHICLE_LICENSE_FLOW = _FLOW_BUILDER {
        loop { 查找车型信息 } >> [
            (true): {
                抽取车辆信息 >> 处理行驶证阶段性结果 >> 抽取保险基本信息 >> 抽取保险信息
            }
        ]
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Quote Flows">
    static final _QUOTE_FLOW = _FLOW_BUILDER {
        loop { 查找车型信息 } >> 精准报价 >> 获取报价 >> 报价后处理器
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Insure Flows">
    static final _INSURE_FLOW = _FLOW_BUILDER {
        检查险种清单 >> 获取核保信息 >> 核保后处理器
    }

    //</editor-fold>

}
