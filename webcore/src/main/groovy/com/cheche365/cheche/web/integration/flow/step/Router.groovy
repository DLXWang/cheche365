package com.cheche365.cheche.web.integration.flow.step

import static com.cheche365.cheche.web.integration.flow.step.Router.DefaultRoute.DEFAULT_DONE
import static org.springframework.integration.handler.LoggingHandler.Level.WARN

/**
 * 路由
 * Created by liheng on 2018/5/16 0016.
 */
class Router<T> extends AEndpoint {

    static final DEFAULT_ROUTE = 'default' // 若需要流程需要自定义默认路由需使用此key

    private boolean hasDefault
    private Map<Object, Closure> subFlows
    private DefaultRoute defaultRoute // 默认路由

    Router(String expression, Closure endpointConfigurer = null, boolean hasDefault = true, DefaultRoute defaultRoute = DEFAULT_DONE) {
        super(expression, endpointConfigurer)
        this.hasDefault = hasDefault
        this.defaultRoute = defaultRoute
    }

    Router(Closure closure, Closure endpointConfigurer = null, boolean hasDefault = true, DefaultRoute defaultRoute = DEFAULT_DONE) {
        super(T, closure, endpointConfigurer)
        this.hasDefault = hasDefault
        this.defaultRoute = defaultRoute
    }

    Router(String expression, DefaultRoute defaultRoute) {
        this(expression)
        this.defaultRoute = defaultRoute
    }

    Router(Closure closure, DefaultRoute defaultRoute) {
        this(closure)
        this.defaultRoute = defaultRoute
    }

    def routerConfigurer = { m ->
        m.resolutionRequired(!hasDefault)
        def defaultRoute = subFlows.remove DEFAULT_ROUTE
        subFlows.each { key, subFlow -> m.subFlowMapping(key, { f -> subFlow.run([flowBuilder: f]) }) }
        if (hasDefault) {
            m.defaultSubFlowMapping(defaultRoute ? { f -> defaultRoute.run([flowBuilder: f]) } : this.defaultRoute.route)
        }
    }

    @Override
    Map<String, Closure> getBuildMapping() {
        [
            (EXPRESSION): { it.route expression, routerConfigurer, endpointConfigurer },
            (CLOSURE)   : { it.route payloadType, closure, routerConfigurer, endpointConfigurer }
        ]
    }

    void setSubFlows(Map<Object, Closure> subFlows) {
        this.subFlows = subFlows
    }

    enum DefaultRoute {

        DEFAULT_DONE({ f ->
            f.log(WARN, { '没有找到对应路由 messageID：' + it.payload.id })
                .handle(Object.class, { p, h -> })
        }),
        DEFAULT_CONTINUE({ f ->
            f.handle(Object.class, { p, h -> p })
        })

        private Closure route

        DefaultRoute(Closure route) {
            this.route = route
        }
    }
}
