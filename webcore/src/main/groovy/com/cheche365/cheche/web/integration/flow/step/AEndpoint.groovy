package com.cheche365.cheche.web.integration.flow.step

import com.cheche365.cheche.web.integration.flow.TIntegrationStep
import org.springframework.messaging.MessageHandler

import static com.cheche365.cheche.core.context.ApplicationContextHolder.getApplicationContext

/**
 * 消息端点（Message EndPoint）基类
 * Created by liheng on 2018/5/15 0015.
 */
abstract class AEndpoint implements TIntegrationStep {

    protected static final String EXPRESSION = 'expression'
    protected static final String BEAN_NAME = 'beanName'
    protected static final String SERVICE = 'service'
    protected static final String CLOSURE = 'closure'
    protected static final String HANDLER = 'handler'

    protected String expression
    protected Class payloadType
    protected String beanName
    protected def service
    protected String methodName
    protected Closure closure
    protected Closure endpointConfigurer
    protected String constructorArgumentType
    protected MessageHandler handler

    AEndpoint() {
    }

    AEndpoint(String expression, Closure endpointConfigurer) {
        this.expression = expression
        this.endpointConfigurer = endpointConfigurer
        this.constructorArgumentType = EXPRESSION
    }

    AEndpoint(String beanName, String methodName, Closure endpointConfigurer) {
        this.beanName = beanName
        this.methodName = methodName
        this.endpointConfigurer = endpointConfigurer
        this.constructorArgumentType = BEAN_NAME
    }

    AEndpoint(Class serviceType, String methodName, Closure endpointConfigurer) {
        this.service = applicationContext.getBean serviceType
        this.methodName = methodName
        this.endpointConfigurer = methodName ? endpointConfigurer : null
        this.constructorArgumentType = SERVICE
    }

    AEndpoint(Class payloadType, Closure closure, Closure endpointConfigurer) {
        this.payloadType = payloadType
        this.closure = closure
        this.endpointConfigurer = endpointConfigurer
        this.constructorArgumentType = CLOSURE
    }

    AEndpoint(MessageHandler handler, Closure endpointConfigurer) {
        this.handler = handler
        this.endpointConfigurer = endpointConfigurer
        this.constructorArgumentType = HANDLER
    }

    @Override
    def build(context) {
        buildMapping[constructorArgumentType] context.flowBuilder
    }

    abstract Map<String, Closure> getBuildMapping()
}
