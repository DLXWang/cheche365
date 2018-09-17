package com.cheche365.cheche.developer.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PartnerOrderRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.AmendSyncObject
import com.cheche365.cheche.core.service.PurchaseOrderService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @Author shanxf
 * @Date 2018/4/24  14:46
 */
@Service
@Slf4j
class OrderInfoService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository

    @Autowired
    private PartnerOrderRepository partnerOrderRepository

    @Autowired
    private PaymentRepository paymentRepository

    @Autowired
    private PurchaseOrderService purchaseOrderService

    Object assembleOrderInfo(String orderNo) {
        PurchaseOrder order = purchaseOrderRepository.findFirstByOrderNo(orderNo)
        PartnerOrder partnerOrder = validOrder(order)
        List<Payment> allPayment = paymentRepository.findByPurchaseOrder(order)

        new AmendSyncObject(purchaseOrderService).convert(partnerOrder, allPayment)

    }

    PartnerOrder validOrder(PurchaseOrder purchaseOrder) {
        if (!purchaseOrder) {
            throw new BusinessException(BusinessException.Code.BAD_QUOTE_PARAMETER, "订单不存在")
        }

        PartnerOrder partnerOrder = partnerOrderRepository.findFirstByPurchaseOrderId(purchaseOrder.id)

        if (!partnerOrder) {
            throw new BusinessException(BusinessException.Code.BAD_QUOTE_PARAMETER, "非第三方订单")
        }
        partnerOrder

    }

}
