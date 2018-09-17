package com.cheche365.cheche.scheduletask.integration

import com.cheche365.cheche.scheduletask.integration.handle.MarketingSuccessHandler
import com.cheche365.cheche.scheduletask.integration.handle.QuotePhotoHandler
import com.cheche365.cheche.scheduletask.integration.handle.QuoteRecordHandler
import com.cheche365.cheche.web.integration.flow.TIntegrationFlows
import com.cheche365.cheche.web.integration.flow.step.Filter
import com.cheche365.cheche.web.integration.flow.step.Handler
import com.cheche365.cheche.web.integration.flow.step.adapter.RedisInBoundChannelAdapter

import static com.cheche365.cheche.web.integration.Constants._SYNC_MARKETING_SUCCESS_QUEUE
import static com.cheche365.cheche.web.integration.Constants._SYNC_MO_APPLICATION_LOG_QUEUE
import static com.cheche365.cheche.web.integration.Constants._SYNC_QUOTE_PHOTO_QUEUE

/**
 * Created by liheng on 2018/6/6 0006.
 */
class ScheduleTaskIntegrationFlows implements TIntegrationFlows {

    Map _STEP_NAME_CLAZZ_MAPPINGS = [
        活动数据进电销消息入站: new RedisInBoundChannelAdapter(_SYNC_MARKETING_SUCCESS_QUEUE),
        报价数据进电销消息入站  : new RedisInBoundChannelAdapter(_SYNC_MO_APPLICATION_LOG_QUEUE),
        拍照报价进电销消息入站: new RedisInBoundChannelAdapter(_SYNC_QUOTE_PHOTO_QUEUE),
        消息过滤       : new Filter({ p -> 'persist' == p.headers.entityChangeType }),
        活动数据进电销    : new Handler(MarketingSuccessHandler),
        报价数据进电销    : new Handler(QuoteRecordHandler),
        拍照报价进电销    : new Handler(QuotePhotoHandler)
    ]

    private final _SYNC_MARKETING_SUCCESS_FLOW = getFlowBuilder().call {
        活动数据进电销消息入站 >> 消息过滤 >> 活动数据进电销
    }

    private final _SYNC_QUOTE_RECORD_FLOW = getFlowBuilder().call {
        报价数据进电销消息入站 >> 消息过滤 >> 报价数据进电销
    }

    private final _SYNC_QUOTE_PHOTO_FLOW = getFlowBuilder().call {
        拍照报价进电销消息入站 >> 消息过滤 >> 拍照报价进电销
    }

    List _FLOWS = [
        _SYNC_MARKETING_SUCCESS_FLOW, _SYNC_QUOTE_RECORD_FLOW, _SYNC_QUOTE_PHOTO_FLOW
    ]
}
