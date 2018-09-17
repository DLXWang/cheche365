package com.cheche365.cheche.developer.service

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.OrderTransmissionStatus
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.service.OrderOperationInfoService
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.developer.util.StatusConstants
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * @Author shanxf
 * @Date 2018/4/24  15:49
 */
@Slf4j
abstract class SyncOrderProcess {

    @Autowired
    public OrderOperationInfoService orderOperationInfoService
    @Autowired
    public OrderRelatedService orService

    abstract OrderStatus status()

    abstract void handle(String orderNo)

    @Transactional
    void modifyOrderInfo(OrderStatus orderStatus, String orderNo, PaymentStatus paymentStatus = null) {
        OrderRelatedService.OrderRelated or = orService.initByOrderNo(orderNo);
        or.po.status = orderStatus
        //or.po.statusDisplay = orderStatus.description
        or.toBePersist << or.po
        if(OrderStatus.Enum.FINISHED_5 == orderStatus) {
            log.info("模拟同步保单")
            syncBillNos(or)
        }
        if (paymentStatus) {
            Payment payment = or.findPending()
            if (payment) {
                log.info("支付状态从${payment.status.description} 更新为 ${paymentStatus.description}")
                payment.status = paymentStatus
                or.toBePersist << payment
            } else {
                log.info("支付状态为 ${paymentStatus.description}，未找到要处理的payment")
            }
        }

        or.persist()
        log.info("模拟修改订单状态为，orderNo:{}, status:{}, statusDisplay:{}", or.po.orderNo, or.po.status, or.po.statusDisplay)
        syncOrderCenter(or.po)

        or.po

    }

    def syncOrderCenter(PurchaseOrder order) {
        if (order.status == OrderStatus.Enum.FINISHED_5) {
            return orderOperationInfoService.updatePurchaseOrderStatusForServiceSuccess(order)
        }

        OrderTransmissionStatus orderTransmissionStatus = StatusConstants.CHECHE_STATUS_MAPPING.get(order.status) ?: StatusConstants.CHECHE_STATUS_MAPPING.get(order.orderSubStatus)
        if (!orderTransmissionStatus) {
            log.info("非预期中需要同步到出单中心的订单状态 ${order.status.description}")
            return
        }

        log.info("同步出单中心订单状态,web订单状态 ${order.status.description},出单中心状态${orderTransmissionStatus.description}")
        orderOperationInfoService.updateOrderTransmissionStatus(order, orderTransmissionStatus)
    }

    void syncBillNos(OrderRelatedService.OrderRelated or) {
        if (or.ci) {
            or.ci.policyNo = "cheche123"
            or.ci.proposalNo = "cheche123"
            or.ci.effectiveDate = new Date() + 1
            or.ci.expireDate = new Date() + 365

            or.toBePersist << or.ci
        }

        if (or.insurance) {
            or.insurance.policyNo = "cheche123"
            or.insurance.proposalNo = "cheche123"
            or.insurance.effectiveDate = new Date() + 1
            or.insurance.expireDate = new Date() + 365

            or.toBePersist << or.insurance
        }

    }

}
