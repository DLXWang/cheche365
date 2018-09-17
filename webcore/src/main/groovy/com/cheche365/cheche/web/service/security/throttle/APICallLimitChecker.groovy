package com.cheche365.cheche.web.service.security.throttle

import groovy.util.logging.Slf4j
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 01/02/2018.
 */

@Service
@Slf4j
class APICallLimitChecker {

    static final API_ID_SINOSAFE_INSURE_CALLBACK = 'api_id_sinosafe_insure_callback'
    static final CONFIG = [
            (API_ID_SINOSAFE_INSURE_CALLBACK) : [
                    limit: 3
            ]
    ]

    StringRedisTemplate redisTemplate

    APICallLimitChecker(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate
    }

    void oneMore(String key, String subKey){
        Long current = redisTemplate.opsForHash().increment(key, subKey, 1)
        log.debug('api计数器自增，key: {}, subKey: {}, 当前数量: {}', key, subKey, current)
    }

    boolean meetLimit(String key, String subKey){
        Long current = (redisTemplate.opsForHash().get(key, subKey) as Long) ?: 0
        return current >= CONFIG[key].limit

    }
}
