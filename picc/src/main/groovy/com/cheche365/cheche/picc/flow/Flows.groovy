package com.cheche365.cheche.picc.flow

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
import com.cheche365.cheche.parser.flow.steps.UpdateAutoInfo
import com.cheche365.cheche.parser.flow.steps.UpdateSupplementInfo
import com.cheche365.cheche.picc.flow.step.v2.FindCarModel2List
import com.cheche365.cheche.picc.flow.step.v2.BackToNormalQuote
import com.cheche365.cheche.picc.flow.step.v2.FindCarModel02
import com.cheche365.cheche.picc.flow.step.v2.FindCarModelByVin
import com.cheche365.cheche.picc.flow.step.v2.FindCarModelByVin01
import com.cheche365.cheche.picc.flow.step.v2.FindCarModelJYQuery01
import com.cheche365.cheche.picc.flow.step.v2.LoadCalculateInfo
import com.cheche365.cheche.picc.flow.step.v2.ReinsuranceBJProposal
import com.cheche365.cheche.picc.flow.step.v2.VerificationForInsureQuery
import com.cheche365.flow.business.flow.step.ExtractVehicleInfo
import com.cheche365.cheche.parser.flow.steps.ExtractVehicleModels
import com.cheche365.cheche.parser.flow.steps.ProcessVehicleLicenseStagedResult
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.cheche.parser.flow.steps.QuotePreProcessor
import com.cheche365.cheche.picc.flow.step.AdjustPurchasePrice
import com.cheche365.cheche.picc.flow.step.CalculateForBZ
import com.cheche365.cheche.picc.flow.step.CalculateForBZTaxTypeB
import com.cheche365.cheche.picc.flow.step.CalculateForBZTaxTypeN
import com.cheche365.cheche.picc.flow.step.CalculateForBatch
import com.cheche365.cheche.picc.flow.step.CalculateForChangeKind
import com.cheche365.cheche.picc.flow.step.CalculateForQuickRenewal
import com.cheche365.cheche.picc.flow.step.CalculateForXuBao
import com.cheche365.cheche.picc.flow.step.CancelSafeDefray
import com.cheche365.cheche.picc.flow.step.CarBlackList
import com.cheche365.cheche.picc.flow.step.CarBlackListCar
import com.cheche365.cheche.picc.flow.step.CheckBZ
import com.cheche365.cheche.picc.flow.step.CheckInsuranceItemSingle
import com.cheche365.cheche.picc.flow.step.CheckPeriod
import com.cheche365.cheche.picc.flow.step.CheckPolicy
import com.cheche365.cheche.picc.flow.step.CheckPriceForCar
import com.cheche365.cheche.picc.flow.step.CheckProfit
import com.cheche365.cheche.picc.flow.step.CheckQuickRenewal
import com.cheche365.cheche.picc.flow.step.CheckReinsurancePeriod
import com.cheche365.cheche.picc.flow.step.CheckRenewal
import com.cheche365.cheche.picc.flow.step.FindCarModel
import com.cheche365.cheche.picc.flow.step.FindCarModelByBrandName
import com.cheche365.cheche.picc.flow.step.FindCarModelByBrandName03
import com.cheche365.cheche.picc.flow.step.FindCarModelShanghai04
import com.cheche365.cheche.picc.flow.step.FindVehicleBrandModel
import com.cheche365.cheche.picc.flow.step.GetCaptcha
import com.cheche365.cheche.picc.flow.step.GetCheckPeriod
import com.cheche365.cheche.picc.flow.step.GetCheckReinsurance
import com.cheche365.cheche.picc.flow.step.GetERiskData
import com.cheche365.cheche.picc.flow.step.GetNewCaptcha
import com.cheche365.cheche.picc.flow.step.GetNewUseYears
import com.cheche365.cheche.picc.flow.step.GetRenewalInfo
import com.cheche365.cheche.picc.flow.step.GetUniqueId
import com.cheche365.cheche.picc.flow.step.GetUseYears
import com.cheche365.cheche.picc.flow.step.HistoryCarVerifyCaptcha
import com.cheche365.cheche.picc.flow.step.InsuredBlackList
import com.cheche365.cheche.picc.flow.step.RegisterUniqueId
import com.cheche365.cheche.picc.flow.step.SaveProposal
import com.cheche365.cheche.picc.flow.step.VerifyCaptcha
import com.cheche365.cheche.picc.flow.step.VerifyNewCaptcha
import com.cheche365.cheche.picc.flow.step.v2.CalculateBIForChangeItemKind
import com.cheche365.cheche.picc.flow.step.v2.CalculateCI
import com.cheche365.cheche.picc.flow.step.v2.CalculateForRenewal
import com.cheche365.cheche.picc.flow.step.v2.CheckReuse
import com.cheche365.cheche.picc.flow.step.v2.FindCarModel01
import com.cheche365.cheche.picc.flow.step.v2.FindCarModel03
import com.cheche365.cheche.picc.flow.step.v2.FindCarModel04
import com.cheche365.cheche.picc.flow.step.v2.FindCarModelJYQuery
import com.cheche365.cheche.picc.flow.step.v2.GetCarModelsByPlatFormModelCode
import com.cheche365.cheche.picc.flow.step.v2.InitKindInfo
import com.cheche365.cheche.picc.flow.step.v2.NormalProposal
import com.cheche365.cheche.picc.flow.step.v2.PreForCalBI
import com.cheche365.cheche.picc.flow.step.v2.ToInsurance
import com.cheche365.cheche.picc.flow.step.v2.CalculateForBatch as CalculateForBatch_v2
import com.cheche365.cheche.picc.flow.step.v2.GetRenewalInfo as GetRenewalInfo_v2
import com.cheche365.cheche.picc.flow.step.v2.GetCaptcha as GetCaptcha_v2
import com.cheche365.cheche.picc.flow.step.v2.VerifyCaptcha as VerifyCaptcha_v2



