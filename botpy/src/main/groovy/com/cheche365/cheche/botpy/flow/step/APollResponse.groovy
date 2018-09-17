package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.common.flow.IStep
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.util.BusinessUtils.getRequestIdForPath
import static com.cheche365.cheche.botpy.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.Method.GET

/**
 * 抽象轮询返回值
 */
@Component
abstract class APollResponse implements IStep {

    @Override
    run(context) {
        def result = null
        pollTimes.times {
            result = sendParamsAndReceive context, apiPath + getRequestIdForPath(context, requestIdPath), [:], GET, log
            if (checkFinished(result)) {
                directive = Closure.DONE
            } else {
                result = null
                sleep pollSleepTime
            }
        }

        if (result) {
            dealResultFsrv context, result
        } else {
            getFatalErrorFSRV "requestId： ${getRequestIdForPath(context, requestIdPath)}，异步回调超时"
        }

    }

    protected getPollTimes() {
        3
    }

    protected getPollSleepTime() {
        2000
    }

    protected checkFinished(result) {
        result.is_done
    }

    abstract protected getApiPath()
    abstract protected getRequestIdPath()
    abstract protected dealResultFsrv(context, result)

}
