package com.cheche365.cheche.pingan.flow.step.m

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts



/**
 * 获取续保套餐
 * Created by wangxin on 2016/5/18.
 */
@Component
@Slf4j
class GetRenewalPackage extends ABizQuote {

    @Override
    protected getCommercialQuoteResult(context, result) {
        def errorMsgs = result.errorMsgs
        if (errorMsgs || '0' == result.bizPremium?.premiums?.totalPremium) {
            def msg = errorMsgs.collect { error ->
                [error.paramName, error.text]
            }
            log.warn '获取商业险续保套餐失败 {}', msg
            disableCommercial context
            getContinueFSRV false
        } else {
            getContinueFSRV true
        }
    }

    @Override
    protected getRequestParams(context, step) {
        [
            flowId                   : context.flowId,
            'bizConfig.pkgName'      : 'renewal',
            responseProtocol         : 'json',
            'bizInfo.beginDate'      : getCommercialInsurancePeriodTexts(context).first as String,
            'bizInfo.isNeedRuleCheck': false,
            __xrc                    : context.__xrc
        ]
    }

}
