package com.cheche365.cheche.baoxian.flow

import com.cheche365.cheche.baoxian.flow.step.CreateTaskAByIdentity
import com.cheche365.cheche.baoxian.flow.step.CreateTaskAByName
import com.cheche365.cheche.baoxian.flow.step.CreateTaskAByVinNo
import com.cheche365.cheche.baoxian.flow.step.CreateTaskB
import com.cheche365.cheche.baoxian.flow.step.Deduct
import com.cheche365.cheche.baoxian.flow.step.GetAreas
import com.cheche365.cheche.baoxian.flow.step.GetCarModelInfosByName
import com.cheche365.cheche.baoxian.flow.step.GetCarModelInfosByVIN
import com.cheche365.cheche.baoxian.flow.step.GetProviders
import com.cheche365.cheche.baoxian.flow.step.GetQuoteResult
import com.cheche365.cheche.baoxian.flow.step.InsureMsgHandler
import com.cheche365.cheche.baoxian.flow.step.Pay
import com.cheche365.cheche.baoxian.flow.step.Refund
import com.cheche365.cheche.baoxian.flow.step.ReinsureRequest
import com.cheche365.cheche.baoxian.flow.step.SubmitHumanQuote
import com.cheche365.cheche.baoxian.flow.step.SubmitHumanQuoteWithFailureQuote
import com.cheche365.cheche.baoxian.flow.step.SubmitQuote
import com.cheche365.cheche.baoxian.flow.step.UpdateQuoteInfoForChangeItem
import com.cheche365.cheche.baoxian.flow.step.UpdateQuoteInfoForDelivery
import com.cheche365.cheche.baoxian.flow.step.UpdateQuoteInfoForProviderAndInsurance
import com.cheche365.cheche.baoxian.flow.step.UploadImage
import com.cheche365.cheche.baoxian.flow.step.v2.AccessToken
import com.cheche365.cheche.baoxian.flow.step.v2.GetQuoteResultInsureFirst
import com.cheche365.cheche.baoxian.flow.step.v2.InsureStateChecker
import com.cheche365.cheche.baoxian.flow.step.v2.ReSubmitQuote
import com.cheche365.cheche.baoxian.flow.step.v2.SubmitInsure
import com.cheche365.cheche.baoxian.flow.step.v2.UpdateQuoteInfoForDeliveryBeforePayments
import com.cheche365.cheche.baoxian.flow.step.v2.SubmitQuoteForNewProviders
import com.cheche365.cheche.baoxian.flow.step.v2.UpdateQuoteInfoForInsure
import com.cheche365.cheche.baoxian.flow.step.v2.ValidatePay
import com.cheche365.cheche.baoxian.flow.step.v2.ValidateQuote
import com.cheche365.cheche.baoxian.flow.step.v2m.CheckTaskId
import com.cheche365.cheche.baoxian.flow.step.v2m.CreateTaskB2M
import com.cheche365.cheche.baoxian.flow.step.v2m.GetProviders2M
import com.cheche365.cheche.baoxian.flow.step.v2m.GetQuoteResultInsureFirst2M
import com.cheche365.cheche.baoxian.flow.step.v2m.QuotePostProcessor2M
import com.cheche365.cheche.baoxian.flow.step.v2m.SubmitQuote2M
import com.cheche365.cheche.baoxian.flow.step.v2m.UpdateQuoteInfoForProviderAndInsurance2M
import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.common.flow.step.Identity
import com.cheche365.cheche.parser.flow.steps.CheckInsurancesCheckList
import com.cheche365.cheche.parser.flow.steps.CheckSupplementInfo
import com.cheche365.cheche.parser.flow.steps.ExtractAdditionalQuoteRecordInfo
import com.cheche365.cheche.parser.flow.steps.InsurePostProcessor
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.cheche.parser.flow.steps.UpdateAdditionalQuoteRecordInfo
import groovy.transform.PackageScope
import com.cheche365.cheche.baoxian.flow.step.v2m.UpdateQuoteInfoForChangeItemAndCarInfo
import com.cheche365.cheche.baoxian.flow.step.v2m.SimpleGetConcurrentQuoteResult

import static com.cheche365.cheche.baoxian.flow.Constants._API_PATH_PREFIX_NEW
import static com.cheche365.cheche.baoxian.flow.Constants._API_PATH_PREFIX_OLD



/**
 * Created by wangxin on 2017/2/10.
 */
class Flows {

