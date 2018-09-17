package com.cheche365.cheche.externalpayment.handler.botpy.callback.status

import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalapi.api.botpy.BotpyProposalStatusAPI
import com.cheche365.cheche.externalpayment.model.BotpyBodyRecord
import com.cheche365.cheche.externalpayment.model.BotpyBodyStatus
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 19/03/2018.
 */

@Service
@Slf4j
class PaidHandler extends BotpyStatusChangeHandler {

    @Autowired
    BotpyProposalStatusAPI proposalStatusAPI

    @Override
    boolean support(BotpyBodyRecord record) {
        BotpyCallBackBody.STATUS_PAID == record.proposalStatus()
    }

    @Override
    def handle(BotpyBodyRecord record, OrderRelatedService.OrderRelated or) {
        super.handle(record, or)

        def statusResult = proposalStatusAPI.call(record.proposalId())
        log.info("投保单信息查询结果:{}", statusResult)
        BotpyBodyStatus callBackBody = new BotpyBodyStatus(statusResult)
        if (!callBackBody.isError() ) {
            callBackBody.syncBillNos(or)
            or.persist()
        }
    }
}
