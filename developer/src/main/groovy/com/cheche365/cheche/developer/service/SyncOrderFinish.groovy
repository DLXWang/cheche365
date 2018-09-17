package com.cheche365.cheche.developer.service

import com.cheche365.cheche.core.model.OrderStatus
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

/**
 * @Author shanxf
 * @Date 2018/4/24  20:32
 */
@Service
@Slf4j
class SyncOrderFinish extends SyncOrderProcess {

    @Override
    OrderStatus status() {
        OrderStatus.Enum.FINISHED_5
    }

    @Override
    void handle(String orderNo) {
        log.info("模拟修改订单完成状态，orderNo:{}", orderNo)
        modifyOrderInfo(status(),orderNo)
    }
}
