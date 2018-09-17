package com.cheche365.cheche.partner.api.common

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.repository.PurchaseOrderGiftRepository
import com.cheche365.cheche.core.service.AmendSyncObject
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.partner.serializer.CommonBillsStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.OrderStatus.Enum.PAID_3
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.NOTPAYMENT_1
import static com.cheche365.cheche.core.model.PaymentType.Enum.PARTIALREFUND_3

/**
 * Created by mahong on 06/03/2017.
 */
@Service
@Order(value = 1)
class CommonPay extends CommonApi {

    @Autowired
    private PurchaseOrderGiftRepository purchaseOrderGiftRepository

    @Autowired
    PurchaseOrderService purchaseOrderService

    @Override
    prepareData(Object rawData) {
        List<Payment> payments= getPayments(rawData)
        PartnerOrder partnerOrder = getPartnerOrder(rawData)
        Boolean partialRefund = (PAID_3 == partnerOrder.purchaseOrder.status) && payments.findAll {
            PARTIALREFUND_3 == it.paymentType && NOTPAYMENT_1 == it.status
        }
        if (partialRefund && apiPartner(partnerOrder).supportAmend()) {
            return new AmendSyncObject(purchaseOrderService).convert(partnerOrder, payments,rawData)
        }

        CommonBillsStatus billsStatus = new CommonBillsStatus()
        billsStatus.generatePartnerOrder(partnerOrder)
        if (partnerOrder.apiPartner.supportAmend() && OrderStatus.Enum.PAID_3 == partnerOrder.purchaseOrder.status) {
            if (payments) {
                payments.findAll{payment->payment.channel.customerPay}.last().with {
                    billsStatus.put("payment", ["id": id, "amount": amount, "status": status, "paymentType": paymentType])
                }
            }
        }
        billsStatus
    }

    @Override
    List supportOrderStatus() {
        return [OrderStatus.Enum.PAID_3, OrderStatus.Enum.REFUNDING_10, OrderStatus.Enum.REFUNDED_9, OrderStatus.Enum.CANCELED_6]
    }

}