    @PackageScope
    static final _STEP_NAME_CLAZZ_MAPPINGS = [

        同前              : Identity,
        报价后处理器          : QuotePostProcessor,
        检查险种清单          : CheckInsurancesCheckList,
        检查补充信息          : CheckSupplementInfo,
        核保后处理器          : InsurePostProcessor,
        抽取附加QR信息        : ExtractAdditionalQuoteRecordInfo,
        更新附加QR信息        : UpdateAdditionalQuoteRecordInfo,
        报价后处理器2M        : QuotePostProcessor2M,

        获取投保地区          : new GetAreas(_API_PATH_PREFIX_OLD + '/getAreas'),
        获取供应商列表         : new GetProviders(_API_PATH_PREFIX_OLD),
        车型名称获取车型列表      : new GetCarModelInfosByName(_API_PATH_PREFIX_OLD + '/getCarModelInfos'),
        VIN码获取车型列表      : new GetCarModelInfosByVIN(_API_PATH_PREFIX_OLD + '/getCarModelInfos'),
        创建续保报价并模糊查询车型   : new CreateTaskAByName(_API_PATH_PREFIX_OLD),
        创建续保报价并身份证号查询车型 : new CreateTaskAByIdentity(_API_PATH_PREFIX_OLD),
        创建非续保报价         : new CreateTaskB(_API_PATH_PREFIX_OLD),
        创建续保报价并车架号查询车型  : new CreateTaskAByVinNo(_API_PATH_PREFIX_OLD),
        提交报价            : new SubmitQuote(_API_PATH_PREFIX_OLD),
        重新提交报价          : new SubmitHumanQuote(_API_PATH_PREFIX_OLD),
        报价失败重新提交报价      : new SubmitHumanQuoteWithFailureQuote(_API_PATH_PREFIX_OLD),
        修改套餐信息          : new UpdateQuoteInfoForChangeItem(_API_PATH_PREFIX_OLD + '/updateQuoteInfo'),
        修改供应商和险种信息      : new UpdateQuoteInfoForProviderAndInsurance(_API_PATH_PREFIX_OLD + '/updateQuoteInfo'),
        获取报价信息          : GetQuoteResult,
        先核保流程获取报价信息     : GetQuoteResultInsureFirst,
        核保失败退款申请        : new Refund(_API_PATH_PREFIX_OLD),
        提交被保人信息         : new UpdateQuoteInfoForDelivery(_API_PATH_PREFIX_OLD + '/updateQuoteInfo'),
        核保状态判断          : InsureMsgHandler,
        影像上传            : new UploadImage(_API_PATH_PREFIX_OLD),
        支付请求接口          : new Pay(_API_PATH_PREFIX_OLD),
        重新核保            : new ReinsureRequest(_API_PATH_PREFIX_OLD),
        备用金支付           : new Deduct(_API_PATH_PREFIX_OLD),

        获取投保地区2         : new GetAreas(_API_PATH_PREFIX_NEW + '/getAgreementAreas'),
        获取供应商列表2        : new GetProviders(_API_PATH_PREFIX_NEW),
        车型名称获取车型列表2     : new GetCarModelInfosByName(_API_PATH_PREFIX_NEW + '/queryCarModelInfos'),
        VIN码获取车型列表2     : new GetCarModelInfosByVIN(_API_PATH_PREFIX_NEW + '/queryCarModelInfos'),
        创建续保报价并模糊查询车型2  : new CreateTaskAByName(_API_PATH_PREFIX_NEW),
        创建续保报价并身份证号查询车型2: new CreateTaskAByIdentity(_API_PATH_PREFIX_NEW),
        创建非续保报价2        : new CreateTaskB(_API_PATH_PREFIX_NEW),
        创建续保报价并车架号查询车型2 : new CreateTaskAByVinNo(_API_PATH_PREFIX_NEW),
        提交报价2           : new SubmitQuote(_API_PATH_PREFIX_NEW),
        剔除不可投保的公司后提交报价 : new SubmitQuoteForNewProviders(_API_PATH_PREFIX_NEW),
        修改套餐信息2         : new UpdateQuoteInfoForChangeItem(_API_PATH_PREFIX_NEW + '/updateTask'),
        修改套餐信息及车辆信息    : new UpdateQuoteInfoForChangeItemAndCarInfo(_API_PATH_PREFIX_NEW + '/updateTask'),
        修改供应商和险种信息2     : new UpdateQuoteInfoForProviderAndInsurance(_API_PATH_PREFIX_NEW + '/updateTask'),
        支付前提交配送信息       : new UpdateQuoteInfoForDeliveryBeforePayments(_API_PATH_PREFIX_NEW + '/updateTask'),
        核保失败退款申请2       : new Refund(_API_PATH_PREFIX_NEW),
        提交被保人信息2        : new UpdateQuoteInfoForInsure(_API_PATH_PREFIX_NEW + '/updateTask'),
        支付请求接口2         : new Pay(_API_PATH_PREFIX_NEW),
        影像上传2           : new UploadImage(_API_PATH_PREFIX_NEW),
        重新核保2           : new ReinsureRequest(_API_PATH_PREFIX_NEW),
        备用金支付2          : new Deduct(_API_PATH_PREFIX_NEW),
        重新提交报价2         : new ReSubmitQuote(_API_PATH_PREFIX_NEW),
        获取token         : AccessToken,
        提交核保任务          : SubmitInsure,
        报价有效期判断         : ValidateQuote,
        支付有效期判断         : ValidatePay,
        核保状态判断2         : InsureStateChecker,
        检查taskId        : CheckTaskId,

        创建非续保报价2M        : new CreateTaskB2M(_API_PATH_PREFIX_NEW),
        提交报价2M          : new SubmitQuote2M(_API_PATH_PREFIX_NEW),
        获取供应商列表2M       : new GetProviders2M(_API_PATH_PREFIX_NEW),
        先核保流程获取报价信息2M   : GetQuoteResultInsureFirst2M,
        修改供应商和险种信息2M    : new UpdateQuoteInfoForProviderAndInsurance2M(_API_PATH_PREFIX_NEW + '/updateTask'),
        并发报价          : SimpleGetConcurrentQuoteResult
    ]

