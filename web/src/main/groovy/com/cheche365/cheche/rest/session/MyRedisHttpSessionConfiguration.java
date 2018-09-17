package com.cheche365.cheche.rest.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

/**
 * Created by liqiang on 10/31/15.
 */
@Configuration
@EnableScheduling
public class MyRedisHttpSessionConfiguration extends RedisHttpSessionConfiguration {

    @Override
    @Bean
    @Autowired
    public RedisOperationsSessionRepository sessionRepository(
        @Qualifier("sessionRedisTemplate") RedisOperations<Object, Object> sessionRedisTemplate,
        ApplicationEventPublisher applicationEventPublisher) {
        RedisOperationsSessionRepository sessionRepository = new MyRedisOperationsSessionRepository(sessionRedisTemplate);
        sessionRepository.setDefaultMaxInactiveInterval(1800);
        return sessionRepository;
    }

    public void setImportMetadata(AnnotationMetadata importMetadata) {

//        Map<String, Object> enableAttrMap = importMetadata.getAnnotationAttributes(EnableRedisHttpSession.class.getName());
//        AnnotationAttributes enableAttrs = AnnotationAttributes.fromMap(enableAttrMap);
//        if(enableAttrs == null) {
//            // search parent classes
//            Class<?> currentClass = ClassUtils.resolveClassName(importMetadata.getClassName(), beanClassLoader);
//            for(Class<?> classToInspect = currentClass ;classToInspect != null; classToInspect = classToInspect.getSuperclass()) {
//                EnableRedisHttpSession enableRedisHttpSessionAnnotation = AnnotationUtils.findAnnotation(classToInspect, EnableRedisHttpSession.class);
//                if(enableRedisHttpSessionAnnotation == null) {
//                    continue;
//                }
//                enableAttrMap = AnnotationUtils
//                    .getAnnotationAttributes(enableRedisHttpSessionAnnotation);
//                enableAttrs = AnnotationAttributes.fromMap(enableAttrMap);
//            }
//        }
//        maxInactiveIntervalInSeconds = enableAttrs.getNumber("maxInactiveIntervalInSeconds");
    }
}
