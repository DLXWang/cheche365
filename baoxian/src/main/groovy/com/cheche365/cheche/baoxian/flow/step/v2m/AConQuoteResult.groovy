package com.cheche365.cheche.baoxian.flow.step.v2m

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.service.TPsuedoSync
import com.cheche365.flow.core.service.TSimpleConcurrentService
import groovy.json.JsonBuilder
import org.springframework.stereotype.Component

import static com.cheche365.cheche.baoxian.flow.Constants._COMPANY_I2O_MAPPINGS
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.core.model.LogType.Enum.BAOXIAN_35
import static com.cheche365.cheche.parser.util.BusinessUtils.saveAppLog

/**
 * 并发获取报价结果抽象类
 */
@Component
abstract class AConQuoteResult implements IStep, TPsuedoSync, TSimpleConcurrentService {


    private static final _PRE_HANDLER = { prvIdPrefix, context, log ->
        [context.taskId + '_' + prvIdPrefix, null]
    }

    private static final _POST_HANDLER = { result, tid, log ->
        result
    }

    private static final _ASYNC_RUNNABLE = { tid, log ->
        [tid, null]
    }

    private static final _JOB_BLUEPRINT_QUOTE = { company, service, context ->
        def result = service.call()
        [company, result]
    }

    def invokeAndWaitResult(company, context) {
        def prvIdPrefix = _COMPANY_I2O_MAPPINGS[company.id]
        invokeAndWait(_ASYNC_RUNNABLE, [
            timeoutInSeconds: getEnvProperty(context, 'baoxian.quoting_timeout_in_seconds') as long,
            preHandler      : _PRE_HANDLER.curry(prvIdPrefix, context),
            postHandler     : _POST_HANDLER,
            callbackHandler : context.messageHandler,
        ])
    }

    @Override
    run(context) {

        log.info '伪同步，等待泛华报价异步回调结果，taskId：{}，保险公司：{}', context.taskId, context.insuranceCompany.code

        def priorityMappings = context.insuranceCompany.withIndex().collectEntries { insuranceCompany, index ->
            [(insuranceCompany): index]
        }
        def serviceConfig = [
            0L     : [priorityMappings: priorityMappings],
            default: [options: [
                timeout       : 250L,
                maxResultCount: context.insuranceCompany.size()
            ]]
        ]
        def services = context.insuranceCompany.collectEntries { ic ->
            [(ic): {
                delegate.invokeAndWaitResult(ic, context)
            }]
        }

        // 并发获取异步回调报价结果
        context.quoteRecordMappings = service(
            services, serviceConfig, _JOB_BLUEPRINT_QUOTE, [context], context.parserTaskPGroup
        ).with { results ->
            context.insuranceCompany.collectEntries { ic ->
                def result = results.find { newIC, _ ->  newIC == ic }.last()
                log.info '{}, {}的报价回调结果：{}', ic.code, context.taskId, result
                def licensePlateNo = context.auto.licensePlateNo
                saveAppLog(context.logRepo, BAOXIAN_35, context.taskId, ic.name, result ? new JsonBuilder(result).toString() : '可能由于超时导致无法获得响应结果', this.class.name, "$licensePlateNo:response")

                if (result) {
                    context.accurateInsurancePackage = context.insurancePackage.clone()
                    context.insuranceCompany = ic
                    def fsrv = resolveResult(context, result)
                    [(ic): [code: 0, quoteRecord: context.newQuoteRecord, fsrv: fsrv]]
                } else {
                    def fsrv = getFatalErrorFSRV("${context.taskId}， 异步回调超时")
                    [(ic): [code: -1, quoteRecord: null, fsrv: fsrv]]
                }
            }
        }

        getContinueFSRV('报价成功')

    }

    abstract def resolveResult(context, result)
}
