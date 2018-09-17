package com.cheche365.cheche.partner.api.kuaiqian

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.repository.OrderStatusRepository
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository
import com.cheche365.cheche.core.service.AmendSyncObject
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.service.QuoteSupplementInfoService
import com.cheche365.cheche.web.service.system.SystemUrlGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_ORDER_CREATE_URL
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey

/**
 * Created by shanxf on 2017/7/20
 */
@Service
class KuaiQianCreate extends KuaiQianApi {

    @Autowired
    SystemUrlGenerator systemUrlGenerator

    @Autowired
    PurchaseOrderService purchaseOrderService

    @Autowired
    QuoteSupplementInfoService supplementInfoService

    @Autowired
    private PurchaseOrderAmendRepository orderAmendRepository

    @Autowired
    private OrderStatusRepository orderStatusRepository

    @Override
    List supportOrderStatus() {
        return [OrderStatus.Enum.PENDING_PAYMENT_1, OrderStatus.Enum.INSURE_FAILURE_7, OrderStatus.Enum.FINISHED_5]
    }

    @Override
    def prepareData(rawData) {
        def converter
        PartnerOrder partnerOrder = getPartnerOrder(rawData)
        AmendSyncObject amendSyncObject = new AmendSyncObject(purchaseOrderService)
        converter = amendSyncObject.convert(partnerOrder, paymentRepository.findByPurchaseOrder(partnerOrder.purchaseOrder))
        def orderAmended = orderAmendRepository.findLatestAmendNotFullRefundNotCancel(partnerOrder.purchaseOrder) as boolean

        if (orderAmended) {
            converter.expireTime = null
        }
        converter.put("payUrl", systemUrlGenerator.toPaymentUrlOriginal(partnerOrder.purchaseOrder.orderNo))
        converter = signParam(partnerOrder, converter)
        return converter
    }

    @Override
    String apiUrl(apiInput) {
        findByPartnerAndKey(apiPartner(apiInput), SYNC_ORDER_CREATE_URL)?.value
    }

}
