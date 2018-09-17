package com.cheche365.cheche.web.integration

/**
 * 相关消息端点{@link com.cheche365.cheche.web.integration.flow.step.Transformer}
 * Created by liheng on 2018/6/18 0018.
 */
interface IIntegrationTransformer<T, RT> {

    RT transform(T payload)
}
