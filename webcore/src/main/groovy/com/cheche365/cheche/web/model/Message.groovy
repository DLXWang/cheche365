package com.cheche365.cheche.web.model

import org.apache.commons.lang3.builder.ReflectionToStringBuilder

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static java.util.UUID.randomUUID
import static org.apache.commons.lang3.SerializationUtils.clone
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE

/**
 * Created by liheng on 2018/5/22 0022.
 */
class Message<T> implements Serializable {

    private static final long serialVersionUID = 1L

    private String id
    private Class payloadClassType
    Map<String, Object> headers
    T payload

    Message(T payload) {
        this.id = randomUUID().toString()
        this.payloadClassType = payload.getClass()
        this.payload = payload
        this.headers = [:]
    }

    String getId() {
        return id
    }

    Class getPayloadClassType() {
        return payloadClassType
    }

    Message<T> addHeader(String key, Object value) {
        this.headers << [(key): value]
        this
    }

    Message<T> addHeaders(Map<String, Object> headers) {
        this.headers = mergeMaps this.headers, headers
        this
    }

    Message<T> copyHeaders(Message message) {
        this.headers = mergeMaps this.headers, message.headers
        this
    }

    @Override
    String toString() {
        new ReflectionToStringBuilder(this, SHORT_PREFIX_STYLE)
    }

    Message<T> clone() {
        headers?.isClone ? this : clone(addHeader('isClone', true))
    }
}
