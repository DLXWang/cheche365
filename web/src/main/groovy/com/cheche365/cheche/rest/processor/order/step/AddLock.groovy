package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.web.service.order.PurchaseOrderLockService
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by wen on 2018/8/3.
 */
@Component
@Slf4j
class AddLock implements TPlaceOrderStep {

    @Override
    Object run(Object context) {
        PurchaseOrder order = context.order
        PurchaseOrderLockService lockService = context.lockService

        if(context.isBotpy || context.isAgentParser){
            log.debug("{} 开始添加订单锁 " , order.orderNo)
            lockService.lock(order.orderNo)
        }

        return getContinueFSRV(true)

    }
}
