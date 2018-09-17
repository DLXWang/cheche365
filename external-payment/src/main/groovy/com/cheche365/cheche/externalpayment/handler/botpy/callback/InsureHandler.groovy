package com.cheche365.cheche.externalpayment.handler.botpy.callback

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.service.IContextWithTTLSupport
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import groovy.util.logging.Log4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.externalpayment.model.BotpyCallBackBody.IMAGES_PROPOSAL_STATUS_REDIS_KEY

/**
 * Created by zhengwei on 15/03/2018.
 * 核保回调处理器
 */


@Log4j
@Service
class InsureHandler extends BotpyCallbackHandler{

    @Autowired
    private PurchaseOrderImageService poiService

    IContextWithTTLSupport globalContext

    @Autowired
    private PurchaseOrderService purchaseOrderService

    @Override
    boolean support(BotpyCallBackBody callBackBody) {
        BotpyCallBackBody.TYPE_INSURE == callBackBody.type()
    }

    @Override
    def handle(BotpyCallBackBody callBackBody, OrderRelatedService.OrderRelated or) {

        String orderNo = getBotpyGlobalContext().get(callBackBody.notificationId())
        if (orderNo) {
            if (!callBackBody.success()) {
                PurchaseOrder order = purchaseOrderService.findFirstByOrderNo(orderNo)
                String comment = callBackBody.getImagesComment()
                !comment ?: poiService.persistCustomImage(order, comment)
            } else {
                stringRedisTemplate.opsForHash().put(IMAGES_PROPOSAL_STATUS_REDIS_KEY, orderNo, callBackBody.proposalStatus())
            }
        }

        if(callBackBody.asyncWaiting()){
            log.info("${callBackBody.notificationId()} 核保回调将由parse伪同步处理")
            callBackBody.forward()
            return
        }

        log.debug("金斗云核保回调处理,notification_id : ${callBackBody.notificationId()},跟踪单号: ${callBackBody.trackingNo()} ;投保单号:${callBackBody.proposalId()};")
        PurchaseOrder order = or.po

        if(callBackBody.manuallyInsure()) {
            log.debug("金斗云核保回调->人工核保;订单号:${order.orderNo}")
        }


        if(callBackBody.waitPayStatus()) {//金斗云返回的人工核保标志和投保单状态，均为保险公司最终的核保结果，不代表接下来的流程走向
            order.status = OrderStatus.Enum.PENDING_PAYMENT_1
            order.statusDisplay = null

            or.toBePersist << order
            callBackBody.syncBillNos(or)

            log.debug("金斗云核保回调状态为核保成功;订单号:${order.orderNo}")
        } else {//核保失败或者其他未知状态，记录审核意见到出单中心
            def records = callBackBody.auditRecords()
            def comment = callBackBody.dataComment()
            if(comment) {
                persistLog(callBackBody, or, LogType.Enum.INSURE_FAILURE_1)
            } else {
                order.statusDisplay = null
            }

            order.status = OrderStatus.Enum.INSURE_FAILURE_7
            or.toBePersist << order
            log.debug("金斗云核保回调状态为核保失败;原因：${comment}，审核意见:${records};车车订单号:${or.po.orderNo};订单状态:${or.po.status.description};")
        }

        or.persist()
    }


    //直接注入导致运行时这个bean为空，可能跟bean的初始化顺序有关
    private synchronized getBotpyGlobalContext() {

        if(!globalContext) {
            globalContext = ApplicationContextHolder.applicationContext.getBean('botpyGlobalContext')
        }
        return globalContext
    }
}
