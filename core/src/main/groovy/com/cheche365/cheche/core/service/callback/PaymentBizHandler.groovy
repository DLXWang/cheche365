package com.cheche365.cheche.core.service.callback

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentBizHandler implements IBizHandler{

    private final Logger logger = LoggerFactory.getLogger(PaymentBizHandler.class);

    @Autowired
    private PaymentRepository paymentRepository
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Override
    boolean handler(Payment payment,boolean isSuccess) {
        if(isSuccess){
            doOnPaySuccess(payment)
        }else{
            doOnPayFail(payment)
        }
        return true
    }

    private void doOnPaySuccess(Payment payment){
        //修改payment 和 purchaseOrder 状态
        PaymentStatus paymentStatus = PaymentStatus.Enum.PAYMENTSUCCESS_2
        OrderStatus orderStatus = OrderStatus.Enum.PAID_3
        modifyStatus(payment,paymentStatus,orderStatus)
    }

    private void doOnPayFail(Payment payment){
        //修改payment 和 purchaseOrder 状态
        PaymentStatus paymentStatus = PaymentStatus.Enum.PAYMENTFAILED_3
        OrderStatus orderStatus = payment.getPurchaseOrder().getOperator() == null ? OrderStatus.Enum.PENDING_PAYMENT_1 : OrderStatus.Enum.HANDLING_2
        modifyStatus(payment,paymentStatus,orderStatus)
    }

    @Transactional
    private void modifyStatus(Payment payment, PaymentStatus paymentStatus, OrderStatus orderStatus){
        PurchaseOrder purchaseOrder = payment.getPurchaseOrder()
        //update payment
        payment.setStatus(paymentStatus)
        paymentRepository.save(payment)

        //update purchaseOrder
        purchaseOrder.setStatus(orderStatus)
        purchaseOrderRepository.save(purchaseOrder)
    }
}
