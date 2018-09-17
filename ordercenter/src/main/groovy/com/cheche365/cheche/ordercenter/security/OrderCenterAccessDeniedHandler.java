package com.cheche365.cheche.ordercenter.security;

import com.cheche365.cheche.ordercenter.constants.ExceptionConstants;
import com.cheche365.cheche.ordercenter.constants.OrderCenterConstants;
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
public class OrderCenterAccessDeniedHandler implements AccessDeniedHandler {

    private Logger logger = LoggerFactory.getLogger(OrderCenterAccessDeniedHandler.class);

    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        if(logger.isDebugEnabled()) {
            logger.debug("order center access is denied is starting.");
        }

        request.getSession().setAttribute(ExceptionConstants.ACCESS_ERROR_CODE, ExceptionConstants.ACCESS_ERROR_MESSAGE);

        if(logger.isDebugEnabled()) {
            logger.debug("order center access is denied is finished.");
        }
    }
}