/**
 * 所有流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        // common 中的
        同前        : Identity,
        无          : NoOp,
        非预期结束   : UnexpectedEnd,
        // parser 中的
        识别新验证码 : Decaptcha,
        检查险种清单 : CheckInsurancesCheckList,
        检查补充信息 : CheckSupplementInfo,
        报价前处理器     : QuotePreProcessor,
        报价后处理器     : QuotePostProcessor,
        检查续保通道流程 : CheckRenewalFlow,
        抽取Auto信息      : ExtractAutoInfo,
        更新Auto信息      : UpdateAutoInfo,
        抽取续保车辆信息 : ExtractVehicleInfo,
        抽取车型列表 : ExtractVehicleModels,
        抽取保险基本信息  : ExtractInsuranceBasicInfo,
        抽取保险信息  : ExtractInsuranceInfo,
        处理行驶证阶段性结果: ProcessVehicleLicenseStagedResult,
        更新补充信息 : UpdateSupplementInfo,
        // flow step 中的
        获取流程唯一标识 : GetUniqueId,
        检查车辆黑名单旧接口 : CarBlackList,
        检查车辆黑名单新接口 : CarBlackListCar,
        检查人员黑名单 : InsuredBlackList,
        注册UniqueID到车型 : RegisterUniqueId,
        注册UniqueID到流程 : CheckProfit,
        查找车型 : FindCarModel,
        查找车型上海04 : FindCarModelShanghai04,
        获取车型价格 : CheckPriceForCar,
        获取再保险周期 : GetCheckReinsurance,
        获取保险周期 : GetCheckPeriod,
        检查是否续保 : CheckRenewal,
        获取验证码 : GetCaptcha,
        校验验证码 : VerifyCaptcha,
        获取新验证码 : GetNewCaptcha,
        校验新验证码 : VerifyNewCaptcha,
        历史车辆校验验证码 : HistoryCarVerifyCaptcha,
        获取车辆使用年数 : GetUseYears,
        获取车辆使用年数新接口 : GetNewUseYears,
        获取续保信息 : GetRenewalInfo,
        获取价格 : AdjustPurchasePrice,
        获取风险数据 : GetERiskData,
        计算非续保全险报价 : CalculateForBatch,
        计算快速续保全险报价 : CalculateForQuickRenewal,
        计算续保全险报价 : CalculateForXuBao,
        计算商业险 : CalculateForChangeKind,
        计算交强险 : CalculateForBZ,
        检查保险周期 : CheckPeriod,
        检查承保政策 : CheckPolicy,
        检查交强险 : CheckBZ,
        检查再保险周期 : CheckReinsurancePeriod,
        计算交强险补充并纳税方式 : CalculateForBZTaxTypeB,
        计算交强险仅纳税方式 : CalculateForBZTaxTypeN,
        根据品牌型号查找车型02 : FindCarModelByBrandName,
        根据品牌型号查找车型03 : FindCarModelByBrandName03,
        检查是否快速续保全险报价 : CheckQuickRenewal,
        检查险别 : CheckInsuranceItemSingle,
        根据品牌型号查找精友车型2: FindVehicleBrandModel,

        // V2流程步骤
        获取流程唯一标识V2    : NormalProposal,
        重新获取北京流程信息   : ReinsuranceBJProposal,
        商业险报价前校验V2    : PreForCalBI,
        初始化险种信息V2     : InitKindInfo,
        校验验证码V2       : VerifyCaptcha_v2,
        查找精友车型V2      : FindCarModelJYQuery,
        查找精友车型V2_01   : FindCarModelJYQuery01,
        查找车型01V2       : FindCarModel01,
        根据品牌型号查找车型02V2: FindCarModel02,
        根据品牌型号查找车型02V2_01 : FindCarModel2List,
        根据品牌型号查找车型03V2: FindCarModel03,
        根据品牌型号查找车型04V2: FindCarModel04,
        根据车架号查找车型     : FindCarModelByVin,
        根据车架号人保查车: FindCarModelByVin01,
        根据平台品牌型号查找车型V2: GetCarModelsByPlatFormModelCode,
        历史车辆校验验证码V2   : CheckReuse,
        获取验证码V2       : GetCaptcha_v2,
        计算交强险V2       : CalculateCI,
        获取续保信息V2      : GetRenewalInfo_v2,
        商业险套餐检查V2     : ToInsurance,
        计算非续保全险报价V2   : CalculateForBatch_v2,
        计算自定义商业险报价V2  : CalculateBIForChangeItemKind,
        计算续保全险报价V2    : CalculateForRenewal,
            加载保费试算信息      : LoadCalculateInfo,
            校验转保验证码        : VerificationForInsureQuery,

        保存保单信息 : SaveProposal,
        取消安全支付暨获取订单号 : CancelSafeDefray,
        强制走转保 :BackToNormalQuote,
    ]

    //<editor-fold defaultstate="collapsed" desc="流程片段">
    private static get_SNIPPET_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    /**
     * 检查黑名单旧接口片段
     */
    private static final _CHECK_BLACKLIST_OLD_SNIPPET = _SNIPPET_BUILDER {
        检查车辆黑名单旧接口 >> 检查人员黑名单
    }

    /**
     * 获取识别并校验新验证码片段
     */
    private static final _GET_RECOGNIZE_AND_VERIFY_NEW_CAPTCHA_SNIPPET = _SNIPPET_BUILDER {
        loop({
            获取新验证码 >> 识别新验证码 >> 校验新验证码
        }, 10)
    }

    /**
     * 计算交强险和商业险片段
     */
    private static final _COMP_AND_COMM_SNIPPET = _SNIPPET_BUILDER {
        计算交强险 >> 计算商业险
    }

    /**
     * 计算商业险和交强险片段
     */
    private static final _COMM_AND_COMP_SNIPPET = _SNIPPET_BUILDER {
        计算商业险 >> 计算交强险
    }

    /**
     * 计算含税交强险及商业险报价片段
     */
    private static final _GET_RENEWAL_COMP_QUOTES_WITH_TAX_SNIPPET = _SNIPPET_BUILDER {
        计算商业险 >> 计算交强险补充并纳税方式 >> [
            (true) : { 计算交强险仅纳税方式 }
        ]
    }

    /**
     * 获取校验历史车辆验证码片段
     */
    private static final _GET_VERIFY_HISTORY_CAR_CAPTCHA_SNIPPET = _SNIPPET_BUILDER {
        loop {
            获取验证码 >> 历史车辆校验验证码
        }
    }

    /**
     * 获取校验验证码片段
     */
    private static final _GET_VERIFY_CAPTCHA_SNIPPET = _SNIPPET_BUILDER {
        loop {
            获取验证码 >> 校验验证码
        }
    }

    /**
     * 获取上海车型片段
     */
    private static final _GET_AUTO_TYPE_SNIPPET_310000 = _SNIPPET_BUILDER {
        查找车型上海04 >> [
            (false): {
                loop({ 查找车型 } ,10)
            }
        ]
    }

    /**
     * 获取北京车型片段
     */
    private static final _GET_AUTO_TYPE_SNIPPET_110000 = _SNIPPET_BUILDER {
        loop { 查找车型 }
    }

    /**
     * 获取深圳车型片段
     */
    private static final _GET_AUTO_TYPE_SNIPPET_440300 = _SNIPPET_BUILDER {
        根据品牌型号查找精友车型2 >> 根据品牌型号查找车型03
    }

    /**
     * 获取广州车型片段
     */
    private static final _GET_AUTO_TYPE_SNIPPET_440100 = _SNIPPET_BUILDER {
        loop { 查找车型 } >> [(false): { 根据品牌型号查找精友车型2 }]
    }

    /**
     * 获取保险和再保险周期片段
     */
    private static final _GET_SNIPPET_V1 = _SNIPPET_BUILDER {
        获取保险周期 >> 获取再保险周期
    }

    /**
     * 抽取续保车辆信息并发送阶段性数据
     */
    private static final _GET_CAR_AND_VEHICLE_INFO_SNIPPET = _SNIPPET_BUILDER {
        抽取续保车辆信息 >> 处理行驶证阶段性结果
    }

    /**
     * 核保片段(功能相关的片段)
     */
    private static final _UNDERWRITING_SNIPPET_DEFAULT = _SNIPPET_BUILDER {
        检查险别 >> 检查交强险 >> 检查保险周期 >> 检查再保险周期 >> 检查险种清单 >> 检查承保政策
    }

    /**
     * 承保片段
     */
    private static final _ORDERING_SNIPPET_DEFAULT = _SNIPPET_BUILDER {
        保存保单信息 >> 取消安全支付暨获取订单号
    }

    /**
     * 获取续保使用年数及车价片段(功能相关的片段)
     * 南京、浙江、广州、四川等地的续保商业险险报价，和南京查询车辆信息的流程包含该片段
     */
    private static final _GET_CAR_YEAR_AND_PRICE_SNIPPET = _SNIPPET_BUILDER {
        获取车型价格 >> 获取保险周期 >> 获取再保险周期 >> 获取车辆使用年数新接口
    }

    /**
     * 非续保商业险联合片段(功能相关的片段)
     * 浙广川深非续保商业险报价模板、南京非续保商业险报价模板等均包含该片段
     * 查找车型后注册车型和获取车型价格、获取起保时间（时间变后重新获取车型价格、注册车型）
     */
    private static final _NOT_RENEWAL_COMM_UNION_SNIPPET = _SNIPPET_BUILDER {
        更新补充信息 >> 注册UniqueID到车型 >> 获取车辆使用年数新接口 >> 获取车型价格 >> [
            (true) : {
                loop { 查找车型 } >> 获取车型价格
            }
        ] >> 获取保险周期 >> 获取再保险周期 >> [
            (true) : {
                获取车辆使用年数新接口 >> 获取车型价格
            }
        ] >> 注册UniqueID到流程
    }

    //郑州流程V2片段

    /**
     * 获取校验验证码片段V2
     */
    private static final _GET_VERIFY_CAPTCHA_SNIPPET_V2 = _SNIPPET_BUILDER {
        loop {
            获取验证码V2 >> 校验验证码V2
        }
    }

    /**
     * 获取历史校验验证码片段V2
     */
    private static final _GET_HISTORY_VERIFY_CAPTCHA_SNIPPET_V2 = _SNIPPET_BUILDER {
        loop {
            获取验证码V2 >> 历史车辆校验验证码V2
        }
    }

    /**
     * V2其他地区非续保车辆根据品牌型号查找车型片段V2
     */
    private static final _FIND_CAR_MODEL_SNIPPET_V2 = _SNIPPET_BUILDER {
        查找精友车型V2 >> 根据品牌型号查找车型03V2
    }

    /**
     * 上海V2非续保车辆根据品牌型号查找车型片段V2
     */
    private static final _FIND_CAR_MODEL_SNIPPET_310000_V2 = _SNIPPET_BUILDER {
        根据品牌型号查找车型02V2 >> 根据品牌型号查找车型03V2
    }

    /**
     * 商业险报价前校验片段V2
     */
    private static final _CHECK_CAR_MODEL_SNIPPET_V2 = _SNIPPET_BUILDER {
        loop {
            商业险报价前校验V2 >> [
                    (2): { 根据平台品牌型号查找车型V2 >> 根据品牌型号查找车型04V2 },
                    (4): {
                        loop({
                            识别新验证码 >> 校验转保验证码
                        }, 10) >> [
                                (false): {
                                    根据平台品牌型号查找车型V2 >> 根据品牌型号查找车型04V2
                                }
                        ]
                    }
            ]
        } >> 加载保费试算信息

    }

    /**
     * 核保片段(功能相关的片段)
     */
    private static final _UNDERWRITING_SNIPPET_V2 = _SNIPPET_BUILDER {
        检查险种清单
    }

    /**
     * 商业险报价及套餐检查片段V2
     */
    private static final _CALCULATE_BI_AND_TO_INSURANCE_SNIPPET_V2 = _SNIPPET_BUILDER {
        loop (
                { 计算交强险V2 >> [
                        (true) : { 计算交强险V2 }
                ] >> 计算自定义商业险报价V2 >> 商业险套餐检查V2
                }, 10)
    }

    /**
     * 初始化商业险险种信息及计算套餐片段V2
     */
    private static final _INIT_CAL_INFO_AND_GET_INSURANCE_PACKAGE_SNIPPET_V2 = _SNIPPET_BUILDER {
        初始化险种信息V2 >> [
            (true): { // 续保
                计算续保全险报价V2
            },
            (false): { // 非续保
                更新补充信息 >> 计算非续保全险报价V2
            }
        ]
    }


    private static final _NAME_FLOW_MAPPINGS = [
        检查黑名单旧接口片段 : _CHECK_BLACKLIST_OLD_SNIPPET,
        获取识别并校验新验证码 :_GET_RECOGNIZE_AND_VERIFY_NEW_CAPTCHA_SNIPPET,
        获取校验历史车辆验证码 :_GET_VERIFY_HISTORY_CAR_CAPTCHA_SNIPPET,
        获取校验验证码 : _GET_VERIFY_CAPTCHA_SNIPPET,
        获取上海车型 : _GET_AUTO_TYPE_SNIPPET_310000,
        获取北京车型 : _GET_AUTO_TYPE_SNIPPET_110000,
        获取深圳车型 : _GET_AUTO_TYPE_SNIPPET_440300,
        获取广州车型 : _GET_AUTO_TYPE_SNIPPET_440100,
        计算含税交强险及商业险报价 : _GET_RENEWAL_COMP_QUOTES_WITH_TAX_SNIPPET,
        计算交强险和商业险 : _COMP_AND_COMM_SNIPPET,
        计算商业险和交强险 : _COMM_AND_COMP_SNIPPET,
        获取保险和再保险周期 : _GET_SNIPPET_V1,
        // 功能相关的片段
        获取续保使用年数及车价片段 : _GET_CAR_YEAR_AND_PRICE_SNIPPET,
        核保片段 : _UNDERWRITING_SNIPPET_DEFAULT,
        承保片段 : _ORDERING_SNIPPET_DEFAULT,
        非续保商业险联合片段 : _NOT_RENEWAL_COMM_UNION_SNIPPET,
        抽取续保车辆信息并发送阶段性数据 : _GET_CAR_AND_VEHICLE_INFO_SNIPPET,

        // 郑州V2流程的片段
        获取校验验证码V2            : _GET_VERIFY_CAPTCHA_SNIPPET_V2,
        获取历史校验验证码V2          : _GET_HISTORY_VERIFY_CAPTCHA_SNIPPET_V2,
        非续保车辆根据品牌型号查找车型上海片段V2: _FIND_CAR_MODEL_SNIPPET_310000_V2,
        非续保车辆根据品牌型号查找车型片段V2  : _FIND_CAR_MODEL_SNIPPET_V2,
        商业险报价前校验片段V2         : _CHECK_CAR_MODEL_SNIPPET_V2,
        商业险报价及套餐检查片段V2       : _CALCULATE_BI_AND_TO_INSURANCE_SNIPPET_V2,
        初始化商业险险种信息及计算套餐片段V2  : _INIT_CAL_INFO_AND_GET_INSURANCE_PACKAGE_SNIPPET_V2
    ]

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="流程模板">

    /**
     * 条件判断模板
     */
    private static final _COMMON_CONDITIONS_TEMPLATE = {
        fork([
            (false): { PH1 },
            (true) : { PH2 },
        ])
    }

    /**
     * 报价模板
     */
    private static final  _QUOTING_TEMPLATE = {
        获取流程唯一标识 >> PH1 >> 报价前处理器 >> 检查是否续保 >> PH2 >> 更新补充信息 >> PH3 >> 报价后处理器
    }

    /**
     * 京沪非续保商业险报价模板
     * PH1为车型查询，北京：获取北京车型， 上海：获取上海车型
     */
    private static final _NOT_RENEWAL_COMM_QUOTING_TEMPLATE_JH = {
        检查续保通道流程 >> 获取校验历史车辆验证码 >> 检查补充信息 >> PH1 >> 注册UniqueID到车型 >>
            获取车辆使用年数 >> 获取车型价格 >> 注册UniqueID到流程 >> 获取保险和再保险周期 >> [
            (true) : { 获取车辆使用年数 }
        ] >> 计算非续保全险报价
    }

    /**
     * 南京非续保商业险报价模板
     */
    private static final _NOT_RENEWAL_COMM_QUOTING_TEMPLATE_320100 = {
        检查续保通道流程 >> 获取识别并校验新验证码 >> 检查补充信息 >> 根据品牌型号查找精友车型2 >> 非续保商业险联合片段 >> 计算非续保全险报价
    }

    /**
     * 浙广川深非续保商业险报价模板（浙江、广州、四川、深圳）
     * 浙江、广州、四川、深圳等地区非续保商业险报价，只有查车方式不同， 中科软：根据品牌型号查找车型02；浙江、广州、四川、深圳等地：根据品牌型号查找精友车型2
     */
    private static final _NOT_RENEWAL_COMM_QUOTING_TEMPLATE_TYPE4 = {
        检查续保通道流程 >> 获取校验历史车辆验证码 >> 检查补充信息 >> PH1 >> 非续保商业险联合片段 >> 计算非续保全险报价
    }

    /**
     * 京沪续保商业险报价模板
     * PH1为车型查询，北京：获取北京车型， 上海：获取上海车型
     */
    private static final _RENEWAL_COMM_QUOTING_TEMPLATE_JH = {
        获取校验验证码 >> 获取续保信息 >> 计算快速续保全险报价 >> PH1 >> 获取车辆使用年数 >>
                注册UniqueID到车型 >> 获取车型价格 >> 注册UniqueID到流程 >> 获取保险和再保险周期 >> 检查是否快速续保全险报价 >> [
                (false): { 计算续保全险报价 }
        ]
    }

    /**
     * 南京续保商业险报价模板
     * 南京续保增加获取价格后拿到车型的分支流程.如果别的城市也是这样,应该把这个片段改了
     */
    private static final _RENEWAL_COMM_QUOTING_TEMPLATE_TYPE1 = {
        获取校验验证码 >> 获取续保信息 >> 获取车辆使用年数新接口 >> 计算快速续保全险报价 >> [
            (false) : {
                获取识别并校验新验证码 >> 获取北京车型 >> 注册UniqueID到车型 >> 获取车型价格 >> [
                    (true) : { 获取北京车型 >> 获取车型价格 }
                ] >> 获取保险周期 >> 获取再保险周期 >> 获取车辆使用年数新接口 >> 注册UniqueID到流程 >> 计算续保全险报价
            },
            (true)  : { 获取续保使用年数及车价片段 }
        ]
    }

    /**
     * 浙广川续保商业险报价模板（浙江、广州、四川）
     */
    private static final _RENEWAL_COMM_QUOTING_TEMPLATE_TYPE3 = {
        获取校验验证码 >> 获取续保信息 >> 获取车辆使用年数新接口 >> 计算快速续保全险报价 >> [
            (false) : {
                获取北京车型 >> 获取车型价格 >> [
                    (true) : {
                        根据品牌型号查找精友车型2 >> 注册UniqueID到车型 >> 获取车型价格
                    }
                ] >> 获取保险和再保险周期 >> 获取车辆使用年数新接口 >> 注册UniqueID到流程 >> 计算续保全险报价
            },
            (true)  : { 获取续保使用年数及车价片段 }
        ]
    }

    /**
     * 深圳续保商业险报价模板
     */
    private static final _RENEWAL_COMM_QUOTING_TEMPLATE_440300 = {
        获取校验验证码 >> 获取续保信息 >> 获取车辆使用年数新接口 >> 计算快速续保全险报价 >> [
                (false): {
                    获取北京车型 >> 注册UniqueID到车型 >> 获取续保使用年数及车价片段 >> 注册UniqueID到流程 >> 计算续保全险报价
                },
                (true) : { 获取续保使用年数及车价片段 }
        ]
    }


    /**
     * 获取车型模板(北京、上海、宁波)
     * PH1为车型查询，北京：获取北京车型；上海：获取上海车型；宁波：根据品牌型号查找精友车型2
     */
    private static final _GET_AUTO_TYPE_TEMPLATE = {
        获取流程唯一标识 >> PH1 >> 抽取车型列表
    }

    /**
     * 获取车型V3模板(深圳)
     */
    private static final _GET_AUTO_TYPE_TEMPLATE_V3 = {
        获取流程唯一标识 >> PH1 >> 检查是否续保 >> PH2 >> PH3
    }

    /**
     * 南京续保获取车型模板
     */
    private static final _RENEWAL_GET_AUTO_TYPE_TEMPLATE_320100 = {
        获取校验验证码 >> 获取续保信息 >> 获取车辆使用年数新接口 >> 计算快速续保全险报价 >> [
                (false): {
                    获取识别并校验新验证码 >> 获取北京车型
                },
                (true) : { 获取续保使用年数及车价片段 },
        ]
    }

    /**
     * 苏深非续保获取车型模板
     * PH1:苏州：根据品牌型号查找车型02；深圳：获取深圳车型
     */
    private static final _NOT_RENEWAL_GET_AUTO_TYPE_TEMPLATE_TYPE2 = {
        检查续保通道流程 >> 获取校验历史车辆验证码 >> 检查补充信息 >>  PH1
    }

    /**
     * 南京非续保获取车型模板
     */
    private static final _NOT_RENEWAL_GET_AUTO_TYPE_TEMPLATE_320100 = {
        检查续保通道流程 >> 获取识别并校验新验证码 >> 检查补充信息 >> 根据品牌型号查找车型02
    }

    /**
     * 苏深续保获取车型模板
     */
    private static final _RENEWAL_GET_AUTO_TYPE_TEMPLATE_TYPE2 = {
        获取校验验证码 >> 获取续保信息 >> 获取车辆使用年数新接口 >> 计算快速续保全险报价 >> [
                (false): { 获取北京车型 },
                (true) : { 获取续保使用年数及车价片段 },
        ]
    }

    /**
     * 获取车辆信息模板
     */
    private static final _GET_VEHICLE_LICENSE_TEMPLATE = {
        获取流程唯一标识 >> 检查是否续保 >> [
                (false): { 获取校验历史车辆验证码 },
                (true) : { 获取校验验证码 >> 获取续保信息 },
        ] >> 抽取续保车辆信息
    }

    /**
     * 获取保险信息模板
     */
    private static final _INSURANCE_INFO_TEMPLATE = {
        获取流程唯一标识 >> 检查是否续保 >> [
                (true) : { PH1 },
                (false): { 获取校验历史车辆验证码 >> 抽取续保车辆信息并发送阶段性数据 }
        ] >> 抽取保险基本信息
    }

    /**
     * 京沪续保保险信息模板
     * PH1为车型查询，北京：获取北京车型， 上海：获取上海车型
     */
    private static final _RENEWAL_INSURANCE_INFO_TEMPLATE_JH = {
        获取校验验证码 >> 获取续保信息 >> 计算快速续保全险报价 >> [
            (false) : {
                PH1 >> 抽取续保车辆信息并发送阶段性数据 >> 获取车辆使用年数 >> 注册UniqueID到车型 >> 获取车型价格 >> 注册UniqueID到流程 >> 获取保险和再保险周期 >> 计算续保全险报价
            }
        ] >> 抽取续保车辆信息并发送阶段性数据
    }

    /**
     * 南京续保保险信息模板
     */
    private static final _RENEWAL_INSURANCE_INFO_TEMPLATE_320100 = {
        获取校验验证码 >> 获取续保信息 >> 获取车辆使用年数新接口 >> 计算快速续保全险报价 >> [
            (false) : {
                获取识别并校验新验证码 >> 获取北京车型 >> 注册UniqueID到车型 >> 获取车型价格 >> [
                    (true) : { 获取北京车型 >> 抽取续保车辆信息并发送阶段性数据 >> 获取车型价格 },
                    (false) : { 抽取续保车辆信息并发送阶段性数据 }
                ] >> 获取保险和再保险周期 >> 获取车辆使用年数新接口 >> 注册UniqueID到流程 >> 计算续保全险报价
            },
            (true)  : { 抽取续保车辆信息并发送阶段性数据 }
        ]
    }

    /**
     * 浙广川续保保险信息模板（浙江、广州、四川）
     */
    private static final _RENEWAL_INSURANCE_INFO_TEMPLATE_TYPE3 = {
        获取校验验证码 >> 获取续保信息 >> 获取车辆使用年数新接口 >> 计算快速续保全险报价 >> [
                (false): {
                    获取北京车型 >> 获取车型价格 >> [
                            (true) : {
                                根据品牌型号查找精友车型2 >> 抽取续保车辆信息并发送阶段性数据 >> 注册UniqueID到车型 >> 获取车型价格
                            },
                            (false): { 抽取续保车辆信息并发送阶段性数据 }
                    ] >> 获取保险和再保险周期 >> 获取车辆使用年数新接口 >> 注册UniqueID到流程 >> 计算续保全险报价
                },
                (true) : { 抽取续保车辆信息并发送阶段性数据 }
        ]
    }

    /**
     * 深圳续保保险信息模板
     */
    private static final _RENEWAL_INSURANCE_INFO_TEMPLATE_440300 = {
        获取校验验证码 >> 获取续保信息 >> 获取车辆使用年数新接口 >> 计算快速续保全险报价 >> [
                (false): {
                    获取北京车型 >> 抽取续保车辆信息并发送阶段性数据 >> 注册UniqueID到车型 >> 获取续保使用年数及车价片段 >> 注册UniqueID到流程 >> 计算续保全险报价
                },
                (true) : { 抽取续保车辆信息并发送阶段性数据 }
        ]
    }

    private static final _NAME_TEMPLATE_MAPPINGS = [
        条件判断模板 : _COMMON_CONDITIONS_TEMPLATE,
        报价模板 : _QUOTING_TEMPLATE,
        京沪非续保商业险报价模板 : _NOT_RENEWAL_COMM_QUOTING_TEMPLATE_JH,
        京沪续保商业险报价模板 : _RENEWAL_COMM_QUOTING_TEMPLATE_JH,
        南京非续保商业险报价模板 : _NOT_RENEWAL_COMM_QUOTING_TEMPLATE_320100,
        南京续保商业险报价模板 : _RENEWAL_COMM_QUOTING_TEMPLATE_TYPE1,
        深圳续保商业险报价模板 : _RENEWAL_COMM_QUOTING_TEMPLATE_440300,
        浙广川深非续保商业险报价模板 : _NOT_RENEWAL_COMM_QUOTING_TEMPLATE_TYPE4,
        浙广川续保商业险报价模板 : _RENEWAL_COMM_QUOTING_TEMPLATE_TYPE3,
        获取车型模板 : _GET_AUTO_TYPE_TEMPLATE,
        获取车型V3模板 : _GET_AUTO_TYPE_TEMPLATE_V3,
        南京非续保获取车型模板 : _NOT_RENEWAL_GET_AUTO_TYPE_TEMPLATE_320100,
        南京续保获取车型模板 : _RENEWAL_GET_AUTO_TYPE_TEMPLATE_320100,
        苏深非续保获取车型模板 : _NOT_RENEWAL_GET_AUTO_TYPE_TEMPLATE_TYPE2,
        苏深续保获取车型模板 : _RENEWAL_GET_AUTO_TYPE_TEMPLATE_TYPE2,
        获取车辆信息模板 : _GET_VEHICLE_LICENSE_TEMPLATE,
        获取保险信息模板: _INSURANCE_INFO_TEMPLATE,
        京沪续保保险信息模板 : _RENEWAL_INSURANCE_INFO_TEMPLATE_JH,
        南京续保保险信息模板 : _RENEWAL_INSURANCE_INFO_TEMPLATE_320100,
        浙广川续保保险信息模板 : _RENEWAL_INSURANCE_INFO_TEMPLATE_TYPE3,
        深圳续保保险信息模板 : _RENEWAL_INSURANCE_INFO_TEMPLATE_440300
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

    /**
     * 上面定义的步骤并非都需要被使用，以下是人保北京续保客户的常规流程：
     *      获取流程唯一标识 >> 检查是否续保 >> 校验验证码 >> 获取续保信息 >> 计算快速续保全险报价 >> 查找车型 >> 获取车辆使用年数 >>
     *      注册UniqueID到车型 >> 检查车辆黑名单新接口 >> 检查车辆黑名单旧接口 >> 获取风险数据 >> 获取车辆使用年数 >> 注册UniqueID到流程 >>
     *      获取出险次数 >> 计算续保全险报价 >> 计算商业险 >> 计算交强险
     * 可以看出上述步骤有重复和多余的，并且对于那些无状态的API可以重排顺序。
     * 以下就是重排后的人保流程。
     */
    /*****************************************************************************************/

    // 南京
    static final _QUOTING_FLOW_320100 = _FLOW_BUILDER {
        make '报价模板',
        [
            PH1: '检查黑名单旧接口片段',
            PH2:[
                '条件判断模板',
                [
                    PH1: [
                        '南京非续保商业险报价模板', [:]
                    ],
                    PH2: [
                        '南京续保商业险报价模板', [:]
                    ]
                ]
            ],
            PH3: '计算商业险和交强险'
        ]
    }

    // 深圳等使用中科软车型库的
    static final _QUOTING_FLOW_440300 = _FLOW_BUILDER {
        make '报价模板',
                [
                        PH1: '检查车辆黑名单新接口',
                        PH2: [
                                '条件判断模板',
                                [
                                        PH1: [
                                                '浙广川深非续保商业险报价模板', [
                                                PH1: '获取深圳车型'
                                        ]
                                        ],
                                        PH2: [
                                                '深圳续保商业险报价模板', [:]
                                        ]
                                ]
                        ],
                        PH3: '计算商业险和交强险'
                ]
    }

    // 浙江、广州、四川等使用精友车型库的
    static final _QUOTING_FLOW_TYPE3 = _FLOW_BUILDER {
        make '报价模板', [
                PH1: '检查车辆黑名单新接口',
                PH2: [
                        '条件判断模板', [
                        PH1: [
                                '浙广川深非续保商业险报价模板', [
                                PH1: '根据品牌型号查找精友车型2'
                        ]
                        ],
                        PH2: [
                                '浙广川续保商业险报价模板', [:]
                        ]
                ]
                ],
                PH3: '计算交强险和商业险'
        ]
    }

    /**
     * 查车流程（根据bihu给的车牌号 姓名，查出六要素，根据其六要素去查车）片段 北京地区及其他地区
     */
    static final _QUOTING_VEHICLE_DEFAULT = _FLOW_BUILDER {

        获取流程唯一标识V2 >>  强制走转保 >> 检查续保通道流程 >>  查找精友车型V2_01 >> [
            车架号查车:{根据车架号人保查车}
        ]
    }

    /**
     * 查车流程（根据bihu给的车牌号 姓名，查出六要素，根据其六要素去查车）片段 上海地区
     */

    static final _CHECK_VEHICLE_SHANGHAI = _FLOW_BUILDER {

         获取流程唯一标识V2 >>  强制走转保 >> 检查续保通道流程 >> 根据品牌型号查找车型02V2_01

    }


    // 北京V2
    static final _QUOTING_FLOW_110000 = _FLOW_BUILDER {
        报价前处理器 >> 检查补充信息 >> 获取流程唯一标识V2 >> [
                (1): { // 转保
                    检查续保通道流程 >> 非续保车辆根据品牌型号查找车型片段V2
                },
                (2): { // 续保
                    强制走转保 >> 检查续保通道流程 >> 非续保车辆根据品牌型号查找车型片段V2
                },
                (3): { // 历史客户
                    强制走转保 >> 检查续保通道流程 >> 非续保车辆根据品牌型号查找车型片段V2
                }
        ] >> 重新获取北京流程信息 >> 商业险报价前校验片段V2 >> 更新补充信息 >> 抽取Auto信息 >> 初始化商业险险种信息及计算套餐片段V2 >> 商业险报价及套餐检查片段V2 >> [
                (true): {
                    商业险报价前校验V2 >> 初始化商业险险种信息及计算套餐片段V2 >> 商业险报价及套餐检查片段V2
                }
        ] >> 更新Auto信息 >> 报价后处理器
    }

    // 上海
    static final _QUOTING_FLOW_310000 = _FLOW_BUILDER {
        报价前处理器 >> 检查补充信息 >> 获取流程唯一标识V2 >> [
                (1): { // 转保
                    检查续保通道流程 >> 非续保车辆根据品牌型号查找车型上海片段V2
                },
                (2): { // 续保
                    强制走转保 >> 非续保车辆根据品牌型号查找车型上海片段V2
                },
                (3): { // 历史客户
                    检查续保通道流程 >> 强制走转保 >> 非续保车辆根据品牌型号查找车型上海片段V2
                }
        ] >> 商业险报价前校验片段V2 >> 更新补充信息 >> 抽取Auto信息 >> 初始化商业险险种信息及计算套餐片段V2 >> 商业险报价及套餐检查片段V2 >> [
                (true): {
                    商业险报价前校验V2 >> 初始化商业险险种信息及计算套餐片段V2 >> 商业险报价及套餐检查片段V2
                }
        ] >> 更新Auto信息 >> 报价后处理器
    }

    // 郑州 通用
    static final _QUOTING_FLOW_410100 = _FLOW_BUILDER {
        报价前处理器 >> 检查补充信息 >> 获取流程唯一标识V2 >> [
                (1): { // 转保
                    检查续保通道流程 >> 非续保车辆根据品牌型号查找车型片段V2
                },
                (2): { // 续保
                    检查续保通道流程 >> 强制走转保 >> 非续保车辆根据品牌型号查找车型片段V2
                },
                (3): { // 历史客户
                    检查续保通道流程 >> 强制走转保 >> 非续保车辆根据品牌型号查找车型片段V2
                }
        ] >> 商业险报价前校验片段V2 >> 更新补充信息 >> 抽取Auto信息  >> 初始化商业险险种信息及计算套餐片段V2 >> 商业险报价及套餐检查片段V2 >> [
                (true): {
                    商业险报价前校验片段V2 >> 初始化商业险险种信息及计算套餐片段V2 >> 商业险报价及套餐检查片段V2
                }
        ] >> 更新Auto信息 >> 报价后处理器
    }

    //    </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Insuring Flows">

    static final _INSURING_FLOW_TYPE3 = new FlowChain(flows: [_QUOTING_FLOW_TYPE3, _UNDERWRITING_SNIPPET_DEFAULT])
    static final _INSURING_FLOW_110000 = new FlowChain(flows: [_QUOTING_FLOW_110000, _UNDERWRITING_SNIPPET_V2])
    static final _INSURING_FLOW_310000 = new FlowChain(flows: [_QUOTING_FLOW_310000, _UNDERWRITING_SNIPPET_V2])
    static final _INSURING_FLOW_320100 = new FlowChain(flows: [_QUOTING_FLOW_320100, _UNDERWRITING_SNIPPET_DEFAULT])
    static final _INSURING_FLOW_440300 = new FlowChain(flows: [_QUOTING_FLOW_440300, _UNDERWRITING_SNIPPET_DEFAULT])
    static final _INSURING_FLOW_410100 = new FlowChain(flows: [_QUOTING_FLOW_410100, _UNDERWRITING_SNIPPET_V2])

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="承保流程">

    static final _ORDERING_FLOW_TYPE3  = new FlowChain(flows: [_QUOTING_FLOW_TYPE3, _UNDERWRITING_SNIPPET_DEFAULT, _ORDERING_SNIPPET_DEFAULT])
    static final _ORDERING_FLOW_110000 = new FlowChain(flows: [_QUOTING_FLOW_110000, _UNDERWRITING_SNIPPET_V2, _ORDERING_SNIPPET_DEFAULT])
    static final _ORDERING_FLOW_310000 = new FlowChain(flows: [_QUOTING_FLOW_310000, _UNDERWRITING_SNIPPET_DEFAULT, _ORDERING_SNIPPET_DEFAULT])
    static final _ORDERING_FLOW_320100 = new FlowChain(flows: [_QUOTING_FLOW_320100, _UNDERWRITING_SNIPPET_DEFAULT, _ORDERING_SNIPPET_DEFAULT])
    static final _ORDERING_FLOW_440300 = new FlowChain(flows: [_QUOTING_FLOW_440300, _UNDERWRITING_SNIPPET_DEFAULT, _ORDERING_SNIPPET_DEFAULT])

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="保险信息流程">
    // 北京(V2流程)
    static final _INSURANCE_BASIC_INFO_FLOW_110000 = _FLOW_BUILDER {
        报价前处理器 >> 获取流程唯一标识V2 >> [
                (1): { // 转保
                    非预期结束
                },
                (2): { // 续保
                    loop {
                        获取验证码V2 >> 校验验证码V2
                    } >> [
                            (true): {
                                获取续保信息V2 >> 抽取续保车辆信息并发送阶段性数据 >> 商业险报价前校验V2
                            }
                    ]
                },
                (3): { // 历史客户
                    loop {
                        获取验证码V2 >> 历史车辆校验验证码V2
                    } >> 抽取续保车辆信息并发送阶段性数据 >> 商业险报价前校验V2
                }
        ] >> 初始化商业险险种信息及计算套餐片段V2 >> 计算交强险V2 >> 抽取保险基本信息
    }

    // 上海
    static final _INSURANCE_BASIC_INFO_FLOW_310000 = _FLOW_BUILDER {
        make '获取保险信息模板', [
                PH1: [
                        '京沪续保保险信息模板', [
                        PH1: '获取上海车型'
                ]
                ]
        ]
    }

    // 南京
    static final _INSURANCE_BASIC_INFO_FLOW_320100 = _FLOW_BUILDER {
        make '获取保险信息模板', [
                PH1: [
                        '南京续保保险信息模板', [:]
                ]
        ]
    }

    // 深圳等使用中科软车型库的
    static final _INSURANCE_BASIC_INFO_FLOW_440300 = _FLOW_BUILDER {
        make '获取保险信息模板', [
                PH1: [
                        '深圳续保保险信息模板', [:]
                ]
        ]
    }

    // 浙江、广州、四川等使用精友车型库的
    static final _INSURANCE_BASIC_INFO_FLOW_TYPE3 = _FLOW_BUILDER {
        make '获取保险信息模板', [
                PH1: [
                        '浙广川续保保险信息模板', [:]
                ]
        ]
    }

    // 郑州等地V2流程获取保险信息
    static final _INSURANCE_BASIC_INFO_FLOW_V2 = _FLOW_BUILDER {
        获取流程唯一标识V2 >> [
                (1): { // 转保
                    非预期结束
                },
                (2): { // 续保
                    loop {
                        获取验证码V2 >> 校验验证码V2
                    } >> [
                            (true): {
                                获取续保信息V2 >> 抽取续保车辆信息并发送阶段性数据 >> 商业险报价前校验片段V2
                            }
                    ]
                },
                (3): { // 历史客户
                    loop {
                        获取验证码V2 >> 历史车辆校验验证码V2
                    } >> [
                            (false): {
                                非预期结束
                            }
                    ] >> 抽取续保车辆信息并发送阶段性数据 >> 商业险报价前校验片段V2
                }
        ] >> 初始化商业险险种信息及计算套餐片段V2 >> 计算交强险V2 >> 抽取保险基本信息
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="保险套餐信息流程">
    private static final INSURANCE_INFO_EXTRACT_FLOW = _FLOW_BUILDER {
        抽取保险信息
    }

    static final _INSURANCE_INFO_FLOW_110000 = new FlowChain(flows: [_INSURANCE_BASIC_INFO_FLOW_110000, INSURANCE_INFO_EXTRACT_FLOW])
    static final _INSURANCE_INFO_FLOW_310000 = new FlowChain(flows: [_INSURANCE_BASIC_INFO_FLOW_310000, INSURANCE_INFO_EXTRACT_FLOW])
    static final _INSURANCE_INFO_FLOW_320100 = new FlowChain(flows: [_INSURANCE_BASIC_INFO_FLOW_320100, INSURANCE_INFO_EXTRACT_FLOW])
    static final _INSURANCE_INFO_FLOW_440300 = new FlowChain(flows: [_INSURANCE_BASIC_INFO_FLOW_440300, INSURANCE_INFO_EXTRACT_FLOW])
    static final _INSURANCE_INFO_FLOW_TYPE3 = new FlowChain(flows: [_INSURANCE_BASIC_INFO_FLOW_TYPE3, INSURANCE_INFO_EXTRACT_FLOW])
    static final _INSURANCE_INFO_FLOW_V2 = new FlowChain(flows: [_INSURANCE_BASIC_INFO_FLOW_V2, INSURANCE_INFO_EXTRACT_FLOW])

    //</editor-fold>

}
