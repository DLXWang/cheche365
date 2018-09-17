package com.cheche365.cheche.baoxian.flow.step.v2m

import com.cheche365.cheche.baoxian.flow.step.AGetQuoteResult
import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.service.TPsuedoSync
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import groovyx.gpars.dataflow.DataflowVariable
import groovyx.gpars.dataflow.Select

import static com.cheche365.cheche.baoxian.flow.Constants._COMPANY_I2O_MAPPINGS
import static com.cheche365.cheche.baoxian.flow.Constants._TASKID_TTL
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static groovyx.gpars.dataflow.Select.TIMEOUT
import static groovyx.gpars.dataflow.Select.createTimeout
import static java.util.concurrent.TimeUnit.HOURS

/**
 * 并发获取报价结果抽象类
 */
@Slf4j
class SimpleGetConcurrentQuoteResult extends AGetQuoteResult implements IStep, TPsuedoSync {

    //泛华异步回调需要的闭包
    private static final _PRE_HANDLER = { prvIdPrefix, taskId, log ->
        [taskId + '_' + prvIdPrefix, null]
    }

    private static final _POST_HANDLER = { result, tid, log ->
        result
    }

    private static final _ASYNC_RUNNABLE = { tid, log ->
        [tid, null]
    }


    private final INVOKE_AND_WAIT_RESULT = { prvId, context, additionalParameters ->
        def quoteResult = invokeAndWait(_ASYNC_RUNNABLE, [
            timeoutInSeconds: getEnvProperty(context, 'baoxian.quoting_timeout_in_seconds') as long,
            preHandler      : _PRE_HANDLER.curry(prvId[0..3], context.taskId),
            postHandler     : _POST_HANDLER,
            callbackHandler : context.messageHandler,
        ])
        if (quoteResult?.taskState in ['13', '30', '33']) {
            context.globalContext.bindWithTTL(quoteResult.taskId + '_' + prvId[0..3], new JsonBuilder(quoteResult).toString(), _TASKID_TTL, HOURS)
        }
        if (_COMPANY_I2O_MAPPINGS[context.insuranceCompany.id] == prvId[0..3]) {
            additionalParameters.destination << quoteResult
        }
    }

    @Override
    getQuoteResult(context) {
        log.info '泛华并发等待报价回调，taskId：{},等待的公司：{}，本次报价的公司：{}', context.taskId, context.providers, context.insuranceCompany.name
        def pgroup = context.parserTaskPGroup

        def destination = new DataflowVariable()

        def services = context.providers.collect { provider ->
            INVOKE_AND_WAIT_RESULT.curry(provider.prvId, context, [prvId: provider.prvId, destination: destination])
        }

        services.each { service ->
            pgroup.task service
        }


        def alt = new Select(pgroup, destination, createTimeout(1000 * (getEnvProperty(context, 'baoxian.quoting_timeout_in_seconds') as long)))
        alt.select().value.with {
            TIMEOUT == it ? [:] : it
        }
    }
}
