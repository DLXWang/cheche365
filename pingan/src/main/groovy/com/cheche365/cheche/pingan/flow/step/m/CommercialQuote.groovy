package com.cheche365.cheche.pingan.flow.step.m

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakWithIgnorableErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsurancePackageItem
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.pingan.flow.step.m.Handlers._CODE_INSURANCE_CHINESE_NAME_MAPPINGS_DEFAULT



/**
 * 商业险报价
 * Created by wangxin on 2016/5/18.
 */
@Component
@Slf4j
class CommercialQuote extends ABizQuote {

    @Override
    protected getCommercialQuoteResult(context, result){
        def errorMsgs = result.errorMsgs
        if (errorMsgs) {
            def msg = errorMsgs.collect { error ->
                [error.paramName, error.text]
            }
            log.warn '获取商业险报价失败 {}', msg
            //商业险报价失败，跳出循环
            disableCommercial context
            getLoopBreakWithIgnorableErrorFSRV false, '获取商业险报价失败'
        } else if (result.bizPremium?.modifyList) {
            result.bizPremium.modifyList.collect {
                adjustInsurancePackageItem context, _CODE_INSURANCE_CHINESE_NAME_MAPPINGS_DEFAULT[it.riderCode].propNameENG, null, it.modifyAmount as double, null
            }

            getLoopContinueFSRV null, '修改套餐建议重新报价'

        } else if (result.bizPremium?.failRules && '0' == result.bizPremium?.premiums?.totalPremium) {
            log.warn '获取商业险报价失败，以默认险种重新进行报价'
            getContinueWithIgnorableErrorFSRV false, '商业险报价失败，尝试调整套餐后重试'
        } else {
            context.bizAmount = result.bizPremium.amounts
            context.bizPremium = result.bizPremium.premiums
            //重新获取盗抢险和自燃险的保额，因为非费改城市和费改城市获取盗抢和自燃的保额的方式不一致，非费改城市只能在报价之后拿到，用覆盖原来保费的方式来记录
            context.necessaryInfo.theftAmount = result.bizPremium.amounts.amount03
            context.necessaryInfo.spontaneousLossAmount = result.bizPremium.amounts.amount18
            log.debug '获取商业险报价 {}', context.bizPremium
            getContinueFSRV true
        }
    }

    @Override
    protected getRequestParams(context,step){
        generateRequestParameters(context, this)
    }

}
