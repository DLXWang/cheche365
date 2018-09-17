package com.cheche365.cheche.core.service.listener

import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.agent.ChannelAgent
import groovy.util.logging.Slf4j
import org.slf4j.MDC

import javax.persistence.PostLoad
import javax.persistence.PostPersist
import javax.persistence.PostUpdate

import static com.cheche365.cheche.common.util.AopUtils._MDC_CONTEXT_NAMES
import static com.cheche365.cheche.common.util.AopUtils.selectFromObject
import static com.cheche365.cheche.core.context.ApplicationContextHolder.getApplicationContext
import static java.util.Calendar.getInstance

/**
 * Created by liheng on 2018/4/12 0012.
 */
@Slf4j
class EntityChangeListener {

    static final ENTITY_CHANGE_LOG_LISTENER_FIELDS_MAPPING = [
        (PurchaseOrder): [status: 'status.status'],
        (Payment)      : [status: 'status.description'],
        (ChannelAgent) : [disable: 'disable']
    ]

    @PostLoad
    void setPrevious(entity) {
        ENTITY_CHANGE_LOG_LISTENER_FIELDS_MAPPING[entity.class].with { listenerFields ->
            if (listenerFields) {
                entity.previous = entity.class.newInstance()
                listenerFields.collect { key, value ->
                    entity.previous[key] = entity[key]
                }
            }
        }
    }

    @PostPersist
    void postPersist(entity) {
        sendMessage entity, [entityChangeType: 'persist']
    }

    @PostUpdate
    void postUpdate(entity) {
        def logMessage = []
        def changedFields = ENTITY_CHANGE_LOG_LISTENER_FIELDS_MAPPING[entity.class]?.findAll {
            entity.previous?."$it.key" != entity."$it.key"
        }?.collectEntries { field, fieldName ->
            logMessage << "${entity.class.simpleName}.${field} 从 ${getFieldByPath(entity.previous, fieldName)} 流转为 ${getFieldByPath(entity, fieldName)}".toString()
            [(field): [old: entity.previous?."${fieldName.split('\\.', 2).first()}", new: entity."${fieldName.split('\\.', 2).first()}"]]
        }
        if (logMessage) {
            log.debug logMessage.join(';')
        }
        sendMessage entity, [entityChangeType: 'update', changedFields: changedFields, logMessage: logMessage]
    }

    static sendMessage(entity, Map<String, Object> headerMapping) {
        if (applicationContext.containsBean('entityChangeService')) {
            applicationContext.getBean('entityChangeService')?.send entity, headerMapping + [metaInfo: selectFromObject(_MDC_CONTEXT_NAMES, MDC) + [ts: instance.time]]
        }
    }

    private static getFieldByPath(entity, path) {
        path.split('\\.').inject(entity) { e, f -> e?."$f" }
    }
}
