package com.cheche365.cheche.parser.ms.app.config

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.service.FileBasedConfigService
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IContext
import com.cheche365.cheche.core.service.IContextService
import com.cheche365.cheche.core.service.IContextWithTTLSupport
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.springcmp.service.spi.AsyncMessageHandlerDelegate
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import com.cheche365.cheche.botpy.service.BotpyService
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate

/**
 * 金斗云微服务的配置
 */
@Configuration
@ComponentScan('com.cheche365.cheche.botpy.app.config')
class BotpyMicroServiceConfig {
    @Bean
    botpyAsyncMessageHandlerDelegate() {
        new AsyncMessageHandlerDelegate(
            { it.notification_id },
            { new JsonSlurper().parseText it }
        )
    }

    @Bean
    IThirdPartyHandlerService botpyService(
        Environment env,
        @Qualifier('iConfigService') IConfigService configService,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        @Qualifier('botpyGlobalContext') IContext globalContext,
        @Qualifier('botpyAsyncMessageHandlerDelegate') messageHandler,
        MoApplicationLogRepository logRepo) {
        new BotpyService(env, configService, insuranceCompanyChecker, globalContext, messageHandler, logRepo)
    }

    @Bean
    IContextWithTTLSupport botpyGlobalContext(
        @Qualifier('redisContextService') IContextService redisContextService,
        @Qualifier('botpyJedisConnectionFactory') RedisConnectionFactory redisConnFactory
    ) {
        def redis = new StringRedisTemplate(redisConnFactory)
        redisContextService.getContext redis: redis, category: 'botpy'
    }
    @Bean
    IConfigService iConfigService(Environment env){
        new FileBasedConfigService(env)
    }

}
