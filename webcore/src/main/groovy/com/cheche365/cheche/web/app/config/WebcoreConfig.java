package com.cheche365.cheche.web.app.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created by zhengwei on 11/14/15.
 */

@EnableCaching
@Configuration
@ComponentScan({
    "com.cheche365.cheche.web",
    "com.cheche365.cheche.externalapi"
})
public class WebcoreConfig {


    @Bean(name = "cacheManager")
    @Primary
    public RedisCacheManager cacheManager(@Qualifier("redisTemplateCache")RedisTemplate<Object, Object> redisTemplate) {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
        redisCacheManager.setUsePrefix(true);
        return redisCacheManager;
    }

    @Bean(name = "jdkCacheManager")
    public RedisCacheManager jdkCacheManager(RedisTemplate<Object, Object> redisTemplate) {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
        redisCacheManager.setUsePrefix(true);
        return redisCacheManager;
    }

}
