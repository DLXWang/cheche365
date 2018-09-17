package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.service.OrderAgentService
import com.cheche365.cheche.core.service.OrderOperationInfoService
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.transaction.Transactional

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by zhengwei on 12/21/16.
 */

@Component
@Slf4j
class SyncOrderCenter implements TPlaceOrderStep {

    @Transactional
    @Override
    def run(Object context) {
        PurchaseOrder order = context.order
        OrderAgentService orderAgentService = context.orderAgentService
        OrderOperationInfoService orderOperationInfoService = context.orderOperationInfoService

        orderAgentService.checkAgent(order);
        orderOperationInfoService.saveOrderCenterInfo(order);

        getContinueFSRV true
    }
}
