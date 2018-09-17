package com.cheche365.cheche.rest

import com.cheche365.cheche.common.flow.Constants
import com.cheche365.cheche.common.flow.IFlowParticipant
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.rest.model.SPBroadCaster
import com.cheche365.cheche.rest.util.CheckUtil
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import org.slf4j.MDC

import java.util.concurrent.Callable

/**
 * Created by zhengwei on 5/3/15.
 * 异步报价器，保险公司每个保险公司一个线程同时报价，同时支持<a href="https://github.com/Atmosphere/atmosphere">websocket</>实时推送报价阶段和报价结果。
 */

@TupleConstructor
@Slf4j
class AsyncQuoter implements Callable<QuoteRecord> {

    Quoter quoter
    SPBroadCaster spBroadCaster

    @Override
    QuoteRecord call() {

        MDC.contextMap = this.mdcContext

        try {
            quoter.additionalParameters.put(Constants._K_FLOW_PARTICIPANT, new IFlowParticipant() {
                @Override
                void sendMessage(Object msg) {
                    spBroadCaster.quoteStageResult(quoter.quoteRecord, quoter.quoteContext, msg)
                }
            })

            QuoteRecord result = quoter.doQuote()
            if (CheckUtil.quoteable(quoter.quoteRecord.channel, quoter.quoteContext.sessionAttrs)) {
                spBroadCaster.doOnSuccess(result, quoter.additionalParameters)
                spBroadCaster.quoteFinish(result)
            }
            return result

        } catch (Exception ex) {
            if (CheckUtil.quoteable(quoter.quoteRecord.channel, quoter.quoteContext.sessionAttrs)) {
                spBroadCaster.doOnException(quoter.quoteRecord, quoter.additionalParameters, ex)
            }
            return null
        }
    }

}
