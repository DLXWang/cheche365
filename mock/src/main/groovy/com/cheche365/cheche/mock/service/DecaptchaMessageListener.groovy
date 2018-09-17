package com.cheche365.cheche.mock.service

import groovy.util.logging.Log4j
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

/**
 * Created by zhengwei on 29/05/2018.
 */

@Service
@Log4j
class DecaptchaMessageListener implements MessageListener {

    StringRedisTemplate redisTemplate

    DecaptchaMessageListener(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate
    }

    @Override
    void onMessage(Message message, byte[] pattern) {
        redisTemplate.opsForValue().set('mock-decaptcha-in', message.toString())
        redisTemplate.expire('mock-decaptcha-in', 2, TimeUnit.MINUTES)
    }
}
