package com.cheche365.cheche.web.service.order

import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.OrderRelatedService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @Author shanxf
 * @Date 2018/4/24  10:49
 */
@Service
@Slf4j
class ModifyOrderService {

    @Autowired
    private OrderRelatedService orService
    @Autowired
    private PaymentRepository paymentRepository

    Map modifyPayAmount(String orderNo){

        log.debug('修改支付金额接口，当前需要修改的订单号为 {}', orderNo)
        OrderRelatedService.OrderRelated or = orService.initByOrderNo(orderNo)
        Payment payment = or.findPending()
        if (!payment)
            return ['message' : '该订单不存在!']
        log.debug('修改订单金额前的金额：{}', payment.amount)

        payment.with {
            def result = [
                message : '修改成功!',
                originalAmount : payment.amount
            ]
            payment.amount = 0.01
            this.paymentRepository.save(payment)
            result << [currentAmount: payment.amount]
            result
        }
    }

}
