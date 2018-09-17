package com.cheche365.cheche.rest.aop

import com.cheche365.cheche.core.annotation.ConcurrentApiCall
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.exception.ConcurrentApiCallLockException
import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.service.DoubleDBService
import groovy.util.logging.Slf4j
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

import java.util.concurrent.TimeUnit

/**
 * Created by tongsong on 2016/12/27 0027.
 *
 */
@Aspect
@Component
@Slf4j
public class ConcurrentApiCallInterceptor {

    static final String CONCURRENT_API_CALL_LOCK_PREFIX = "api:concurrent:call:lock";

    @Autowired
    DoubleDBService mongoDBService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Around(value = "@annotation(com.cheche365.cheche.core.annotation.ConcurrentApiCall)")
    public Object invoke(ProceedingJoinPoint jointPoint) throws Throwable {

        ConcurrentApiCall annotation = ((MethodSignature) jointPoint.getSignature()).getMethod().getAnnotation(ConcurrentApiCall.class)
        String lockKey = generateLockKey(annotation, jointPoint.args)

        log.debug('生成分布式并发锁, key {}', lockKey)
        def locked = false;
        try {
            locked = getDistributedLock(lockKey)
            if(!locked){
                onConcurrentCall(lockKey, jointPoint.args)
            }
            return jointPoint.proceed();
        } finally {
            if(!annotation.exclusive() && locked) {
                releaseDistributedLock(lockKey);
            }
        }
    }

    def generateLockKey(ConcurrentApiCall annotation, Object[] args){
        def keyGenerator = annotation.value().newInstance(this, this)
        return keyGenerator(args)
    }

    def onConcurrentCall(lockKey, args){
        def argsString = args?.findAll {it}?.collect {it.toString()}?.join(',')
        def logContent = "并发api调用，key: ${lockKey} args: ${argsString}"
        log.debug(logContent)
        new MoApplicationLog(
            logType: LogType.Enum.CONCURRENT_API_CALL_36,
            logMessage: logContent,
            createTime: new Date()
        ).with {
            mongoDBService.saveApplicationLog(it)
        }
        throw new ConcurrentApiCallLockException(BusinessException.Code.OPERATION_NOT_ALLOWED, "请求正在处理中")
    }

    public boolean getDistributedLock(String key) {
        boolean locked = redisTemplate.opsForValue().setIfAbsent(CONCURRENT_API_CALL_LOCK_PREFIX+key, "true");
        if (locked) {
            redisTemplate.expire(CONCURRENT_API_CALL_LOCK_PREFIX+key, 30, TimeUnit.SECONDS);
        }
        return locked;
    }

    public void releaseDistributedLock(String key){
        redisTemplate.delete(CONCURRENT_API_CALL_LOCK_PREFIX+key);
    }
}
