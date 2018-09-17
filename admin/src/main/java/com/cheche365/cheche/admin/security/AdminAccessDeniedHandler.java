package com.cheche365.cheche.admin.security;

import com.cheche365.cheche.manage.common.constants.ManageCommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by sunhuazhong on 2015/4/30.
 */
public class AdminAccessDeniedHandler implements AccessDeniedHandler {

    private Logger logger = LoggerFactory.getLogger(AdminAccessDeniedHandler.class);

    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        request.getSession().setAttribute(ManageCommonConstants.ACCESS_ERROR_CODE, ManageCommonConstants.ACCESS_ERROR_MESSAGE);
    }
}
