package com.cheche365.cheche.web.integration.transform

import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.web.integration.IIntegrationTransformer
import com.cheche365.cheche.web.model.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.OrderStatus.Enum.CANCELED_6
import static com.cheche365.cheche.core.model.OrderStatus.Enum.FINISHED_5
import static com.cheche365.cheche.core.model.PaymentType.Enum.PARTIALREFUND_3
import static com.cheche365.cheche.web.integration.IntegrationFlows._SYNC_ORDER_CONFIG

/**
 * Created by liheng on 2018/6/21 0021.
 */
@Service
class SyncOrderTransformer implements IIntegrationTransformer<Message, Message<Map>> {

    @Autowired
    private InsuranceRepository insuranceRepository

    @Autowired
    private CompulsoryInsuranceRepository ciRepository

    @Autowired
    private PaymentRepository paymentRepository

    @Override
    Message<Map> transform(Message message) {
        if (PurchaseOrder == message.payloadClassType) {
            def purchaseOrder = (PurchaseOrder) message.payload
            new Message<Map>(CANCELED_6 == purchaseOrder.status ? [
                order: purchaseOrder
            ] : [
                order              : purchaseOrder,
                payments           : paymentRepository.findByPurchaseOrder(purchaseOrder),
                insurance          : insuranceRepository.findByQuoteRecordId(purchaseOrder.objId),
                compulsoryInsurance: ciRepository.findByQuoteRecordId(purchaseOrder.objId)
            ]).copyHeaders message
        } else if (message.payload.order) {
            def payload = message.payload
            def purchaseOrder = (PurchaseOrder) payload.order
            def payment = payload.payments ? payload.payments.sort { a, b -> a.id != b.id ? a.id <=> b.id : a.updateTime <=> b.updateTime }.last() : null
            def (_0, supplementClasses) = _SYNC_ORDER_CONFIG[purchaseOrder.status]
            payload.payments = Payment in supplementClasses || PARTIALREFUND_3 == payment?.paymentType ? paymentRepository.findByPurchaseOrder(purchaseOrder).with { list ->
                list.removeAll { p -> p.id == payment?.id }
                (list << payment) - null
            } : payment ? [payment] : payment
            payload.insurance = Insurance in supplementClasses ? insuranceRepository.findByQuoteRecordId(purchaseOrder.objId) : payload.insurance
            payload.compulsoryInsurance = CompulsoryInsurance in supplementClasses ? ciRepository.findByQuoteRecordId(purchaseOrder.objId) : payload.compulsoryInsurance
            if (FINISHED_5 == purchaseOrder.status && (payload.insurance && !payload.insurance.policyNo || payload.compulsoryInsurance && !payload.compulsoryInsurance.policyNo)) {
                sleep(2 * 60 * 1000)
                payload.insurance = Insurance in supplementClasses ? insuranceRepository.findByQuoteRecordId(purchaseOrder.objId) : payload.insurance
                payload.compulsoryInsurance = CompulsoryInsurance in supplementClasses ? ciRepository.findByQuoteRecordId(purchaseOrder.objId) : payload.compulsoryInsurance
            }
            message.payload = payload
            message
        } else {
            message.payload.order = message.payload.payments.last().purchaseOrder
            message
        }
    }
}
