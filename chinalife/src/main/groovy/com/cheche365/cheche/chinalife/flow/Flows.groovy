package com.cheche365.cheche.chinalife.flow

import com.cheche365.cheche.chinalife.flow.step.BasePremium
import com.cheche365.cheche.chinalife.flow.step.CarActualPrice
import com.cheche365.cheche.chinalife.flow.step.CarRuleCheck
import com.cheche365.cheche.chinalife.flow.step.CheckKindItems
import com.cheche365.cheche.chinalife.flow.step.CheckLicensePlateNo
import com.cheche365.cheche.chinalife.flow.step.CheckRenewal
import com.cheche365.cheche.chinalife.flow.step.CustomPremium
import com.cheche365.cheche.chinalife.flow.step.FindCarInfo
import com.cheche365.cheche.chinalife.flow.step.FindCarModelInfo
import com.cheche365.cheche.chinalife.flow.step.FindCarModelInfoByMultiBrand
import com.cheche365.cheche.chinalife.flow.step.GetCarAutoTax
import com.cheche365.cheche.chinalife.flow.step.GetCarBZ
import com.cheche365.cheche.chinalife.flow.step.GetCarProposal
import com.cheche365.cheche.chinalife.flow.step.GetClauseTypeVersion
import com.cheche365.cheche.chinalife.flow.step.GetModelByBrand
import com.cheche365.cheche.chinalife.flow.step.GetNewCaptcha
import com.cheche365.cheche.chinalife.flow.step.GetOldProposalPage
import com.cheche365.cheche.chinalife.flow.step.GetPolicy
import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.common.flow.FlowChain
import com.cheche365.cheche.common.flow.step.Identity
import com.cheche365.cheche.common.flow.step.NoOp
import com.cheche365.cheche.common.flow.step.UnexpectedEnd
import com.cheche365.cheche.parser.flow.steps.CheckInsurancesCheckList
import com.cheche365.cheche.parser.flow.steps.CheckRenewalFlow
import com.cheche365.cheche.parser.flow.steps.Decaptcha
import com.cheche365.cheche.parser.flow.steps.ExtractAutoInfo
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceBasicInfo
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceInfo
import com.cheche365.cheche.parser.flow.steps.UpdateAutoInfo
import com.cheche365.cheche.parser.flow.steps.UpdateSupplementInfo
import com.cheche365.flow.business.flow.step.ExtractVehicleInfo
import com.cheche365.cheche.parser.flow.steps.ProcessVehicleLicenseStagedResult
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.cheche.parser.flow.steps.QuotePreProcessor



