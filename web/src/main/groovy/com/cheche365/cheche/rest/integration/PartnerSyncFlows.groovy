package com.cheche365.cheche.rest.integration

import com.cheche365.cheche.rest.integration.handle.AgentRegisterRewardsHandler
import com.cheche365.cheche.rest.integration.handle.CleanAgentSessionHandler
import com.cheche365.cheche.rest.integration.handle.SendSmsHandler
import com.cheche365.cheche.rest.integration.handle.SyncChannelAgentHandler
import com.cheche365.cheche.rest.integration.handle.SyncOrderHandler
import com.cheche365.cheche.web.integration.flow.TIntegrationFlows
import com.cheche365.cheche.web.integration.flow.step.Handler
import com.cheche365.cheche.web.integration.flow.step.Router
import com.cheche365.cheche.web.integration.flow.step.adapter.RedisInBoundChannelAdapter

import static com.cheche365.cheche.web.integration.Constants._SYNC_CHANNEL_AGENT_QUEUE
import static com.cheche365.cheche.web.integration.Constants._SYNC_PURCHASE_ORDER_QUEUE
import static com.cheche365.cheche.web.integration.flow.step.Router.DefaultRoute.DEFAULT_CONTINUE

class PartnerSyncFlows implements TIntegrationFlows {

    Map _STEP_NAME_CLAZZ_MAPPINGS = [
        代理人消息入站  : new RedisInBoundChannelAdapter(_SYNC_CHANNEL_AGENT_QUEUE),
        判断用户是否禁用 : new Router({ p -> p.headers.changedFields?.disable?.new }, DEFAULT_CONTINUE),
        同步代理人信息  : new Handler(SyncChannelAgentHandler),
        清除用户登录信息 : new Handler(CleanAgentSessionHandler),
        判断用户是否新注册: new Router({ p -> 'persist' == p.headers.entityChangeType }),
        新用户注册奖励  : new Handler(AgentRegisterRewardsHandler),
        发送短信     : new Handler(SendSmsHandler),

        订单同步消息入站 : new RedisInBoundChannelAdapter(_SYNC_PURCHASE_ORDER_QUEUE),
        订单同步     : new Handler(SyncOrderHandler)
    ]

    private final _SYNC_CHANNEL_AGENT_FLOW = getFlowBuilder().call {
        代理人消息入站 >> 判断用户是否禁用 >> route([
            (true): { 清除用户登录信息 }
        ]) >> 同步代理人信息 >> 判断用户是否新注册 >> route([
            (true): { 新用户注册奖励 >> 发送短信 }
        ])
    }

    private final _SYNC_PURCHASE_ORDER_FLOW = getFlowBuilder().call {
        订单同步消息入站 >> 订单同步
    }

    List _FLOWS = [
        _SYNC_CHANNEL_AGENT_FLOW, _SYNC_PURCHASE_ORDER_FLOW
    ]
}
