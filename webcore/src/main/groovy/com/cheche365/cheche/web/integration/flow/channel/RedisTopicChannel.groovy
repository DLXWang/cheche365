package com.cheche365.cheche.web.integration.flow.channel

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.integration.redis.channel.SubscribableRedisChannel
import org.springframework.messaging.MessageChannel

import static com.cheche365.cheche.core.context.ApplicationContextHolder.getApplicationContext

/**
 * Created by liheng on 2018/6/25 0025.
 */
class RedisTopicChannel extends AMessageChannels {

    RedisTopicChannel(String topicName) {
        super(topicName)
    }

    MessageChannel doGet() {
        new SubscribableRedisChannel(applicationContext.getBean(JedisConnectionFactory), topicName)
    }
}
