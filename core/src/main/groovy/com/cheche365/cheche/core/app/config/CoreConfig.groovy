package com.cheche365.cheche.core.app.config

import com.cheche365.cheche.core.service.IContextService
import com.cheche365.cheche.core.service.RedisContextService
import com.cheche365.cheche.core.service.ResourceProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.PropertySource
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.convert.DbRefResolver
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext

/**
 * @author Huabin
 */
@Configuration
@ComponentScan([
    'com.cheche365.cheche.core.model',
    'com.cheche365.cheche.core.context',
    'com.cheche365.cheche.core.service',
    'com.cheche365.cheche.core.message',
    'com.cheche365.cheche.core.serializer.cache',
    'com.cheche365.cheche.core.app.config'
])
@ImportResource('classpath:META-INF/spring/common-context.xml')
@PropertySource('classpath:/properties/core.properties')
@EnableConfigurationProperties(ResourceProperties)
class CoreConfig {

    @Bean
    IContextService redisContextService(@Qualifier('stringRedisTemplate') defaultRedis) {
        new RedisContextService(defaultRedis)
    }

    @Autowired
    private MongoDbFactory mongoFactory;

    @Autowired
    private MongoMappingContext mongoMappingContext;

    @Bean
    public MappingMongoConverter mongoConverter() throws Exception {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoFactory);
        MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        mongoConverter.setMapKeyDotReplacement("_");
        mongoConverter.afterPropertiesSet();
        return mongoConverter;
    }
}
