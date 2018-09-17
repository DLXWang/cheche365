package com.cheche365.cheche.externalpayment.handler.botpy.callback

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalpayment.handler.botpy.callback.status.BotpyStatusChangeHandler
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 15/03/2018.
 * 保单状态变更回调处理器，批量通知，报文中包含多张保单信息
 */

@Slf4j
@Service
class StatusChangeHandler extends BotpyCallbackHandler {

    @Autowired
    private List<BotpyStatusChangeHandler> handlers

    @Override
    boolean support(BotpyCallBackBody callBackBody) {
        BotpyCallBackBody.TYPE_STATUS_CHANGE == callBackBody.type()
    }

    @Override
    def handle(BotpyCallBackBody callBackBody, OrderRelatedService.OrderRelated or) {

        callBackBody.records().each { record ->
            persistLog(record, or)

            or = initORByPid(record.proposalId())
            if(or) {
                BotpyStatusChangeHandler handler = handlers.find {it.support(record) }
                if (!handler) {
                    throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "金斗云投保单状态未匹配到车车订单状态: ${record.proposalStatus()}")
                }

                handler.handle(record, or)
            } else {
                log.error("根据proposal_id ${record.proposalId()}未找到车车订单，忽略该回调处理")
            }
        }
    }
}
