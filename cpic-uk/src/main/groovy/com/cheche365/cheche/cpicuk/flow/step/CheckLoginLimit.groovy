package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV



/**
 * 检查是否已达登录次数限制（一分钟内）
 */
@Component
@Slf4j
class CheckLoginLimit implements IStep {

    @Override
    Object run(Object context) {

        //检查登录限制类是QuoteLimitChecker，放在additionalParameters中
        //def quoteLimitChecker = context.additionalParameters.quote_limit_checker

        //quoteLimitChecker.meetLimit context.quoteRecord, context.username
        //quoteLimitChecker.oneMore context.quoteRecord, context.username
        //log.debug '登录限制检查完毕'
        getContinueFSRV '登录限制检查完毕'
    }
}
