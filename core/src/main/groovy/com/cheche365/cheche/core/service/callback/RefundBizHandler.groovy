package com.cheche365.cheche.core.service.callback

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.PaymentType.Enum.FULLREFUND_4

@Service
class RefundBizHandler implements IBizHandler{

    @Autowired
    private PaymentRepository paymentRepository
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Override
    boolean handler(Payment payment, boolean isSuccess) {
        if(isSuccess){
            payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
            paymentRepository.save(payment);

            PurchaseOrder purchaseOrder = payment.getPurchaseOrder();
            if(FULLREFUND_4 == payment.paymentType){
                purchaseOrder.setStatus(OrderStatus.Enum.REFUNDED_9);
                purchaseOrder.setUpdateTime(new Date());
                purchaseOrderRepository.save(purchaseOrder);
            }
        }else{
            payment.setStatus(PaymentStatus.Enum.PAYMENTFAILED_3);
            paymentRepository.save(payment);
        }
        return true
    }
}
