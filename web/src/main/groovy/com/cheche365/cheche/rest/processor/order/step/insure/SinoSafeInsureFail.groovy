package com.cheche365.cheche.rest.processor.order.step.insure

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import com.cheche365.cheche.rest.processor.order.step.TPlaceOrderStep
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by zhengwei on 20/03/2018.
 */

@Component
class SinoSafeInsureFail implements TPlaceOrderStep {

    @Override
    Object run(Object context) {
        Exception e = context.insureFailException
        PurchaseOrder order = context.order
        PurchaseOrderImageService poiService = context.poiService
        def persistentState = context.additionalParameters.persistentState

        if(e.getCode().codeValue == 5001){
            order.statusDisplay='核保中'
        }

        if (e.getCode().codeValue == 2008 && persistentState?.isUpdateImages && persistentState?.updateImagesErrorMessage) {
            poiService.persistCustomImage(order, persistentState.updateImagesErrorMessage)
        }
        return getContinueFSRV(true)
    }
}
