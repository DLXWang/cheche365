package com.cheche365.cheche.externalpayment.controller

import com.cheche365.cheche.core.annotation.ConcurrentApiCall
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.WebPurchaseOrderRepository
import com.cheche365.cheche.externalpayment.service.AnswernPayNotifyService
import com.cheche365.cheche.web.response.RestResponseEnvelope
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import static com.cheche365.cheche.core.exception.BusinessException.Code.OBJECT_NOT_EXIST
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.NOTPAYMENT_1
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.PAYMENTSUCCESS_2
import static org.springframework.http.HttpStatus.OK

/**
 * Created by chenqc on 2016/11/30.
 */
@RestController
@RequestMapping("/ax/pay")
@Slf4j
class AnswernPayResource {

    @Autowired
    private PaymentRepository paymentRepository

    @Autowired
    private AnswernPayNotifyService answernPayNotifyService

    @Autowired
    private WebPurchaseOrderRepository orderRepository


    @ConcurrentApiCall(value = { args -> args.first().orderNo })
    @PostMapping('/notify')
    @ResponseBody
    HttpEntity<RestResponseEnvelope<Object>> notify(@RequestParam Map<String, String> formParams) {
        log.info '接收到安心支付后台异步报文：{}', formParams
        def orderNo = formParams.orderNo
        def payment = paymentRepository.findByOutTradeNo orderNo
        if (!payment) {
            throw new BusinessException(OBJECT_NOT_EXIST, '不存在未支付的记录,交易号：' + orderNo)
        } else if (PAYMENTSUCCESS_2 == payment.status) {
            return new ResponseEntity<>(new RestResponseEnvelope('success'), OK)
        } else if (NOTPAYMENT_1 != payment.status) {
            throw new BusinessException(OBJECT_NOT_EXIST, '交易状态为：' + payment.status.description + '，交易号：' + orderNo)
        }
        if ('0' == formParams.payResult) {
            throw new BusinessException(OBJECT_NOT_EXIST, '订单 ' + payment?.purchaseOrder?.orderNo + ' 支付失败')
        }
        def order = payment.purchaseOrder
        log.info '接收安心支付后台异步报文开始，交易号：{}, 订单号：{}', orderNo, order.orderNo

        answernPayNotifyService.saveOrderAndPayment order, payment, formParams

        if (!formParams.attach) {
            answernPayNotifyService.insuranceNotify formParams, order
        } else {
            answernPayNotifyService.restart payment
        }
        log.info '接收安心支付后台异步报文结束，订单号：{}', order.orderNo
        new ResponseEntity<>(new RestResponseEnvelope('success'), OK)
    }
}
