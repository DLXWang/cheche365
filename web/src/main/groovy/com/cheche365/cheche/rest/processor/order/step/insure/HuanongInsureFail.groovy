package com.cheche365.cheche.rest.processor.order.step.insure

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.rest.processor.order.step.TPlaceOrderStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by wen on 2018/8/22.
 */
@Component
@Slf4j
class HuanongInsureFail implements TPlaceOrderStep {

    @Override
    Object run(Object context) {
        Exception e = context.insureFailException
        PurchaseOrder order = context.order
        def persistentState = context.additionalParameters.persistentState

        log.debug("华农debug，订单号:${order.orderNo},errorCode:${e.getCode().codeValue},persistentState:${persistentState}")
        order.orderSourceId = persistentState?.formId

        if(e.getCode().codeValue == 5001){
            order.statusDisplay='核保中'
        }

        return getContinueFSRV(true)
    }
}
