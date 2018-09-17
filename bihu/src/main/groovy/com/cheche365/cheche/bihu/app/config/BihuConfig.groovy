package com.cheche365.cheche.bihu.app.config

import com.cheche365.cheche.core.service.IContext
import com.cheche365.cheche.core.service.IContextService
import com.cheche365.cheche.core.service.ISelfIncrementCountingCurrentLimiter
import com.cheche365.cheche.core.service.SimpleCountingCurrentLimiter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate

import static com.cheche365.cheche.core.constants.GlobalContextConstants._SIMPLE_CURRENT_LIMITER_GET_KEY_BASE



/**
 * 壁虎的Spring配置注释类
 */
@Configuration
@ComponentScan([
    'com.cheche365.cheche.core.app.config',
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.bihu.app.config',
    'com.cheche365.cheche.bihu.service',
    'com.cheche365.cheche.bihu.model',
])
@EnableJpaRepositories('com.cheche365.cheche.bihu.repository')
@ImportResource(
    'classpath:META-INF/spring/redis-context-bihu.xml'
)
@PropertySource('classpath:/properties/bihu.properties')
@EnableAutoConfiguration
class BihuConfig {

    private static final _CURRENT_LIMITER_GET_KEY_FIND_VEHICLE_INFO = _SIMPLE_CURRENT_LIMITER_GET_KEY_BASE.curry 'bihu', 'find-vehicle-info'

    @Autowired
    private Environment env

    @Bean
    IContext bihuGlobalContext(
        @Qualifier('redisContextService') IContextService redisContextService,
        @Qualifier('bihuJedisConnectionFactory') RedisConnectionFactory redisConnFactory
    ) {
        def redis = new StringRedisTemplate(redisConnFactory)
        redisContextService.getContext redis: redis, category: 'bihu'
    }

    @Bean
    ISelfIncrementCountingCurrentLimiter bihuAPIThrottleFindVehicleInfo(
        @Qualifier('bihuGlobalContext') IContext context
    ) {
        def max = env.getProperty('bihu.api_throttle_threshold_find_vehicle_info') as long
        new SimpleCountingCurrentLimiter(context, max, _CURRENT_LIMITER_GET_KEY_FIND_VEHICLE_INFO)
    }

}
