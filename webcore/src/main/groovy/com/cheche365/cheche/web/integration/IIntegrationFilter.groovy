package com.cheche365.cheche.web.integration

/**
 * 相关消息端点{@link com.cheche365.cheche.web.integration.flow.step.Filter}
 * Created by liheng on 2018/5/15 0015.
 */
interface IIntegrationFilter<T> {

    boolean filter(T payload)
}
