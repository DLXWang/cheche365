package com.cheche365.cheche.externalpayment.handler

import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderOperationInfoService
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalpayment.model.BaseCallbackBody
import groovy.util.logging.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

/**
 * Created by wenling on 2018/3/20.
 */
@Service
@Slf4j
class SyncPurchaseOrderHandler {
    private Logger logger = LoggerFactory.getLogger(SyncPurchaseOrderHandler.class);
    @Autowired
    OrderOperationInfoService orderOperationInfoService

    @Autowired
    CompulsoryInsuranceRepository ciRepo

    @Autowired
    InsuranceRepository iRepo

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository

    @Autowired
    private RedisTemplate redisTemplate


    static final Map CHECHE_STATUS_MAPPING = [
        (OrderStatus.Enum.INSURE_FAILURE_7)  : OrderTransmissionStatus.Enum.UNDERWRITING_FAILED,
        (OrderStatus.Enum.PENDING_PAYMENT_1) : OrderTransmissionStatus.Enum.UNPAID,
        (OrderStatus.Enum.PAID_3)             : OrderTransmissionStatus.Enum.CONFIRM_TO_ORDER,
        (OrderStatus.Enum.CANCELED_6)         : OrderTransmissionStatus.Enum.CANCELED,
        (OrderSubStatus.Enum.FAILED_1)        : OrderTransmissionStatus.Enum.UNCONFIRMED,
        (OrderStatus.Enum.REFUNDING_10)      : OrderTransmissionStatus.Enum.APPLY_FOR_REFUND,
        (OrderStatus.Enum.REFUNDED_9)        : OrderTransmissionStatus.Enum.REFUNDED
    ]

    def syncOrderCenter(PurchaseOrder order){

        if(order.status == OrderStatus.Enum.FINISHED_5){
            return orderOperationInfoService.updatePurchaseOrderStatusForServiceSuccess(order)
        }

        OrderTransmissionStatus orderTransmissionStatus=CHECHE_STATUS_MAPPING.get(order.status) ?: CHECHE_STATUS_MAPPING.get(order.orderSubStatus)
        if(!orderTransmissionStatus){
            log.debug("非预期中需要同步到出单中心的订单状态 ${order.status.description}")
            return
        }

        log.debug("同步出单中心订单状态,web订单状态 ${order.status.description},出单中心状态${orderTransmissionStatus.description}")
        orderOperationInfoService.updateOrderTransmissionStatus(order,orderTransmissionStatus)
    }

    def syncInsurances(Insurance insurance, CompulsoryInsurance compulsoryInsurance){
        if(insurance){
            insurance.updateTime=new Date()
            iRepo.save(insurance);
            log.debug("save insurance success")
        }
        if(compulsoryInsurance){
            compulsoryInsurance.updateTime=new Date()
            ciRepo.save(compulsoryInsurance)
            log.debug("save compulsoryInsurance success")
        }
    }

    def purchaseOrderPaidHandle(Payment payment,boolean isPaid = true ){
        PurchaseOrder purchaseOrder = payment.purchaseOrder
        if(isPaid){
            purchaseOrder.subStatus = null
            purchaseOrder.status = OrderStatus.Enum.FINISHED_5
            logger.info("订单号 ${purchaseOrder.orderNo} 出单成功，订单状态更新为 ${purchaseOrder.status.description}")
        }else{
            purchaseOrder.subStatus = OrderSubStatus.Enum.FAILED_1
            purchaseOrder.status = OrderStatus.Enum.PAID_3
            purchaseOrderRepository.save(purchaseOrder)
            logger.info("订单号 ${purchaseOrder.orderNo} 承保失败，订单状态更新为 ${purchaseOrder.status.description}")
        }

        payment.status = PaymentStatus.Enum.PAYMENTSUCCESS_2
        paymentRepository.save(payment)

        syncOrderCenter(purchaseOrder)
    }

    def syncBillsAndOrderCenter(OrderRelatedService.OrderRelated or, OrderStatus orderStatus, PaymentStatus paymentStatus, BaseCallbackBody callbackBody){
        Payment payment = or.findPending()
        log.info("订单号：{}， 开始更新订单状态，原始状态：[orderStatus:{}, paymentStatus:{}]，目标状态：[orderStatus:{}, paymentStatus:{}]", or.po.orderNo, or.po.status.id, payment?.status?.id, orderStatus.id, paymentStatus.id)

        callbackBody.syncBillNos(or)
        or.po.status = orderStatus
        or.toBePersist << or.po
        if (payment){
            log.info("订单号：{}，没有可更新的payment", or.po.orderNo)
            payment.status = paymentStatus
            or.toBePersist << payment
        }
        or.persist()

        log.info("订单号:{}，更新订单状态完毕，更新后状态 orderStatus:{}, paymentStatus:{}", or.po.orderNo, or.po.status.id, payment?.status?.id)

        syncOrderCenter(or.po)
    }

  Boolean safeSyncBillsAndOrderCenter(OrderRelatedService.OrderRelated or, OrderStatus orderStatus, PaymentStatus paymentStatus, BaseCallbackBody callbackBody){
      boolean locked = getDistributedLock(or.po.orderNo)
      if (locked){
          log.info("成功锁住{}，将进行同步操作", or.po.orderNo)
          syncBillsAndOrderCenter(or, orderStatus, paymentStatus, callbackBody)
          releaseDistributedLock(or.po.orderNo)
          log.info("同步操作完成，释放锁{}", or.po.orderNo)
          return true
      } else {
          log.info("获取锁失败，订单：{}，正在被其他线程处理", or.po.orderNo)
          return false
      }
  }

    private boolean getDistributedLock(String orderNo) {
        boolean locked = redisTemplate.opsForValue().setIfAbsent(getDistributedLockKey(orderNo), orderNo)
        if (locked) {
            redisTemplate.expire(getDistributedLockKey(orderNo), 3, TimeUnit.SECONDS)
        }
        return locked
    }

    private void releaseDistributedLock(String orderNo) {
        redisTemplate.delete(getDistributedLockKey(orderNo))
    }

    private String getDistributedLockKey(String orderNo) {
        return "order:sync:lock:" + orderNo
    }


}
