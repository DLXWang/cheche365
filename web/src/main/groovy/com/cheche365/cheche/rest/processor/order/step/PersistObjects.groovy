package com.cheche365.cheche.rest.processor.order.step

import groovy.util.logging.Slf4j
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component

import javax.transaction.Transactional

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.externalpayment.service.AnswernPayService.answernOutTradeNoKey
import static com.cheche365.cheche.rest.util.WebFlowUtil.getBusinessErrorFSRV
import static com.cheche365.cheche.rest.util.WebFlowUtil.get_STATUS__LAZY_END_FLOW_ERROR
import static java.util.concurrent.TimeUnit.DAYS

/**
 * Created by zhengwei on 12/21/16.
 */

@Component
@Slf4j
class PersistObjects implements TPlaceOrderStep {

    @Transactional
    @Override
    def run(Object context) {
        // 安心需要更新outTradeNo
        if (context.additionalParameters.outTradeNo) {
            context.stringRedisTemplate.opsForValue().putAt answernOutTradeNoKey(context.order.orderNo), context.additionalParameters.outTradeNo
            context.stringRedisTemplate.expire answernOutTradeNoKey(context.order.orderNo), 90, DAYS
        }

        doPersist(context)
        def (_flag, _status, payload, msg) = prefabFSRV.get()
        if (_STATUS__LAZY_END_FLOW_ERROR == _status) {
            getBusinessErrorFSRV (_status, msg)
        } else {
            getContinueFSRV null
        }

    }

    def doPersist(Map context){
        def repositories = context.findAll{it.value instanceof CrudRepository}.collect {it.value}

        Iterator<Integer> iterator = context.toBePersistObjects.iterator();
        while (iterator.hasNext()) {
            def toBePersist = iterator.next()
            toBePersist?.with{ obj ->
                repositories.find {it.respondsTo('save', toBePersist)}.save(obj)
            }

            iterator.remove()
        }

    }
}
