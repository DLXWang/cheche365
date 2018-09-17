package com.cheche365.cheche.web.integration.handle

import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.OrderOperationInfo
import com.cheche365.cheche.core.model.OrderProcessHistory
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.model.PartnerOrderSync
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.PurchaseOrderAmend
import com.cheche365.cheche.core.model.PurchaseOrderHistory
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.web.integration.IIntegrationHandler
import com.cheche365.cheche.web.model.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service

import static com.cheche365.cheche.web.integration.handle.EntityChangeLogHandler.Index.orderNo
import static com.cheche365.cheche.web.integration.handle.EntityChangeLogHandler.Index.outTradeNo
import static com.cheche365.cheche.web.integration.handle.EntityChangeLogHandler.Index.partnerOrder
import static com.cheche365.cheche.web.integration.handle.EntityChangeLogHandler.Index.purchaseOrder
import static com.cheche365.cheche.web.integration.handle.EntityChangeLogHandler.Index.quoteRecord

/**
 * Created by liheng on 2018/5/24 0024.
 */
@Service
class EntityChangeLogHandler implements IIntegrationHandler<Message> {

    static final ENTITY_CHANGE_LOG = 'entity_change_log'

    static final ENTITY_CHANGE_LOG_RECORD_FIELDS_MAPPING = [
        (QuoteRecord)         : ['insurancePackage.id', 'premium', 'discount', 'type.description', 'quoteFlowType.name', 'channel.description'],
        (PurchaseOrder)       : ['status.status', 'subStatus.name', 'payableAmount', 'paidAmount', 'channel.description', 'sourceChannel.description'],
        (Insurance)           : ['proposalNo', 'policyNo'],
        (CompulsoryInsurance) : ['proposalNo', 'policyNo'],
        (Payment)             : ['id', 'amount', 'status.description', 'outTradeNo', 'paymentType.description'],
        (PartnerOrder)        : ['ApiPartner.code', 'state'],
        (PartnerOrderSync)    : ['status', 'receiveSyncMessage', 'purchaseOrderStatus.status'],
        (OrderOperationInfo)  : ['originalStatus.status', 'currentStatus.status'],
        (OrderProcessHistory) : ['originalStatus', 'currentStatus', 'orderProcessType.name'],
        (PurchaseOrderAmend)  : ['paymentType.name', 'purchaseOrderAmendStatus.name'],
        (PurchaseOrderHistory): ['operationType.name']
    ]

    static final ENTITY_CHANGE_LOG_INDEXES_MAPPING = [
        (QuoteRecord)         : [(quoteRecord): 'id'],
        (PurchaseOrder)       : [(purchaseOrder): 'id', (orderNo): 'orderNo', (quoteRecord): 'objId'],
        (Insurance)           : [(quoteRecord): 'quoteRecord.id'],
        (CompulsoryInsurance) : [(quoteRecord): 'quoteRecord.id'],
        (Payment)             : [(purchaseOrder): 'purchaseOrder.id', (outTradeNo): 'outTradeNo'],
        (PartnerOrder)        : [(purchaseOrder): 'purchaseOrder.id', (partnerOrder): 'id'],
        (PartnerOrderSync)    : [(partnerOrder): 'partnerOrder.id'],
        (OrderOperationInfo)  : [(purchaseOrder): 'purchaseOrder.id'],
        (OrderProcessHistory) : [(purchaseOrder): 'purchaseOrder.id'],
        (PurchaseOrderAmend)  : [(purchaseOrder): 'purchaseOrder.id'],
        (PurchaseOrderHistory): [(purchaseOrder): 'purchaseOrder.id']
    ]

    @Autowired
    private MongoTemplate template

    static getFieldByPath(entity, path) {
        path.split('\\.').inject(entity) { e, f -> e?."$f" }
    }

    enum Index {

        quoteRecord, purchaseOrder, orderNo, outTradeNo, partnerOrder
    }

    @Override
    Message handle(Message message) {
        if (ENTITY_CHANGE_LOG_RECORD_FIELDS_MAPPING[message.payloadClassType]) {
            template.save message.headers.metaInfo + [
                entityChangeType: message.headers.entityChangeType,
                logMessage      : message.headers.logMessage,
                entityClassType : message.payloadClassType.name,
                fields          : ENTITY_CHANGE_LOG_RECORD_FIELDS_MAPPING[message.payloadClassType]?.collectEntries { field ->
                    [(field.split('\\.').first()): getFieldByPath(message.payload, field)]
                },
                changedFields   : message.headers.changedFields,
                indexes         : ENTITY_CHANGE_LOG_INDEXES_MAPPING[message.payloadClassType]?.collectEntries { key, value ->
                    [(key): getFieldByPath(message.payload, value)]
                }
            ], ENTITY_CHANGE_LOG
        }
        message
    }
}
