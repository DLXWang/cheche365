package com.cheche365.cheche.web.integration

/**
 * 相关消息端点{@link com.cheche365.cheche.web.integration.flow.step.Handler}
 * Created by liheng on 2018/5/11 0011.
 */
interface IIntegrationHandler<T> {

    /**
     * @param payload
     * @return
     */
    T handle(T payload)
}
