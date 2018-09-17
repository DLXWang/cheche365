package com.cheche365.cheche.web.integration.flow.step.adapter

import com.cheche365.cheche.web.integration.flow.step.Handler
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.integration.redis.outbound.RedisQueueOutboundChannelAdapter

import static com.cheche365.cheche.core.context.ApplicationContextHolder.getApplicationContext

/**
 * Created by liheng on 2018/6/14 0014.
 */
class RedisOutBoundChannelAdapter extends Handler {

    RedisOutBoundChannelAdapter(String queueName, RedisSerializer serializer = new JdkSerializationRedisSerializer(), Closure endpointConfigurer = null) {
        super(new RedisQueueOutboundChannelAdapter(queueName, applicationContext.getBean(JedisConnectionFactory)).with {
            it.serializer = serializer
            it
        }, endpointConfigurer)
    }
}
