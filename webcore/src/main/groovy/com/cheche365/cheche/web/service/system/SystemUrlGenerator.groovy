package com.cheche365.cheche.web.service.system

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.service.spi.ISystemUrlGenerator
import com.cheche365.cheche.externalapi.api.shorturl.BatchCreateAPI
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service


/**
 * Created by zhengwei on 12/24/15.
 */

@Service
class SystemUrlGenerator implements ISystemUrlGenerator {

    @Autowired
    RedisTemplate redisTemplate
    @Autowired
    PurchaseOrderService poService
    @Autowired
    BatchCreateAPI shortUrlApi

    @Autowired
    OrderImageURL orderImagePage
    @Autowired
    SuspendBillURL suspendBillPage
    @Autowired
    RenewalURL renewalPage
    @Autowired
    QRURL qrPage
    @Autowired
    PartnerPaymentURL partnerPaymentPage

    @Autowired
    @Qualifier('selfPaymentURL')
    SelfPaymentURL selfPaymentPage

    @Autowired
    PartnerOrderDetailURL partnerOrderDetailPage


    String toShortUrl(String longUrl) {
        shortUrlApi.call(longUrl) ?: longUrl
    }

    String toImageUrl(String orderNo) {
        orderImagePage.toServerLink(orderNo)
    }

    String toSuspendBillUrlOriginal(String orderNo) {
        suspendBillPage.toClientPage(orderNo)
    }

    String toQrUrl(Long qrId, Channel channel) {
        qrPage.toClientPage(qrId, channel)
    }

    String toPaymentUrl(String orderNo) {
        String paymentUrlOriginal = toPaymentUrlOriginal(orderNo);
        return toShortUrl(paymentUrlOriginal);
    }


    String toPaymentUrlOriginal(String orderNo) {
        toPaymentUrlOriginal this.poService.findFirstByOrderNo(orderNo)
    }

    String toPaymentUrlOriginal(PurchaseOrder purchaseOrder) {

        Channel channel = purchaseOrder?.sourceChannel
        if (channel && channel.isThirdPartnerChannel()) {
            partnerPaymentPage.toServerLink(purchaseOrder)
        } else {
            selfPaymentPage.toServerLink(purchaseOrder.orderNo)
        }
    }

    String renewalOrder(String orderNo){
        renewalPage.toServerLink(orderNo)
    }

    @Override
    String toOrderDetailUrl(String orderNo) {
        String orderDetailUrlOriginal = toOrderDetailUrlOriginal(orderNo)
        return toShortUrl(orderDetailUrlOriginal)
    }

    String toOrderDetailUrlOriginal(String orderNo){
        PurchaseOrder purchaseOrder = this.poService.findFirstByOrderNo(orderNo)
        partnerOrderDetailPage.toServerLink(purchaseOrder)
    }

}
