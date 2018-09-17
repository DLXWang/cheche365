package com.cheche365.cheche.web.service.order

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

/**
 * Created by wen on 2018/8/3.
 */
@Service
@Slf4j
class PurchaseOrderLockService {

    @Autowired
    StringRedisTemplate stringRedisTemplate

    void lock(String orderNo){
        stringRedisTemplate.opsForValue().set(insureLockFlagKey(orderNo) ,'insure',10, TimeUnit.MINUTES)
        log.debug('添加订单锁结束, {}', insureLockFlagKey(orderNo))
    }

    void unLock(String orderNo){
        if(stillLocking(orderNo)) {
            stringRedisTemplate.delete(insureLockFlagKey(orderNo))
            log.debug('释放订单锁结束, {}', insureLockFlagKey(orderNo))
        } else {
            log.debug('{}, 未锁定，无需释放锁', orderNo)
        }
    }

    boolean stillLocking(String orderNo){
        orderNo && stringRedisTemplate.hasKey(insureLockFlagKey(orderNo))
    }

    static String insureLockFlagKey(String orderNo){
        "insure_lock_" + orderNo
    }

}
