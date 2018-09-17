package com.cheche365.cheche.pingan.flow

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.common.flow.FlowChain
import com.cheche365.cheche.common.flow.step.Identity
import com.cheche365.cheche.common.flow.step.NoOp
import com.cheche365.cheche.common.flow.step.UnexpectedEnd
import com.cheche365.cheche.parser.flow.steps.CheckInsurancesCheckList
import com.cheche365.cheche.parser.flow.steps.CheckRenewalFlow
import com.cheche365.cheche.parser.flow.steps.CheckSupplementInfo
import com.cheche365.cheche.parser.flow.steps.Decaptcha
import com.cheche365.cheche.parser.flow.steps.ExtractAutoInfo
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceBasicInfo
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceInfo
import com.cheche365.cheche.parser.flow.steps.ExtractVehicleModels
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.cheche.parser.flow.steps.UpdateAutoInfo
import com.cheche365.cheche.parser.flow.steps.UpdateSupplementInfo
import com.cheche365.cheche.pingan.flow.step.m.CalculateCommercialPremium
import com.cheche365.cheche.pingan.flow.step.m.CommercialQuote
import com.cheche365.cheche.pingan.flow.step.m.ForceQuote
import com.cheche365.cheche.pingan.flow.step.m.GetNewCaptcha
import com.cheche365.cheche.pingan.flow.step.m.GetRenewalPackage
import com.cheche365.cheche.pingan.flow.step.m.GetVehicleModels
import com.cheche365.cheche.pingan.flow.step.m.InfoConfirm
import com.cheche365.cheche.parser.flow.steps.QuotePreProcessor
import com.cheche365.cheche.pingan.flow.step.m.AdjustInsurancePackage
import com.cheche365.cheche.pingan.flow.step.m.QuoteResultInspection
import com.cheche365.cheche.pingan.flow.step.m.QuoteSaveQuoteInfo
import com.cheche365.cheche.pingan.flow.step.m.RenewalCheckAndGetFlowId
import com.cheche365.cheche.pingan.flow.step.m.RenewalSaveQuoteInfo
import com.cheche365.cheche.pingan.flow.step.m.ToAudit
import com.cheche365.cheche.pingan.flow.step.m.ToQueryInfo
import com.cheche365.cheche.pingan.flow.step.m.VerifyNewCaptcha



