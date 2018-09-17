package com.cheche365.cheche.externalpayment.handler.botpy.callback

import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalapi.api.botpy.BotpyProposalStatusAPI
import com.cheche365.cheche.externalpayment.handler.SyncPurchaseOrderHandler
import com.cheche365.cheche.externalpayment.model.BotpyBodyStatus
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by wen on 18/05/2018.
 * 支付状态结果回调处理器
 */

@Slf4j
@Service
class PaymentStatusHandler extends BotpyCallbackHandler {

    @Autowired
    BotpyProposalStatusAPI proposalStatusAPI

    @Autowired
    SyncPurchaseOrderHandler syncOrderCenterHandler

    @Override
    boolean support(BotpyCallBackBody callBackBody) {
        BotpyCallBackBody.TYPE_PAYMENT_STATUS == callBackBody.type()
    }

    @Override
    def handle(BotpyCallBackBody callBackBody, OrderRelatedService.OrderRelated or) {

        if(callBackBody.paySuccessStatus()){

            if(callBackBody.payStatusCallbackProcessed()){
                log.info("金斗云支付状态回调，投保单${or.po.orderSourceId}的支付出单状态已被StatusChange先行处理，忽略")
                return
            }

            if(or.po.statusFinished()){
                log.info("金斗云支付状态回调，投保单${or.po.orderSourceId}已出单，此次不做处理，忽略")
                return
            }

            def statusResult = proposalStatusAPI.call(callBackBody.proposalId())
            log.info("投保单信息查询结果:{}", statusResult)

            BotpyBodyStatus bodyStatus = new BotpyBodyStatus(statusResult)
            if (bodyStatus.isError()) {
                log.error("投保单号 ${callBackBody.proposalId()} 查询投保单信息返回非预期结果")
            }else{
                callBackBody.payStatusCallbackProcess()
                bodyStatus.syncBillNos(or)
                or.persist()
                log.debug("投保单号 ${bodyStatus.proposalId()},已获取投保单信息并处理完毕")

                syncOrderCenterHandler.purchaseOrderPaidHandle(or.findPending())
            }

        }else{
            log.error("金斗云投保单 ${callBackBody.proposalId()}支付状态回调返回非预期结果")
        }

    }


}
