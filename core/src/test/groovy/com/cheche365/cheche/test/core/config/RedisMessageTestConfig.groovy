package com.cheche365.cheche.test.core.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.*
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.listener.RedisMessageListenerContainer

/**
 * Created by zhengwei on 2/13/17.
 */

@TestConfiguration
@ComponentScan([
    'com.cheche365.cheche.core.context',
    'com.cheche365.cheche.core.message'
])
@ImportResource([
    'classpath:META-INF/spring/common-context.xml',
    'classpath:META-INF/spring/redis-context.xml',
])
@PropertySource([
    'classpath:META-INF/spring/redis.properties'
])
class RedisMessageTestConfig {

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        new JedisConnectionFactory();
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());

        return container;
    }

}
