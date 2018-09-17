package com.cheche365.cheche.web.integration.flow.step

/**
 * 转换器
 * Created by liheng on 2018/6/18 0018.
 */
class Transformer<T> extends AEndpoint {

    Transformer(String expression, Closure endpointConfigurer = null) {
        super(expression, endpointConfigurer)
    }

    Transformer(Closure closure, Closure endpointConfigurer = null) {
        super(T, closure, endpointConfigurer)
    }

    Transformer(Class serviceType, Closure endpointConfigurer = null, String methodName = 'transform') {
        super(serviceType, methodName, endpointConfigurer)
    }

    @Override
    Map<String, Closure> getBuildMapping() {
        [
            (EXPRESSION): { it.transform expression, endpointConfigurer },
            (CLOSURE)   : { it.transform payloadType, closure, endpointConfigurer },
            (SERVICE)   : { it.transform service, methodName, endpointConfigurer }
        ]
    }
}
