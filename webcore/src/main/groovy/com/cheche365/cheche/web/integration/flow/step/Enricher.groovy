package com.cheche365.cheche.web.integration.flow.step

import com.cheche365.cheche.web.model.MessageChannel

import static com.cheche365.cheche.web.integration.flow.step.Enricher.EnrichType.HEADERS
import static com.cheche365.cheche.web.integration.flow.step.Enricher.EnrichType.PAYLOAD

/**
 * 消息增强器
 * Created by liheng on 2018/6/21 0021.
 */
class Enricher extends AEndpoint {

    private static final String ENRICH_HEADERS = 'enrichHeaders'

    private EnrichType enrichType
    private Map<String, Object> properties

    /**
     * 自定义
     * @param closure
     * @param endpointConfigurer
     */
    Enricher(Closure closure, Closure endpointConfigurer = null, EnrichType enrichType = PAYLOAD) {
        super(null, closure, endpointConfigurer)
        this.enrichType = enrichType
    }

    Enricher(Map<String, Object> headers, Closure endpointConfigurer = null) {
        this.properties = headers
        this.endpointConfigurer = endpointConfigurer
        this.enrichType = HEADERS
        this.constructorArgumentType = ENRICH_HEADERS
    }

    Enricher(MessageChannel requestChannel, String name, Closure requestPayload = null, Closure function = { p ->
        p.payload
    }, Closure endpointConfigurer = null, EnrichType enrichType = PAYLOAD) {
        this(requestChannel, name, null, requestPayload, function, endpointConfigurer, enrichType)
    }

    Enricher(MessageChannel requestChannel, String name, Map<String, Object> properties, Closure function = { p -> p.payload }, Closure endpointConfigurer = null, EnrichType enrichType = PAYLOAD) {
        this(requestChannel, name, properties, null, function, endpointConfigurer, enrichType)
    }

    /**
     * 通用
     * @param requestChannel 增强消息请求渠道
     * @param name (spring Message)headers/payload增强字段
     * @param properties 请求参数
     * @param requestPayload 请求参数，同properties，使用时二选一，如：{ m -> m.payload }* @param function 返回增强消息处理器
     * @param endpointConfigurer
     * @param enrichType
     */
    private Enricher(MessageChannel requestChannel, String name, Map<String, Object> properties, Closure requestPayload, Closure function, Closure endpointConfigurer, EnrichType enrichType) {
        super(null, { e ->
            e.requestChannel(requestChannel.channelName)
            properties?.each { key, value -> e.property key, value }
            requestPayload ? e.requestPayload(requestPayload) : e
            PAYLOAD == enrichType ? e.propertyFunction(name, function) : e.headerFunction(name, function)
        }, endpointConfigurer)
        this.enrichType = PAYLOAD
    }

    @Override
    Map<String, Closure> getBuildMapping() {
        [
            (CLOSURE)       : {
                PAYLOAD == enrichType ? it.enrich(closure, endpointConfigurer) : it.enrichHeaders(closure, endpointConfigurer)
            },
            (ENRICH_HEADERS): { it.enrichHeaders properties, endpointConfigurer }
        ]
    }

    enum EnrichType {

        HEADERS, PAYLOAD
    }
}
