package com.cheche365.cheche.core.util

import org.springframework.data.redis.core.StringRedisTemplate

import java.util.concurrent.TimeUnit

import static java.util.concurrent.TimeUnit.DAYS

/**
 * Created by liheng on 2018/1/24 024.
 */
class StringRedisUtil {

    static getCount(StringRedisTemplate redisTemplate, key, defaultValue = 0, long timeout = 1, TimeUnit unit = DAYS) {
        getValue(redisTemplate, key, defaultValue, timeout, unit) as Integer
    }

    static getValue(StringRedisTemplate redisTemplate, key, defaultValue = null, long timeout = 1, TimeUnit unit = DAYS) {
        if (!redisTemplate.hasKey(key) && defaultValue) {
            setValue redisTemplate, key, defaultValue as String, timeout, unit
        }
        redisTemplate.opsForValue().get key
    }

    static addCount(StringRedisTemplate redisTemplate, String key, long delta = 1) {
        redisTemplate.opsForValue().increment key, delta
    }

    static setValue(StringRedisTemplate redisTemplate, String key, String value, long timeout = 1, TimeUnit unit = DAYS) {
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set key, value, timeout, unit
        }
    }

    static addSet(StringRedisTemplate redisTemplate, String key, String otherKey, long timeout = 1, TimeUnit unit = DAYS) {
        redisTemplate.opsForSet().add key, otherKey
        redisTemplate.expire key, timeout, unit
    }

    static getSet(StringRedisTemplate redisTemplate, String key) {
        redisTemplate.opsForSet().members key
    }

    static isInSet(StringRedisTemplate redisTemplate, String key, String otherKey) {
        redisTemplate.opsForSet().isMember key, otherKey
    }

    static addHash(StringRedisTemplate redisTemplate, String key, Map m, long timeout = 1, TimeUnit unit = DAYS) {
        redisTemplate.opsForHash().putAll key, m
        redisTemplate.expire key, timeout, unit
    }

    static getHash(StringRedisTemplate redisTemplate, String key, hashKeys) {
        redisTemplate.opsForHash().get key, hashKeys
    }

}
