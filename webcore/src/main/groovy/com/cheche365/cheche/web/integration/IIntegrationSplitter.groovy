package com.cheche365.cheche.web.integration

import org.springframework.messaging.Message

/**
 * 相关消息端点{@link com.cheche365.cheche.web.integration.flow.step.Splitter}
 * Created by liheng on 2018/6/20 0020.
 */
interface IIntegrationSplitter<T> {

    List split(Message<T> message)
}
