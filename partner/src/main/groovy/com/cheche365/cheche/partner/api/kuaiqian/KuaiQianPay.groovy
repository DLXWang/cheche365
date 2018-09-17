package com.cheche365.cheche.partner.api.kuaiqian

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository
import com.cheche365.cheche.core.repository.PurchaseOrderGiftRepository
import com.cheche365.cheche.core.service.AmendSyncObject
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.partner.serializer.CommonBillsStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_ORDER_CREATE_URL
import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_ORDER_UPDATE_URL
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PAID_3
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.NOTPAYMENT_1
import static com.cheche365.cheche.core.model.PaymentType.Enum.PARTIALREFUND_3

/**
 * Created by shanxf on 2017/7/20
 */
@Service
class KuaiQianPay extends KuaiQianApi {

    @Autowired
    private PurchaseOrderGiftRepository purchaseOrderGiftRepository

    @Autowired
    private PurchaseOrderAmendRepository orderAmendRepository

    @Autowired
    PurchaseOrderService purchaseOrderService

    @Override
    prepareData(Object rawData) {
        def converter
        List<Payment> payments= getPayments(rawData)
        PartnerOrder partnerOrder = getPartnerOrder(rawData)
        if (isPartialRefund(partnerOrder)) {
            converter = new AmendSyncObject(purchaseOrderService).convert(partnerOrder, payments,rawData)
            converter = signParam(partnerOrder, converter)
            return converter
        }

        CommonBillsStatus billsStatus = new CommonBillsStatus()
        converter = billsStatus.generatePartnerOrder(partnerOrder)
        if (OrderStatus.Enum.PAID_3 == partnerOrder.purchaseOrder.status) {
            if (payments) {
                payments.last().with {
                    billsStatus.put("payment", ["id": id, "amount": amount, "status": status, "paymentType": paymentType])
                }
            }
        }
        converter = signParam(partnerOrder, converter)
        return converter
    }


    @Override
    Boolean isPartialRefund(Object partnerOrder) {
        List<Payment> payments = paymentRepository.findByPurchaseOrder(partnerOrder.purchaseOrder)
        (PAID_3 == partnerOrder.purchaseOrder.status) && payments.findAll {
            PARTIALREFUND_3 == it.paymentType && NOTPAYMENT_1 == it.status
        }
    }

    @Override
    List supportOrderStatus() {
        return [OrderStatus.Enum.PAID_3, OrderStatus.Enum.REFUNDING_10, OrderStatus.Enum.REFUNDED_9, OrderStatus.Enum.CANCELED_6]
    }

    @Override
    String apiUrl(apiInput) {
        if (isPartialRefund(apiInput)) {
            return findByPartnerAndKey(apiPartner(apiInput), SYNC_ORDER_CREATE_URL)?.value
        }
        return findByPartnerAndKey(apiPartner(apiInput), SYNC_ORDER_UPDATE_URL)?.value
    }
}
