package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getResponseResult
import static com.cheche365.cheche.parser.util.BusinessUtils.resolveNewQuoteRecordInContext

/**
 * 计算商业险报价
 * Created by wangxin on 2015/11/4.
 */
@Component
@Slf4j
class CalculateCommercialPremium implements IStep {

    @Override
    run(context) {
        //解决Null + Null的问题,和在bizPremium里面有险种缺失的问题
        updateBizPremium context
        log.info '组装商业险的QuoteRecord'
        getResponseResult(resolveNewQuoteRecordInContext(context), context, this)
    }

    private void updateBizPremium(context) {
        //bizPremium是bizQuote获取的报价
        def bizPremium = context.bizPremium ?: [:]
        //bizResult是toSupplementInfo获取的报价
        def bizResult = context.bizResult ?: [:]
        context.bizPremium = bizResult + bizPremium
    }

}
