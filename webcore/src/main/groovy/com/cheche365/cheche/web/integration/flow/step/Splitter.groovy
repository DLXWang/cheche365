package com.cheche365.cheche.web.integration.flow.step

import org.springframework.integration.splitter.AbstractMessageSplitter

/**
 * 拆分器
 * Created by liheng on 2018/6/20 0020.
 */
class Splitter<T> extends AEndpoint {

    Splitter(String beanName, String methodName = 'split', Closure endpointConfigurer = null) {
        super(beanName, methodName, endpointConfigurer)
    }

    Splitter(Class serviceType, String methodName = 'split', Closure endpointConfigurer = null) {
        super(serviceType, methodName, endpointConfigurer)
    }

    Splitter(Closure closure, Closure endpointConfigurer = null) {
        super(T, closure, endpointConfigurer)
    }

    Splitter(AbstractMessageSplitter splitter, Closure endpointConfigurer = null) {
        super(splitter, endpointConfigurer)
    }

    @Override
    Map<String, Closure> getBuildMapping() {
        [
            (HANDLER)  : { it.split handler, endpointConfigurer },
            (CLOSURE)  : { it.split payloadType, closure, endpointConfigurer },
            (BEAN_NAME): { it.split beanName, methodName, endpointConfigurer },
            (SERVICE)  : { it.split service, methodName, endpointConfigurer }
        ]
    }
}
