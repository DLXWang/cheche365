package com.cheche365.cheche.cpicuk.flow

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.common.flow.step.Identity
import com.cheche365.cheche.common.flow.step.NoOp
import com.cheche365.cheche.common.flow.step.UnexpectedEnd
import com.cheche365.cheche.cpicuk.flow.step.CalculatePremium
import com.cheche365.cheche.cpicuk.flow.step.CheckInsureStatus
import com.cheche365.cheche.cpicuk.flow.step.CheckLoginLimit
import com.cheche365.cheche.cpicuk.flow.step.CheckPaymentState
import com.cheche365.cheche.cpicuk.flow.step.DealCacheQRCode
import com.cheche365.cheche.cpicuk.flow.step.FindVehicleByCodeOther
import com.cheche365.cheche.cpicuk.flow.step.GetCacheQRCode
import com.cheche365.cheche.cpicuk.flow.step.GetChannels
import com.cheche365.cheche.cpicuk.flow.step.GetLoginCaptcha
import com.cheche365.cheche.cpicuk.flow.step.GetPayCodeByInsureNo
import com.cheche365.cheche.cpicuk.flow.step.GetPaymentRecord
import com.cheche365.cheche.cpicuk.flow.step.GetQRCodeByPayNo
import com.cheche365.cheche.cpicuk.flow.step.GetQuoteRecordState
import com.cheche365.cheche.cpicuk.flow.step.IDCardCollect
import com.cheche365.cheche.cpicuk.flow.step.IDCardUpdatePhone
import com.cheche365.cheche.cpicuk.flow.step.InsureUnderInfo
import com.cheche365.cheche.cpicuk.flow.step.Login
import com.cheche365.cheche.cpicuk.flow.step.LoginSuccess
import com.cheche365.cheche.cpicuk.flow.step.MoreBjIdentifyCode
import com.cheche365.cheche.cpicuk.flow.step.NullifyGetPaymentRecord
import com.cheche365.cheche.cpicuk.flow.step.NullifyPaymentOrder
import com.cheche365.cheche.cpicuk.flow.step.PartnerLogin
import com.cheche365.cheche.cpicuk.flow.step.Pay
import com.cheche365.cheche.cpicuk.flow.step.PaymentRecordMan
import com.cheche365.cheche.cpicuk.flow.step.QueryCarModelByVINNO
import com.cheche365.cheche.cpicuk.flow.step.QueryCarModelInfo
import com.cheche365.cheche.cpicuk.flow.step.QueryClauseInfo
import com.cheche365.cheche.cpicuk.flow.step.QueryFastLoginInfo
import com.cheche365.cheche.cpicuk.flow.step.QueryIdentityInformation
import com.cheche365.cheche.cpicuk.flow.step.QueryInsureInfo
import com.cheche365.cheche.cpicuk.flow.step.QueryPayment
import com.cheche365.cheche.cpicuk.flow.step.QueryPureriskAndVehicleInfo
import com.cheche365.cheche.cpicuk.flow.step.QueryQuickOfferByPlate
import com.cheche365.cheche.cpicuk.flow.step.QueryQuotationPolicy
import com.cheche365.cheche.cpicuk.flow.step.QuickSave
import com.cheche365.cheche.cpicuk.flow.step.SaveClauseInfo
import com.cheche365.cheche.cpicuk.flow.step.SaveInsureInfo
import com.cheche365.cheche.cpicuk.flow.step.SelectCarWay
import com.cheche365.cheche.cpicuk.flow.step.SmsConfirm
import com.cheche365.cheche.cpicuk.flow.step.SubmitInsureInfo
import com.cheche365.cheche.cpicuk.flow.step.SubmitInsureInfoAgain
import com.cheche365.cheche.cpicuk.flow.step.UploadImage
import com.cheche365.cheche.cpicuk.flow.step.VehicleQueryConfirm
import com.cheche365.cheche.cpicuk.flow.step.VehicleQueryValidation
import com.cheche365.cheche.cpicuk.flow.step.VerifyLoginCaptcha
import com.cheche365.cheche.parser.flow.steps.CheckInsurancesCheckList
import com.cheche365.cheche.parser.flow.steps.CheckRenewalFlow
import com.cheche365.cheche.parser.flow.steps.CheckSupplementInfo
import com.cheche365.cheche.parser.flow.steps.Decaptcha
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceBasicInfo
import com.cheche365.cheche.parser.flow.steps.ExtractInsuranceInfo
import com.cheche365.cheche.parser.flow.steps.InsurePostProcessor
import com.cheche365.cheche.parser.flow.steps.ProcessVehicleLicenseStagedResult
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.cheche.parser.flow.steps.SelectCarModel
import com.cheche365.cheche.parser.flow.steps.UpdateSupplementInfo
import com.cheche365.flow.business.flow.step.ExtractVehicleInfo



