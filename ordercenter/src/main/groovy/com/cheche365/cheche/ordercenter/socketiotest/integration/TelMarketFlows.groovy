package com.cheche365.cheche.ordercenter.socketiotest.integration

import com.cheche365.cheche.web.integration.flow.TIntegrationFlows
import com.cheche365.cheche.web.integration.flow.step.Filter
import com.cheche365.cheche.web.integration.flow.step.Handler
import com.cheche365.cheche.web.integration.flow.step.adapter.RedisInBoundChannelAdapter

import static com.cheche365.cheche.web.integration.Constants._SYNC_PUSH_QUEUE

class TelMarketFlows implements TIntegrationFlows {

    Map _STEP_NAME_CLAZZ_MAPPINGS = [
        电销推送消息入站: new RedisInBoundChannelAdapter(_SYNC_PUSH_QUEUE),
        消息过滤    : new Filter({ p -> 'persist' == p.headers.entityChangeType }),
        同步信息    : new Handler(TelMarketPushHandler)
    ]

    private final _SYNC_PUSH_SUCCESS_FLOW = getFlowBuilder().call {
        电销推送消息入站 >> 消息过滤 >> 同步信息
    }


    List _FLOWS = [
        _SYNC_PUSH_SUCCESS_FLOW
    ]
}

