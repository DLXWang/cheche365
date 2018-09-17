package com.cheche365.cheche.botpy.app.config

import com.cheche365.cheche.botpy.service.BotpyService
import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IContext
import com.cheche365.cheche.core.service.IContextService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.springcmp.service.spi.AsyncMessageHandlerDelegate
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate



/**
 * 金斗云的Spring配置注释类
 */
@Configuration
@ComponentScan([
    'com.cheche365.cheche.parserapi.app.config',
    'com.cheche365.springcmp.app.config',
    'com.cheche365.cheche.core.app.config',
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.botpy.service',
])
@ImportResource([
    'classpath:META-INF/spring/botpy-context.xml',
    'classpath:META-INF/spring/botpy-redis-context.xml'
])
@EnableAutoConfiguration
class BotpyConfig {

    @Bean
    botpyAsyncMessageHandlerDelegate() {
        new AsyncMessageHandlerDelegate(
            { it.notification_id },
            { new JsonSlurper().parseText it }
        )
    }

    @Bean
    IThirdPartyHandlerService botpyService(
        ApplicationContextHolder _appContextHolder, // <-- 绝对不许删，看上面javadoc
        Environment env,
        IConfigService configService,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        @Qualifier('botpyGlobalContext') IContext globalContext,
        @Qualifier('botpyAsyncMessageHandlerDelegate') messageHandler,
        MoApplicationLogRepository logRepo) {
        new BotpyService(env, configService, insuranceCompanyChecker, globalContext, messageHandler, logRepo)
    }

    @Bean
    IContext botpyGlobalContext(
        @Qualifier('redisContextService') IContextService redisContextService,
        @Qualifier('botpyJedisConnectionFactory') RedisConnectionFactory redisConnFactory
    ) {
        def redis = new StringRedisTemplate(redisConnFactory)
        redisContextService.getContext redis: redis, category: 'botpy'
    }


}
