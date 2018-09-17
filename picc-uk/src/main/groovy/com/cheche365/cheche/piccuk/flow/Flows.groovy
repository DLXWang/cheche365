package com.cheche365.cheche.piccuk.flow

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
import com.cheche365.cheche.parser.flow.steps.SelectCarModel
import com.cheche365.cheche.parser.flow.steps.UpdateSupplementInfo
import com.cheche365.cheche.piccuk.flow.step.CalculatePremium
import com.cheche365.cheche.piccuk.flow.step.CheckInsureStatus
import com.cheche365.cheche.piccuk.flow.step.CheckLoginLimit
import com.cheche365.cheche.piccuk.flow.step.DeleteProposal
import com.cheche365.cheche.piccuk.flow.step.EditCancelProposal
import com.cheche365.cheche.piccuk.flow.step.EditCancelUnderwrite
import com.cheche365.cheche.piccuk.flow.step.EditCitemCar
import com.cheche365.cheche.piccuk.flow.step.EditRenewalCopy
import com.cheche365.cheche.piccuk.flow.step.EditSubmitUnderwrite
import com.cheche365.cheche.piccuk.flow.step.GetCarActualValue
import com.cheche365.cheche.piccuk.flow.step.GetCarActualValueAgain
import com.cheche365.cheche.piccuk.flow.step.GetCarInfo
import com.cheche365.cheche.piccuk.flow.step.GetCarModelByVIN
import com.cheche365.cheche.piccuk.flow.step.GetCarModelInfo
import com.cheche365.cheche.piccuk.flow.step.GetCarModelInfoAgain
import com.cheche365.cheche.piccuk.flow.step.GetCarModelInfoByBrandName
import com.cheche365.cheche.piccuk.flow.step.GetCarModelInfoByVIN
import com.cheche365.cheche.piccuk.flow.step.GetCarModelInfoVL
import com.cheche365.cheche.piccuk.flow.step.GetCheckUserMsg
import com.cheche365.cheche.piccuk.flow.step.GetTicket
import com.cheche365.cheche.piccuk.flow.step.GetToken
import com.cheche365.cheche.piccuk.flow.step.Insert
import com.cheche365.cheche.piccuk.flow.step.InsertUpdateInsureInfo
import com.cheche365.cheche.piccuk.flow.step.InsertUpdateSpecialAgreement
import com.cheche365.cheche.piccuk.flow.step.Login
import com.cheche365.cheche.piccuk.flow.step.LoginSuccess
import com.cheche365.cheche.piccuk.flow.step.LoopBreak
import com.cheche365.cheche.piccuk.flow.step.QueryPayFor
import com.cheche365.cheche.piccuk.flow.step.QuerySpecialAgreement
import com.cheche365.cheche.piccuk.flow.step.QueryVehiclePMCheck
import com.cheche365.cheche.piccuk.flow.step.QueryVehiclePMConfirm
import com.cheche365.cheche.piccuk.flow.step.QuickProposal
import com.cheche365.cheche.piccuk.flow.step.QuickRenewalProposal
import com.cheche365.cheche.piccuk.flow.step.SelectCarWay
import com.cheche365.cheche.piccuk.flow.step.SelectProposal
import com.cheche365.cheche.piccuk.flow.step.SelectRenewal
import com.cheche365.cheche.piccuk.flow.step.SelectRenewalPolicyNo
import com.cheche365.cheche.piccuk.flow.step.ShowUnderwriteMessage
import com.cheche365.cheche.piccuk.flow.step.v2.CheckCodeIsRight
import com.cheche365.cheche.piccuk.flow.step.v2.CheckPayStatusInit
import com.cheche365.cheche.piccuk.flow.step.v2.EditFeeInfor
import com.cheche365.cheche.piccuk.flow.step.v2.EditPayFeeByWeChat
import com.cheche365.cheche.piccuk.flow.step.v2.EditPayFeeByWeChatADD
import com.cheche365.cheche.piccuk.flow.step.v2.EditPayFeeByWeChatDENGJI
import com.cheche365.cheche.piccuk.flow.step.v2.GetChannels
import com.cheche365.cheche.piccuk.flow.step.v2.GetWeChatQRCode
import com.cheche365.cheche.piccuk.flow.step.v2.GotoJfcd
import com.cheche365.cheche.piccuk.flow.step.v2.IDCarCheck
import com.cheche365.cheche.piccuk.flow.step.v2.PaymentCompletion
import com.cheche365.cheche.piccuk.flow.step.v2.PrepareEditByJF
import com.cheche365.cheche.piccuk.flow.step.v2.QueryWeChatResult
import com.cheche365.cheche.piccuk.flow.step.v2.SaveByJF
import com.cheche365.cheche.piccuk.flow.step.v2.SaveByWeChat
import com.cheche365.cheche.piccuk.flow.step.v2.SaveByWeChatDENGJI
import com.cheche365.cheche.piccuk.flow.step.v2.SelectProposalForCheckStatus
import com.cheche365.cheche.piccuk.flow.step.v2.WeChatInvalid
import com.cheche365.cheche.piccuk.flow.step.v3.EditSubmitUnderwriteAgain
import com.cheche365.cheche.piccuk.flow.step.v3.GoToFileUpload
import com.cheche365.cheche.piccuk.flow.step.v3.ImageDate
import com.cheche365.cheche.piccuk.flow.step.v3.ImageScanUpdateAction
import com.cheche365.cheche.piccuk.flow.step.v3.ImageScanUpdateVTree
import com.cheche365.cheche.piccuk.flow.step.v3.SelectProposalForInsureStatus
import com.cheche365.cheche.piccuk.flow.step.v3.UploadImage
import com.cheche365.flow.business.flow.step.ExtractVehicleInfo



