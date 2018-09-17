package com.cheche365.cheche.manage.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by yellow on 2017/9/26.
 * 采用redis对目标方法进行同步锁定
 * 目前只支持method 锁定，不能对数据锁定
 */
@Component
@Aspect
public class LockAspect {
    private Logger logger=LoggerFactory.getLogger(LockAspect.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final static String LOCK_KEY = "method.sync.lock";

    @Pointcut("@annotation(com.cheche365.cheche.manage.common.annotation.Lock)")
    public void setLock() {
    }

    @Around(value = "setLock()")
    public void lock(ProceedingJoinPoint point){
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Method realMethod=null;
        try {
            realMethod = point.getTarget().getClass().getDeclaredMethod(signature.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
           logger.error("the target method can not find ,class name:{}",point.getTarget().getClass().getName(),e);
        }
        String lockValue=point.getTarget().getClass().getName()+":"+realMethod.getName();
        if(stringRedisTemplate.opsForSet().isMember(LOCK_KEY,lockValue)){
            throw new RuntimeException("can not execute ,the method "+realMethod.getName()+" is locking ");
        }
        stringRedisTemplate.opsForSet().add(LOCK_KEY,lockValue);
        try {
            point.proceed();
        } catch (Throwable throwable) {
            logger.error("the target method execute error ->",lockValue,throwable);
        }finally {
            stringRedisTemplate.opsForSet().remove(LOCK_KEY,lockValue);
        }
    }
}
