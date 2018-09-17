package com.cheche365.cheche.web.integration.flow.step.adapter

import com.cheche365.cheche.web.integration.flow.step.from.AMessageProducerSupportFrom
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.integration.redis.inbound.RedisQueueMessageDrivenEndpoint

import static com.cheche365.cheche.core.context.ApplicationContextHolder.getApplicationContext

/**
 * Created by liheng on 2018/6/14 0014.
 */
class RedisInBoundChannelAdapter extends AMessageProducerSupportFrom {

    RedisInBoundChannelAdapter(String queueName, RedisSerializer serializer = new JdkSerializationRedisSerializer()) {
        super(new RedisQueueMessageDrivenEndpoint(queueName, applicationContext.getBean(JedisConnectionFactory)).with {
            it.serializer = serializer
            it
        })
    }
}
