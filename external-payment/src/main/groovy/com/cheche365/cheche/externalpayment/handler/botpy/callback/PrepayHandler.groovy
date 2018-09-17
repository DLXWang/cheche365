package com.cheche365.cheche.externalpayment.handler.botpy.callback

import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static com.cheche365.cheche.externalpayment.model.BotpyCallBackBody.POLLING_NOTIFICATIONS_REDIS_KEY

/**
 * Created by zhengwei on 15/03/2018.
 * 预支付回调处理器
 */

@Slf4j
@Service
class PrepayHandler extends BotpyCallbackHandler {

    @Override
    boolean support(BotpyCallBackBody callBackBody) {
        BotpyCallBackBody.TYPE_PREPAY == callBackBody.type()
    }

    @Override
    def handle(BotpyCallBackBody callBackBody, OrderRelatedService.OrderRelated or) {
        stringRedisTemplate.opsForHash().put(POLLING_NOTIFICATIONS_REDIS_KEY, callBackBody.notificationId(), callBackBody.raw)
        log.debug("金斗云渠道返回支付二维码信息,qrUrl : ${callBackBody.paymentQRCodeUrl()} ;")
    }
}
