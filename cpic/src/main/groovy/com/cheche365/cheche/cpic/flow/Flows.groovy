package com.cheche365.cheche.cpic.flow

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.common.flow.FlowChain
import com.cheche365.cheche.common.flow.step.Identity
import com.cheche365.cheche.common.flow.step.NoOp
import com.cheche365.cheche.common.flow.step.UnexpectedEnd
import com.cheche365.cheche.cpic.flow.step.CalcPremium
import com.cheche365.cheche.cpic.flow.step.CalcTravelTax
import com.cheche365.cheche.cpic.flow.step.FindJYVehicleMode
import com.cheche365.cheche.cpic.flow.step.GetIssueCode
import com.cheche365.cheche.cpic.flow.step.GetNewCaptcha
import com.cheche365.cheche.cpic.flow.step.InitQuotation
import com.cheche365.cheche.cpic.flow.step.InitTravelTax
import com.cheche365.cheche.cpic.flow.step.InitVehicleBaseInfo
import com.cheche365.cheche.cpic.flow.step.InitVehicleDetailInfo
import com.cheche365.cheche.cpic.flow.step.LoadCityBranchCode
import com.cheche365.cheche.cpic.flow.step.MatchVehicleMode
import com.cheche365.cheche.cpic.flow.step.QueryVehicleModelOnPlatformNew
import com.cheche365.cheche.cpic.flow.step.QuoteUnderwriting
import com.cheche365.cheche.cpic.flow.step.SubmitVehicleBaseInfoNonRenewal
import com.cheche365.cheche.cpic.flow.step.SubmitVehicleBaseInfoRenewal
import com.cheche365.cheche.cpic.flow.step.SubmitVehicleDetailInfo
import com.cheche365.cheche.cpic.flow.step.VehicleDetailInfo
import com.cheche365.cheche.cpic.flow.step.VerifyCaptcha
import com.cheche365.cheche.cpic.flow.step.VerifyNewCaptcha
import com.cheche365.cheche.parser.flow.steps.CheckInsurancesCheckList
import com.cheche365.cheche.parser.flow.steps.Decaptcha
import com.cheche365.cheche.parser.flow.steps.ExtractAutoInfo
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceBasicInfo
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceInfo
import com.cheche365.cheche.parser.flow.steps.NotFoundVehicleInfo
import com.cheche365.cheche.parser.flow.steps.UpdateAutoInfo
import com.cheche365.cheche.parser.flow.steps.UpdateSupplementInfo
import com.cheche365.flow.business.flow.step.ExtractVehicleInfo
import com.cheche365.cheche.parser.flow.steps.ExtractVehicleModels
import com.cheche365.cheche.parser.flow.steps.ProcessVehicleLicenseStagedResult
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor



