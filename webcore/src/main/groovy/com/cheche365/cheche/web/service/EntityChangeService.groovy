package com.cheche365.cheche.web.service

import com.cheche365.cheche.web.model.Message
import org.springframework.stereotype.Service

import static com.cheche365.cheche.web.integration.Constants._ENTITY_CHANGE_CHANNEL

/**
 * Created by liheng on 2018/4/12 0012.
 */
@Service
class EntityChangeService {

    def send(Object obj, Map<String, Object> headerMapping) {
        _ENTITY_CHANGE_CHANNEL.send new Message(obj).addHeaders(headerMapping)
    }
}
