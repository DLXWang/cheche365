package com.cheche365.cheche.web.interceptor;

import com.cheche365.cheche.core.constants.Device;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.util.IpUtil;
import com.cheche365.cheche.web.service.http.SessionUtils;
import com.cheche365.cheche.web.util.UserDeviceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static com.cheche365.cheche.common.util.AopUtils._CHANNEL_CODE;
import static com.cheche365.cheche.common.util.AopUtils._DEVICE_TYPE;
import static com.cheche365.cheche.common.util.AopUtils._FLOW_ID;
import static com.cheche365.cheche.common.util.AopUtils._IP;
import static com.cheche365.cheche.common.util.AopUtils._SESSION_ID;
import static com.cheche365.cheche.common.util.AopUtils._MOBILE;

/**
 * 添加sessionId(如果IOS/Android端加入uuid),用户名,客户端类型,IP地址.
 * Created by chenxiaozhe on 15-7-28.
 */
public class LogbackInterceptor extends HandlerInterceptorAdapter {

    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    private Logger logger = LoggerFactory.getLogger(LogbackInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        startTime.set(System.currentTimeMillis());
        setLogValue(request);
        return super.preHandle(request,response,handler);
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove(_IP);
        MDC.remove(_MOBILE);
        MDC.remove(_DEVICE_TYPE);
        MDC.remove(_SESSION_ID);
        MDC.remove(_CHANNEL_CODE);
        MDC.remove(_FLOW_ID);

        if (logger.isDebugEnabled()) {
            long spentTime = System.currentTimeMillis() - startTime.get();
            if (spentTime > 500) {
                logger.debug("found slow request {}, it spent {} milliseconds.", request.getRequestURL(),spentTime);
            }
        }
    }

    private void setLogValue(HttpServletRequest request) {
        String deviceType = "UNKNOWN";
        String ip = StringUtils.EMPTY;
        String mobile = StringUtils.EMPTY;
        String sessionId = StringUtils.EMPTY;
        String channelCode = StringUtils.EMPTY;
        try {
            Device device = UserDeviceUtil.getDeviceType(request);
            deviceType = device.equals(Device.COMPUTER) ? "COMPUTER" : (device.equals(Device.MOBILE) ? "MOBILE" : "UNKNOWN");
            //IP地址
            ip = IpUtil.getIP(request);
            //用户名称
            mobile = StringUtils.EMPTY;
            Object user = SessionUtils.get(request.getSession(), SessionUtils.USER_RELATED);
            if (user != null) {
                mobile = ((User)user).getMobile();
            }
            //sessionId,(如果IOS端加入uuid)
            sessionId = request.getSession(true).getId();
            channelCode  = StringUtils.EMPTY;
            Object channelObj = request.getSession().getAttribute(WebConstants.SESSION_KEY_CLIENT_TYPE);
            if (channelObj != null) {
                channelCode  = channelObj.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MDC.put(_IP, ip);
        MDC.put(_MOBILE, mobile);
        MDC.put(_DEVICE_TYPE, deviceType);
        MDC.put(_SESSION_ID, sessionId);
        MDC.put(_CHANNEL_CODE, channelCode);
        MDC.put(_FLOW_ID, UUID.randomUUID().toString());
    }
}
