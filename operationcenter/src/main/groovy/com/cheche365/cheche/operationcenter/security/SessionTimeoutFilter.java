package com.cheche365.cheche.operationcenter.security;

import com.cheche365.cheche.manage.common.security.ManageCommonSecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/6/4.
 */
public class SessionTimeoutFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(SessionTimeoutFilter.class);
    private List<String> urlList = new ArrayList<>();

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
        urlList.add("/operationcenter");
        urlList.add("/operationcenter/login");
        urlList.add("/operationcenter/login/error");
        urlList.add("/operationcenter/logout");
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        // 用户请求URL
        String url = httpRequest.getRequestURI();
        // 登录请求、退出请求不需要处理
        if (urlList.contains(url) || !url.contains("/operationcenter")) {
            chain.doFilter(request, response);
        } else {
            // 验证当前登陆用户
            boolean isValidUser = checkCurrentInternalUser();
            // 超时处理，ajax请求超时设置超时状态，页面请求超时则返回提示并重定向
            if (session == null || isValidUser) {
                // 登陆url
                String loginUrl = httpRequest.getContextPath() + "/home.jsp";
                // 判断是否为ajax请求
                if (httpRequest.getHeader("x-requested-with") != null
                        && httpRequest.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
                    httpResponse.addHeader("sessionstatus", "timeOut");
                    httpResponse.addHeader("loginPath", loginUrl);
                    logger.info("session超时，请重新登录");
//                    throw new RuntimeException("session超时");
                } else {
                    String str = "<script language='javascript'>alert('会话过期，请重新登录。');"
                            + "window.top.location.href='"
                            + loginUrl
                            + "';</script>";
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
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    private boolean checkCurrentInternalUser() {
        boolean isValidUser = false;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            ManageCommonSecurityUser user = (ManageCommonSecurityUser) authentication.getPrincipal();
        } catch (Exception ex) {
            if (ex instanceof ClassCastException) {
                isValidUser = true;
            }
        }
        return isValidUser;
    }
}
