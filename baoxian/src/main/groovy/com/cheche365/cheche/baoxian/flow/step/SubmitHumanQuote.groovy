package com.cheche365.cheche.baoxian.flow.step

import groovy.transform.InheritConstructors
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV



/**
 * 对套餐进行修改后重新申请报价
 * Created by wangxin on 2017/2/13.
 */
@Component
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class SubmitHumanQuote extends ASubmitHumanQuote {

    @Override
    protected getSuccessfulFsrv() {
        getContinueFSRV true
    }

}