/**
 * 人保UK流程
 * <pre>
 * GetToken >> GetTicket >> Login >> QuickProposal >> GetCheckUserMsg
 *   >> SelectRenewalPolicyNo >> [
 *      0: CheckRenewalFlow,
 *      1: SelectRenewal >> QuickRenewalProposal,
 *      2: QuickRenewalProposal]
 *   >> GetCarInfo >> GetCarModelInfo >> GetCarActualValue
 *   >> 循环(3次, CalculatePremium) >> QuotePostProcessor
 * </pre>
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        // common中的
        同前          : Identity,
        无           : NoOp,
        非预期结束       : UnexpectedEnd,

        // common中的
        获取Token     : GetToken,
        获取Ticket    : GetTicket,
        进入车险承保系统    : Login,
        进入车商渠道专用出单  : QuickProposal,
        核对保险公司信息    : GetCheckUserMsg,

        // parser里面的
        检查补充信息      : CheckSupplementInfo,
        报价后处理器      : QuotePostProcessor,
        核保后处理器      : InsurePostProcessor,
        检查续保通道流程    : CheckRenewalFlow,
        检查险种清单      : CheckInsurancesCheckList,
        处理行驶证阶段性结果  : ProcessVehicleLicenseStagedResult,
        更新补充信息      : UpdateSupplementInfo,

        // M站转保接口
        检查是否续保      : SelectRenewalPolicyNo,
        获取保单号       : SelectRenewal,
        获取续保套餐      : QuickRenewalProposal,
        获取车辆信息      : GetCarInfo,
        获取车型信息      : GetCarModelInfo,
        通过车架号获取车型信息 : GetCarModelByVIN,
        通过车架号获取车型列表 : GetCarModelInfoByVIN,
        通过品牌型号获取车型列表: GetCarModelInfoByBrandName,
        再次获取车型信息    : GetCarModelInfoAgain,
        获取车型信息VL    : GetCarModelInfoVL,
        获取车辆真实价格    : GetCarActualValue,
        再次获取车辆真实价格  : GetCarActualValueAgain,
        抽取车辆信息      : ExtractVehicleInfo,
        抽取保险基本信息    : ExtractInsuranceBasicInfo,
        抽取保险信息      : ExtractInsuranceInfo,
        报价          : CalculatePremium,

        保存报价        : Insert,
        核保          : EditSubmitUnderwrite,
        判断核保状态      : CheckInsureStatus,
        核保意见        : ShowUnderwriteMessage,

        查询投保单       : SelectProposal,
        撤销投保单       : EditCancelProposal,
        取消投保单       : EditCancelUnderwrite,
        删除投保单       : DeleteProposal,

        // 支付片段
        获取支付方式      : GetChannels,
        初始化状态检查     : GotoJfcd,
        检查保单支付状态    : PrepareEditByJF,
        保存收款信息      : SaveByJF,
        编辑微信二维码信息确认 : EditPayFeeByWeChat,
        获取微信支付二维码   : GetWeChatQRCode,
        缴费信息页面      : EditFeeInfor,
        创建微信交费记录    : SaveByWeChat,
        编辑微信二维码信息创建 : EditPayFeeByWeChatADD,
        编辑微信二维码信息登记 : EditPayFeeByWeChatDENGJI,
        更新微信交费记录状态  : SaveByWeChatDENGJI,
        作废二维码记录     : WeChatInvalid,

        交管车辆查询二维码   : QueryVehiclePMCheck,
        交管车辆查询      : QueryVehiclePMConfirm,
        识别交管验证码     : new Decaptcha('queryVehicleDaptchaTextKey', 'decaptcha-in-type02'),
        查询支付结果预处理   : CheckPayStatusInit,
        检查支付状态      : QueryWeChatResult,

        检查保单号是否生成   : SelectProposalForCheckStatus,
        缴费完成确认      : PaymentCompletion,

        //上传影像
        获取影像上传的跳转路径 : GoToFileUpload,
        跳转到影像系统     : ImageScanUpdateAction,
        获取原有影像参数    : ImageScanUpdateVTree,
        生成影像更新时间    : ImageDate,
        上传影像        : UploadImage,
        上传照片提交核保    : EditSubmitUnderwriteAgain,
        查看审核结果      : SelectProposalForInsureStatus,

//        判断是否需要身份采集: EditIDCardCheck,
//        身份信息采集    : PickInfoWithGshell,
        发送身份证验证码    : IDCarCheck,
        校验身份证验证码    : CheckCodeIsRight,

        记录登陆成功信息    : LoginSuccess,
        检查登录次数限制    : CheckLoginLimit,

        选择车型        : SelectCarModel,
        查车型方式       : SelectCarWay,
        组装续保参数      : EditRenewalCopy,
        获取续保详细信息    : EditCitemCar,
        查询手续费率      : QueryPayFor,
        查询特别约定      : QuerySpecialAgreement,
        再次保存        : InsertUpdateSpecialAgreement,
        终止循环        : LoopBreak,
        更新报价单地址信息   : InsertUpdateInsureInfo
    ]

    //<editor-fold defaultstate="collapsed" desc="流程片段">

    private static get_SNIPPET_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    /**
     * 北京登录片段
     */
    private static final _LOGIN_110000_SNIPPET = _SNIPPET_BUILDER {
        获取Token >> 获取Ticket >> 进入车险承保系统 >> 进入车商渠道专用出单 >> 核对保险公司信息
    }

    /**
     * 登录片段
     */
    private static final _LOGIN_SNIPPET = _SNIPPET_BUILDER {
        获取Token >> 获取Ticket >> 进入车险承保系统
    }

    /**
     * 车型查询
     */
    private static final _GET_CAR_MODEL_SNIPPET = _SNIPPET_BUILDER {
        查车型方式 >> [
            自动选车: { 通过车架号获取车型信息 >> 通过车架号获取车型列表 >> 通过品牌型号获取车型列表 >> 选择车型 },
            选车  : { 通过车架号获取车型信息 >> 通过车架号获取车型列表 >> [通过品牌型号获取车型列表: { 通过品牌型号获取车型列表 }] }
        ] >> 获取车辆真实价格
    }

    /**
     * 获取续保套餐片段
     */
    private static final _RENEWAL_SNIPPET = _SNIPPET_BUILDER {
        获取车辆信息 >> 获取续保套餐
    }

    /**
     * 获取续保套餐及抽取车辆片段
     */
    private static final _RENEWAL_AND_EXTRACT_VEHICLE_SNIPPET = _SNIPPET_BUILDER {
        获取车辆信息 >> 抽取车辆信息 >> 处理行驶证阶段性结果 >> 获取续保套餐
    }
    /**
     * 核保
     */
    private static final _INSURING_SNIPPET = _SNIPPET_BUILDER {
        更新报价单地址信息 >> 判断核保状态 >> [
            去核保        : {
                loop({
                    核保 >> [
                        核保通过  : { 核保后处理器 >> 终止循环 },
                        查看核保意见: {
                            核保意见 >> [
                                增加特别约定: { 查询特别约定 >> 再次保存 }
                            ]
                        },
                    ]
                }, 10)
            },
            上传影像资料     : { 获取影像上传的跳转路径 >> 跳转到影像系统 >> 获取原有影像参数 >> 生成影像更新时间 >> 上传影像 >> 上传照片提交核保 },
            查询审核结果以继续核保: { 查看审核结果 }
        ]
    }

    /**
     * 获取车型片段
     */
    private static final _VEHICLE_LICENSE_SNIPPET = _SNIPPET_BUILDER {
        获取车辆信息 >> 获取车型信息VL >> 抽取车辆信息
    }

    /**
     * 取消并删除投保单片段
     */
    private static final _CANCEL_AND_DELETE_PROPOSAL_SNIPPET = _SNIPPET_BUILDER {
        loop({
            查询投保单 >> [
                cancelUnderwrite: {
                    取消投保单
                },
                cancelProposal  : {
                    撤销投保单
                }
            ] >> 删除投保单
        }, 10)
    }

    /**
     * 微信二维码信息创建片段
     */
    private static final _CREATE_PAYFEE_BY_WECHAT_SNIPPET = _SNIPPET_BUILDER {
        编辑微信二维码信息创建 >> 创建微信交费记录
    }

    /**
     * 微信二维码信息登记片段
     */
    private static final _UPDATE_PAYFEE_BY_WECHAT_SNIPPET = _SNIPPET_BUILDER {
        编辑微信二维码信息登记 >> 更新微信交费记录状态
    }

    /**
     * 作废并且重新生成微信二维码信息片段
     */
    private static final _INVALID_CREATE_UPDATE_PAYFEE_BY_WECHAT_SNIPPET = _SNIPPET_BUILDER {
        作废二维码记录 >> 缴费信息页面 >> 编辑微信二维码信息创建 >> 创建微信交费记录 >> 编辑微信二维码信息登记 >> 更新微信交费记录状态 >> 编辑微信二维码信息确认
    }

    /**
     * 交管查车片段
     */
    private static final _QUERY_VEHICLE_PM_SNIPPET = _SNIPPET_BUILDER {
        loop({ 交管车辆查询二维码 >> 识别交管验证码 >> 交管车辆查询 }, 10)
    }

    /**
     * 查询支付结果片段
     */
    private static final _QUERY_PAYMENT_STATUS_SNIPPET = _SNIPPET_BUILDER {
        查询支付结果预处理 >> 初始化状态检查 >> 检查保单支付状态 >> 检查支付状态
    }


    private static final _NAME_FLOW_MAPPINGS = [
        北京登录片段         : _LOGIN_110000_SNIPPET,
        登录片段           : _LOGIN_SNIPPET,
        核保片段           : _INSURING_SNIPPET,
        车型查询片段         : _GET_CAR_MODEL_SNIPPET,
        获取续保套餐片段       : _RENEWAL_SNIPPET,
        获取车型片段         : _VEHICLE_LICENSE_SNIPPET,
        取消并删除投保单片段     : _CANCEL_AND_DELETE_PROPOSAL_SNIPPET,
        获取续保套餐及抽取车辆    : _RENEWAL_AND_EXTRACT_VEHICLE_SNIPPET,
        微信二维码创建片段      : _CREATE_PAYFEE_BY_WECHAT_SNIPPET,
        微信二维码登记片段      : _UPDATE_PAYFEE_BY_WECHAT_SNIPPET,
        作废并且重新生成微信二维码片段: _INVALID_CREATE_UPDATE_PAYFEE_BY_WECHAT_SNIPPET,
        交管查车片段         : _QUERY_VEHICLE_PM_SNIPPET,
        查询支付结果片段       : _QUERY_PAYMENT_STATUS_SNIPPET,
    ]

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="流程模板">

    /**
     * 获取车型模板
     */
    private static final _GET_AUTO_TYPE_TEMPLATE = {
        检查补充信息 >> 登录片段 >> 获取车型片段
    }

    /**
     * 获取续保套餐片段模板
     */
    private static final _GET_RENEWAL_TEMPLATE = {
        fork([
            // 转保
            (0): {
                检查续保通道流程
            },
            // 续保
            (1): {
                获取保单号 >> 获取续保套餐片段
            },
            // 脱保
            (2): {
                获取续保套餐片段
            },
        ])
    }

    /**
     * 报价模板
     */
    private static final _QUOTING_TEMPLATE = {
        检查登录次数限制 >> 检查补充信息 >> PH1 >> 检查是否续保 >> [
            续保: { 组装续保参数 >> 获取续保详细信息 }
        ] >> 车型查询片段 >> PH2 >> loop { 报价 >> 再次获取车型信息 >> 选择车型 >> 再次获取车辆真实价格 } >> 检查险种清单 >> 报价后处理器 >> PH3 >> 保存报价
    }

    private static final _INSURANCE_INFO_TEMPLATE = {
        检查补充信息 >> 登录片段 >> 检查是否续保 >> [
            (1): {
                获取保单号 >> 获取续保套餐及抽取车辆
            },
            (2): {
                获取续保套餐及抽取车辆
            },
        ] >> 抽取保险基本信息 >> PH1
    }

    private static final _NAME_TEMPLATE_MAPPINGS = [
        获取续保套餐模板: _GET_RENEWAL_TEMPLATE,
        报价模板    : _QUOTING_TEMPLATE,
        获取车型模板  : _GET_AUTO_TYPE_TEMPLATE,
        获取续保信息模板: _INSURANCE_INFO_TEMPLATE
    ]

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="报价、核保流程">

    /**
     * 登录校验流程
     */
    static final _CHECK_LOGIN = _FLOW_BUILDER {
        获取Token >> 获取Ticket >> 记录登陆成功信息
    }

    private static get_FLOW_BUILDER() {
        new FlowBuilder(
            nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS,
            nameFlowMappings: _NAME_FLOW_MAPPINGS,
            nameTemplateMappings: _NAME_TEMPLATE_MAPPINGS
        )
    }

    static final _QUOTING_FLOW = _FLOW_BUILDER {
        make '报价模板', [
            PH1: '登录片段',
            PH2: '无',
            PH3: '无'
        ]
    }

    static final _QUOTING_FLOW_110000 = _FLOW_BUILDER {
        make '报价模板', [
            PH1: '北京登录片段',
            PH2: '无',
            PH3: '查询手续费率'
        ]
    }

    static final _QUOTING_FLOW_320100 = _FLOW_BUILDER {
        make '报价模板', [
            PH1: '登录片段',
            PH2: '交管查车片段',
            PH3: '无'
        ]
    }

    static final _INSURING_FLOW_110000 = _FLOW_BUILDER {
        检查补充信息 >> 登录片段 >> 更新报价单地址信息 >> 判断核保状态 >> [
            去核保        : {
                loop({
                    核保 >> [
                        核保通过  : { 核保后处理器 >> 发送身份证验证码 >> 终止循环 },
                        查看核保意见: {
                            核保意见 >> [
                                增加特别约定: { 查询特别约定 >> 再次保存 }
                            ]
                        },
                    ]
                }, 10)
            },
            上传影像资料     : { 获取影像上传的跳转路径 >> 跳转到影像系统 >> 获取原有影像参数 >> 生成影像更新时间 >> 上传影像 >> 上传照片提交核保 },
            查询审核结果以继续核保: {
                查看审核结果 >> [
                    核保通过: { 发送身份证验证码 }
                ]
            },
            身份采集验证码发送失败: { 发送身份证验证码 }
        ] >> 校验身份证验证码
    }

    static final _INSURING_FLOW = _FLOW_BUILDER {
        检查补充信息 >> 登录片段 >> 核保片段
    }

    //</editor-fold>

    static final _INSURANCE_INFO_FLOW = _FLOW_BUILDER {
        make '获取续保信息模板', [
            PH1: '抽取保险信息'
        ]
    }

    static final _INSURANCE_BASIC_INFO_FLOW = _FLOW_BUILDER {
        make '获取续保信息模板', [
            PH1: '无'
        ]
    }

    //<editor-fold defaultstate="collapsed" desc="获取支付相关信息流程">
    /*  缴费信息页面全部状态
     * >> 缴费信息页面 >> [
      *      登记  : { 微信二维码登记片段 },
      *      交费登记: { 微信二维码登记片段 },
      *      未创建 : { 微信二维码创建片段 >> 微信二维码登记片段 },
      *      登记确认:{} // 这里不需要处理 直接执行下一步
       * ]
     */

    static final _GET_PAYMENT_CHANNELS_FLOW = _FLOW_BUILDER {
        获取支付方式
    }

    static final _GET_PAYMENT_INFO_FLOW_DEFAULT = _FLOW_BUILDER {
        登录片段 >> 初始化状态检查 >> 检查保单支付状态 >> [
            生成缴费通知单号: { 保存收款信息 }
        ] >> 缴费信息页面 >> [
            登记  : { 微信二维码登记片段 },
            交费登记: { 微信二维码登记片段 },
            未创建 : { 微信二维码创建片段 >> 微信二维码登记片段 },
        ] >> 编辑微信二维码信息确认 >> [
            链接过期: { 作废并且重新生成微信二维码片段 }
        ] >> 获取微信支付二维码
    }

    /**
     * 获取支付结果
     */
    static final _CHECK_PAYMENT_STATUS_FLOW = _FLOW_BUILDER {
        登录片段 >> loop(
            {
                查询支付结果片段 >> [
                    生成保单号: {
                        检查保单号是否生成 >> [
                            创建保单号: { 缴费完成确认 }
                        ]
                    }
                ]
            }, 10)
    }
    //</editor-fold>

}
