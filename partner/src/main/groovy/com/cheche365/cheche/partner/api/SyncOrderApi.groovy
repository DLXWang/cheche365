package com.cheche365.cheche.partner.api

import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.partner.utils.EmailTemplate
import groovy.text.SimpleTemplateEngine
import groovy.util.logging.Slf4j

import java.text.SimpleDateFormat

import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_ORDER_URL
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey
import static com.cheche365.cheche.core.model.LogType.Enum.PARTNER_SYNC_59

/**
 * Created by zhengwei on 7/11/16.
 */
@Slf4j
abstract class SyncOrderApi extends PartnerApi {

    @Override
    ApiPartner apiPartner(partnerOrder) {
        partnerOrder.apiPartner
    }

    @Override
    def apiUrl(apiInput) {
        findByPartnerAndKey(apiPartner(apiInput), SYNC_ORDER_URL)?.value
    }

    abstract List supportOrderStatus()

    @Override
    def genApiInputDetailInfo(apiInput) {
        PurchaseOrder purchaseOrder = apiInput.getPurchaseOrder()
        SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        def bindingOrdinary = [
            l1: purchaseOrder.getOrderNo(),
            l2: purchaseOrder.getStatus().getStatus(),
            l3: purchaseOrder.getType().getName(),
            l4: purchaseOrder.getSourceChannel().getDescription(),
            l5: simpleFormatter.format(purchaseOrder.getCreateTime()),
            l6: ((purchaseOrder.getUpdateTime() == null) ? "" : simpleFormatter.format(purchaseOrder.getUpdateTime()))
        ]
        def engine = new SimpleTemplateEngine()
        def templateOrdinary = engine.createTemplate(EmailTemplate.textOrdinary).make(bindingOrdinary)
        return templateOrdinary.toString()
    }

    PartnerOrder getPartnerOrder(rawData) {
        PartnerOrder partnerOrder
        if (rawData instanceof PartnerOrder) {
            partnerOrder = rawData
        } else {
            partnerOrder = rawData.partnerOrder
        }
        partnerOrder
    }

    @Override
    Map toLogParam(apiInput) {
        [
            prefix    : "${apiInput.purchaseOrder.orderNo} ",
            logType   : PARTNER_SYNC_59,
            objId     : apiInput.purchaseOrder.id,
            objTable  : "purchase_order"
        ]
    }

}
