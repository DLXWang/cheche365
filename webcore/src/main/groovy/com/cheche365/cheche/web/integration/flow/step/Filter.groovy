package com.cheche365.cheche.web.integration.flow.step
/**
 * 过滤器
 * Created by liheng on 2018/5/15 0015.
 */
class Filter<T> extends AEndpoint {

    Filter(String expression, Closure endpointConfigurer = null) {
        super(expression, endpointConfigurer)
    }

    /**
     * closure返回值必须为boolean
     * 例如：{ p -> p > 0 }*/
    Filter(Closure closure, Closure endpointConfigurer = null) {
        super(T, closure, endpointConfigurer)
    }

    Filter(Class serviceType, String methodName = 'filter', Closure endpointConfigurer = null) {
        super(serviceType, methodName, endpointConfigurer)
    }

    @Override
    Map<String, Closure> getBuildMapping() {
        [
            (EXPRESSION): { it.filter expression, endpointConfigurer },
            (CLOSURE)   : { it.filter payloadType, closure, endpointConfigurer },
            (SERVICE)   : { it.filter service, methodName, endpointConfigurer }
        ]
    }
}
