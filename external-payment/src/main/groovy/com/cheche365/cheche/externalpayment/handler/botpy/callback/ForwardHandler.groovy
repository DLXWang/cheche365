package com.cheche365.cheche.externalpayment.handler.botpy.callback

import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 15/03/2018.
 * 伪同步回调处理器
 */

@Slf4j
@Service
class ForwardHandler extends BotpyCallbackHandler {

    @Override
    boolean support(BotpyCallBackBody callBackBody) {
        return callBackBody.actionForward()
    }

    @Override
    def handle(BotpyCallBackBody callBackBody, OrderRelatedService.OrderRelated or) {
        log.debug("转发金斗云投保异步回调: ${callBackBody.typeDesc()}")

        callBackBody.forward()
    }
}
