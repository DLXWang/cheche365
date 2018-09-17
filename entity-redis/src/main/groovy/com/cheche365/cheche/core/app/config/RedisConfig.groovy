package com.cheche365.cheche.core.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.PropertySource

@Configuration
@ImportResource([
    'classpath:META-INF/spring/redis-context.xml'
])
@PropertySource('classpath:META-INF/spring/redis.properties')
class RedisConfig {
}
