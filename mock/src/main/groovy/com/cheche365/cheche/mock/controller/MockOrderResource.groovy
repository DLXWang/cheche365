package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.PaymentSerialNumberGenerator
import com.cheche365.cheche.core.service.PurchaseOrderIdService

import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.response.RestResponseEnvelope
import com.cheche365.cheche.core.service.WebPurchaseOrderService

import com.cheche365.cheche.web.counter.annotation.NonProduction
import com.cheche365.cheche.web.service.ZhongAnSignService
import com.cheche365.cheche.wechat.WechatPayService
import com.cheche365.cheche.wechat.payment.OrderPaymentManager
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by wenling on 2018/1/5.
 */
@RestController
@RequestMapping("/v1.6/mock/order")
@Slf4j
public class MockOrderResource extends ContextResource {

    @Autowired
    PurchaseOrderIdService orderIdService
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
    private PurchaseOrderIdService purchaseOrderIdService;
    @Autowired
    ZhongAnSignService zhongAnSignService

    @NonProduction
    @RequestMapping(value = "orderNo")
    createOrderNo() {
        return orderIdService.getNext('T')
    }

    @RequestMapping(value = "/ceb/outTradeNo")
    createOutTradeNo( ){
        purchaseOrderIdService.getCebPayNextSeq()
    }

    @RequestMapping(value="/za/{outTradeNo}", method= RequestMethod.GET)
    HttpEntity<RestResponseEnvelope> proposalStatus(@PathVariable(value = "outTradeNo") String outTradeNo){
        getResponseEntity(zhongAnSignService.isUnsigned(outTradeNo))
    }
}
