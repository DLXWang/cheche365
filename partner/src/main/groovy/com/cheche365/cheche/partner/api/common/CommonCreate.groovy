package com.cheche365.cheche.partner.api.common

import com.cheche365.cheche.core.model.InsurancePurchaseOrderRebate
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentOrderRebateRepository
import com.cheche365.cheche.core.service.AmendSyncObject
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.service.QuoteSupplementInfoService
import com.cheche365.cheche.partner.serializer.CommonBills
import com.cheche365.cheche.web.service.InsurancePurchaseOrderRebateService
import com.cheche365.cheche.web.service.system.SystemUrlGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

/**
 * Created by mahong on 06/03/2017.
 */
@Service
@Order(value = 1)
class CommonCreate extends CommonApi {

    @Autowired
    SystemUrlGenerator systemUrlGenerator

    @Autowired
    PurchaseOrderService purchaseOrderService

    @Autowired
    QuoteSupplementInfoService supplementInfoService

    @Autowired
    InsurancePurchaseOrderRebateService insurancePurchaseOrderRebateService

    @Autowired
    ChannelAgentOrderRebateRepository channelAgentOrderRebateRepository

    @Autowired
    private PurchaseOrderAmendRepository orderAmendRepository

    def prepareData(rawData) {
        def converter
        def orderAmended
        if (apiPartner(rawData).supportAmend()) {
            AmendSyncObject amendSyncObject = new AmendSyncObject(purchaseOrderService)
            sleep(3000) //异步导致增补同步第三方状态错误，所以查询payments前休眠3秒
            converter = amendSyncObject.convert(rawData, paymentRepository.findByPurchaseOrder(rawData.purchaseOrder))
            orderAmended = orderAmendRepository.findLatestAmendNotFullRefundNotCancel(rawData.purchaseOrder) as boolean
            if (orderAmended) {
                converter.expireTime = null
            }

        } else {
            CommonBills commonBills = new CommonBills(purchaseOrderService, supplementInfoService)
            converter = commonBills.convert(rawData)
        }

        converter.put("payUrl", systemUrlGenerator.toPaymentUrlOriginal(rawData.purchaseOrder.orderNo))

        if (apiPartner(rawData).needSyncRebate() && rawData.purchaseOrder.status == OrderStatus.Enum.FINISHED_5) {
            sleep(3000) // 查库前休眠3秒
            InsurancePurchaseOrderRebate insurancePurchaseOrderRebate = insurancePurchaseOrderRebateService.findByPurchaseOrder(rawData.purchaseOrder)
            def commissionMap = ["commercialAmount": insurancePurchaseOrderRebate ? insurancePurchaseOrderRebate.upCommercialAmount : 0,
                                 "compulsoryAmount": insurancePurchaseOrderRebate ? insurancePurchaseOrderRebate.upCompulsoryAmount : 0]
            converter.put("commission", commissionMap)
        }
        return converter
    }

    def prepareDataNew(rawData) {
        def converter
        PartnerOrder partnerOrder = rawData.partnerOrder
        def purchaseOrder = partnerOrder.purchaseOrder
        if (apiPartner(partnerOrder).supportAmend()) {
            converter = new AmendSyncObject(purchaseOrderService).convert(partnerOrder, rawData.payments, rawData)
            sleep(3000) // 查库前休眠3秒
            if (orderAmendRepository.findLatestAmendNotFullRefundNotCancel(purchaseOrder)) {
                converter.expireTime = null
            }
        } else {
            CommonBills commonBills = new CommonBills(purchaseOrderService, supplementInfoService)
            converter = commonBills.convert(partnerOrder, rawData)
        }

        converter.payUrl = systemUrlGenerator.toPaymentUrlOriginal(purchaseOrder)

        if (apiPartner(partnerOrder).needSyncRebate() && purchaseOrder.status == OrderStatus.Enum.FINISHED_5) {
            sleep(3000) // 查库前休眠3秒
            def insurancePurchaseOrderRebate = insurancePurchaseOrderRebateService.findByPurchaseOrder(purchaseOrder)
            converter.commission = [commercialAmount: insurancePurchaseOrderRebate ? insurancePurchaseOrderRebate.upCommercialAmount : 0,
                                    compulsoryAmount: insurancePurchaseOrderRebate ? insurancePurchaseOrderRebate.upCompulsoryAmount : 0]
        }
        if (purchaseOrder.sourceChannel.levelAgent && purchaseOrder.status == OrderStatus.Enum.FINISHED_5) {
            sleep(3000) // 查库前休眠3秒
            converter.commission = channelAgentOrderRebateRepository.findAllByPurchaseOrder(purchaseOrder).collect {
                [agentId         : it.channelAgent.id, commercialRebate: it.commercialRebate, commercialAmount: it.commercialAmount,
                 compulsoryRebate: it.compulsoryRebate, compulsoryAmount: it.compulsoryAmount]
            }
        }
        converter
    }

    @Override
    List supportOrderStatus() {
        return [OrderStatus.Enum.PENDING_PAYMENT_1, OrderStatus.Enum.INSURE_FAILURE_7, OrderStatus.Enum.FINISHED_5]
    }

}
