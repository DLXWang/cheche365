package com.cheche365.cheche.operationcenter.service.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

/**
 * Created by yinJianBin on 2017/9/12.
 * 继承RedisHttpSessionConfiguration类,引入spring-session,将session交给spring-redis管理
 */
@Configurable
public class OperationCenterRedisHttpSessionConfiguration extends RedisHttpSessionConfiguration {

    public static final String KEY_PREFIX="opc";

    @Override
    @Bean
    @Autowired
    public RedisOperationsSessionRepository sessionRepository(
            @Qualifier("sessionRedisTemplate") RedisOperations<Object, Object> sessionRedisTemplate,
            ApplicationEventPublisher applicationEventPublisher) {

        OperationCenterRedisOperationSessionRepository sessionRepository = new OperationCenterRedisOperationSessionRepository(sessionRedisTemplate);
        sessionRepository.setDefaultMaxInactiveInterval(1800);
        sessionRepository.setRedisKeyNamespace(KEY_PREFIX);
        return sessionRepository;

    }

    @Bean
    public ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        //do nothing for now
//        super.setImportMetadata(importMetadata);
    }
}
