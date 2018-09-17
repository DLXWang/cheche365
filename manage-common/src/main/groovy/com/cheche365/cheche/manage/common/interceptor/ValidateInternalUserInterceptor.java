package com.cheche365.cheche.manage.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * 验证当前登陆用户
 * Created by sunhuazhong on 2016/6/7.
 */
public class ValidateInternalUserInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<String> urlList = Arrays.asList(
            "/admin", "/admin/login", "/admin/login/error", "/admin/logout",
            "/orderCenter", "/orderCenter/login", "/orderCenter/login/error", "/orderCenter/logout",
            "/operationcenter", "/operationcenter/login", "/operationcenter/login/error", "/operationcenter/logout");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();//用户请求URL
        if (!urlList.contains(url)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.error("当前用户会话过期，请重新登录,URL:{}", url);
                redirectToHomePage(request, response);
                return false;
            }
        }

        return super.preHandle(request, response, handler);
    }

    private void redirectToHomePage(HttpServletRequest request, HttpServletResponse response) {
        // 登陆url
        String loginUrl = request.getContextPath() + "/home.jsp";
        // 判断是否为ajax请求
        if (request.getHeader("x-requested-with") != null
                && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
            response.addHeader("sessionstatus", "timeOut");
            response.addHeader("loginPath", loginUrl);
        } else {
            String str = "<script language='javascript'>alert('会话过期，请重新登录。');"
                    + "window.top.location.href='" + loginUrl + "';</script>";
            response.setContentType("text/html;charset=UTF-8");// 解决中文乱码
            try {
                PrintWriter writer = response.getWriter();
                writer.write(str);
                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
