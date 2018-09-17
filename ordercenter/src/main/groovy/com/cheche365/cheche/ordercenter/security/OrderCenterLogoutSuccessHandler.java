package com.cheche365.cheche.ordercenter.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/4/28.
 */
public class OrderCenterLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(OrderCenterLogoutSuccessHandler.class);

    private SessionRegistry sessionRegistry;

    public SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }

    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                         Authentication authentication) throws IOException, ServletException {
        if(authentication != null) {
            // 客服人员对应的session信息
            List<SessionInformation> sessionInformationList = sessionRegistry.getAllSessions(authentication.getPrincipal(), false);
            if (!CollectionUtils.isEmpty(sessionInformationList)) {
                // 设置客户人员的session过期
                sessionInformationList.forEach(sessionInformation -> sessionInformation.expireNow());
            }
            super.handle(request, response, authentication);
        }
    }
}
