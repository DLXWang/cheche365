package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderAttributeRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalpayment.handler.SyncPurchaseOrderHandler
import com.cheche365.cheche.externalpayment.model.AgentParserCallbackBody
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.AttributeType.Enum.AGENT_PARSER_PICC_PAY_TYPE_4
import static com.cheche365.cheche.core.model.AttributeType.Enum.AGENT_PARSER_PICC_SERIAL_NO_3
import static com.cheche365.cheche.core.model.OrderStatus.Enum.FINISHED_5
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.PAYMENTSUCCESS_2
import static com.cheche365.cheche.core.util.MockUrlUtil.MOCK_BASE_URL
import static com.cheche365.cheche.core.util.MockUrlUtil.findMockSessionAttribute

@Service
@Slf4j
class AgentParserStatusService extends PaymentStatusPollingService{

    @Autowired
    InsuranceRepository insuranceRepository

    @Autowired
    CompulsoryInsuranceRepository compulsoryInsuranceRepository

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository

    @Autowired(required = false)
    List<IThirdPartyPaymentService> thirdPartyPaymentServices

    @Autowired
    OrderRelatedService orderRelatedService

    @Autowired
    SyncPurchaseOrderHandler syncPurchaseOrderHandler

    @Autowired
    QuoteRecordRepository quoteRecordRepository

    @Autowired
    PurchaseOrderAttributeRepository purchaseOrderAttributeRepo


    IThirdPartyPaymentService getPaymentInfoService(InsuranceCompany company, QuoteSource quoteSource){
        thirdPartyPaymentServices.find{
            it.isSuitable([insuranceCompany:company, quoteSource:quoteSource])
        }
    }


    List buildPayStatusParams(List<PurchaseOrder> orders, String clientIdentifier){

        def ordersGroupByCommonInfo = [:]
        orders.collect { order ->
            QuoteRecord quoteRecord = quoteRecordRepository.findOne(order.objId)
            def key = [partnerCode: quoteRecord.channel?.apiPartner?.code, companyId: quoteRecord.insuranceCompany.id, areaId: quoteRecord.area.id]
            if (ordersGroupByCommonInfo[key]) {
                ordersGroupByCommonInfo[key] << order
            } else {
                ordersGroupByCommonInfo << [(key): [order]]
            }
        }
        def queryParamsByGroup = ordersGroupByCommonInfo.collect {commonQrInfo, orderList->
            def params = [
                queryInfoList:[],
                additionalParameters:[(MOCK_BASE_URL): findMockSessionAttribute(clientIdentifier, MOCK_BASE_URL)]
            ]
            orderList.each {PurchaseOrder order ->
                def queryInfo = [
                    commercial: insuranceRepository.findByQuoteRecordId(order.objId)?.proposalNo,
                    compulsory: compulsoryInsuranceRepository.findByQuoteRecordId(order.objId)?.proposalNo,
                    orderNo   : order.orderNo,
                ]
                Payment payment = paymentRepository.findFirstByPurchaseOrder(order)
                queryInfo << [
                    paymentNo: payment.itpNo,
                    serialNo : purchaseOrderAttributeRepo.findByPurchaseOrderAndType(order, AGENT_PARSER_PICC_SERIAL_NO_3)?.value,
                    payType  : purchaseOrderAttributeRepo.findByPurchaseOrderAndType(order, AGENT_PARSER_PICC_PAY_TYPE_4)?.value
                ]

                params.queryInfoList << queryInfo
            }
            QuoteRecord quoteRecord = quoteRecordRepository.findOne(orderList[0].objId)
            params.additionalParameters << [quoteRecord:quoteRecord]
            params << [paymentInfoService: getPaymentInfoService(quoteRecord.insuranceCompany, quoteRecord.type)]
            params
        }
        log.info('小鳄鱼待查询订单分组结果：{}', queryParamsByGroup)
        queryParamsByGroup
    }

