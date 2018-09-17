package com.cheche365.cheche.aibao.flow

import com.cheche365.cheche.aibao.flow.step.DefaultPriceQuote
import com.cheche365.cheche.aibao.flow.step.FindCModelsByCardBrand
import com.cheche365.cheche.aibao.flow.step.FindCModelsByVINNumber
import com.cheche365.cheche.aibao.flow.step.UserPriceQuote
import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.parser.flow.steps.CheckSupplementInfo
import com.cheche365.cheche.parser.flow.steps.InsurePostProcessor
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.cheche.parser.flow.steps.QuotePreProcessor
import com.cheche365.cheche.aibao.flow.step.CheckInsureStatus
import com.cheche365.cheche.aibao.flow.step.IssueCodeTransfer
import com.cheche365.cheche.aibao.flow.step.QuoteToProposal
import com.cheche365.cheche.aibao.flow.step.UnderWritingInspection
import com.cheche365.cheche.parser.flow.steps.SelectCarModel



/**
 * 所有流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        检查补充信息   : CheckSupplementInfo,
        报价前处理器   : QuotePreProcessor,
        承保检查     : UnderWritingInspection,
        根据车架号查车  : FindCModelsByVINNumber,
        根据品牌型号查车 : FindCModelsByCardBrand,
        默认报价     : DefaultPriceQuote,
        自由报价     : UserPriceQuote,
        核保       : QuoteToProposal,
        报价后处理器   : QuotePostProcessor,

        判断核保状态   : CheckInsureStatus,
        下单及提交核保接口: QuoteToProposal,
        校验身份证验证码 : IssueCodeTransfer,
        核保后处理器   : InsurePostProcessor,
        车型匹配     : SelectCarModel,


    ]

    //<editor-fold defaultstate="collapsed" desc="查车片段">
    private static final get_SNIPPET_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    private static final _GET_VEHICLE_MODE_SNIPPET = _SNIPPET_BUILDER {
        根据品牌型号查车 >> 根据车架号查车 >> 车型匹配
    }

    private static final _NAME_FLOW_MAPPINGS = [
        通过不同的方式查车片段: _GET_VEHICLE_MODE_SNIPPET
    ]
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="模板片段">
    private static final _QUOTING_TEMPLATE = {
        报价前处理器 >> 承保检查 >> PH1 >> loop { 默认报价 } >> loop { 自由报价 } >> 报价后处理器
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
        检查补充信息 >> 下单及提交核保接口
    }

    static final _INSURE_FLOW_110000 = _FLOW_BUILDER {
        // TODO 测试loop校验身份
//        检查补充信息 >> loop({ 校验身份证验证码 }, 2) >> 下单及提交核保接口
        检查补充信息 >> 判断核保状态 >> [未创建投保单: { 校验身份证验证码 }] >> 下单及提交核保接口 >> 核保后处理器
    }
    //</editor-fold>

}
