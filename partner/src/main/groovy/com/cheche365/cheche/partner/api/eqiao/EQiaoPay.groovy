package com.cheche365.cheche.partner.api.eqiao

import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.service.AmendSyncObject
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.partner.serializer.CommonBillsStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.OrderStatus.Enum.CANCELED_6
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PAID_3
import static com.cheche365.cheche.core.model.OrderStatus.Enum.REFUNDED_9
import static com.cheche365.cheche.core.model.OrderStatus.Enum.REFUNDING_10
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.NOTPAYMENT_1
import static com.cheche365.cheche.core.model.PaymentType.Enum.PARTIALREFUND_3

/**
 * Created by liheng on 2017/7/7 007.
 */
@Service
class EQiaoPay extends EQiaoApi {

    @Autowired
    PurchaseOrderService purchaseOrderService

    @Override
    List supportOrderStatus() {
        [PAID_3, REFUNDING_10, REFUNDED_9, CANCELED_6]
    }

    @Override
    def prepareData(Object rawData) {
        List<Payment> payments= getPayments(rawData)
        PartnerOrder partnerOrder = getPartnerOrder(rawData)
        def partialRefund = (PAID_3 == partnerOrder.purchaseOrder.status) && payments.findAll {
            PARTIALREFUND_3 == it.paymentType && NOTPAYMENT_1 == it.status
        }
        if (partialRefund) {
            return assembleForm(new AmendSyncObject(purchaseOrderService).convert(partnerOrder, payments,rawData))
        }

        def billsStatus = new CommonBillsStatus()
        billsStatus.generatePartnerOrder(partnerOrder)
        billsStatus.uid = ''
        if (PAID_3 == partnerOrder.purchaseOrder.status) {
            if (payments) {
                payments.last().with {
                    billsStatus.put("payment", ["id": id, "amount": amount, "status": status, "paymentType": paymentType])
                }
            }
        }
        assembleForm billsStatus
    }

    @Override
    def call(dataMap) {
        PartnerOrder partnerOrder = getPartnerOrder(dataMap)
        if (partnerOrder.state) {
            super.call(dataMap)
        } else {
            log.info '由于没有state信息易桥订单{}不做同步', partnerOrder?.purchaseOrder.orderNo
        }
    }
}
