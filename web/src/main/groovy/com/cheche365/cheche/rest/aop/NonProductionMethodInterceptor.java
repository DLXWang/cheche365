package com.cheche365.cheche.rest.aop;


import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.util.RuntimeUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by mahong on 2016/3/16.
 */
@Aspect
@Component
public class NonProductionMethodInterceptor{

    Logger logger = LoggerFactory.getLogger(NonProductionMethodInterceptor.class);

    @Around(value = "@annotation(com.cheche365.cheche.web.counter.annotation.NonProduction)")
    public Object invoke(ProceedingJoinPoint jointPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) jointPoint.getSignature();
        Method method = signature.getMethod();
        if (RuntimeUtil.isProductionEnv()) {
            logger.error("非法操作-生产环境下试图调用接口方法：{}", method);
            throw new BusinessException(BusinessException.Code.NON_FATAL, "非法操作");
        }
        return jointPoint.proceed();
    }
}
