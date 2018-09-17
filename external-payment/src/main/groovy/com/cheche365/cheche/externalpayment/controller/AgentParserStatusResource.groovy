package com.cheche365.cheche.externalpayment.controller

import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderAttributeRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import com.cheche365.cheche.externalpayment.service.AgentParserStatusService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.model.AttributeType.Enum.AGENT_PARSER_PICC_PAY_TYPE_4
import static com.cheche365.cheche.core.model.AttributeType.Enum.AGENT_PARSER_PICC_SERIAL_NO_3

/**
 * 以下接口由定时任务调用查询订单状态
 */
@RestController
@RequestMapping("/agentParser/status")
@Slf4j
class AgentParserStatusResource {

    @Autowired
    AgentParserStatusService agentParserStatusService

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    QuoteRecordRepository quoteRecordRepository

    @Autowired
    PurchaseOrderAttributeRepository purchaseOrderAttributeRepository


    @RequestMapping(value = '', method = RequestMethod.POST)
    def queryAndHandleStatus(HttpServletRequest request) {
        List<PurchaseOrder> orders = purchaseOrderRepository.findByStatusAndQuoteSource(OrderStatus.Enum.PENDING_PAYMENT_1, QuoteSource.Enum.AGENTPARSER_9, new Date() - 5)

        orders = filterOrders(orders)

        log.info('接收到定时任务查询小鳄鱼订单支付状态请求，库中待查询查询订单：{}', orders.orderNo)
        List handResults = agentParserStatusService.checkAndHandlePayStatus(orders, request.session.id)

        [
            result:'success',
            message:'批量查询小鳄鱼订单支付状态完毕',
            handResults:handResults
        ]
    }

    private List<PurchaseOrder> filterOrders(List<PurchaseOrder> orders){
        orders.findAll { order ->
            QuoteRecord quoteRecord = quoteRecordRepository.findOne(order.objId)
            List<Payment> payments =paymentRepository.findCustomerPendingPayments(order)
            if (InsuranceCompany.Enum.CPIC_25000 == quoteRecord.insuranceCompany && payments && payments.first().itpNo){
                return true
            }
            if (InsuranceCompany.Enum.PICC_10000 == quoteRecord.insuranceCompany &&
                purchaseOrderAttributeRepository.findByPurchaseOrderAndType(order, AGENT_PARSER_PICC_SERIAL_NO_3)?.value &&
                purchaseOrderAttributeRepository.findByPurchaseOrderAndType(order, AGENT_PARSER_PICC_PAY_TYPE_4)?.value) {
                return true
            }
            log.info('必要信息为空，定时任务忽略此订单支付状态查询，订单号：{}', order.orderNo)
            false
        }
    }

}