/**
 * 人寿财险报价流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        // common中的
        同前          : Identity,
        无           : NoOp,
        非预期结束       : UnexpectedEnd,

        // parser里面的
        更新补充信息      : UpdateSupplementInfo,
        报价前处理器      : QuotePreProcessor,
        报价后处理器      : QuotePostProcessor,
        检查续保通道流程    : CheckRenewalFlow,
        检查险种清单      : CheckInsurancesCheckList,
        抽取Auto信息      : ExtractAutoInfo,
        更新Auto信息      : UpdateAutoInfo,
        抽取续保车辆信息    : ExtractVehicleInfo,
        抽取套餐基本信息    : ExtractInsuranceBasicInfo,
        抽取保险信息      : ExtractInsuranceInfo,
        处理行驶证阶段性结果: ProcessVehicleLicenseStagedResult,
        识别新验证码      : Decaptcha,

        获取车辆信息      : FindCarInfo,
        获取基础套餐      : BasePremium,
        计算套餐        : CustomPremium,
        获取车型        : FindCarModelInfo,
        获取价格        : CarActualPrice,
        交强险报价       : GetCarBZ,
        车船税报价       : GetCarAutoTax,
        根据品牌型号获取车型  : GetModelByBrand,
        根据多个品牌型号获取车型: FindCarModelInfoByMultiBrand,
        自动核保        : CarRuleCheck,
        车牌号检查       : CheckLicensePlateNo,
        必须险种检查      : CheckKindItems,
        检查是否续保      : GetPolicy,
        是否续保        : CheckRenewal,
        获取续保套餐      : GetOldProposalPage,
        获取条款版本      : GetClauseTypeVersion,
        获取城市信息      : GetCarProposal,
        获取新验证码      : GetNewCaptcha
    ]


    //<editor-fold defaultstate="collapsed" desc="流程片段">

    private static final get_SNIPPET_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    //报价片段
    private static final _QUOTING_FLOW_SNIPPET = _SNIPPET_BUILDER {
        更新补充信息 >> 获取价格 >> 自动核保 >>
            是否续保 >> [
            (true) : {
                loop { 获取基础套餐 }
            },
            (false): {
                检查续保通道流程 >> loop({
                    获取基础套餐 >> [
                        (1): {
                            获取价格 >> 获取基础套餐
                        },
                        (2): {
                            识别新验证码 >> 获取基础套餐
                        }
                    ]
                }, 10)
            }
        ] >> 必须险种检查 >> 计算套餐 >> loop { 交强险报价 } >> 车船税报价 >> 更新Auto信息 >> 报价后处理器
    }

    //获取并识别新验证码片段
    private static final GET_AND_DECODE_NEW_CAPTCHA_SNIPPET = _SNIPPET_BUILDER {
        获取新验证码 >> 识别新验证码
    }

    /**
     * 获取车型片段
     */
    private static final _GET_AUTO_TYPE_SNIPPET = _SNIPPET_BUILDER {
        获取条款版本 >> 获取车型 >> 抽取Auto信息
    }

    /**
     * 根据多个品牌型号获取车型片段
     */
    private static final _GET_AUTO_TYPE_BY_MULTI_BRAND_SNIPPET = _SNIPPET_BUILDER {
        获取条款版本 >> 根据品牌型号获取车型 >> 根据多个品牌型号获取车型 >> 抽取Auto信息
    }

    private static final _OLD_FIND_CAR_MODEL_INFO_SNIPPET = _SNIPPET_BUILDER {
        获取条款版本 >> 根据品牌型号获取车型 >> 获取车型 >> 抽取Auto信息
    }

    // 核保片段
    private static final _UNDERWRITING_SNIPPET = _SNIPPET_BUILDER {
        检查险种清单
    }

    // 抽取保险信息片段
    private static final _EXTRACT_INSURANCE_INFO_SNIPPET = _SNIPPET_BUILDER {
        抽取续保车辆信息 >> 处理行驶证阶段性结果 >> 抽取套餐基本信息 >> 抽取保险信息
    }

    private static final _NAME_FLOW_MAPPINGS = [
        报价片段          : _QUOTING_FLOW_SNIPPET,
        获取并识别新验证码片段   : GET_AND_DECODE_NEW_CAPTCHA_SNIPPET,
        获取车型片段        : _GET_AUTO_TYPE_SNIPPET,
        根据多个品牌型号获取车型片段: _GET_AUTO_TYPE_BY_MULTI_BRAND_SNIPPET,
        获取旧的车型片段      : _OLD_FIND_CAR_MODEL_INFO_SNIPPET,
        核保片段          : _UNDERWRITING_SNIPPET,
        抽取保险信息片段      : _EXTRACT_INSURANCE_INFO_SNIPPET
    ]

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="流程模板">

    /**
     * 条件判断模板
     */
    private static final _COMMON_CONDITIONS_TEMPLATE = {
        fork([
            (true): {
                PH1
            },
            (false) : {
                PH2
            }
        ])
    }

    /**
     * 获取车辆信息模板
     */
    private static final _FIND_CAR_INFO_TEMPLATE = {
        loop ({
            PH1 >> 获取车辆信息
        }, 10) >> 获取条款版本 >> 获取车型 >> PH2 >> 抽取Auto信息
    }

    /**
     * 报价模板
     */
    private static final _QUOTING_TEMPLATE = {
        获取城市信息 >> 车牌号检查 >> 检查是否续保 >> PH1 >> 报价片段
    }

    /**
     * 获取保险信息模板
     */
    private static final _INSURANCE_INFO_TEMPLATE = {
        检查是否续保 >> PH1
    }

    private static final _NAME_TEMPLATE_MAPPINGS = [
        条件判断模板  : _COMMON_CONDITIONS_TEMPLATE,
        报价模板    : _QUOTING_TEMPLATE,
        获取车辆信息模板: _FIND_CAR_INFO_TEMPLATE,
        获取保险信息模板: _INSURANCE_INFO_TEMPLATE
    ]

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="报价、核保流程">

    private static get_FLOW_BUILDER() {
        new FlowBuilder(
            nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS,
            nameFlowMappings: _NAME_FLOW_MAPPINGS,
            nameTemplateMappings: _NAME_TEMPLATE_MAPPINGS
        )
    }

    //北京
    static final _QUOTING_FLOW_TYPE1 = _FLOW_BUILDER {
        make '报价模板', [
            PH1: [
                '获取车辆信息模板', [
                    PH1: '无',
                    PH2: '无'
                ]
            ]
        ]
    }

    // 上海深圳杭州等不需要选择车型的
    static final _QUOTING_FLOW_TYPE2 = _FLOW_BUILDER {
        make '报价模板', [
            PH1: '获取车型片段'
        ]
    }
    //广州老的让选择车型的
    static final _QUOTING_FLOW_TYPE3 = _FLOW_BUILDER {
        make '报价模板', [
            PH1: '获取旧的车型片段'
        ]
    }
    //广州等让选择车型的
    static final _QUOTING_FLOW_TYPE4 = _FLOW_BUILDER {
        make '报价模板', [
            PH1: '根据多个品牌型号获取车型片段'
        ]
    }
    //苏州、南京 需要校验验证码
    static final _QUOTING_FLOW_TYPE5 = _FLOW_BUILDER {
        make '报价模板', [
            PH1: [
                '获取车辆信息模板', [
                    PH1: '获取并识别新验证码片段',
                    PH2: [
                        '条件判断模板', [
                            PH1: '根据多个品牌型号获取车型片段',
                            PH2: '无'
                        ]
                    ]
                ]
            ]
        ]
    }

    static final _INSURING_FLOW_TYPE1 = new FlowChain(flows: [_QUOTING_FLOW_TYPE1, _UNDERWRITING_SNIPPET])
    static final _INSURING_FLOW_TYPE2 = new FlowChain(flows: [_QUOTING_FLOW_TYPE2, _UNDERWRITING_SNIPPET])
    static final _INSURING_FLOW_TYPE3 = new FlowChain(flows: [_QUOTING_FLOW_TYPE3, _UNDERWRITING_SNIPPET])
    static final _INSURING_FLOW_TYPE4 = new FlowChain(flows: [_QUOTING_FLOW_TYPE4, _UNDERWRITING_SNIPPET])
    static final _INSURING_FLOW_TYPE5 = new FlowChain(flows: [_QUOTING_FLOW_TYPE5, _UNDERWRITING_SNIPPET])

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="获取保险信息流程">

    static final _INSURANCE_BASIC_INFO_FLOW = _FLOW_BUILDER {
        make '获取保险信息模板', [
            PH1: '抽取套餐基本信息'
        ]
    }

    static final _INSURANCE_INFO_FLOW = _FLOW_BUILDER {
        make '获取保险信息模板', [
            PH1: '抽取保险信息片段'
        ]
    }

    //</editor-fold>

}
