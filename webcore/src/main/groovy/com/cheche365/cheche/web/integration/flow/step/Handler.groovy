package com.cheche365.cheche.web.integration.flow.step

import org.springframework.messaging.MessageHandler

import static com.cheche365.cheche.web.integration.Constants._POLLER_CONFIGURER
import static org.apache.commons.lang3.SerializationUtils.clone
import static org.springframework.integration.dsl.channel.MessageChannels.queue

/**
 * Service Activator消息处理器
 *
 * handle返回值即发往下一个消息通道payload值
 * return null 可以结束消息流程
 * 如果handle为流程最后一个步骤需return null
 *
 * Created by liheng on 2018/5/15 0015.
 */
class Handler<T> extends AEndpoint {

    private Boolean isAsync = false

    /**
     * 例如：
     * { p, h -> println '测试闭包:' + p;p }
     * { p -> println '测试闭包:' + p;p }
     */
    Handler(Closure closure, Closure endpointConfigurer = null, Boolean isAsync = false) {
        super(T, closure, endpointConfigurer)
        this.isAsync = isAsync
    }

    Handler(String beanName, String methodName = 'handle', Closure endpointConfigurer = null, Boolean isAsync = false) {
        super(beanName, methodName, endpointConfigurer)
        this.isAsync = isAsync
    }

    Handler(Class serviceType, String methodName = 'handle', Closure endpointConfigurer = null, Boolean isAsync = false) {
        super(serviceType, methodName, endpointConfigurer)
        this.isAsync = isAsync
    }

    Handler(MessageHandler handler, Closure endpointConfigurer = null) {
        super(handler, endpointConfigurer)
    }

    private BASE_HANDLE = { _1, _2, _3, flow ->
        if (isAsync) {
            flow.transform(Object, { p -> clone(p) }).channel(queue())
        }
        flow.handle _1, _2, _3 ?: isAsync ? _POLLER_CONFIGURER : null
    }

    @Override
    Map<String, Closure> getBuildMapping() {
        [
            (HANDLER)  : { it.handle handler, endpointConfigurer },
            (CLOSURE)  : BASE_HANDLE.curry(payloadType, closure, endpointConfigurer),
            (BEAN_NAME): BASE_HANDLE.curry(beanName, methodName, endpointConfigurer),
            (SERVICE)  : BASE_HANDLE.curry(service, methodName, endpointConfigurer)
        ]
    }
}