/**
 * 太平洋UK流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        //common里面的
        同前          : Identity,
        无           : NoOp,
        非预期结束       : UnexpectedEnd,

        // parser里面的
        检查登录次数限制    : CheckLoginLimit,
        检查续保通道流程    : CheckRenewalFlow,
        报价后处理器      : QuotePostProcessor,
        核保后处理器      : InsurePostProcessor,
        抽取续保车辆信息    : ExtractVehicleInfo,
        抽取保险基本信息    : ExtractInsuranceBasicInfo,
        抽取保险信息      : ExtractInsuranceInfo,
        抽取车辆信息      : ExtractVehicleInfo,
        处理行驶证阶段性结果  : ProcessVehicleLicenseStagedResult,
        检查险种清单      : CheckInsurancesCheckList,
        更新补充信息      : UpdateSupplementInfo,
        根据品牌型号查车型   : QueryCarModelInfo,
        根据车牌和车架号查车型 : QueryCarModelByVINNO,
        检查补充信息      : CheckSupplementInfo,


        获取登录页面      : Login,
        获取登录验证码     : GetLoginCaptcha,
        识别登陆验证码     : Decaptcha,
        校验登录验证码     : VerifyLoginCaptcha,
        获取快速登录信息    : QueryFastLoginInfo,
        代理人登录       : PartnerLogin,
        获取纯风险保费和车辆信息: QueryPureriskAndVehicleInfo,
        生成报价单       : QuickSave,
        获取报价前信息     : QueryInsureInfo,
        报价          : CalculatePremium,
        保存报价信息      : SaveInsureInfo,

        //核保步骤
        判断核保状态      : CheckInsureStatus,
        根据报价单号查询报价条款: QueryClauseInfo,
        保存投保信息以创建投保单: SaveClauseInfo,
        提交投保信息创建保单  : SubmitInsureInfo,
        查询支付状态      : QueryPayment, // 只有通过审核的保单在保费支付中可以查得到
        查询身份信息      : QueryIdentityInformation,
        更新手机        : IDCardUpdatePhone,//更新
        采集身份信息      : IDCardCollect,
        向手机发送短信验证码  : SmsConfirm,
        提交承保验证码     : MoreBjIdentifyCode,
        请求支付        : Pay,
        获取支付方式      : GetChannels,
        校验支付状态      : CheckPaymentState,
        作废支付订单      : NullifyPaymentOrder,
        拉取支付二维码     : GetQRCodeByPayNo,
        根据保单号获取支付单号 : GetPayCodeByInsureNo,
        获取缓存二维码     : GetCacheQRCode,
        处理缓存二维码     : DealCacheQRCode,
        //上传影像
        上传照片        : UploadImage,
        再次提交投保信息    : SubmitInsureInfoAgain,
        人工审核        : InsureUnderInfo,
        查询保单信息      : QueryQuotationPolicy,
        支付人员        : PaymentRecordMan,
        支付查询        : GetPaymentRecord,
        作废订单支付查询    : NullifyGetPaymentRecord,
        识别交管验证码     : new Decaptcha('calculateDaptchaTextKey', 'decaptcha-in-type02'),
        获取报价单状态     : GetQuoteRecordState,
        获取交管验证码     : VehicleQueryValidation,
        根据交信管息调整车辆信息: VehicleQueryConfirm,
        记录登陆成功信息    : LoginSuccess,
        查车型方式       : SelectCarWay,
        续保          : QueryQuickOfferByPlate,
        选择车型        : SelectCarModel,
        再次获取车型      : ReBuildCar,
        调整品牌型号      : FindVehicleByCodeOther,
    ]

    //<editor-fold defaultstate="collapsed" desc="流程片段">

    private static get_SNIPPET_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    /**
     * 登录片段
     */
    private static final _LOGIN_SNIPPET = _SNIPPET_BUILDER {
        loop({ 获取登录页面 >> 获取登录验证码 >> 识别登陆验证码 >> 校验登录验证码 }, 10) >> 获取快速登录信息 >> 代理人登录
    }

    /**
     * 检验的登陆
     */
    static final _CHECK_LOGIN = _FLOW_BUILDER {
        loop({ 获取登录页面 >> 获取登录验证码 >> 识别登陆验证码 >> 校验登录验证码 }, 10) >> 记录登陆成功信息
    }

    /**
     * 身份信息采集片段
     */
    private static final _ID_COLLECT_SNIPPET = _SNIPPET_BUILDER {
        查询支付状态 >> loop {
            查询身份信息 >> 更新手机 >> 采集身份信息
        } >> 向手机发送短信验证码
    }

    /**
     * 查车片段
     */
    private static final _GET_AUTO_TYPE_SNIPPET = _SNIPPET_BUILDER {
        查车型方式 >> [
            自动选车: { 根据品牌型号查车型 >> 根据车牌和车架号查车型 >> 选择车型 >> 调整品牌型号 },
            选车  : {
                根据品牌型号查车型 >> [
                    根据车牌和车架号查车型: { 根据车牌和车架号查车型 >> 调整品牌型号 }]
            }]
    }

    /**
     * 交管查车片段
     */
    private static final _QUERY_VEHICLE_PM_SNIPPET = _SNIPPET_BUILDER {
        loop({ 获取交管验证码 >> 识别交管验证码 >> 根据交信管息调整车辆信息 }, 20)
    }


    private static final _NAME_FLOW_MAPPINGS = [
        登录并且进入报价系统: _LOGIN_SNIPPET,
        采集身份信息片段  : _ID_COLLECT_SNIPPET,
        查车片段      : _GET_AUTO_TYPE_SNIPPET,
        交管查车片段    : _QUERY_VEHICLE_PM_SNIPPET,
    ]

    /**
     * 报价模板
     */
    private static final _QUOTING_TEMPLATE = {
        检查登录次数限制 >> 检查补充信息 >> 登录并且进入报价系统 >> PH1 >> 查车片段 >> loop({
            PH2 >> 获取纯风险保费和车辆信息 >> 生成报价单 >> 获取报价前信息 >> 报价 >> [
                重新获取报价验证码: { 报价 },
                重新报价     : { 再次获取车型 >> 选择车型 >> 调整品牌型号 }
            ]
        }, 2) >> 保存报价信息 >> 报价后处理器
    }


    private static final _NAME_TEMPLATE_MAPPINGS = [
        报价模板: _QUOTING_TEMPLATE,
    ]

    static final _QUOTING_FLOW = _FLOW_BUILDER {
        make '报价模板', [
            PH1: '续保',
            PH2: '无',
        ]
    }

    static final _QUOTING_FLOW_320100 = _FLOW_BUILDER {
        make '报价模板', [
            PH1: '续保',
            PH2: '交管查车片段'
        ]
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="报价、核保流程">

    private static get_FLOW_BUILDER() {
        new FlowBuilder(
            nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS,
            nameFlowMappings: _NAME_FLOW_MAPPINGS,
            nameTemplateMappings: _NAME_TEMPLATE_MAPPINGS,
        )
    }


    static final _INSURING_FLOW_110000 = _FLOW_BUILDER {
        登录并且进入报价系统 >> 判断核保状态 >> [
            未创建投保单     : {
                根据报价单号查询报价条款 >> 保存投保信息以创建投保单 >> 提交投保信息创建保单 >> 采集身份信息片段
            },
            采集客户的身份信息  : { 采集身份信息片段 },
            上传影像资料     : { 上传照片 >> 再次提交投保信息 >> 人工审核 },
            查询审核结果以继续核保: { 查询保单信息 >> 采集身份信息片段 }
        ] >> 提交承保验证码 >> 查询支付状态 >> 请求支付
    }

    static final _INSURING_FLOW_DEFAULT = _FLOW_BUILDER {
        登录并且进入报价系统 >> 判断核保状态 >> [
            未创建投保单     : {
                根据报价单号查询报价条款 >> 保存投保信息以创建投保单 >> 提交投保信息创建保单 >> [
                    重新获取核保验证码: { 提交投保信息创建保单 },
                ]
            },
            上传影像资料     : { 上传照片 >> 再次提交投保信息 >> 人工审核 },
            查询审核结果以继续核保: { 查询保单信息 }
        ]
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="获取支付相关信息流程">
    static final _GET_PAYMENT_INFO_FLOW = _FLOW_BUILDER {
        获取缓存二维码 >> [
            获取二维码失败: {
                登录并且进入报价系统 >> 根据保单号获取支付单号 >> 拉取支付二维码
            },
        ] >> 处理缓存二维码
    }

    static final _GET_PAYMENT_INFO_FLOW_DEFAULT = _FLOW_BUILDER {
        获取缓存二维码 >> [
            获取二维码失败: {
                登录并且进入报价系统 >> 查询支付状态 >> [
                    符合支付初始状态: { 支付人员 >> 请求支付 },
                    继续查询支付结果: { 支付查询 >> 拉取支付二维码 },
                ]
            },
        ] >> 处理缓存二维码


    }

    static final _GET_PAYMENT_CHANNELS_FLOW = _FLOW_BUILDER {
        获取支付方式
    }

    static final _CHECK_PAYMENT_STATUS_FLOW = _FLOW_BUILDER {
        登录并且进入报价系统 >> 校验支付状态
    }

    static final _GET_QUOTE_RECORD_STATUS_FLOW = _FLOW_BUILDER {
        登录并且进入报价系统 >> 获取报价单状态
    }

    static final _CATEGORY_CANCEL_PAY__FLOW = _FLOW_BUILDER {
        登录并且进入报价系统 >> 作废订单支付查询 >> 作废支付订单
    }

    //</editor-fold>
}



