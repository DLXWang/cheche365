package com.cheche365.cheche.externalpayment.handler.botpy.callback

import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalpayment.handler.BotpyMergeInsuranceHandler
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by wenling on 2018/3/28.
 * 验证身份证验证码结果回调处理器
 */

@Slf4j
@Service
class VerifyIDCodeHandler extends BotpyCallbackHandler {

    @Autowired
    BotpyMergeInsuranceHandler mergeInsuranceHandler

    @Override
    boolean support(BotpyCallBackBody callBackBody) {
        BotpyCallBackBody.TYPE_VERIFY_IDCODE == callBackBody.type()
    }

    @Override
    def handle(BotpyCallBackBody callBackBody, OrderRelatedService.OrderRelated or) {
        callBackBody.forward()
        log.info("${callBackBody.notificationId()} 核保回调将由parse伪同步处理")

        //TODO:对于险种变更暂时不做处理
         if(callBackBody.success()){
//             log.info("金斗云验证身份证验证码结果回调处理")
//             mergeInsuranceHandler.syncBills(callBackBody,or)
//             callBackBody.syncBillNos(or)
//
//             or.persist()

             if (or.insurance && callBackBody.premium() != or.insurance.premium) {
                 log.info("商业险险种变更，投保单号：{}", callBackBody.proposalId())
             }
             if (or.ci && (callBackBody.ciPremium() != or.ci.compulsoryPremium || callBackBody.ciAutoTax() != or.ci.autoTax)) {
                 log.info("交强险险种变更，投保单号：{}", callBackBody.proposalId())
             }
         }

    }

}
