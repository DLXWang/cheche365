package com.cheche365.cheche.huanong.flow

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.huanong.flow.step.Submit
import com.cheche365.cheche.huanong.flow.step.CheckInsureStatus
import com.cheche365.cheche.huanong.flow.step.ImageUpload
import com.cheche365.cheche.huanong.flow.step.IssueCodeCheck
import com.cheche365.cheche.huanong.flow.step.ProposalStatusQry
import com.cheche365.cheche.huanong.flow.step.PushNewQuoteResult
import com.cheche365.cheche.huanong.flow.step.QuotedPriceAndCompare
import com.cheche365.cheche.parser.flow.steps.CheckSupplementInfo
import com.cheche365.cheche.parser.flow.steps.InsurePostProcessor
import com.cheche365.cheche.parser.flow.steps.QuotePostProcessor
import com.cheche365.cheche.parser.flow.steps.QuotePreProcessor
import com.cheche365.flow.business.flow.step.ExtractVehicleInfo
import com.cheche365.cheche.huanong.flow.step.ModelsQuery
import com.cheche365.cheche.huanong.flow.step.QuotedPrice
import com.cheche365.cheche.huanong.flow.step.TokenStep
import com.cheche365.cheche.huanong.flow.step.CheckBreakFlag
import com.cheche365.cheche.parser.flow.steps.SelectCarModel
import com.cheche365.cheche.huanong.flow.step.UpDateQuotePriceQR


/**
 * 所有流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        抽取车辆信息   : ExtractVehicleInfo,
        检查补充信息   : CheckSupplementInfo,
        报价前处理器   : QuotePreProcessor,
        报价后处理器   : QuotePostProcessor,
        核保后处理器   : InsurePostProcessor,
        获取TOKEN令牌: TokenStep,
        车型查询     : ModelsQuery,
        车型匹配     : SelectCarModel,
        精准报价     : QuotedPrice,
        二次报价及结果比对: QuotedPriceAndCompare,
        推送二次报价结果 : PushNewQuoteResult,
        提交核保     : Submit,
        终止外层循环判断 : CheckBreakFlag,
        判断核保状态   : CheckInsureStatus,
        验证码录入校验接口: IssueCodeCheck,
        影像上传接口   : ImageUpload,
        核保状态查询接口 : ProposalStatusQry,
        更新QR     : UpDateQuotePriceQR
    ]


    private static get_FLOW_BUILDER() {
        new FlowBuilder(
            nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS
        )
    }


    static final _QUOTING_FLOW = _FLOW_BUILDER {
        检查补充信息 >> 获取TOKEN令牌 >> loop({ 终止外层循环判断 >> 车型查询 >> 车型匹配 >> loop({ 精准报价 }, 2) }, 2) >> 更新QR >> 报价后处理器
    }

    static final _INSURING_FLOW = _FLOW_BUILDER {
        检查补充信息 >> 获取TOKEN令牌 >> 判断核保状态 >>
            [
                未创建保单: { 二次报价及结果比对 >> 提交核保 },
                上传影像 : { 影像上传接口 >> 提交核保 },
            ]
    }
}
