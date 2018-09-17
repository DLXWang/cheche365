package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalapi.api.botpy.BotpyPaymentStatusAPI
import com.cheche365.cheche.externalpayment.handler.SyncPurchaseOrderHandler
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

import java.util.concurrent.ExecutorService

import static com.cheche365.cheche.core.model.OrderStatus.Enum.FINISHED_5
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PAID_3

@Service
@Slf4j
class BotpyPaymentStatusPollingService extends PaymentStatusPollingService{

    @Autowired
    @Qualifier("pollingExecutorService")
    private ExecutorService executorService

    @Autowired
    BotpyPaymentStatusAPI botpyPaymentStatusAPI

    @Autowired
    OrderRelatedService orderRelatedService

    @Autowired
    SyncPurchaseOrderHandler syncPurchaseOrderHandler

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository

    @Override
    boolean support(QuoteSource quoteSource) {
        QuoteSource.Enum.PLATFORM_BOTPY_11 == quoteSource
    }

    @Override
    Map checkAndHandlePayStatus(PurchaseOrder order, String clientIdentifier) {
        PurchaseOrder po = purchaseOrderRepository.findOne(order.id)
        if (po.status in [FINISHED_5, PAID_3]){
            log.info("订单号：{}，待查询订单可能由回调改为已支付，忽略本次轮询查询", order.orderNo)
            [result: POLLING_END, thirdparty:'金斗云', message:'待查询订单可能由回调改为已支付，忽略轮询本次查询']
        } else {
            log.info("开始请求金斗云支付状态查询接口，订单号：{}", order.orderNo)
            def result = botpyPaymentStatusAPI.call(order.orderSourceId, clientIdentifier)
            log.info("金斗云支付状态查询接口已调用，同步响应结果：{}", result)
            [result: POLLING_CONTINUE, orderNo: po.orderNo, thirdparty:'金斗云', notificationId:result?.notification_id]
        }

    }

}
