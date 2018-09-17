package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.core.service.PaymentSerialNumberGenerator
import com.cheche365.cheche.externalpayment.service.AgentParserPayChannelService
import com.cheche365.cheche.core.service.WebPurchaseOrderService
import com.cheche365.cheche.web.counter.annotation.NonProduction
import com.cheche365.cheche.web.response.RestResponseEnvelope
import com.cheche365.cheche.wechat.TradeType
import com.cheche365.cheche.wechat.WechatPayService
import com.cheche365.cheche.wechat.message.UnifiedOrderRequest
import com.cheche365.cheche.wechat.payment.OrderPaymentManager
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import static com.cheche365.cheche.core.model.Channel.Enum.WAP_8

/**
 * Created by taichangwei on 2017/6/26.
 */
@RestController
@RequestMapping("/v1.6/mock/pay")
@Slf4j
public class MockPayResource {

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository
    @Autowired
    OrderPaymentManager orderPaymentManager
    @Autowired
    WechatPayService wechatPayService
    @Autowired
    WebPurchaseOrderService poService
    @Autowired
    PaymentSerialNumberGenerator serialNumberGenerator
    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    AgentParserPayChannelService agentParserPayChannelService

    @Autowired
    OrderRelatedService orderRelatedService


    @NonProduction
    @RequestMapping(value = "initWechatPrePayParams")
    toUnifiedOrderRequest(@RequestParam(value = "orderNo") String orderNo){

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(orderNo)
        User user = purchaseOrder.getApplicant()
        Double amount = purchaseOrder.getPaidAmount()
        Channel channel = purchaseOrder.getSourceChannel()
        TradeType tradeType
        switch (channel) {
            case Channel.selfApp():
                tradeType = TradeType.APP
                break
            case WAP_8:
                tradeType = TradeType.MWEB
                break
            default:
                tradeType = TradeType.JSAPI
        }

        UnifiedOrderRequest unifiedOrderRequest = orderPaymentManager.toUnifiedOrderRequest(orderNo+"Z0011",user,"车险服务订单",amount,tradeType,channel)

        new ResponseEntity<>(new RestResponseEnvelope<>(unifiedOrderRequest.toXmlString()), HttpStatus.OK)
    }


    @NonProduction
    @RequestMapping(value = "queryPayment")
    queryPayment(@RequestParam(value = 'paymentId')  Long paymentId){
        paymentRepository.findOne(paymentId)
    }

}
