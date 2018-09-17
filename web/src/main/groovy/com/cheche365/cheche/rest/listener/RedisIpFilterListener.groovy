package com.cheche365.cheche.rest.listener

import com.cheche365.cheche.core.util.AddressUtil
import com.cheche365.cheche.core.util.CacheUtil
import groovy.util.logging.Slf4j
import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.util.Assert

import java.util.concurrent.TimeUnit

import static com.cheche365.cheche.core.message.IPFilterMessage.KEY_IP_DAY
import static com.cheche365.cheche.core.message.IPFilterMessage.KEY_IP_HOUR
import static com.cheche365.cheche.core.message.IPFilterMessage.QUEUE_SET
import static com.cheche365.cheche.core.message.IPFilterMessage.SET_FOREIGN_COUNTRY
import static com.cheche365.cheche.core.message.IPFilterMessage.countCache
import static com.cheche365.cheche.core.message.IPFilterMessage.dayHashKey
import static com.cheche365.cheche.core.message.IPFilterMessage.hourHashKey
import static com.cheche365.cheche.core.message.IPFilterMessage.ipKey

@Slf4j
@Component
class RedisIpFilterListener implements MessageListener {

    @Autowired
    private RedisTemplate redisTemplate

    @Autowired
    private StringRedisTemplate stringRedisTemplate

    @Override
    void onMessage(Message message, byte[] pattern) {

        try {
            String param = String.valueOf(message.toString())
            Assert.notNull(param, "ip is not null!")

            Map<String, String> ipMessage = CacheUtil.doJacksonDeserialize(param, Map.class)
            if (stringRedisTemplate.opsForSet().remove(QUEUE_SET, ipKey(ipMessage)) <= 0) {
                return
            }

            String countryCode = AddressUtil.ip2LocationTaoBao(ipMessage.ip)
            log.debug('code:{}, ip: {}, countryCode: {}', ipMessage.code, ipMessage.ip, countryCode)
            if (countryCode && ("CN" != countryCode)) {
                redisTemplate.opsForSet().add(SET_FOREIGN_COUNTRY, ipMessage.ip)
            }

            Date now = Calendar.getInstance().getTime()
            countCache(redisTemplate, KEY_IP_HOUR, hourHashKey(ipMessage, now), 2L, TimeUnit.HOURS)
            countCache(redisTemplate, KEY_IP_DAY, dayHashKey(ipMessage, now), 2L, TimeUnit.DAYS)

        } catch (Exception e) {
            log.error("拦截海外ip,ip2Country转换过程出错, exception:{}", ExceptionUtils.getStackTrace(e))
        }
    }
}
