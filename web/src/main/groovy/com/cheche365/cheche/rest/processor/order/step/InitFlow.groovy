package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.service.QuoteConfigService
import com.cheche365.cheche.core.service.QuoteService
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.model.OrderStatus.Enum.INSURE_FAILURE_7

/**
 * Created by zhengwei on 15/01/2018.
 */

@Component
class InitFlow implements TPlaceOrderStep {


    @Override
    Object run(Object context) {

        PurchaseOrder orderInDB
        QuoteRecord quoteRecord
        PurchaseOrder orderFromWeb = context.orderFromWeb
        PurchaseOrderService orderService = context.orderService
        QuoteService quoteService = context.quoteService
        QuoteConfigService quoteConfigService = context.quoteConfigService
        String objId = context.objId

        if("reInsure" == orderFromWeb.getFlow()){
            orderInDB=orderService.findFirstByOrderNo(objId)
            quoteRecord = orderInDB ? quoteService.getById(orderInDB.getObjId()) : null
        }else {
            quoteRecord = quoteService.getById(Long.valueOf(objId))
            orderInDB=orderService.findByQuoteRecordId(quoteRecord.getId())
        }

        if (!quoteRecord) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "报价不存在。")
        }


        context.quoteRecord = quoteRecord
        context.order = orderInDB ?: orderFromWeb

        context.isBotpy = quoteConfigService.isBotpy(quoteRecord)
        context.isAgentParser = quoteConfigService.isAgentParser(quoteRecord)


        return  ("reInsure" == orderFromWeb.getFlow() || INSURE_FAILURE_7 == orderInDB?.status) ? getContinueFSRV("reInsure") : getContinueFSRV("common")
    }
}
