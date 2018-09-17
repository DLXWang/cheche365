package com.cheche365.cheche.externalpayment.handler

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.externalpayment.service.PaymentStatusPollingService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9
import static com.cheche365.cheche.externalpayment.service.PaymentStatusPollingService.POLLING_CONTINUE
import static com.cheche365.cheche.externalpayment.service.PaymentStatusPollingService.POLLING_END
import static com.cheche365.cheche.externalpayment.service.PaymentStatusPollingService.POLLING_SUCCESS

/**
 * Created by wen on 2018/5/28.
 */
class PaymentStatusPollingHandler implements Runnable {

    private Logger logger = LoggerFactory.getLogger(PaymentStatusPollingHandler.class)

    private PurchaseOrder order
    private PaymentStatusPollingService pollingService
    private String clientIdentifier

    private boolean pollingFlag = true
    private int currentPollingCount = 0
    private int nextPollingTime = 0


    PaymentStatusPollingHandler(PurchaseOrder order, PaymentStatusPollingService pollingService, String clientIdentifier) {
        this.order = order
        this.pollingService = pollingService
        this.clientIdentifier = clientIdentifier
    }


    @Override
    void run() {

        logger.info("确认支付后，开始轮询操作，订单号：{}", order.orderNo)
        while (pollingFlag && 0 <= currentPollingCount && currentPollingCount < 6) {
            sleep(getSleepTime())
            purchaseOrderHandle(order)
        }
        logger.info("退出此次轮询，订单号：{}，共轮询{}次",  order.orderNo, currentPollingCount)
    }

    def purchaseOrderHandle(PurchaseOrder order) {

        def handResult = pollingService.checkAndHandlePayStatus(order, clientIdentifier)

        if (handResult) {
            if (POLLING_SUCCESS == handResult.result) {
                logger.info("第{}次轮询{}，成功获取{}支付状态并处理成功，结束轮询", currentPollingCount, order.orderNo, handResult.thirdparty)
                pollingFlag = false
            } else if (POLLING_CONTINUE == handResult.result) {
                logger.info("第{}次轮询{}，{}正在处理该订单，{}秒后继续轮询", currentPollingCount, order.orderNo, handResult.thirdparty, nextPollingTime/1000)
                pollingFlag = true
            } else if(POLLING_END == handResult.result){
                logger.info("第{}次轮询{}，{}查询订单状态轮询结束，原因：{}", currentPollingCount,  order.orderNo, handResult.thirdparty, handResult.message)
                pollingFlag = false
            } else {
                logger.info("第{}次轮询{}，{}订单支付状态查询失败，结束轮询", currentPollingCount,  order.orderNo, handResult.thirdparty)
                pollingFlag = false
            }
        } else {
            logger.info("第{}次轮询{}，查询结果为空，{}秒后继续轮询", currentPollingCount,   order.orderNo, nextPollingTime/1000)
            pollingFlag = true
        }
    }

    private int getSleepTime(){
        currentPollingCount++
        nextPollingTime = currentPollingCount < 3 ? 15*1000 : 60*1000
        nextPollingTime
    }

}

