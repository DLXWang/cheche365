package com.cheche365.cheche.sinosafe.flow

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.common.flow.step.Identity
import com.cheche365.cheche.common.flow.step.NoOp
import com.cheche365.cheche.sinosafe.flow.step.AddVerificationCode
import com.cheche365.cheche.sinosafe.flow.step.AppNoStatus
import com.cheche365.cheche.sinosafe.flow.step.CheckNewCar
import com.cheche365.cheche.sinosafe.flow.step.FindVehicleByJY
import com.cheche365.cheche.sinosafe.flow.step.ApplyInsureForInsuring
import com.cheche365.cheche.sinosafe.flow.step.ApplyInsureForQuoting
import com.cheche365.cheche.sinosafe.flow.step.QuotePrice
import com.cheche365.cheche.parser.flow.steps.CheckInsurancesCheckList
import com.cheche365.cheche.parser.flow.steps.CheckRenewalFlow
import com.cheche365.cheche.parser.flow.steps.CheckSupplementInfo
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.cheche.sinosafe.flow.step.SendVerificationCode
import com.cheche365.cheche.sinosafe.flow.step.UploadImage
import com.cheche365.cheche.sinosafe.flow.step.UpDateQuotePriceQR
import com.cheche365.cheche.sinosafe.flow.step.CheckChannel
import com.cheche365.cheche.sinosafe.flow.step.RRCQuotePrice
import com.cheche365.cheche.parser.flow.steps.SelectCarModel

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
        精友车型查询  : FindVehicleByJY,
        报价      : QuotePrice,
        更新QR    : UpDateQuotePriceQR,
        车险保单状态查询: AppNoStatus,
        报价流程提核  : ApplyInsureForQuoting,
        核保流程提核  : ApplyInsureForInsuring,
        上传影像    : UploadImage,
        发送短信验证码 : SendVerificationCode,
        补充短信验证码 : AddVerificationCode,
        验证渠道    : CheckChannel,
        人人车渠道   : RRCQuotePrice,
        选择车型    : SelectCarModel,
    ]

    //<editor-fold defaultstate="collapsed" desc="流程片段">

    private static get_SNIPPET_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    /**
     * 核保片段
     */
    private static final _INSURING_TEMPLATE = _SNIPPET_BUILDER {
        loop { 核保流程提核 }
    }

    private static final _NAME_FLOW_MAPPINGS = [
        核保片段: _INSURING_TEMPLATE
    ]

    // </editor-fold>

    private static final _QUOTING_TEMPLATE = {
        检查补充信息 >> 精友车型查询 >> 选择车型 >> loop({
            验证渠道 >> [
                renrenche: { 人人车渠道 },
                none     : { 报价 }
            ]
        }, 3) >> 更新QR >> 报价后处理器
    }
    /**
     * 报价模板
     */
    private static final _NAME_TEMPLATE_MAPPINGS = [
        报价模板: _QUOTING_TEMPLATE,
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

    final static _INSURE_FLOW = _FLOW_BUILDER {
        上传影像 >> 核保片段
    }

    final static _INSURE_FLOW_BJ = _FLOW_BUILDER {
        上传影像 >> 核保片段 >> 补充短信验证码
    }

    //  </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="承报">

//    private static final _ORDERING_FLOW_DEFAULT = _FLOW_BUILDER {
//    }
    //  </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="报价流程">
    //通用
    static final _QUOTING_FLOW_DEFAULT = _FLOW_BUILDER {
        make '报价模板',
            [
                PH1: '无'
            ]
    }
    //</editor-fold>
    //</editor-fold>
//    static final _INSURING_FLOW_TYPE1 = new FlowChain(flows: [_UNDERWRITING_FLOW_DEFAULT])

//    static final _ORDERING_FLOW_TYPE1 = new FlowChain(flows: [_ORDERING_FLOW_DEFAULT])

    //<editor-fold defaultstate="collapsed" desc="上传影像流程">
    static final _UPLOADING_FLOW = _FLOW_BUILDER {
        上传影像
    }

    //</editor-fold>

}
