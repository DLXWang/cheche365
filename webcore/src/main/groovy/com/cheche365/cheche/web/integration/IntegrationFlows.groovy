package com.cheche365.cheche.web.integration

import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.MarketingSuccess
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentStatus
import com.cheche365.cheche.core.model.PaymentType
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuotePhoto
import com.cheche365.cheche.core.model.TelMarketingCenterRepeat
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.web.integration.flow.TIntegrationFlows
import com.cheche365.cheche.web.integration.flow.step.Aggregator
import com.cheche365.cheche.web.integration.flow.step.Channels
import com.cheche365.cheche.web.integration.flow.step.Filter
import com.cheche365.cheche.web.integration.flow.step.Handler
import com.cheche365.cheche.web.integration.flow.step.Router
import com.cheche365.cheche.web.integration.flow.step.Transformer
import com.cheche365.cheche.web.integration.flow.step.adapter.RedisOutBoundChannelAdapter
import com.cheche365.cheche.web.integration.flow.step.from.MessageChannelFrom
import com.cheche365.cheche.web.integration.handle.EntityChangeLogHandler
import com.cheche365.cheche.web.integration.handle.SupplementOrderImageHandler
import com.cheche365.cheche.web.integration.transform.SyncOrderTransformer
import com.cheche365.cheche.web.model.Message
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

import static com.cheche365.cheche.core.model.OrderStatus.Enum.CANCELED_6
import static com.cheche365.cheche.core.model.OrderStatus.Enum.FINISHED_5
import static com.cheche365.cheche.core.model.OrderStatus.Enum.INSURE_FAILURE_7
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PAID_3
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PENDING_PAYMENT_1
import static com.cheche365.cheche.core.model.OrderStatus.Enum.REFUNDED_9
import static com.cheche365.cheche.core.model.OrderStatus.Enum.REFUNDING_10
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.NOTPAYMENT_1
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.PAYMENTSUCCESS_2
import static com.cheche365.cheche.core.model.PaymentType.Enum.ADDITIONALPAYMENT_2
import static com.cheche365.cheche.core.model.PaymentType.Enum.FULLREFUND_4
import static com.cheche365.cheche.core.model.PaymentType.Enum.INITIALPAYMENT_1
import static com.cheche365.cheche.core.model.PaymentType.Enum.PARTIALREFUND_3
import static com.cheche365.cheche.web.integration.Constants._ENTITY_CHANGE_CHANNEL
import static com.cheche365.cheche.web.integration.Constants._SYNC_CHANNEL_AGENT_QUEUE
import static com.cheche365.cheche.web.integration.Constants._SYNC_MARKETING_SUCCESS_QUEUE
import static com.cheche365.cheche.web.integration.Constants._SYNC_MO_APPLICATION_LOG_QUEUE
import static com.cheche365.cheche.web.integration.Constants._SYNC_ORDER_CHANNEL
import static com.cheche365.cheche.web.integration.Constants._SYNC_PURCHASE_ORDER_QUEUE
import static com.cheche365.cheche.web.integration.Constants._SYNC_PUSH_QUEUE
import static com.cheche365.cheche.web.integration.Constants._SYNC_QUOTE_PHOTO_QUEUE
import static org.springframework.integration.dsl.channel.MessageChannels.executor

class IntegrationFlows implements TIntegrationFlows {

    //<editor-fold defaultstate="collapsed" desc="订单同步消息聚合相关">
    static final _SYNC_ORDER_CONFIG = [
        (PENDING_PAYMENT_1): [
            { messages -> // 核保失败 >> 等待付款；核保通过 等待付款 >> 等待付款；TODO 增补 出单中 >> 等待付款
                (checkOrderOldStatus(messages, INSURE_FAILURE_7) ||
                    checkOrderOldStatus(messages, null) && checkPayment(messages, NOTPAYMENT_1, INITIALPAYMENT_1) /*||
                    checkOrderOldStatus(messages, PAID_3) && checkPayment(messages, NOTPAYMENT_1, ADDITIONALPAYMENT_2)*/) &&
                    checkInsurance(messages)
            }, [Payment]],
        (PAID_3)           : [
            { messages -> // 等待付款 >> 出单中；TODO 部分退款 取消全额退款
                checkOrderOldStatus(messages, PENDING_PAYMENT_1) && checkPayment(messages, PAYMENTSUCCESS_2) /*||
                    checkOrderOldStatus(messages, null) && checkPayment(messages, NOTPAYMENT_1, PARTIALREFUND_3) &&
                    checkInsurance(messages)*/
            }, null],
        (FINISHED_5)       : [
            { messages -> // 出单中 >> 订单完成；等待付款 >> 订单完成；反录
                checkOrderOldStatus(messages, PAID_3) || checkOrderOldStatus(messages, PENDING_PAYMENT_1) || checkOrderOldStatus(messages, null, 'persist')
            }, [Insurance, CompulsoryInsurance, Payment]],
        (CANCELED_6)       : [{ messages -> true }, null], // 等待付款 >> 订单取消
        (INSURE_FAILURE_7) : [
            { messages -> // 等待付款 >> 核保失败
                checkOrderOldStatus(messages, PENDING_PAYMENT_1) && checkPayment(messages, NOTPAYMENT_1, INITIALPAYMENT_1) &&
                    checkInsurance(messages)
            }, [Payment]],
        (REFUNDED_9)       : [
            { messages -> // 退款中 >> 退款成功
                checkOrderOldStatus(messages, REFUNDING_10) && checkPayment(messages, PAYMENTSUCCESS_2, FULLREFUND_4)
            }, null],
//        (REFUNDING_10)     : [
//            { messages -> // 出单中 >> 退款中 TODO 全额退款处理逻辑和增补在一起
//                checkOrderOldStatus(messages, PAID_3) && checkPayment(messages, NOTPAYMENT_1, FULLREFUND_4)
//            }, null]
    ]

