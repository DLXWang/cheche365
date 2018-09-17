package com.cheche365.cheche.web.integration

import com.cheche365.cheche.web.integration.flow.TIntegrationConstants
import com.cheche365.cheche.web.model.Message
import com.cheche365.cheche.web.model.MessageChannel

import static com.cheche365.cheche.web.model.MessageChannel.ChannelType.QUEUE
import static org.springframework.integration.dsl.core.Pollers.fixedDelay

/**
 * Created by liheng on 2018/5/14 0014.
 */
class Constants implements TIntegrationConstants {

    static final _POLLER_CONFIGURER = { c -> c.poller(fixedDelay(3000)) }

    //<editor-fold defaultstate="collapsed" desc="消息渠道">
    public static final MessageChannel _ENTITY_CHANGE_CHANNEL = new MessageChannel('entityChangeChannel')
    public static final MessageChannel<Message> _SYNC_ORDER_CHANNEL = new MessageChannel<Message>('syncOrderChannel', QUEUE)
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Redis消息队列">
    static final _SYNC_CHANNEL_AGENT_QUEUE = 'SyncChannelAgentQueue'

    static final _SYNC_MARKETING_SUCCESS_QUEUE = 'SyncMarketingSuccessQueue'

    static final _SYNC_QUOTE_PHOTO_QUEUE = 'SyncQuotePhotoQueue'

    static final _SYNC_MO_APPLICATION_LOG_QUEUE = 'SyncMoApplicationLogQueue'

    static final _SYNC_PUSH_QUEUE = 'SyncPushQueue'

    static final _SYNC_PURCHASE_ORDER_QUEUE = 'SyncPurchaseOrderQueue'
    //</editor-fold>
}
