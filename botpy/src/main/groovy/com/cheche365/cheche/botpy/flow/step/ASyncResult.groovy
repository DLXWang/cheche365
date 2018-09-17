package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.core.service.TPsuedoSync
import groovy.json.JsonBuilder
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.util.BusinessUtils.getNotificationIdForPath
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.core.model.LogType.Enum.BOTPY_56
import static com.cheche365.cheche.parser.util.BusinessUtils.saveAppLog
import static com.cheche365.flow.core.util.FlowUtils.getBusinessTimeoutFSRV


/**
 * 伪同步金斗云回调
 */
@Component
abstract class ASyncResult implements TPsuedoSync {


    private static final _PRE_HANDLER = { notificationId, log ->
        [notificationId, null]
    }

    private static final _POST_HANDLER = { result, tid, log ->
        result
    }

    private static final _ASYNC_RUNNABLE = { tid, log ->
        [tid, null]
    }

    protected syncResult(context, path, type) {
        def notificationId = getNotificationIdForPath(context, getApiPath(context, path))
        log.info '伪同步，等待金斗云异步回调结果，notification_id：{}，保险公司：{}', notificationId, context.insuranceCompany.code

        def result = invokeAndWait _ASYNC_RUNNABLE, [
            timeoutInSeconds: getEnvProperty(context, 'insure_timeout_in_seconds') as long,
            preHandler      : _PRE_HANDLER.curry(notificationId),
            postHandler     : _POST_HANDLER,
            callbackHandler : context.messageHandler,
        ]

        def licensePlateNo = context.auto.licensePlateNo
        saveAppLog(context.logRepo, BOTPY_56, notificationId, context.insuranceCompany?.name, result ? new JsonBuilder(result).toString() : '可能由于超时导致无法获得响应结果', this.class.name, "$licensePlateNo:response")

        log.info '{}的异步回调结果：{}', notificationId, result
        if (result) {
            resolveResult context, result, type
        } else {
            getBusinessTimeoutFSRV([notification_id: notificationId, type: type], "${notificationId} 异步回调超时")
        }
    }

    abstract protected getApiPath(context, path)

    abstract protected resolveResult(context, result, type)
}
