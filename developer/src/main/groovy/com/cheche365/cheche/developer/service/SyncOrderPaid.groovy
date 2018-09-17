package com.cheche365.cheche.developer.service

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PaymentStatus
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

/**
 * @Author shanxf
 * @Date 2018/4/24  15:52
 */
@Service
@Slf4j
class SyncOrderPaid extends SyncOrderProcess {

    @Override
    OrderStatus status() {
        OrderStatus.Enum.PAID_3
    }

    @Override
    void handle(String orderNo) {
        log.info("模拟修改为出单中状态，orderNo:{}", orderNo)
        modifyOrderInfo(status(), orderNo, PaymentStatus.Enum.PAYMENTSUCCESS_2)

    }
}
