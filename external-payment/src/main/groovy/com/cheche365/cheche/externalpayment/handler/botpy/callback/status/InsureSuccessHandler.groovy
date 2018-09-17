package com.cheche365.cheche.externalpayment.handler.botpy.callback.status

import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.externalpayment.model.BotpyBodyRecord
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey

/**
 * Created by zhengwei on 16/03/2018.
 * 核保成功处理器
 */

@Service
class InsureSuccessHandler extends BotpyStatusChangeHandler {

    @Autowired
    QuoteRecordCacheService cacheService

    @Override
    boolean support(BotpyBodyRecord record) {
        BotpyCallBackBody.STATUS_INSURE_SUCCESS == record.proposalStatus()
    }

    @Override
    def handle(BotpyBodyRecord record, OrderRelatedService.OrderRelated or) {

        super.handle(record,or)

        Map additionalQRMap = cacheService.getPersistentState(persistQRParamHashKey(or.qr.getId()))
        if(additionalQRMap?.persistentState){
            additionalQRMap.persistentState.proposal_status = record.proposalStatus()
            cacheService.cachePersistentState(persistQRParamHashKey(or.qr.getId()), additionalQRMap)
        }

    }

}