/**
 * 平安报价流程
 * Created by houjinxin on 2015/6/30.
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        // common中的
        同前        : Identity,
        无          : NoOp,
        非预期结束   : UnexpectedEnd,

        // parser里面的
        报价前处理器     : QuotePreProcessor,
        报价后处理器     : QuotePostProcessor,
        检查续保通道流程   : CheckRenewalFlow,
        检查险种清单     : CheckInsurancesCheckList,
        检查补充信息     : CheckSupplementInfo,
        抽取车型列表      : ExtractVehicleModels,
        抽取保险基本信息   : ExtractInsuranceBasicInfo,
        抽取保险信息      : ExtractInsuranceInfo,
        更新补充信息      : UpdateSupplementInfo,

        // M站转保接口
        M站续保检查     : RenewalCheckAndGetFlowId,
        计算商业险报价    : CalculateCommercialPremium,
        获取交强险报价    : ForceQuote,
        查询车型是否含税   : ToQueryInfo,
        获取新验证码     : GetNewCaptcha,
        识别新验证码     : Decaptcha,
        校验新验证码     : VerifyNewCaptcha,
        M站获取车型列表   : GetVehicleModels,
        M站商业险报价    : CommercialQuote,
        获取上年商业险续保套餐: GetRenewalPackage,
        商业险套餐检查    : QuoteResultInspection,
        报价保存车辆信息   : QuoteSaveQuoteInfo,
        续保保存车辆信息   : RenewalSaveQuoteInfo,
        确认信息           : InfoConfirm,
        查询商业险交强险审计结果   : ToAudit,
        更新Auto信息        : UpdateAutoInfo,
        获取Auto信息        : ExtractAutoInfo,
        修改全险套餐        : AdjustInsurancePackage
    ]



    //<editor-fold defaultstate="collapsed" desc="流程片段">

    private static get_SNIPPET_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }



    /**
     * 获取、识别并校验新验证码的片段
     */
    private static final _GET_RECOGNIZE_AND_VERIFY_NEW_CAPTCHA_SNIPPET = _SNIPPET_BUILDER {
        loop({
            获取新验证码 >> 识别新验证码 >> 校验新验证码
        }, 10)
    }

    /**
     * 续保免税获取车型片段
     */
    private static final _RENEWAL_TAX_FREE_GET_AUTO_TYPE_SNIPPET = _SNIPPET_BUILDER {
        查询车型是否含税 >> M站获取车型列表 >> 检查补充信息 >> 报价保存车辆信息
    }

    /**
     * 转保获取车型片段
     */
    private static final _NOT_RENEWAL_GET_AUTO_TYPE_SNIPPET = _SNIPPET_BUILDER {
        检查续保通道流程 >> 查询车型是否含税 >> 更新补充信息 >> M站获取车型列表 >> 检查补充信息
    }

    /**
     * 获取商业险和交强险报价片段
     */
    private static final _GET_COMMERCIAL_AND_COMPULSORY_QUOTES_SNIPPET = _SNIPPET_BUILDER {
        fork([
            (true): {
                loop({
                    M站商业险报价 >> [
                        (true) : {
                            商业险套餐检查
                        },
                        (false): {
                            修改全险套餐
                        }
                    ]
                }, 5) >> [
                    (true): {
                        计算商业险报价
                    }
                ]
            }
        ]) >> 获取交强险报价 >> [
            (true): {
                获取交强险报价
            }
        ] >> 获取Auto信息 >> 更新Auto信息
    }

    private static final _NAME_FLOW_MAPPINGS = [
        获取识别并校验新验证码: _GET_RECOGNIZE_AND_VERIFY_NEW_CAPTCHA_SNIPPET,
        续保免税获取车型  : _RENEWAL_TAX_FREE_GET_AUTO_TYPE_SNIPPET,
        转保获取车型   : _NOT_RENEWAL_GET_AUTO_TYPE_SNIPPET,
        获取商业险和交强险报价: _GET_COMMERCIAL_AND_COMPULSORY_QUOTES_SNIPPET
    ]

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="流程模板">

    /**
     * 续保含税获取车型片段模板（PH1意为PlaceHolder1）
     */
    private static final _RENEWAL_TAX_GET_AUTO_TYPE_TEMPLATE = {
        检查补充信息 >> PH1 >> 续保保存车辆信息 >> [
            (true) : {
                获取上年商业险续保套餐
            },
            (false): {
                查询车型是否含税 >> M站获取车型列表 >> 检查补充信息 >> 报价保存车辆信息
            }
        ]
    }

    /**
     * 转保获取车型模板
     */
    private static final _NON_RENEWAL_GET_AUTO_TYPE_TEMPLATE = {
        转保获取车型 >> PH1 >> 报价保存车辆信息
    }

    /**
     * 获取车型模板
     */
    private static final _GET_AUTO_TYPE_TEMPLATE = {
        fork([
            // 续保
            (0): {
                查询车型是否含税 >> [
                    (true)  : {
                        PH1
                    },
                    (false) : {
                        续保免税获取车型
                    }
                ]
            },
            // 转保
            (1): {
                PH2
            }
        ])
    }

    /**
     * 报价模板
     */
    private static final _QUOTING_TEMPLATE = {
        报价前处理器 >> M站续保检查 >> PH1 >> 更新补充信息 >> 获取商业险和交强险报价 >> 检查险种清单 >> 报价后处理器
    }

    /**
     * 获取保险信息模板
     */
    private static final _INSURANCE_INFO_TEMPLATE = {
        M站续保检查 >> [
            (0): { PH1 >> 查询车型是否含税 >> 续保保存车辆信息 >> 获取上年商业险续保套餐 },  //续保
        ] >> 抽取保险基本信息 >> PH2
    }

    private static final _NAME_TEMPLATE_MAPPINGS = [
        续保含税获取车型模板  : _RENEWAL_TAX_GET_AUTO_TYPE_TEMPLATE,
        转保获取车型模板     : _NON_RENEWAL_GET_AUTO_TYPE_TEMPLATE,
        获取车型模板        : _GET_AUTO_TYPE_TEMPLATE,
        报价模板            : _QUOTING_TEMPLATE,
        获取保险信息模板     : _INSURANCE_INFO_TEMPLATE
    ]

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="报价、核保流程">

    private static get_FLOW_BUILDER() {
        new FlowBuilder(
            nameClazzMappings   : _STEP_NAME_CLAZZ_MAPPINGS,
            nameFlowMappings    : _NAME_FLOW_MAPPINGS,
            nameTemplateMappings: _NAME_TEMPLATE_MAPPINGS
        )
    }

    static final _QUOTING_AND_RENEWAL_FLOW = _FLOW_BUILDER {
        make '报价模板',
        [
            PH1:
            [
                '获取车型模板',
                [
                    PH1: [
                        '续保含税获取车型模板',
                        [PH1: '同前']
                    ],
                    PH2: [
                        '转保获取车型模板',
                        [PH1: '同前']
                    ]
                ]
            ]
        ]
    }


    static final _QUOTING_AND_RENEWAL_FLOW_TYPE2 = _FLOW_BUILDER {
        make '报价模板',
        [
            PH1:
            [
                '获取车型模板',
                [
                    PH1: [
                        '续保含税获取车型模板',
                        [PH1: '获取识别并校验新验证码']
                    ],
                    PH2: [
                        '转保获取车型模板',
                        [PH1: '获取识别并校验新验证码']
                    ]
                ]
            ]
        ]
    }



    // 浙江在成功报价且提交订单后有核保步骤
    private static final _UNDERWRITING_SNIPPET = _SNIPPET_BUILDER {
        确认信息 >> 查询商业险交强险审计结果
    }


    // TODO 需要测试北上广深
    static final _INSURING_FLOW_DEFAULT  = new FlowChain(flows: [_QUOTING_AND_RENEWAL_FLOW])
    // 南京，苏州
    static final _INSURING_FLOW_TYPE2  = new FlowChain(flows: [_QUOTING_AND_RENEWAL_FLOW_TYPE2, _UNDERWRITING_SNIPPET])
    // 浙江
    static final _INSURING_FLOW_330100 = new FlowChain(flows: [_QUOTING_AND_RENEWAL_FLOW, _UNDERWRITING_SNIPPET])

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="获取保险信息流程">

    static final _INSURANCE_BASIC_INFO_FLOW_DEFAULT = _FLOW_BUILDER {
        make '获取保险信息模板',
        [
            PH1: '无',
            PH2: '无'
        ]
    }

    static final _INSURANCE_BASIC_INFO_FLOW_320100 =  _FLOW_BUILDER {
        make '获取保险信息模板',
        [
            PH1: '获取识别并校验新验证码',
            PH2: '无'
        ]
    }

    static final _INSURANCE_INFO_FLOW_DEFAULT = _FLOW_BUILDER {
        make '获取保险信息模板',
        [
            PH1: '无',
            PH2: '抽取保险信息'
        ]
    }

    static final _INSURANCE_INFO_FLOW_320100 = _FLOW_BUILDER {
        make '获取保险信息模板',
        [
            PH1: '获取识别并校验新验证码',
            PH2: '抽取保险信息'
        ]
    }

    //</editor-fold>


}
