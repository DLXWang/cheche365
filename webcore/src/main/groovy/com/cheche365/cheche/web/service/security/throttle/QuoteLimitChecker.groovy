package com.cheche365.cheche.web.service.security.throttle

import com.cheche365.cheche.core.exception.IllegalOperationException
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IConfigService
import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew

/**
 * Created by zhengwei on 09/07/2018.
 */

@Slf4j
@Service
class QuoteLimitChecker {

    static final QUOTE_CALL_TIMES = 'quote_call_times'

    StringRedisTemplate redisTemplate
    Environment env
    IConfigService configService

    QuoteLimitChecker(StringRedisTemplate redisTemplate, Environment env, IConfigService configService) {
        this.redisTemplate = redisTemplate
        this.env = env
        this.configService = configService
    }

    void oneMore(QuoteRecord qr, String account){
        def (String firstLevel, String secondLevel) = cacheKey(qr, account)

        setHashExpire(firstLevel)
        redisTemplate.opsForHash().increment(firstLevel, secondLevel, 1)
    }

    void meetLimit(QuoteRecord qr, String account) {
        def (String firstLevel, String secondLevel) = cacheKey(qr, account)
        def currentCallTimes = (redisTemplate.opsForHash().get(firstLevel, secondLevel) ?: 0)  as Integer
        def limit = findLimit(qr.insuranceCompany.id as String)
        if(!limit){
            log.warn('保险公司{} 无报价次数上限配置，忽略校验', qr.insuranceCompany.id)
            return
        }

        if (currentCallTimes >= limit) {
            log.debug('报价次数到达上限，key: {}, currentCallTimes: {}, limit: {}', secondLevel, currentCallTimes, limit)
            throw new IllegalOperationException('报价排队中，请稍后操作')
        }
    }

    static cacheKey(QuoteRecord qr, String account){
        Date now = new Date()

        [
            [
                QUOTE_CALL_TIMES,
                now[Calendar.DAY_OF_MONTH]
            ].join('_'),

            [
                qr.insuranceCompany.id,
                account,
                now[Calendar.HOUR_OF_DAY],
                now[Calendar.MINUTE]
            ].join('_')
        ]



    }

    def findLimit(String companyId) {
        def newEnv = [env: env, configService: configService, namespace: 'quote_call_limit']
        getEnvPropertyNew(newEnv, companyId, null, []) as Integer
    }

    void setHashExpire(String key){
        if(redisTemplate.opsForHash().size(key) == 0){
            redisTemplate.opsForHash().put(key, "expireKey", "24hours");
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        }
    }



}
