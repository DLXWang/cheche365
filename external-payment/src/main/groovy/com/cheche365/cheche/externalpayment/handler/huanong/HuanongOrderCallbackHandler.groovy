package com.cheche365.cheche.externalpayment.handler.huanong

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PaymentStatus
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import com.cheche365.cheche.externalpayment.handler.SyncPurchaseOrderHandler
import com.cheche365.cheche.externalpayment.model.HuanongCallbackBody
import com.pingplusplus.model.Order
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.externalpayment.model.HuanongCallbackBody.HUANONG_UNDERWRITE_STATUS

/**
 * Created by wen on 2018/8/7.
 */
@Service
@Slf4j
class HuanongOrderCallbackHandler extends HuanongCallbackHandler{

    @Autowired
    private PurchaseOrderImageService poiService

    @Autowired
    SyncPurchaseOrderHandler syncPurchaseOrderHandler

    @Override
    boolean support(HuanongCallbackBody body) {
        body.isOrder()
    }

    @Override
    def handle(HuanongCallbackBody body){

        OrderRelatedService.OrderRelated or = super.handle(body)

        if(OrderStatus.Enum.FINISHED_5 == or.po.status){
            log.debug("订单号 ${or.po.orderNo} 已出单，忽略")
            return
        }

        if(body.isOrderSuccess()){
            syncPurchaseOrderHandler.syncBillsAndOrderCenter(or,OrderStatus.Enum.FINISHED_5,PaymentStatus.Enum.PAYMENTSUCCESS_2,body)
        }else{
            syncPurchaseOrderHandler.syncBillsAndOrderCenter(or,OrderStatus.Enum.PAID_3,PaymentStatus.Enum.PAYMENTSUCCESS_2,body)
        }

    }


}
