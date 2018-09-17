package com.cheche365.cheche.baoxian.app.config

import com.cheche365.cheche.baoxian.service.BaoXianService
import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.service.IContextService
import com.cheche365.cheche.core.service.IContextWithTTLSupport
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.springcmp.service.spi.AsyncMessageHandlerDelegate
import groovy.json.JsonSlurper
import groovyx.gpars.group.PGroup
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate


@Configuration
@ComponentScan([
    'com.cheche365.springcmp.app.config',
    'com.cheche365.cheche.core.app.config',
    'com.cheche365.cheche.parserapi.app.config',
    'com.cheche365.cheche.baoxian.service',
    'com.cheche365.cheche.core.context'
])
@ImportResource([
    'classpath:META-INF/spring/baoxian-context.xml',
    'classpath:META-INF/spring/baoxian-redis-context.xml'
])
class BaoXianConfig {
    @Bean
    baoxianAsyncMessageHandlerDelegate() {
        new AsyncMessageHandlerDelegate(
            { it.taskId + '_' + it.prvId[0..3] },
            { new JsonSlurper().parseText it }
        )
    }

    @Bean
    IThirdPartyHandlerService baoXianService(
        ApplicationContextHolder _appContextHolder, // <-- 绝对不许删，看上面javadoc
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        @Qualifier('baoxianGlobalContext') IContextWithTTLSupport globalContext,
        @Qualifier('baoxianAsyncMessageHandlerDelegate') messageHandler,
        MoApplicationLogRepository logRepo,
        PGroup parserTaskPGroup
    ) {
        new BaoXianService(env, insuranceCompanyChecker, globalContext, messageHandler, logRepo, parserTaskPGroup)
    }

    @Bean
    IContextWithTTLSupport baoxianGlobalContext(
        @Qualifier('redisContextService') IContextService redisContextService,
        @Qualifier('baoxianJedisConnectionFactory') RedisConnectionFactory redisConnFactory
    ) {
        def redis = new StringRedisTemplate(redisConnFactory)
        redisContextService.getContext redis: redis, category: 'baoxian'
    }
}
