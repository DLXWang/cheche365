package com.cheche365.cheche.manage.common.interceptor;

import com.cheche365.cheche.common.util.ServletUtils;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志中添加内部账户名,IP地址.参照web端的日志拦截器
 * Created by sunhuazhong on 16-1-11.
 */
public class ManageCommonLogBackInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(ManageCommonLogBackInterceptor.class);

    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        startTime.set(System.currentTimeMillis());
        setLogValue(request);
        return super.preHandle(request, response, handler);
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove(WebConstants.IP);
        MDC.remove(WebConstants.USER);

        if (logger.isDebugEnabled()) {
            long spentTime = System.currentTimeMillis() - startTime.get();
            if (spentTime > 500) {
                logger.debug("found slow request {}, it spent {} milliseconds.", request.getRequestURL(), spentTime);
            }
        }
    }

    private void setLogValue(HttpServletRequest request) {
        String ip = StringUtils.EMPTY;
        String userName = StringUtils.EMPTY;
        try {
            //IP地址
            ip = ServletUtils.getIP(request);
            //用户名称
            SecurityContext context = SecurityContextHolder.getContext();
            if (context != null && context.getAuthentication() != null
                && !(context.getAuthentication().getPrincipal() instanceof String)) {
                org.springframework.security.core.userdetails.User user =
                    (org.springframework.security.core.userdetails.User) context.getAuthentication().getPrincipal();
                InternalUser internalUser = internalUserRepository.findFirstByEmail(user.getUsername());
                userName = internalUser.getName();
            }
        } catch (Exception ex) {
            logger.error("set log value error", ex);
            userName = StringUtils.EMPTY;
        }
        MDC.put(WebConstants.IP, ip);
        MDC.put(WebConstants.USER, userName);
    }
}
