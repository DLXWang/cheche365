package com.cheche365.cheche.operationcenter.security;

import com.cheche365.cheche.operationcenter.constants.OperationCenterConstants;
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
public class OperationCenterAccessDeniedHandler implements AccessDeniedHandler {

    private Logger logger = LoggerFactory.getLogger(OperationCenterAccessDeniedHandler.class);

    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        request.getSession().setAttribute(OperationCenterConstants.ACCESS_ERROR_CODE, OperationCenterConstants.ACCESS_ERROR_MESSAGE);
    }
}
