package com.cheche365.cheche.pinganuk.flow

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.common.flow.step.Identity
import com.cheche365.cheche.common.flow.step.NoOp
import com.cheche365.cheche.common.flow.step.UnexpectedEnd
import com.cheche365.cheche.parser.flow.steps.CheckInsurancesCheckList
import com.cheche365.cheche.parser.flow.steps.CheckRenewalFlow
import com.cheche365.cheche.parser.flow.steps.CheckSupplementInfo
import com.cheche365.cheche.parser.flow.steps.Decaptcha
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceBasicInfo
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceInfo
import com.cheche365.cheche.parser.flow.steps.InsurePostProcessor
import com.cheche365.cheche.parser.flow.steps.ProcessVehicleLicenseStagedResult
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.cheche.parser.flow.steps.UpdateSupplementInfo
import com.cheche365.cheche.pinganuk.flow.step.ApplyPolicy
import com.cheche365.cheche.pinganuk.flow.step.ApplyQueryAndQuote
import com.cheche365.cheche.pinganuk.flow.step.AutoModelCodeQuery
import com.cheche365.cheche.pinganuk.flow.step.CircVehicleInfoQuery
import com.cheche365.cheche.pinganuk.flow.step.DeleteApplied
import com.cheche365.cheche.pinganuk.flow.step.DeleteQuotePolicy
import com.cheche365.cheche.pinganuk.flow.step.GetApplyPolicy
import com.cheche365.cheche.pinganuk.flow.step.GetQuotationNo
import com.cheche365.cheche.pinganuk.flow.step.GetRateFactorList
import com.cheche365.cheche.pinganuk.flow.step.GetTheftAmount
import com.cheche365.cheche.pinganuk.flow.step.Login
import com.cheche365.cheche.pinganuk.flow.step.QuickSearch
import com.cheche365.cheche.pinganuk.flow.step.QuickSearchVoucher
import com.cheche365.cheche.pinganuk.flow.step.QuoteBaseInfo
import com.cheche365.cheche.pinganuk.flow.step.QuoteVehicleTypeInfoQuery
import com.cheche365.cheche.pinganuk.flow.step.RenewalVehicleTypeInfoQuery
import com.cheche365.cheche.pinganuk.flow.step.RepAndDelApplied
import com.cheche365.cheche.pinganuk.flow.step.SystemTransfer
import com.cheche365.cheche.pinganuk.flow.step.v2.CasSuccessLogin
import com.cheche365.cheche.pinganuk.flow.step.v2.CheckInsureStatus
import com.cheche365.cheche.pinganuk.flow.step.v2.GetAuthorization
import com.cheche365.cheche.pinganuk.flow.step.v2.GetChannels
import com.cheche365.cheche.pinganuk.flow.step.v2.GetInsurePage
import com.cheche365.cheche.pinganuk.flow.step.v2.GetLoginCaptcha
import com.cheche365.cheche.pinganuk.flow.step.v2.GetPaymentJumpInfo
import com.cheche365.cheche.pinganuk.flow.step.v2.GetPolicyInfo
import com.cheche365.cheche.pinganuk.flow.step.v2.JumpPaymentPage
import com.cheche365.cheche.pinganuk.flow.step.v2.PickIdentityInfo
import com.cheche365.cheche.pinganuk.flow.step.v2.PickIdentitySendMsg
import com.cheche365.cheche.pinganuk.flow.step.v2.PickInfoWithGshell
import com.cheche365.cheche.pinganuk.flow.step.v2.VerifyIdentityMsg
import com.cheche365.cheche.pinganuk.flow.step.v2.VerifyLoginCaptcha
import com.cheche365.cheche.pinganuk.flow.step.v2.VerifyPayment
import com.cheche365.flow.business.flow.step.ExtractVehicleInfo


