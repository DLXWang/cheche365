package com.cheche365.cheche.idcredit.app.config

import com.cheche365.cheche.core.service.IContext
import com.cheche365.cheche.core.service.IContextService
import com.cheche365.cheche.core.service.ISelfIncrementCountingCurrentLimiter
import com.cheche365.cheche.core.service.SimpleCountingCurrentLimiter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate

import static com.cheche365.cheche.core.constants.GlobalContextConstants._SIMPLE_CURRENT_LIMITER_GET_KEY_BASE

/**
 * 绿湾的Spring配置注释类
 */
@Configuration
@ComponentScan([
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.idcredit.service'
])
@ImportResource([
    'classpath:META-INF/spring/redis-context-idcredit.xml'
])
class IdcreditConfig {

    private static final _CURRENT_LIMITER_GET_KEY_GET_TOKEN         = _SIMPLE_CURRENT_LIMITER_GET_KEY_BASE.curry 'idcredit', 'get-token'
    private static final _CURRENT_LIMITER_GET_KEY_FIND_VEHICLE_INFO = _SIMPLE_CURRENT_LIMITER_GET_KEY_BASE.curry 'idcredit', 'find-vehicle-info'

    @Autowired
    private Environment env


    @Bean
    IContext idcreditGlobalContext(
        @Qualifier('redisContextService') IContextService redisContextService,
        @Qualifier('idcreditJedisConnectionFactory') RedisConnectionFactory redisConnFactory
    ) {
        def redis = new StringRedisTemplate(redisConnFactory)
        redisContextService.getContext redis: redis, category: 'idcredit'
    }

    @Bean
    ISelfIncrementCountingCurrentLimiter idcreditAPIThrottleGetToken(
        @Qualifier('idcreditGlobalContext') IContext context
    ) {
        def max = env.getProperty('idcredit.api_throttle_threshold_get_token') as long
        new SimpleCountingCurrentLimiter(context, max, _CURRENT_LIMITER_GET_KEY_GET_TOKEN)
    }

    @Bean
    ISelfIncrementCountingCurrentLimiter idcreditAPIThrottleFindVehicleInfo(
        @Qualifier('idcreditGlobalContext') IContext context
    ) {
        def max = env.getProperty('idcredit.api_throttle_threshold_find_vehicle_info') as long
        new SimpleCountingCurrentLimiter(context, max, _CURRENT_LIMITER_GET_KEY_FIND_VEHICLE_INFO)
    }

}
