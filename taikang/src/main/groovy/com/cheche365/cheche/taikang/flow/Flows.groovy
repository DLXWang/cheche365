package com.cheche365.cheche.taikang.flow

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.parser.flow.steps.CheckSupplementInfo
import com.cheche365.cheche.parser.flow.steps.InsurePostProcessor
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.cheche.parser.flow.steps.QuotePreProcessor
import com.cheche365.cheche.parser.flow.steps.SelectCarModel
import com.cheche365.cheche.taikang.flow.step.CheckInsureStatus
import com.cheche365.cheche.taikang.flow.step.CarModelQueryByVehicleJYCode
import com.cheche365.cheche.taikang.flow.step.CarModelQueryByVehicleHyCode
import com.cheche365.cheche.taikang.flow.step.CheckIssueCode
import com.cheche365.cheche.taikang.flow.step.KindsInit
import com.cheche365.cheche.taikang.flow.step.PreQuoteProposal
import com.cheche365.cheche.taikang.flow.step.PriceQuote
import com.cheche365.cheche.taikang.flow.step.QuoteToProposal
import com.cheche365.cheche.taikang.flow.step.TaiKangKindsCheck
import com.cheche365.cheche.taikang.flow.step.TokenStep
import com.cheche365.cheche.taikang.flow.step.CarModelQueryByVehicleType
import com.cheche365.flow.business.flow.step.ExtractVehicleInfo



/**
 * 所有流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        抽取车辆信息    : ExtractVehicleInfo,
        检查补充信息    : CheckSupplementInfo,
        报价前处理器    : QuotePreProcessor,
        报价后处理器    : QuotePostProcessor,
        核保后处理器    : InsurePostProcessor,
        查车后流程     : SelectCarModel,
        获取TOKEN令牌 : TokenStep,
        根据车型查车    : CarModelQueryByVehicleType,
        根据精友车型编码查车: CarModelQueryByVehicleJYCode,
        根据行业车型编码查车: CarModelQueryByVehicleHyCode,
        校验险种      : TaiKangKindsCheck,
        判断核保状态    : CheckInsureStatus,
        险别初始化     : KindsInit,
        报价        : PriceQuote,
        核保预处理接口   : PreQuoteProposal,
        下单及提交核保接口 : QuoteToProposal,
        校验身份证验证码  : CheckIssueCode,
    ]
    //<editor-fold defaultstate="collapsed" desc="查车片段">
    private static final get_SNIPPET_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    private static final _GET_VEHICLE_MODE_SNIPPET = _SNIPPET_BUILDER {
        根据车型查车 >> [
            根据精友编码查车: {
                根据精友车型编码查车 >> [
                    根据行业编码查车: { 根据行业车型编码查车 }
                ]
            }
        ]
    }

    private static final _NAME_FLOW_MAPPINGS = [
        通过不同的方式查车片段: _GET_VEHICLE_MODE_SNIPPET
    ]
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="模板片段">
    private static final _QUOTING_TEMPLATE = {
        检查补充信息 >> 获取TOKEN令牌 >> PH1 >> 险别初始化 >> 校验险种 >> loop { 报价 } >> 报价后处理器
    }

    private static final _NAME_TEMPLATE_MAPPINGS = [
        报价模板: _QUOTING_TEMPLATE,
    ]
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="报价">
    private static get_FLOW_BUILDER() {
        new FlowBuilder(
            nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS,
            nameFlowMappings: _NAME_FLOW_MAPPINGS,
            nameTemplateMappings: _NAME_TEMPLATE_MAPPINGS
        )
    }

    static final _QUOTE_FLOW = _FLOW_BUILDER {
        make '报价模板',
            [
                PH1: '通过不同的方式查车片段'
            ]
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="核保">
    static final _INSURE_FLOW = _FLOW_BUILDER {
        检查补充信息 >> 核保预处理接口 >> [
            验证码处理   : { 下单及提交核保接口 },
            无图片验证码输入: { 下单及提交核保接口 }
        ] >> 核保后处理器
    }

    static final _INSURE_FLOW_110000 = _FLOW_BUILDER {
        检查补充信息 >> 判断核保状态 >> [
            未创建投保单: {
                核保预处理接口 >> [
                    验证码处理   : { 下单及提交核保接口 },
                    无图片验证码输入: { 下单及提交核保接口 }
                ] >> 核保后处理器
            }
        ] >> 校验身份证验证码
    }
    //</editor-fold>

}
