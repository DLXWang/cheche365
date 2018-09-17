package com.cheche365.cheche.externalpayment.handler.botpy.callback.status

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalpayment.handler.SyncPurchaseOrderHandler
import com.cheche365.cheche.externalpayment.model.BotpyBodyRecord
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import static com.cheche365.cheche.externalpayment.model.BotpyCallBackBody.IMAGES_PROPOSAL_STATUS_REDIS_KEY
/**
 * Created by zhengwei on 16/03/2018.
 * 保单状态变更子处理器，每个实例对应一种保单状态
 */

@Service
@Slf4j
abstract class BotpyStatusChangeHandler {

    @Autowired
    SyncPurchaseOrderHandler syncOrderCenterHandler

    @Autowired
    StringRedisTemplate stringRedisTemplate

    abstract boolean support(BotpyBodyRecord record)

    def handle(BotpyBodyRecord record, OrderRelatedService.OrderRelated or) {

        def orderStatus = BotpyCallBackBody.BOTPY_ORDER_STATUS.get(record.proposalStatus())
        if (!orderStatus) {
            log.debug("金斗云回调状态${record.proposalStatus()}, 无对应车车处理逻辑，忽略")
            return
        }

        if(record.isPaidProcessed()){
            log.debug("金斗云投保单 ${record.proposalId()} 回调状态 ${record.proposalStatus()} , 已被支付订单状态回调先行处理，忽略")
            return
        }

        if (!orderStatus.legalCurrentStatus.contains(or.po.status)) {
            log.error("金斗云投保单 ${record.proposalId()} 状态校验失败,  当前订单状态： ${or.po.status.description} 回调状态： ${record.proposalStatus()}")
            return
        }

        stringRedisTemplate.opsForHash().put(IMAGES_PROPOSAL_STATUS_REDIS_KEY, or.po.orderNo, record.proposalStatus())

        if (!orderStatus.toStatus) {
            log.debug("金斗云投保单 ${record.proposalId()} 回调状态${orderStatus.desc}，此状态车车不做处理，车车订单状态${or.po.status.description}")
        } else if (or.po.status == orderStatus.toStatus) {
            log.debug("金斗云投保单 ${record.proposalId()} 回调状态${orderStatus.desc}与车车订单状态${or.po.status.description}一致，不做处理")
        } else {
            log.debug("金斗云投保单 ${record.proposalId()} 回调状态${orderStatus.desc}, 车车订单 ${or.po.orderNo} 订单状态从${or.po.status.description} 更新为 ${orderStatus.toStatus.description}" +
                "statusDisplay 从${or.po.statusDisplay} 更新为 ${orderStatus.statusDisplay}")
            or.po.status = orderStatus.toStatus
            or.po.statusDisplay = orderStatus.statusDisplay
            or.toBePersist << or.po
            record.syncBillNos(or)

            if(orderStatus.paymentStatus){
                Payment payment = or.findPending()
                if (payment){
                    log.debug("支付状态从${ payment.status.description} 更新为 ${orderStatus.paymentStatus.description}")
                    payment.status = orderStatus.paymentStatus
                    or.toBePersist << payment
                } else {
                    log.debug("支付状态为 ${orderStatus.paymentStatus.description}，未找到要处理的payment")
                }

            }

            or.persist()

            if(OrderStatus.Enum.FINISHED_5 == or.po.status){
                record.payStatusCallbackProcess()
            }

            syncOrderCenterHandler.syncOrderCenter(or.po)
        }
    }
}
