package com.cheche365.cheche.externalpayment.handler.botpy.callback

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import groovy.util.logging.Log4j
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 15/03/2018.
 * 人工核保回调处理器
 */

@Log4j
@Service
class InsureManuallyHandler extends BotpyCallbackHandler {

    @Override
    boolean support(BotpyCallBackBody callBackBody) {
        BotpyCallBackBody.TYPE_INSURE_MANUALLY == callBackBody.type()
    }

    @Override
    def handle(BotpyCallBackBody callBackBody, OrderRelatedService.OrderRelated or) {

        PurchaseOrder purchaseOrder = or.po
        purchaseOrder.status = OrderStatus.Enum.INSURE_FAILURE_7
        purchaseOrder.statusDisplay = '核保中'

        or.toBePersist << purchaseOrder
        or.persist()
        log.debug("金斗云人工核保回调处理,订单号: ${or.po.orderNo}, 订单状态: ${purchaseOrder.status.description}")
    }
}
