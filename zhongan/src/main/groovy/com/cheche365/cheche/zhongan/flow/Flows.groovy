package com.cheche365.cheche.zhongan.flow

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.common.flow.FlowChain
import com.cheche365.cheche.common.flow.step.Identity
import com.cheche365.cheche.common.flow.step.NoOp
import com.cheche365.cheche.parser.flow.steps.CheckInsurancesCheckList
import com.cheche365.cheche.parser.flow.steps.CheckRenewalFlow
import com.cheche365.cheche.parser.flow.steps.CheckSupplementInfo
import com.cheche365.cheche.parser.flow.steps.Decaptcha
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.cheche.parser.flow.steps.SelectCarModel
import com.cheche365.cheche.zhongan.flow.step.ApplyInquiryPrice
import com.cheche365.cheche.zhongan.flow.step.ConfirmIdentifyCode
import com.cheche365.cheche.zhongan.flow.step.ConfirmOrderAndUnderwriting
import com.cheche365.cheche.zhongan.flow.step.CreatePolicy
import com.cheche365.cheche.zhongan.flow.step.GetCaptcha
import com.cheche365.cheche.zhongan.flow.step.GetIdentifyCode
import com.cheche365.cheche.zhongan.flow.step.QueryCLInfo
import com.cheche365.cheche.zhongan.flow.step.QuerySignStatus
import com.cheche365.cheche.zhongan.flow.step.QuerySpecialPromise
import com.cheche365.cheche.zhongan.flow.step.QueryVehicleModel
import com.cheche365.cheche.zhongan.flow.step.QuoteConfirm
import com.cheche365.cheche.zhongan.flow.step.QuotePrice
import com.cheche365.cheche.zhongan.flow.step.VehicleActualPrice
import com.cheche365.cheche.zhongan.flow.step.VehicleConfirm



/**
 * 众安保险报价流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [

        无       : NoOp,
        同前      : Identity,

        检查险种清单  : CheckInsurancesCheckList,
        报价后处理器  : QuotePostProcessor,
        检查续保通道流程: CheckRenewalFlow,
        检查补充信息  : CheckSupplementInfo,
        车辆查询    : QueryCLInfo,
        车型查询    : QueryVehicleModel,
        询价申请    : ApplyInquiryPrice,
        车辆实际价值查询: VehicleActualPrice,
        报价      : QuotePrice,
        核保及订单确认 : ConfirmOrderAndUnderwriting,
        承保      : CreatePolicy,
        生成特别约定  : QuerySpecialPromise,
        报价确认    : QuoteConfirm,
        获取验证码   : GetCaptcha,
        识别验证码   : Decaptcha,
        车型确认    : VehicleConfirm,
        签名查询    : QuerySignStatus,
        身份验证码获取 : GetIdentifyCode,
        身份验证码回填 : ConfirmIdentifyCode,
        默认车型选择  : SelectCarModel,
    ]

    //<editor-fold defaultstate="collapsed" desc="流程片段">
    private static get_SNIPPET_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    /**
     * 验证码片段
     */
    private static final _CHECK_DECAPTCHA_SNIPPET = _SNIPPET_BUILDER {

        loop({ 获取验证码 >> 识别验证码 >> 车型确认 }, 10)

    }

    private static final _NAME_FLOW_MAPPINGS = [
        验证码校验片段: _CHECK_DECAPTCHA_SNIPPET
    ]

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="流程模板">
    private static final _QUOTING_TEMPLATE = {
        检查补充信息 >> 车辆查询 >> 车型查询 >> [
            精确车型查询: {默认车型选择}
        ] >> PH1 >> 询价申请 >> [
            车型列表变更: { 默认车型选择 >> 询价申请 }
        ] >> 车辆实际价值查询 >> loop({ 报价 }, 10) >> 报价后处理器
    }

    private static final _QUOTING_TEMPLATE_JIANGSU = {
        检查补充信息 >> 车辆查询 >> PH1 >> 车型查询 >> [
            精确车型匹配: {默认车型选择}
        ] >> 询价申请 >> [
            车型列表变更: { 默认车型选择 >> 询价申请 },
        ] >> 车辆实际价值查询 >> loop({ 报价 }, 10) >> 报价后处理器
    }

    /**
     * 报价模板
     */
    private static final _NAME_TEMPLATE_MAPPINGS = [
        报价模板    : _QUOTING_TEMPLATE,
        江苏地区报价模板: _QUOTING_TEMPLATE_JIANGSU
    ]

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="报价，核保，承保流程">
    private static get_FLOW_BUILDER() {
        new FlowBuilder(
            nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS,
            nameFlowMappings: _NAME_FLOW_MAPPINGS,
            nameTemplateMappings: _NAME_TEMPLATE_MAPPINGS
        )
    }
    //<editor-fold defaultstate="collapsed" desc="核保">

    private static final _UNDERWRITING_FLOW_DEFAULT = _FLOW_BUILDER {
        loop({ 核保及订单确认 }, 10)
    }

    private static final _UNDERWRITING_FLOW_BEIJING = _FLOW_BUILDER {
        loop({核保及订单确认}, 10) >> 身份验证码回填 >> [
            (true): { 身份验证码获取 }
        ]
    }

    private static final _UNDERWRITING_FLOW_SHENZHEN = _FLOW_BUILDER {
        loop({核保及订单确认}, 10)
    }

    //  </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="承报">

    private static final _ORDERING_FLOW_DEFAULT = _FLOW_BUILDER {
        承保
    }
    //  </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="报价流程">
    //江苏地区
    static final _QUOTING_FLOW_320000 = _FLOW_BUILDER {
        make "江苏地区报价模板",
            [
                PH1: '验证码校验片段'
            ]

    }
    //通用
    static final _QUOTING_FLOW_DEFAULT = _FLOW_BUILDER {
        make '报价模板',
            [
                PH1: '无'
            ]
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="流程片段">
    /**
     * 签名查询
     */
    static final _QUERY_SIGNSTATUS_DEFAULT = _FLOW_BUILDER {
        签名查询
    }
//</editor-fold>
    static final _INSURING_FLOW_TYPE1 = new FlowChain(flows: [_UNDERWRITING_FLOW_DEFAULT])

    static final _INSURING_FLOW_TYPE1_BEIJING = new FlowChain(flows: [_UNDERWRITING_FLOW_BEIJING])

    static final _INSURING_FLOW_TYPE1_SHENZHEN = new FlowChain(flows: [_UNDERWRITING_FLOW_SHENZHEN])

    static final _ORDERING_FLOW_TYPE1 = new FlowChain(flows: [_ORDERING_FLOW_DEFAULT])


}