    List<AgentParserCallbackBody> checkPayStatus(List<PurchaseOrder> orders, String clientIdentifier) {
        def queryParamsByGroup = buildPayStatusParams(orders, clientIdentifier)
        def statusResults = []
        queryParamsByGroup.each { params ->
            try{
                IThirdPartyPaymentService paymentInfoService = params.paymentInfoService
                if (params && paymentInfoService) {
                    log.info("开始调用parser服务查询小鳄鱼订单支付状态，准备参数：{}", params)
                    def result = paymentInfoService.checkPaymentState(params.queryInfoList, params.additionalParameters)
                    log.info("小鳄鱼订单状态查询结果：{}", result)
                    !result ?: (statusResults += result)
                }else {
                    log.info('必要信息为空，params:{}', params)
                }
            }catch (Exception e){
                log.error('查询小鳄鱼支付状态异常，错误信息:{}', e)
            }
        }
        (statusResults - null).collect { Map result ->
            new AgentParserCallbackBody(result)
        }
    }

    Map handleStatus(AgentParserCallbackBody callbackBody) {
        OrderRelatedService.OrderRelated or = orderRelatedService.initByOrderNo(callbackBody.orderNo())
        log.info("待处理订单，订单号：{}, 原始状态：{}", or.po.orderNo, or.po.status.id)
        if (callbackBody.isSuccess()) {
            def handleSuccess = syncPurchaseOrderHandler.safeSyncBillsAndOrderCenter(or, FINISHED_5, PAYMENTSUCCESS_2, callbackBody)
            if (handleSuccess){
                [result: POLLING_SUCCESS, orderNo: or.po.orderNo, thirdparty:'小鳄鱼', thirdpartyStatus:callbackBody.payStatus(), orderStatus:or.po.status.id]
            } else {
                [result: POLLING_END, orderNo: or.po.orderNo, thirdparty:'小鳄鱼', thirdpartyStatus:callbackBody.payStatus(), orderStatus:or.po.status.id, message:"订单正在被其他线程处理"]
            }
        } else if (callbackBody.isProcessing()) {
            log.info("订单号:{}, 当前订单小鳄鱼正在处理中", or.po.orderNo)
            [result: POLLING_CONTINUE, orderNo: or.po.orderNo, thirdparty:'小鳄鱼', thirdpartyStatus:callbackBody.payStatus(), orderStatus:or.po.status.id]
        } else {
            log.info("订单号:{}, 查询小鳄鱼订单支付状态查询异常，小鳄鱼返回状态：{}", or.po.orderNo, callbackBody.payStatus())
            [result: POLLING_END, orderNo: or.po.orderNo, thirdparty:'小鳄鱼', thirdpartyStatus:callbackBody.payStatus(), orderStatus:or.po.status.id, message:callbackBody.message()]
        }
    }

    List checkAndHandlePayStatus(List<PurchaseOrder> orders, String clientIdentifier = null){
        log.info("小鳄鱼支付状态查询，待查询的订单：{}", orders.orderNo)
        def toQueryOrders = orders.findAll { order ->
            if (order){
                QuoteRecord qr = quoteRecordRepository.findOne(order.objId)
                order && OrderStatus.Enum.PENDING_PAYMENT_1 == order.status && QuoteSource.Enum.AGENTPARSER_9 == qr.getType()
            }
        }
        if (!toQueryOrders){
            log.info("小鳄鱼实际可查询的订单为空，退出查询")
            return [[result: POLLING_END, thirdparty:'小鳄鱼', message:'实际可查询订单为空']]
        }
        List<AgentParserCallbackBody> callbackBodyList = checkPayStatus(toQueryOrders, clientIdentifier)
        callbackBodyList.collect { callbackBody ->
            handleStatus(callbackBody)
        }
    }

    @Override
    boolean support(QuoteSource quoteSource) {
        QuoteSource.Enum.AGENTPARSER_9 == quoteSource
    }

    @Override
    Map checkAndHandlePayStatus(PurchaseOrder order, String clientIdentifier = null) {
        List handleResults = checkAndHandlePayStatus([order], clientIdentifier)
        handleResults ? handleResults.first() : [:]
    }
}
