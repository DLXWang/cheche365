package com.cheche365.cheche.botpy.flow

import com.cheche365.cheche.botpy.flow.step.CheckIdentityCaptcha
import com.cheche365.cheche.botpy.flow.step.CheckInsureStatus
import com.cheche365.cheche.botpy.flow.step.CheckSpecialEngage
import com.cheche365.cheche.botpy.flow.step.CreateFindICModelsByAutoModel
import com.cheche365.cheche.botpy.flow.step.CreateFindICModelsByVinNo
import com.cheche365.cheche.botpy.flow.step.CreateProposal
import com.cheche365.cheche.botpy.flow.step.CreateQuote
import com.cheche365.cheche.botpy.flow.step.FindVehicleInfo
import com.cheche365.cheche.botpy.flow.step.PollFindICModels
import com.cheche365.cheche.botpy.flow.step.PollFindICModelsForAutoType
import com.cheche365.cheche.botpy.flow.step.PollQuote
import com.cheche365.cheche.botpy.flow.step.SendIdentityCaptcha
import com.cheche365.cheche.botpy.flow.step.SubmitInsurance
import com.cheche365.cheche.botpy.flow.step.SubmitInsuranceAgain
import com.cheche365.cheche.botpy.flow.step.SubmitSync
import com.cheche365.cheche.botpy.flow.step.UploadImage
import com.cheche365.cheche.botpy.flow.step.SelectICModelsFlow
import com.cheche365.cheche.botpy.flow.step.CreateRenewals
import com.cheche365.cheche.botpy.flow.step.PollRenewals
import com.cheche365.cheche.botpy.flow.step.PollFindICModelsByVinNo
import com.cheche365.cheche.botpy.flow.step.PollFindICModelsByCode
import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.parser.flow.steps.CheckInsurancesCheckList
import com.cheche365.cheche.parser.flow.steps.CheckSupplementInfo
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceBasicInfo
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceInfo
import com.cheche365.cheche.parser.flow.steps.InsurePostProcessor
import com.cheche365.cheche.parser.flow.steps.ProcessVehicleLicenseStagedResult
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.cheche.parser.flow.steps.QuotePreProcessor
import com.cheche365.cheche.parser.flow.steps.SelectCarModel
import com.cheche365.flow.business.flow.step.ExtractVehicleInfo



/**
 * 所有流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        抽取车辆信息        : ExtractVehicleInfo,
        处理行驶证阶段性结果    : ProcessVehicleLicenseStagedResult,
        抽取保险基本信息      : ExtractInsuranceBasicInfo,
        抽取保险信息        : ExtractInsuranceInfo,

        检查补充信息        : CheckSupplementInfo,
        报价前处理器        : QuotePreProcessor,

        查询车辆信息        : FindVehicleInfo,
        依据车架号车型查询     : CreateFindICModelsByVinNo,
        依据品牌型号车型查询    : CreateFindICModelsByAutoModel,
        轮询保险公司车型查询结果  : PollFindICModels,
        轮询保险公司车型列表查询结果: PollFindICModelsForAutoType,
        创建报价请求        : CreateQuote,
        轮询报价结果        : PollQuote,

        报价后处理器        : QuotePostProcessor,

        检索保险公司账号特别约定  : CheckSpecialEngage,
        判断核保状态        : CheckInsureStatus,
        创建投保单         : CreateProposal,
        提交核保          : SubmitInsurance,
        上传照片提交核保      : SubmitInsuranceAgain,
        发送身份证验证码      : SendIdentityCaptcha,
        校验身份证验证码      : CheckIdentityCaptcha,
        上传影像          : UploadImage,
        提交同步          : SubmitSync,

        检查险种清单        : CheckInsurancesCheckList,
        核保后处理器        : InsurePostProcessor,

        车型流程选择        : SelectICModelsFlow,
        创建续保查询        : CreateRenewals,
        轮询续保查询结果     : PollRenewals,
        轮询车架号车型查询结果: PollFindICModelsByVinNo,
        轮询品牌型号车型查询结果: PollFindICModelsByCode,
        默认车型选择         : SelectCarModel,
    ]

    //<editor-fold defaultstate="collapsed" desc="流程片段">

    private static final get_SNIPPET_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    private static final _GET_VEHICLE_MODE = _SNIPPET_BUILDER {
        车型流程选择 >> [
            精确车型查询 : {
                创建续保查询 >> 轮询续保查询结果 >> 依据车架号车型查询 >> 轮询车架号车型查询结果  >> 依据品牌型号车型查询 >> 轮询品牌型号车型查询结果 >> 默认车型选择
            },
            车型模糊匹配 : {
                依据品牌型号车型查询 >> 轮询保险公司车型查询结果
            }
        ]
    }

    private static final _NAME_FLOW_MAPPINGS = [
        综合车型查询片段    : _GET_VEHICLE_MODE
    ]

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="模板片段">

    private static final _QUOTING_TEMPLATE = {
        报价前处理器 >> 检查补充信息 >> PH1 >> loop({ 创建报价请求 >> 轮询报价结果 >> [
            自动修正车型 : {默认车型选择}
        ]}, 2) >> 报价后处理器
    }

    private static final _NAME_TEMPLATE_MAPPINGS = [
        报价模板: _QUOTING_TEMPLATE,
    ]
    //</editor-fold>

    private static get_FLOW_BUILDER() {
        new FlowBuilder(
            nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS,
            nameFlowMappings: _NAME_FLOW_MAPPINGS,
            nameTemplateMappings: _NAME_TEMPLATE_MAPPINGS,
        )
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Quote Flows">
    static final _QUOTE_FLOW = _FLOW_BUILDER {
        make '报价模板',
            [
                PH1: '综合车型查询片段',
            ]
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Insure Flows">
    static final _INSURE_FLOW_110000 = _FLOW_BUILDER {
        检查补充信息 >> 判断核保状态 >> [
            需要上传影像         : {
                上传影像 >> 上传照片提交核保
            },
            未创建投保单         : {
                检索保险公司账号特别约定 >> loop {
                    创建投保单 >> 提交核保
                } >> 发送身份证验证码
            },
            人工核保成功待发送身份证验证码: {
                发送身份证验证码
            }
        ] >> 校验身份证验证码 >> 核保后处理器
    }

    static final _INSURE_FLOW = _FLOW_BUILDER {
        检查补充信息 >> 判断核保状态 >> [
            需要上传影像: {
                上传影像 >> 上传照片提交核保
            },
            未创建投保单: {
                检索保险公司账号特别约定 >> loop {
                    创建投保单 >> 提交核保
                }
            }
        ] >> 核保后处理器
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="查车型列表流程">
    static final _QUERY_VEHICLE_FLOW = _FLOW_BUILDER {
        依据车架号车型查询 >> 轮询保险公司车型列表查询结果 >> [
            未查到车型列表: {
                依据品牌型号车型查询 >> 轮询保险公司车型列表查询结果
            }
        ]
    }

    //</editor-fold>

}