/**
 * 平安UK流程
 * <pre>
 * Login >> QuoteBaseInfo >> SystemTransfer >> GetRateFactorList >> 循环(3次, QuickSearch)
 *  >> Identity >> [
 *      true: QuickSearchVoucher >> RenewalVehicleTypeInfoQuery,
 *      false: CheckRenewalFlow >> AutoModelCodeQuery >> CircVehicleInfoQuery >> QuoteVehicleTypeInfoQuery
 *   ]
 *  >> QuotePreProcessor >> GetQuotationNo >> GetInsuredAmount
 *  >> 循环(10次, ApplyQueryAndQuote >> [
 *          true: GetInsuredAmount
 *      ])
 *  >> QuotePostProcessor
 *  </pre>
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        //common里面的
        同前        : Identity,
        无         : NoOp,
        非预期结束     : UnexpectedEnd,

        // parser里面的
        检查续保通道流程  : CheckRenewalFlow,
        报价后处理器    : QuotePostProcessor,
        核保后处理器    : InsurePostProcessor,
        抽取续保车辆信息  : ExtractVehicleInfo,
        抽取保险基本信息  : ExtractInsuranceBasicInfo,
        抽取保险信息    : ExtractInsuranceInfo,
        抽取车辆信息    : ExtractVehicleInfo,
        处理行驶证阶段性结果: ProcessVehicleLicenseStagedResult,
        检查险种清单    : CheckInsurancesCheckList,
        更新补充信息    : UpdateSupplementInfo,
        检查补充信息    : CheckSupplementInfo,

        登录        : Login,
        获取账户基本信息  : QuoteBaseInfo,
        进入报价系统    : SystemTransfer,
        快速检索      : QuickSearch,
        快速检索信息获取  : QuickSearchVoucher,
        获取商业险税率因子 : GetRateFactorList,
        车型校验      : AutoModelCodeQuery,
        车辆查询      : CircVehicleInfoQuery,
        转保车型查询    : QuoteVehicleTypeInfoQuery,
        续保车型查询    : RenewalVehicleTypeInfoQuery,
        获取询价单号    : GetQuotationNo,
        获取折损价格    : GetTheftAmount,
        报价        : ApplyQueryAndQuote,
        核保        : ApplyPolicy,
        查询投保单     : GetApplyPolicy,
        删除询价单号    : DeleteQuotePolicy,
        删除投保单     : DeleteApplied,
        删除待缴费投保单  : RepAndDelApplied,
        //v2流程
        校验登陆验证码   : VerifyLoginCaptcha,
        获取验证码     : GetLoginCaptcha,
        识别登陆验证码   : new Decaptcha('loginCaptchaText'),
        成功登录      : CasSuccessLogin,
        获取投保中心页   : GetInsurePage,
        获取代理授权信息  : GetAuthorization,
        悟空系统信息采集  : PickInfoWithGshell,
        采集身份信息    : PickIdentityInfo,
        采集身份发送验证码 : PickIdentitySendMsg,
        验证采集身份验证码 : VerifyIdentityMsg,
        判断核保状态    : CheckInsureStatus,
        获取保单信息    : GetPolicyInfo,
        支付校验      : VerifyPayment,
        获取跳转支付页面  : GetPaymentJumpInfo,
        跳转支付页获取二维码: JumpPaymentPage,
        获取支付方式    : GetChannels
    ]

    //<editor-fold defaultstate="collapsed" desc="流程片段">

    private static get_SNIPPET_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    /**
     * 登录并且进入报价系统片段
     */
    private static final _LOGIN_AND_QUOTE_ORG_INFO_SNIPPET = _SNIPPET_BUILDER {
        登录 >> 获取账户基本信息 >> 进入报价系统
    }

    /**
     * 登录并且进入报价系统V2片段
     */
    private static final _LOGIN_AND_QUOTE_ORG_INFO_V2_SNIPPET = _SNIPPET_BUILDER {
        loop({ 获取验证码 >> 识别登陆验证码 >> 校验登陆验证码 }, 10) >> 成功登录 >> 获取投保中心页 >> 获取账户基本信息 >> 进入报价系统 >> 获取代理授权信息
    }

    /**
     * 续保获取车型片段
     */
    private static final _RENEWAL_GET_AUTO_TYPE_SNIPPET = _SNIPPET_BUILDER {
        快速检索信息获取 >> 续保车型查询
    }

    /**
     * 转保获取车型片段
     */
    private static final _NOT_RENEWAL_GET_AUTO_TYPE_SNIPPET = _SNIPPET_BUILDER {
        //todo : Be cautions!!!
//        车型校验 >> 车辆查询 >> 转保车型查询
        车型校验 >> 车辆查询
    }

    /**
     * 获取商业险和交强险报价片段
     */
    private static final _GET_COMMERCIAL_AND_COMPULSORY_QUOTES_SNIPPET = _SNIPPET_BUILDER {
        获取询价单号 >> 更新补充信息 >> 获取折损价格 >> loop({
            报价 >> [
                (true): {
                    获取折损价格 >> 删除询价单号
                }
            ]
        }, 10)
    }

    /**
     * 核保片段
     */
    private static final _UNDERWRITING_SNIPPET = _SNIPPET_BUILDER {
        判断核保状态 >> [
            未创建投保单      : {
                核保 >> [
                    代缴费状态: {
                        悟空系统信息采集 >> 采集身份信息 >> 采集身份发送验证码
                    },
                    其他状态 : {
                        删除投保单
                    }
                ]
            },
            核保成功未发送采集验证码: {
                悟空系统信息采集 >> 采集身份信息 >> 采集身份发送验证码
            }
        ] >> 验证采集身份验证码
    }

    /**
     * 获取车型列表片段
     */
    private static final _VEHICLE_LICENSE_SNIPPET = _SNIPPET_BUILDER {
        loop { 快速检索 } >> [
            (true): {
                快速检索信息获取 >> 抽取续保车辆信息
            }
        ]
    }

    private static final _NAME_FLOW_MAPPINGS = [
        登录并且进入报价系统  : _LOGIN_AND_QUOTE_ORG_INFO_SNIPPET,
        续保获取车型      : _RENEWAL_GET_AUTO_TYPE_SNIPPET,
        转保获取车型      : _NOT_RENEWAL_GET_AUTO_TYPE_SNIPPET,
        获取商业险和交强险报价 : _GET_COMMERCIAL_AND_COMPULSORY_QUOTES_SNIPPET,
        获取车型列表      : _VEHICLE_LICENSE_SNIPPET,
        登录并且进入报价系统V2: _LOGIN_AND_QUOTE_ORG_INFO_V2_SNIPPET,
        核保并获取支付链接   : _UNDERWRITING_SNIPPET
    ]

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="流程模板">

    /**
     * 获取车型模版
     */
    private static final _GET_AUTO_TYPE_SNIPPET = {
        fork([
            // 续保
            (true) : {
                PH1
            },
            // 转保
            (false): {
                检查续保通道流程 >> PH2
            }
        ])
    }

    /**
     * 报价模板
     */
    private static final _QUOTING_TEMPLATE = {
        登录并且进入报价系统 >> 获取商业险税率因子 >> loop { 快速检索 } >> PH1 >> 获取商业险和交强险报价 >> PH2 >> 检查险种清单 >> 报价后处理器
    }

    /**
     * 报价模板
     */
    private static final _QUOTING_V2_TEMPLATE = {
        检查补充信息 >> 登录并且进入报价系统V2 >> 获取商业险税率因子 >> loop { 快速检索 } >> PH1 >> 获取商业险和交强险报价 >> PH2 >> 检查险种清单 >> 报价后处理器
    }

    /**
     * 获取车型列表模板
     */
    private static final _VEHICLE_LICENSE_TEMPLATE = {
        登录并且进入报价系统 >> 获取车型列表
    }

    /**
     * 获取续保信息模板
     */
    private static final _INSURANCE_INFO_TEMPLATE = {
        登录并且进入报价系统 >> 获取商业险税率因子 >> loop { 快速检索 } >> PH1 >> 抽取车辆信息 >> 处理行驶证阶段性结果 >> 抽取保险基本信息 >> PH2
    }

    private static final _NAME_TEMPLATE_MAPPINGS = [
        报价模板    : _QUOTING_TEMPLATE,
        获取车型模板  : _GET_AUTO_TYPE_SNIPPET,
        获取车型列表模板: _VEHICLE_LICENSE_TEMPLATE,
        获取续保信息模板: _INSURANCE_INFO_TEMPLATE,
        报价模板V2  : _QUOTING_V2_TEMPLATE
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

    static final _QUOTING_AND_RENEWAL_V2_FLOW = _FLOW_BUILDER {
        make '报价模板V2', [
            PH1: [
                '获取车型模板', [
                PH1: '续保获取车型',
                PH2: '转保获取车型'
            ]
            ],
            PH2: '无'
        ]
    }

    static final _INSURING_V2_FLOW = _FLOW_BUILDER {
        登录并且进入报价系统V2 >> 核保并获取支付链接
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="获取保险信息流程">

    static final _INSURANCE_INFO_FLOW_DEFAULT = _FLOW_BUILDER {
        make '获取续保信息模板', [
            PH1: [
                '获取车型模板', [
                PH1: '快速检索信息获取',
                PH2: '无'
            ]
            ],
            PH2: '抽取保险信息'
        ]
    }

    static final _INSURANCE_BASIC_INFO_FLOW_DEFAULT = _FLOW_BUILDER {
        make '获取续保信息模板', [
            PH1: [
                '获取车型模板', [
                PH1: '快速检索信息获取',
                PH2: '无'
            ]
            ],
            PH2: '无'
        ]
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="获取支付相关信息流程">
    static final _GET_PAYMENT_INFO_V2_FLOW = _FLOW_BUILDER {
        登录并且进入报价系统V2 >> 获取保单信息 >> 支付校验 >> 获取跳转支付页面 >> 跳转支付页获取二维码
    }

    static final _GET_PAYMENT_CHANNELS_FLOW = _FLOW_BUILDER {
        获取支付方式
    }
    //</editor-fold>
}
