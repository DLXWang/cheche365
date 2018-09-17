package com.cheche365.cheche.rest.web.session;

import com.cheche365.cheche.core.constants.WebConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shanxf on 2017/10/12.
 * 车车对出单中心接口session维持策略
 */
@Service
public class OrderCenterSessionHandler {

    private Logger logger = LoggerFactory.getLogger(OrderCenterSessionHandler.class);

    public boolean isOrderCenter(HttpServletRequest request) {
        return StringUtils.isNotBlank(request.getHeader(WebConstants.ORDER_CENTER_TOKEN));
    }

    public String get(HttpServletRequest request) {
        logger.debug("order center session id " + request.getHeader(WebConstants.ORDER_CENTER_TOKEN));
        return request.getHeader(WebConstants.ORDER_CENTER_TOKEN);
    }
}
