package com.cheche365.cheche.web.integration

/**
 * 相关消息端点{@link com.cheche365.cheche.web.integration.flow.step.from.MessageSourceFrom}
 * Created by liheng on 2018/6/15 0015.
 */
interface IIntegrationFromHandler<T> {

    T handle()
}
