package com.cheche365.cheche.partner.api.eqiao

import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository
import com.cheche365.cheche.core.service.AmendSyncObject
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.service.QuoteSupplementInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.OrderStatus.Enum.FINISHED_5
import static com.cheche365.cheche.core.model.OrderStatus.Enum.INSURE_FAILURE_7
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PENDING_PAYMENT_1

/**
 * Created by liheng on 2017/7/7 007.
 */
@Service
class EQiaoCreate extends EQiaoApi {

    @Autowired
    PurchaseOrderService purchaseOrderService
    @Autowired
    QuoteSupplementInfoService supplementInfoService
    @Autowired
    private PurchaseOrderAmendRepository orderAmendRepository

    @Override
    List supportOrderStatus() {
        [PENDING_PAYMENT_1, INSURE_FAILURE_7, FINISHED_5]
    }

    @Override
    def prepareData(rawData) {
        def converter
        PartnerOrder partnerOrder = getPartnerOrder(rawData)
        def amendSyncObject = new AmendSyncObject(purchaseOrderService)
        converter = amendSyncObject.convert(partnerOrder, paymentRepository.findByPurchaseOrder(partnerOrder.purchaseOrder))
        converter.uid = ''
        def orderAmended = orderAmendRepository.findLatestAmendNotFullRefundNotCancel(partnerOrder.purchaseOrder) as boolean
        if (orderAmended) {
            converter.expireTime = null
        }
        assembleForm converter
    }

    @Override
    def call(dataMap) {
        PartnerOrder partnerOrder = getPartnerOrder(dataMap)
        if (partnerOrder.state) {
            super.call(dataMap)
        } else {
            log.info '由于没有state信息易桥订单{}不做同步', partnerOrder.purchaseOrder.orderNo
        }
    }
}
