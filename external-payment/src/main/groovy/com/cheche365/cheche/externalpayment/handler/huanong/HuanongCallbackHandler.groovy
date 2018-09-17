package com.cheche365.cheche.externalpayment.handler.huanong

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.OrderSourceType
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalpayment.model.HuanongCallbackBody
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by wen on 2018/8/7.
 */
@Service
@Slf4j
abstract class HuanongCallbackHandler {

    @Autowired
    OrderRelatedService orService

    abstract boolean support(HuanongCallbackBody body)

    def handle(HuanongCallbackBody body){
        OrderRelatedService.OrderRelated or = orService.initOR { PurchaseOrderRepository poRepo ->
            poRepo.findPurchaseOrderByOrderSourceId(body.orderNo()).with {
                it ? it.first() : null
            }
        }

        if(!or){
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST,"订单${body.orderNo()}不存在")
        }

        or
    }


}
