package com.cheche365.cheche.rest.aop

import com.cheche365.cheche.web.counter.annotation.CountApiInvoke
import com.cheche365.cheche.web.counter.icounter.APICounter
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

/**
 * Created by zhengwei on 8/27/15.
 */
@Aspect
@Component
class APICounterInterceptor {

    private List<APICounter> counters;

    APICounterInterceptor(List<APICounter> counters){
        this.counters = counters
    }

    @After(value = "@annotation(com.cheche365.cheche.web.counter.annotation.CountApiInvoke)")
    void afterInvoke(JoinPoint jointPoint) throws Throwable {

        CountApiInvoke countApiInvoke = ((MethodSignature) jointPoint.getSignature()).getMethod().getAnnotation(CountApiInvoke.class)
        counters.find {it.apiName() == countApiInvoke.value()}.count()
    }
}
