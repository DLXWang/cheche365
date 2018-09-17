package com.cheche365.cheche.externalpayment.handler.botpy.callback

import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.OrderSourceType
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.DoubleDBService
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalpayment.model.BotpyBodyRecord
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 15/03/2018.
 * 金斗云回调处理器，回调类型包括伪同步回调，纯异步回调和二者结合三种情况
 */

@Slf4j
@Service
abstract class BotpyCallbackHandler{

    @Autowired
    DoubleDBService mongoDBService

    @Autowired
    StringRedisTemplate stringRedisTemplate

    @Autowired
    PurchaseOrderRepository poRepo

    @Autowired
    OrderRelatedService orService

    @Autowired
    MoApplicationLogRepository logRepository

    abstract boolean support(BotpyCallBackBody callBackBody)

    abstract handle(BotpyCallBackBody callBackBody, OrderRelatedService.OrderRelated or)

    OrderRelatedService.OrderRelated initORByPid(String proposalId) {
        if(!proposalId){
            return null
        }

        if(stringRedisTemplate.opsForSet().isMember('botpy-proposal_id_blacklist', proposalId)){
            log.debug("proposal_id: ${proposalId} 在黑名单内，忽略回调处理")
            return
        }

        def or = orService.initOR { PurchaseOrderRepository poRepo ->
            poRepo.findByOrderSourceId(proposalId, OrderSourceType.Enum.PLANTFORM_BOTPY_8).with {
                it ? it.first() : null
            }
        }

        if(!or){
            String orderNo = stringRedisTemplate.opsForValue().get("botpy-"+proposalId) as String
            if(orderNo){
                or = orService.initOR {PurchaseOrderRepository poRepo ->
                    poRepo.findFirstByOrderNo(orderNo)
                }
            }
        }

        or

    }

    void persistLog(BotpyCallBackBody body, OrderRelatedService.OrderRelated or, LogType logType = LogType.Enum.BOTPY_56) {
        def template = MoApplicationLog.applicationLogByPurchaseOrder(or?.po, logType)
        template.logId = body.proposalId()
        template.logMessage = body.parsed
        template.objTable = body instanceof BotpyBodyRecord ? body.statusDesc() : body.typeDesc()

        logRepository.save(template)
    }

}
