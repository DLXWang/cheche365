package com.cheche365.cheche.web.integration.flow.step

/**
 * 聚合器
 * Created by liheng on 2018/6/19 0019.
 */
class Aggregator extends AEndpoint {

    /**
     * 自定义
     * @param closure
     */
    Aggregator(Closure closure) {
        super(null, closure, null)
    }

    /**
     * 通用
     * @param correlation                   分组相关性条件/策略，类型：expression、Closure
     * @param release                       完成条件/策略，类型：expression、Closure
     * @param output                        结果处理，类型：expression、Closure
     * @param expireGroupsUponCompletion    完成该组并删除其所有消息
     * @param groupTimeout                  超时时长，单位ms
     */
    Aggregator(correlation, release, output = null, Long groupTimeout = null, boolean expireGroupsUponCompletion = true) {
        super(null, { a ->
            correlation ? (correlation instanceof String ? a.correlationExpression(correlation) : a.correlationStrategy(correlation)) : a
            release ? (release instanceof String ? a.releaseExpression(release) : a.releaseStrategy(release)) : a
            output ? (output instanceof String ? a.outputExpression(output) : a.outputProcessor(output)) : a
            groupTimeout ? a.groupTimeout(groupTimeout) : a
            a.expireGroupsUponCompletion(expireGroupsUponCompletion)
        }, null)
    }

    @Override
    Map<String, Closure> getBuildMapping() {
        [
            (CLOSURE): { endpointConfigurer ? it.aggregate(closure, endpointConfigurer) : it.aggregate(closure) }
        ]
    }
}