/**
 * 所有流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        //common中的
        同前        : Identity,
        无          : NoOp,
        非预期结束   : UnexpectedEnd,

        //parser中的
        报价后处理器             : QuotePostProcessor,
        检查险种清单             : CheckInsurancesCheckList,
        识别新验证码             : Decaptcha,
        识别商业险转保验证码       : new Decaptcha('bsCaptchaText'),
        识别交强险转保验证码       : new Decaptcha('bzCaptchaText'),
        获取续保车辆信息失败       : NotFoundVehicleInfo,
        抽取车型列表             : ExtractVehicleModels,
        抽取保险基本信息          : ExtractInsuranceBasicInfo,
        抽取保险信息             : ExtractInsuranceInfo,
        抽取车辆信息             : ExtractVehicleInfo,
        抽取auto信息            : ExtractAutoInfo,
        更新auto信息            : UpdateAutoInfo,
        获取续保车辆信息          : ExtractVehicleInfo,
        处理行驶证阶段性结果      : ProcessVehicleLicenseStagedResult,
        更新补充信息            : UpdateSupplementInfo,

//        查询政策信息             : SubmitPolicyInfo,
//        政策决策               : PolicyDecision,
        获取基础信息             : InitVehicleBaseInfo,
        获取城市信息             : LoadCityBranchCode,
        获取新验证码             : GetNewCaptcha,
        校验新验证码并获取车辆基本信息 : VerifyNewCaptcha,
        识别图片验证码           : VerifyCaptcha,
        关键字查询车型信息        : QueryVehicleModelOnPlatformNew,
        车型详细信息             : VehicleDetailInfo,
        查找精友车型库            : FindJYVehicleMode,
        获取续保车辆基本信息       : SubmitVehicleBaseInfoRenewal,
        获取转保车辆基本信息       : SubmitVehicleBaseInfoNonRenewal,
        查询车辆信息V3            : SubmitVehicleDetailInfo,
        初始化车辆详细信息V3       : InitVehicleDetailInfo,
        匹配车型                 : MatchVehicleMode,
        获取商业险报价V3          : CalcPremium,
        初始化报价V3             : InitQuotation,
        初始化交强险V3            : InitTravelTax,
        获取交强险报价V3          : CalcTravelTax,
        商业险核保V3             : QuoteUnderwriting,
        获取手机验证码            : GetIssueCode
    ]

    //<editor-fold defaultstate="collapsed" desc="流程片段">

    private static final get_SNIPPET_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    /**
     * 查询车辆信息片段
     */
    private static final _GET_VEHICLE_INFO_SNIPPET = _SNIPPET_BUILDER {
        loop {
            查询车辆信息V3
        }
    }

    /**
     * 获取精友车型片段
     */
    private static final _JY_VEHICLE_MODE_SNIPPET = _SNIPPET_BUILDER {
        查找精友车型库 >> 匹配车型
    }

    /**
     * 抽取精友车型片段
     */
    private static final _EXTRACT_VEHICLE_MODE_SNIPPET = _SNIPPET_BUILDER {
        查找精友车型库 >> 抽取车型列表
    }

    /**
     * 江苏验证码识别片段
     */
    private static final _GET_CAPTCHA_320100 = _SNIPPET_BUILDER {
        loop({ 获取新验证码 >> 识别新验证码 >> 校验新验证码并获取车辆基本信息 }, 10)
    }

    /**
     * 商业险报价V3片段，新增商业险转保验证码获取、识别、校验部分
     */
    private static final _COMMERCIAL_QUOTING_V3_SNIPPET = _SNIPPET_BUILDER {
        loop { 初始化报价V3 } >> 抽取auto信息 >> loop { 获取商业险报价V3 >> 识别商业险转保验证码 >> 获取商业险报价V3 }
    }

    /**
     * 交强险报价V3片段，新增交強险转保验证码获取、识别、校验部分
     */
    private static final _COMPULSORY_QUOTING_V3_SNIPPET = _SNIPPET_BUILDER {
        loop { 初始化交强险V3 } >> loop { 获取交强险报价V3 >> 识别交强险转保验证码 >> 获取交强险报价V3 }
    }

    /**
     * 获取手机验证码片段
     */
    private static final _GET_ISSUE_CODE_SNIPPET = _SNIPPET_BUILDER {
        loop {
            获取手机验证码
        }
    }

    /**
     * 获取图片验证码片段
     */
    private static final _GET_CAPTCHA_SNIPPET = _SNIPPET_BUILDER {
        loop {
            识别图片验证码
        }
    }

    private static final _NAME_FLOW_MAPPINGS = [
        获取手机验证码片段 : _GET_ISSUE_CODE_SNIPPET,
        获取图片验证码片段 : _GET_CAPTCHA_SNIPPET,
        商业险报价V3片段 : _COMMERCIAL_QUOTING_V3_SNIPPET,
        交强险报价V3片段 : _COMPULSORY_QUOTING_V3_SNIPPET,
        获取精友车型 : _JY_VEHICLE_MODE_SNIPPET,
        江苏验证码识别 : _GET_CAPTCHA_320100,
        抽取精友车型片段 : _EXTRACT_VEHICLE_MODE_SNIPPET,
        查询车辆信息片段 : _GET_VEHICLE_INFO_SNIPPET,
    ]

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="流程模板">

    /**
     * 获取城市及续保车辆信息模板
     */
    private static final _GET_CITY_AND_VEHICLE_INFO_TEMPLATE = {
        获取基础信息 >> loop { 获取城市信息 } >> PH1 >> loop { 获取续保车辆基本信息 }
    }

    /**
     * 车辆信息查询V3模板
     */
    private static final _GET_VEHICLE_INFO_V3_TEMPLATE = {
        loop{ 初始化车辆详细信息V3 } >> [
            (true): {
                PH1
            }
        ] >> PH2
    }

    /**
     * 北京获取车型模板
     */
    private static final _VEHICLE_MODEL_110000_TEMPLATE = {
        loop { 初始化车辆详细信息V3 } >> [
            (true): {
                loop { 初始化车辆详细信息V3 } >> 车型详细信息 >> 抽取车型列表
            }
        ]
    }

    /**
     * 获取车型模板
     */
    private static final _GET_VEHICLE_INFO_TEMPLATE = {
        PH1 >> 抽取车辆信息
    }

    /**
     * 报价模板
     */
    private static final _QUOTING_TEMPLATE = {
        PH1 >> PH2 >> 交强险报价V3片段 >> 更新auto信息 >> PH3
    }

    /**
     * 商业险报价V3模板
     */
    private static final _COMMERCIAL_QUOTING_V3_TEMPLATE = {
        fork([
            (1): {
                更新补充信息 >> PH1 >> 查询车辆信息片段
            },
            (3): {
                更新补充信息 >> loop {
                    获取转保车辆基本信息
                } >> [
                    (1): {
                        PH1 >> 查询车辆信息片段
                    }
                ]
            }
        ]) >> 商业险报价V3片段
    }

    /**
     * 默认商业险报价模板
     */
    private static final _COMMERCIAL_QUOTING_DEFAULT_TEMPLATE = {
        fork([
            (1): {
                更新补充信息 >> PH1 >> 查询车辆信息片段
            },
        ]) >> 商业险报价V3片段
    }

    /**
     * 获取保险信息模板
     */
    static final _INSURING_INFO_TEMPLATE = {
        PH1 >> [
            (3) : {
                获取续保车辆信息失败
            }
        ] >> 抽取车辆信息 >> 处理行驶证阶段性结果 >> loop { 初始化车辆详细信息V3 } >> 查询车辆信息片段 >> loop { 初始化报价V3 } >> 抽取保险基本信息 >> PH2
    }

    private static final _NAME_TEMPLATE_MAPPINGS = [
        报价模板 : _QUOTING_TEMPLATE,
        获取城市及续保车辆信息模板 : _GET_CITY_AND_VEHICLE_INFO_TEMPLATE,
        获取车型模板 : _GET_VEHICLE_INFO_TEMPLATE,
        商业险报价V3模板  : _COMMERCIAL_QUOTING_V3_TEMPLATE,
        默认商业险报价模板 : _COMMERCIAL_QUOTING_DEFAULT_TEMPLATE,
        车辆信息查询V3模板 : _GET_VEHICLE_INFO_V3_TEMPLATE,
        北京车型查询模板 : _VEHICLE_MODEL_110000_TEMPLATE,
        获取保险信息模板 : _INSURING_INFO_TEMPLATE
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

    //北京
    static final _QUOTING_FLOW_V3_110000 = _FLOW_BUILDER {
        make '报价模板',
            [
                PH1: [
                     '获取城市及续保车辆信息模板',
                    [
                        PH1: '获取手机验证码片段',
                    ]
                ],
                PH2: [
                    '商业险报价V3模板',
                    [
                        PH1: [
                            '车辆信息查询V3模板',
                            [
                                PH1: '关键字查询车型信息',
                                PH2: '同前'
                            ]
                        ]
                    ]
                ],
                PH3: '报价后处理器'
            ]
    }

    //salesNew  v3 目前大量地区都已经改为此流程
    static final _QUOTING_FLOW_V3 = _FLOW_BUILDER {
        make '报价模板',
            [
                PH1: [
                    '获取城市及续保车辆信息模板',
                    [
                        PH1: '获取图片验证码片段',
                    ]
                ],
                PH2: [
                    '默认商业险报价模板',
                    [
                        PH1: [
                            '车辆信息查询V3模板',
                            [
                                PH1: '关键字查询车型信息',
                                PH2: '同前'
                            ]
                        ]
                    ]
                ],
                PH3: '报价后处理器'
            ]
    }

    // 江苏
    static final _QUOTING_FLOW_V3_NEW_CAPTCHA = _FLOW_BUILDER {
        make '报价模板',
            [
                PH1: [
                    '获取城市及续保车辆信息模板',
                    [
                        PH1: '获取图片验证码片段',
                    ]
                ],
                PH2: [
                    '默认商业险报价模板',
                    [
                        PH1: [
                            '车辆信息查询V3模板',
                            [
                                PH1: '江苏验证码识别',
                                PH2: '获取精友车型'
                            ]
                        ]
                    ]
                ],
                PH3: '报价后处理器'
            ]
    }

    // 核保流程
    private static final _INSURING_COMMERCIAL_V3 = _FLOW_BUILDER {
        商业险核保V3
    }
    // 核保 检查险种清单
    private static final _INSURING_CHECK_LIST = _FLOW_BUILDER {
        检查险种清单
    }

    // 北京核保
    static final _INSURING_FLOW_V3_110000 = new FlowChain(flows: [_QUOTING_FLOW_V3_110000, _INSURING_COMMERCIAL_V3, _INSURING_CHECK_LIST])
    // 非江苏、北京地区核保
    static final _INSURING_FLOW_V3 = new FlowChain(flows: [_QUOTING_FLOW_V3, _INSURING_COMMERCIAL_V3, _INSURING_CHECK_LIST])
    // 江苏核保
    static final _INSURING_FLOW_V3_NEW_CAPTCHA = new FlowChain(flows: [_QUOTING_FLOW_V3_NEW_CAPTCHA, _INSURING_COMMERCIAL_V3, _INSURING_CHECK_LIST])

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="获取保险信息流程">

    static final _INSURANCE_BASIC_INFO_FLOW_110000 = _FLOW_BUILDER {
        make '获取保险信息模板', [
            PH1: [
                '获取城市及续保车辆信息模板',
                [
                    PH1: '获取手机验证码片段',
                ]
            ],
            PH2: '无'
        ]
    }

    static final _INSURANCE_BASIC_INFO_FLOW_DEFAULT = _FLOW_BUILDER {
        make '获取保险信息模板', [
            PH1: [
                '获取城市及续保车辆信息模板',
                [
                    PH1: '获取图片验证码片段',
                ]
            ],
            PH2: '无'
        ]
    }

    static final _INSURANCE_INFO_FLOW_110000 = _FLOW_BUILDER {
        make '获取保险信息模板', [
            PH1: [
                '获取城市及续保车辆信息模板',
                [
                    PH1: '获取手机验证码片段',
                ]
            ],
            PH2: '抽取保险信息'
        ]
    }

    static final _INSURANCE_INFO_FLOW_DEFAULT = _FLOW_BUILDER {
        make '获取保险信息模板', [
            PH1: [
                '获取城市及续保车辆信息模板',
                [
                    PH1: '获取图片验证码片段',
                ]
            ],
            PH2: '抽取保险信息'
        ]
    }

    //</editor-fold>

}
