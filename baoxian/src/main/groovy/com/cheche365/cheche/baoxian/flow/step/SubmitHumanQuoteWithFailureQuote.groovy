package com.cheche365.cheche.baoxian.flow.step

import groovy.transform.InheritConstructors
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV



/**
 * 报价失败后，对套餐进行修改后重新申请报价
 * Created by wangxin on 2017/2/13.
 */
@Component
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class SubmitHumanQuoteWithFailureQuote extends ASubmitHumanQuote {

    @Override
    protected getSuccessfulFsrv() {
        getLoopContinueFSRV true, '提交报价成功'
    }

}
