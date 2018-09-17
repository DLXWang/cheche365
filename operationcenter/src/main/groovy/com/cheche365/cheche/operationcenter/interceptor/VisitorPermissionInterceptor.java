package com.cheche365.cheche.operationcenter.interceptor;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 环绕通知：在类方法调用前，先判断权限是否匹配。
 * Created by sunhuazhong on 2015/9/16.
 */
public class VisitorPermissionInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private InternalUserManageService internalUserManageService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            VisitorPermission requiredPermissions = ((HandlerMethod) handler).getMethodAnnotation(VisitorPermission.class);
            if (requiredPermissions == null) {
                return super.preHandle(request, response, handler);
            }
            //从注解中获取权限
            String value = requiredPermissions.value();
            // 没有设置权限，则直接执行方法
            if (StringUtils.isEmpty(value)) {
                return super.preHandle(request, response, handler);
            }
            // 当前用户是否有权限访问该方法
            boolean enable = false;
            // 当前用户的权限
            List<String> permissionList = internalUserManageService.listAuthority();
            // 判断是否权限匹配，如果权限匹配，则继续执行方法，否则抛出异常
            String[] values = value.split(",");
            for (String permissionCode : values) {
                if (permissionList.contains(permissionCode)) {
                    enable = true;
                    break;
                }
            }

            if (enable) {
                return super.preHandle(request, response, handler);
            } else {
                logger.error("你没有权限执行该操作");
                response.addHeader("sessionstatus", "accessDenied");
                return false;
            }
        }

        return super.preHandle(request, response, handler);
    }

    public InternalUserManageService getInternalUserManageService() {
        return internalUserManageService;
    }

    public void setInternalUserManageService(InternalUserManageService internalUserManageService) {
        this.internalUserManageService = internalUserManageService;
    }
}