    private static final _QUOTING_TEMPLATE_2 = {
        获取token >> 检查taskId >> [
            没有taskID缓存 : {
                获取投保地区2 >> 获取供应商列表2 >> 车型名称获取车型列表2 >> VIN码获取车型列表2 >> 创建续保报价并模糊查询车型2 >> [
                    创建续保失败: {
                        PH1 >> [
                            创建续保失败: { 创建非续保报价2 },
                            创建续保成功 : { 修改供应商和险种信息2 }
                        ]
                    },
                    创建续保成功 : { 修改供应商和险种信息2 }
                ] >> 提交报价2 >> [
                    (false): {
                        修改供应商和险种信息2 >> 剔除不可投保的公司后提交报价
                    }
                ]
            },
            有taskID缓存: { 修改套餐信息及车辆信息 >> 重新提交报价2 }
        ] >> loop({
            并发报价 >> [
                (false): { 修改套餐信息2 >> 重新提交报价2 }
            ]
        }, 5) >> PH2 >> 抽取附加QR信息 >> 更新附加QR信息 >> 报价后处理器
    }

    private static final _QUOTING_TEMPLATE = {
        获取投保地区 >> 获取供应商列表 >> 车型名称获取车型列表 >> VIN码获取车型列表 >> 创建续保报价并模糊查询车型 >> [
            (false): {
                PH1 >> [
                    (false): { 创建非续保报价 },
                    (true) : { 修改供应商和险种信息 }
                ]
            },
            (true) : { 修改供应商和险种信息 }
        ] >> 提交报价 >> [
            (false): {
                修改套餐信息 >> 重新提交报价
            }
        ] >> loop({
            获取报价信息 >> [
                (false): { 修改套餐信息 >> 报价失败重新提交报价 }
            ]
        }, 5) >> PH2 >> 抽取附加QR信息 >> 更新附加QR信息 >> 报价后处理器
    }


    private static final _NAME_TEMPLATE_MAPPINGS = [
        报价模板 : _QUOTING_TEMPLATE,
        报价模板2: _QUOTING_TEMPLATE_2
    ]

    private static get_FLOW_BUILDER() {
        new FlowBuilder(
            nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS,
            nameTemplateMappings: _NAME_TEMPLATE_MAPPINGS
        )
    }


    final static _QUOTING_FLOW = _FLOW_BUILDER {
        make '报价模板',
            [
                PH1: '创建续保报价并身份证号查询车型',
                PH2: '同前'
            ]
    }

    //江苏地区
    final static _QUOTING_FLOW_INSURE_FIRST = _FLOW_BUILDER {
        make '报价模板2',
            [
                PH1: '创建续保报价并身份证号查询车型2',
                PH2: '同前'
            ]
    }

    final static _QUOTING_FLOW_INSURE_FIRST_M = _FLOW_BUILDER {
        获取token >> 获取投保地区2 >> 获取供应商列表2M >>
            车型名称获取车型列表2 >> VIN码获取车型列表2 >> 创建续保报价并模糊查询车型2 >> [
            (false): {
                创建续保报价并身份证号查询车型2 >> [
                    (false): { 创建非续保报价2M },
                    (true) : { 修改供应商和险种信息2M }
                ]
            },
            (true) : { 修改供应商和险种信息2 }
        ] >> 提交报价2M >> 先核保流程获取报价信息2M >> 报价后处理器2M
    }

    //这里状态6是核保成功，我们不做任何处理，直接在集成层处理
    final static _INSURE_FLOW = _FLOW_BUILDER {
        核保状态判断 >> [
            '3' : { 提交被保人信息 >> [(true): { 支付请求接口 }] },
            '5' : { 影像上传 >> 重新核保 },
            '19': { 核保失败退款申请 }
        ]
    }

    //总共有3中状态，去核保、核保成功、核保退回修改，核保失败直接由service-broker将回调结果传给web端，不在走核保流程
    final static _INSURE_FIRST_FLOW = _FLOW_BUILDER {
        提交被保人信息2 >> [
            (true) : { 影像上传2 >> 提交核保任务 },
            (false): { 提交核保任务 }
        ]
    }

    final static _REFUNDING_FLOW = _FLOW_BUILDER {
        获取供应商列表 >> 核保失败退款申请
    }

    final static _DEDUCTING_FLOW = _FLOW_BUILDER {
        获取供应商列表 >> 备用金支付
    }

    final static _PAYING_FLOW = _FLOW_BUILDER {
        获取供应商列表 >> 支付请求接口
    }

    final static _PAYING_FLOW_INSURE_FIRST = _FLOW_BUILDER {
        获取供应商列表2 >> 支付前提交配送信息 >> 支付请求接口2
    }
}