    private final _SYNC_ORDER_RELEASE_STRATEGY = { g ->
        def messages = g.messages.payload.groupBy { it.payloadClassType }
        def poMessage = messages.get(PurchaseOrder)?.with { orders ->
            orders ? orders.sort { it.payload.updateTime }.last() : null
        }
        poMessage && _SYNC_ORDER_CONFIG.get(poMessage.payload.status).with { checkRelease, _1 -> checkRelease messages } /*||
            !poMessage && checkPayment(messages, PAYMENTSUCCESS_2, ADDITIONALPAYMENT_2)*/ // TODO Payment状态变化单独监听，主要监听支付回调，增补支付回调没有订单变化
    }

    private final _SYNC_ORDER_OUTPUT_PROCESSOR = { g ->
        def messages = g.messages.payload.groupBy { it.payloadClassType }
        new Message<Map>([
            order              : messages.get(PurchaseOrder)?.payload?.sort { it.updateTime }?.last(),
            payments           : messages.get(Payment)?.payload,
            insurance          : messages.get(Insurance)?.payload?.sort { it.updateTime }?.last(),
            compulsoryInsurance: messages.get(CompulsoryInsurance)?.payload?.sort { it.updateTime }?.last()
        ]).copyHeaders(g.messages.payload.first())
    }

    private static checkOrderOldStatus(messages, oldStatus, entityChangeType = 'update') {
        messages.get(PurchaseOrder)?.any {
            oldStatus == it.headers.changedFields?.status?.old && entityChangeType == it.headers.entityChangeType
        }
    }

    private static checkPayment(messages, PaymentStatus status, PaymentType type = null) {
        messages.get(Payment)?.payload?.any { status == it.status && (type ? type == it.paymentType : true) }
    }

    private static checkInsurance(messages) {
        def qr = (messages.get(Insurance) ?: messages.get(CompulsoryInsurance))?.payload
            ?.quoteRecord?.with { qrs -> qrs ? qrs.sort { it.updateTime }.last() : null }
        qr && (qr?.compulsoryPremium || qr?.autoTax ? messages.get(CompulsoryInsurance) : true) && (qr?.premium ? messages.get(Insurance) : true)
    }
    //</editor-fold>

    Map _STEP_NAME_CLAZZ_MAPPINGS = [
        实体变化消息来源   : new MessageChannelFrom(_ENTITY_CHANGE_CHANNEL),
        判断实体类型     : new Router('payload.payloadClassType'),
        保存日志       : new Handler(EntityChangeLogHandler),
        代理人消息出站    : new RedisOutBoundChannelAdapter(_SYNC_CHANNEL_AGENT_QUEUE),
        活动营销进电销消息出站: new RedisOutBoundChannelAdapter(_SYNC_MARKETING_SUCCESS_QUEUE),
        拍照报价进电销消息出站: new RedisOutBoundChannelAdapter(_SYNC_QUOTE_PHOTO_QUEUE),
        报价进电销消息出站  : new RedisOutBoundChannelAdapter(_SYNC_MO_APPLICATION_LOG_QUEUE),
        电销推送消息出站   : new RedisOutBoundChannelAdapter(_SYNC_PUSH_QUEUE),
        出单补充影像       : new Handler(SupplementOrderImageHandler),
        订单同步消息来源   : new MessageChannelFrom(_SYNC_ORDER_CHANNEL),
        订单同步通道     : new Channels(_SYNC_ORDER_CHANNEL),
        判断是否需要聚合   : new Router({ it.headers?.metaInfo?.flowId as boolean }),
        聚合订单同步信息   : new Aggregator('payload.headers.metaInfo.flowId', _SYNC_ORDER_RELEASE_STRATEGY, _SYNC_ORDER_OUTPUT_PROCESSOR, 3 * 60 * 1000L),
        过滤订单       : new Filter({ PurchaseOrder == it.payloadClassType }),
        并发渠道       : new Channels(executor(new ThreadPoolTaskExecutor(corePoolSize: 10).with {
            it.initialize(); it
        }).get()),
        补充订单同步信息   : new Transformer(SyncOrderTransformer),
        订单同步消息出站   : new RedisOutBoundChannelAdapter(_SYNC_PURCHASE_ORDER_QUEUE)
    ]

    private final _ENTITY_CHANGE_FLOW = getFlowBuilder().call {
        实体变化消息来源 >> 保存日志 >> 判断实体类型 >> route([
            (ChannelAgent)            : { 代理人消息出站 },
            (MarketingSuccess)        : { 活动营销进电销消息出站 },
            (QuotePhoto)              : { 拍照报价进电销消息出站 },
            (MoApplicationLog)        : { 报价进电销消息出站 },
            (TelMarketingCenterRepeat): { 电销推送消息出站 },
            (PurchaseOrder)           : { 出单补充影像 >> 订单同步通道 },
            (Payment)                 : { 订单同步通道 },
            (Insurance)               : { 订单同步通道 },
            (CompulsoryInsurance)     : { 订单同步通道 }
        ])
    }

    private final _SYNC_ORDER_FLOW = getFlowBuilder().call {
        订单同步消息来源 >> 判断是否需要聚合 >> route([
            (true) : { 聚合订单同步信息 },
            (false): { 过滤订单 }
        ]) >> 并发渠道 >> 补充订单同步信息 >> 订单同步消息出站
    }

    List _FLOWS = [
        _ENTITY_CHANGE_FLOW, _SYNC_ORDER_FLOW
    ]
}
