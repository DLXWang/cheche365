package com.cheche365.cheche.web.integration.flow.step.from

import org.springframework.integration.core.MessageSource

import static com.cheche365.cheche.web.integration.Constants.get_POLLER_CONFIGURER
import static org.springframework.integration.dsl.IntegrationFlows.from

/**
 * 可轮询某一消息源或方法
 * Created by liheng on 2018/6/14 0014.
 */
class MessageSourceFrom extends AMessageFrom {

    private Object service
    private String methodName
    private Closure endpointConfigurer
    private MessageSource messageSource

    /**
     * 目标方法必须为无参方法，可实现{@link com.cheche365.cheche.web.integration.IIntegrationFromHandler}
     * @param service
     * @param methodName
     * @param endpointConfigurer
     */
    MessageSourceFrom(Object service, String methodName, Closure endpointConfigurer = _POLLER_CONFIGURER) {
        this.service = service
        this.methodName = methodName
        this.endpointConfigurer = endpointConfigurer
    }

    MessageSourceFrom(MessageSource messageSource, Closure endpointConfigurer) {
        this.endpointConfigurer = endpointConfigurer
        this.messageSource = messageSource
    }

    @Override
    def from(context) {
        messageSource ? from(messageSource, endpointConfigurer) : from(service, methodName, endpointConfigurer)
    }
}
