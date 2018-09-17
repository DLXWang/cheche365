package com.cheche365.cheche.rest.processor.order.step.insure

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.OrderSourceType
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.rest.processor.order.step.TPlaceOrderStep
import groovy.util.logging.Slf4j
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.externalpayment.model.BotpyCallBackBody.TIMEOUT_NOTIFICATIONS_REDIS_KEY

/**
 * Created by zhengwei on 20/03/2018.
 */

@Component
@Slf4j
class BotpyInsureFail implements TPlaceOrderStep {

    @Override
    Object run(Object context) {

        BusinessException e = context.insureFailException
        PurchaseOrder order = context.order
        Map persistentState = context.additionalParameters?.persistentState


        order.setOrderSourceType(OrderSourceType.Enum.PLANTFORM_BOTPY_8)
        order.orderSourceId = persistentState?.proposal_id

        if((e.getCode().codeValue == 2014 || e.getCode().codeValue == 5001)){  //2014为人工核保，5001为上传影像提交同步时候发生的异常
            order.statusDisplay='核保中'

            StringRedisTemplate stringRedisTemplate = context.stringRedisTemplate
            def errorBody = e.getErrorObject()
            String notificationId = errorBody?.meta?.notification_id

            log.debug("金斗云同步核保超时;notification_id: ${notificationId} proposal_id :${persistentState?.proposal_id}，订单号:${order.orderNo}")

            if (notificationId) {
                stringRedisTemplate.opsForSet().add(TIMEOUT_NOTIFICATIONS_REDIS_KEY, notificationId)
            } else {
                log.error("金斗云同步核保超时; 处理异常时未找到notification_id, 后续同步会有问题，订单号:${order.orderNo}")
            }

        }

        return getContinueFSRV(true)
    }
}
