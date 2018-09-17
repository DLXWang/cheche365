package com.cheche365.cheche.core.message

import org.apache.commons.lang3.time.DateFormatUtils
import org.springframework.data.redis.core.RedisTemplate

import java.util.concurrent.TimeUnit

class IPFilterMessage extends QueueMessage<String, Map> {

    public static final String QUEUE_NAME = "pubsub:ipfilter"
    public static final String QUEUE_SET = "set:ipfilter"
    public static final String SET_FOREIGN_COUNTRY = "set:ipfilter:foreignCountry"
    public static final String KEY_IP_HOUR = "hash:ipfilter:hour"
    public static final String KEY_IP_DAY = "hash:ipfilter:day"
    public static final List IP_WHITE_LIST = ["124.65.149.30", "1.119.6.70", "36.102.228.110"]
    public static final String CODE_SMS = "sms"
    public static final String CODE_MARKETING = "marketing"

    public IPFilterMessage() {
        super()
    }

    @Override
    String getQueueName() {
        return QUEUE_NAME
    }

    @Override
    String getQueueSet() {
        return QUEUE_SET
    }

    static String ipKey(Map param) {
        param.code + param.ip
    }

    static String dayHashKey(Map param, Date now) {
        param.code + DateFormatUtils.format(now, "yyyyMMdd") + param.ip
    }

    static String hourHashKey(Map param, Date now) {
        param.code + DateFormatUtils.format(now, "yyyyMMddHH") + param.ip
    }

    static countCache(RedisTemplate redisTemplate, String key, String hashKey, Long timeout, TimeUnit unit) {
        if (redisTemplate.opsForHash().hasKey(key, hashKey)) {
            Long count = redisTemplate.opsForHash().get(key, hashKey) as Long
            redisTemplate.opsForHash().put(key, hashKey, count + 1L)
        } else {
            redisTemplate.opsForHash().put(key, hashKey, 1L)
            redisTemplate.expire(key, timeout, unit)
        }
    }

    static cleanCache(RedisTemplate redisTemplate, String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey)
    }
}
