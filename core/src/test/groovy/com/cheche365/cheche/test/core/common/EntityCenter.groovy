package com.cheche365.cheche.test.core.common

import com.cheche365.cheche.core.model.Agent
import com.cheche365.cheche.core.model.AgentRebate
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.util.CacheUtil
import net.sf.json.JSONArray

/**
 * Created by zhengwei on 1/9/17.
 */
class EntityCenter {

    static User customer

    static User userAgent

    static QuoteRecord quoteRecord

    static Agent agent

    static AgentRebate agentRebate

    static List<PartnerOrder> partnerOrderList

    static Insurance insurance

    static CompulsoryInsurance compulsoryInsurance

    static PurchaseOrder PO

    static List<Payment> paymentOneAmendList

    static List<Payment> paymentPartialRefundList

    static List<List<Payment>> paymentList

    static List<Payment> allPayments

    static JSONArray expectedResult

    static JSONArray output

    def static CLASS_TO_HANDLER = [
        (User.class)                : { data ->
            customer = data.find { 1 == it.userType.id };
            userAgent = data.find { 2 == it.userType.id }
        },
        (QuoteRecord.class)         : { quoteRecord = it[0] },
        (Agent.class)               : { agent = it[0] },
        (AgentRebate.class)         : { agentRebate = it[0] },
        (PartnerOrder.class)        : { partnerOrderList = it },
        (Insurance.class)           : { insurance = it[0] },
        (CompulsoryInsurance.class) : { compulsoryInsurance = it[0] },
        (Payment.class)             : { allPayments = it }
    ]

    def static OUTPUT_TO_HANDLER = [
        'expectedResult': { expectedResult = it },
        'output'        : { output = it }
    ]

    static {
        CLASS_TO_HANDLER.each { clazz, handler ->
            def dataInJson = EntityCenter.class.getResource("common_data_${clazz.simpleName.toLowerCase()}.json").text
            handler CacheUtil.doListJacksonDeserialize(dataInJson, clazz)
        }
        OUTPUT_TO_HANDLER.each { k, handler ->
            handler JSONArray.fromObject(EntityCenter.class.getResource("common_data_${k}.json").text)
        }

        PO = new PurchaseOrder(
            id: 1,
            applicant: new User(id:1),
            auto: new Auto(id:1),
            objId: 1,
            status: OrderStatus.Enum.PENDING_PAYMENT_1,
            payableAmount: 2300.00,
            paidAmount: 2300.00,
            sourceChannel: Channel.Enum.WE_CHAT_3
        )
    }

    static {
        paymentOneAmendList = allPayments[0..1]
        paymentPartialRefundList = allPayments[0..2]
        paymentList = [paymentOneAmendList, paymentPartialRefundList]
    }
}
