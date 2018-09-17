package com.cheche365.cheche.ordercenter.aop;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.manage.common.model.InternalUserDataPermission;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.ordercenter.annotation.DataPermission;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yellow on 2017/6/14.
 */
@Component
@Aspect
public class DataPermissionAspect {
    private Logger logger = LoggerFactory.getLogger(DataPermissionAspect.class);
    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private DataPermissionHandler dataPermissionHandler;

    @Pointcut("@annotation(com.cheche365.cheche.ordercenter.annotation.DataPermission)")
    public void executeService() {
    }

    @Around(value = "executeService()")
    public Object filter(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        //Field field = point.getTarget().getClass().getField("predicateList");
        Method realMethod = point.getTarget().getClass().getDeclaredMethod(signature.getName(), method.getParameterTypes());
        Object[] args = point.getArgs();
        if (args != null) {
            List<InternalUserDataPermission> filterPermission = new ArrayList<>();
            InternalUser internalUser = internalUserManageService.getCurrentInternalUser();
            Annotation[] annotations = realMethod.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == DataPermission.class) {
                    DataPermission permission = (DataPermission) annotation;
                    String code=permission.code();
                    String handler = permission.handler();
                    if (StringUtils.isEmpty(code) || StringUtils.isEmpty(handler)) {
                        logger.error("data permission filter annotation param error , class:{} method :{}", point.getTarget().getClass().getName(), method.getName());
                        break;
                    }
                    List<InternalUserDataPermission> internalUserDataPermissions = internalUser.getDataPermission();
                    for (InternalUserDataPermission internalUserDataPermission : internalUserDataPermissions) {
                        if(code.equals(internalUserDataPermission.getCode())){
                            filterPermission.add(internalUserDataPermission);
                        }
                    }
                    args = dataPermissionHandler.handle(args, filterPermission, handler);
                }
            }
        }
       return  point.proceed(args);
    }

}
