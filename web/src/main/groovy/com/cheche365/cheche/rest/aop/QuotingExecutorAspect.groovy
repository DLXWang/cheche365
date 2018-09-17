package com.cheche365.cheche.rest.aop

import groovy.util.logging.Slf4j
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.MDC
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.AopUtils._MDC_CONTEXT_NAMES
import static com.cheche365.cheche.common.util.AopUtils._STATS_META_INFO
import static com.cheche365.cheche.common.util.AopUtils._PHASE_ERROR_CODE_SUCCESS
import static com.cheche365.cheche.common.util.AopUtils._PREFIX_QUOTE_PHASE_CODE
import static com.cheche365.cheche.common.util.AopUtils.getMetaInfoJson
import static com.cheche365.cheche.common.util.AopUtils.selectFromObject

@Aspect
@Slf4j(
    category = 'statistics'
)
@Component
class QuotingExecutorAspect {

    /**
     * 切入报价前集成层服务，统计集成服务状态信息
     * @return
     */
    @Before('bean(quotingExecutorService) && execution(* java.util.concurrent.ExecutorService.submit(java.lang.Runnable))')
    static beforeQuoting() {

        def mdcInfo = selectFromObject _MDC_CONTEXT_NAMES, MDC
        MDC.put _STATS_META_INFO,
                getMetaInfoJson(
                    [
                        phase       : _PREFIX_QUOTE_PHASE_CODE,
                        errorCode   : _PHASE_ERROR_CODE_SUCCESS
                    ] + mdcInfo
                )
        log.info '集成层调用报价服务前'
    }

}
