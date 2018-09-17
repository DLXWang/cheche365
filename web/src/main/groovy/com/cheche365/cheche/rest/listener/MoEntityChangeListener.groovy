package com.cheche365.cheche.rest.listener

import com.cheche365.cheche.core.model.MoApplicationLog
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent
import org.springframework.stereotype.Component

import static com.cheche365.cheche.core.model.LogType.Enum.Quote_Cache_Record_31
import static com.cheche365.cheche.core.service.listener.EntityChangeListener.sendMessage

/**
 * Created by liheng on 2018/6/7 0007.
 */
@Component
class MoEntityChangeListener extends AbstractMongoEventListener<MoApplicationLog> {

    @Override
    void onAfterSave(AfterSaveEvent event) {
        if (Quote_Cache_Record_31 == event.source.logType) {
            sendMessage event.source, [entityChangeType: 'persist']
        }
    }
}
