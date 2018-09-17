package com.cheche365.cheche.web.util;


import com.cheche365.cheche.core.constants.Device;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.model.useragent.AndroidUserAgentHeader;
import com.cheche365.cheche.web.model.useragent.IOSUserAgentHeader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.DeviceUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * add by chenxz
 * 判断客户端设备类型
 */
public class UserDeviceUtil {

    public static final String USER_AGENT_HEADER = "User-Agent";

    public static final String SESSION_KEY_USER_AGENT_HEADER = "session_key_user_agent";

    public static final String SESSION_KEY_DEVICE_TYPE = "session_key_device_type";

    private static final Logger logger = LoggerFactory.getLogger(UserDeviceUtil.class);


    public static Device getDeviceType(HttpServletRequest request) {
        Device device = Device.UNKNOWN;
        Object deviceTypeInSession = request.getSession().getAttribute(SESSION_KEY_DEVICE_TYPE);
        if (deviceTypeInSession != null && StringUtils.isNotBlank(String.valueOf(deviceTypeInSession))) {
            device = CacheUtil.doJacksonDeserialize(String.valueOf(deviceTypeInSession), Device.class);
        } else {
            org.springframework.mobile.device.Device currentDevice = DeviceUtils.getCurrentDevice(request);
            String userAgentString = getUserAgent(request);
            if (userAgentString == null) {
                logger.error("user agent is null , will return Device is UNKNOWN !!!!! ");
                //throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "User-Agent is null");
                return device;
            }
            if (IOSUserAgentHeader.isIOSHeader(userAgentString) || AndroidUserAgentHeader.isAndroidHeader(userAgentString)) {
                device = Device.MOBILE;
            } else if (null != currentDevice && currentDevice.isNormal()) {
                device = Device.COMPUTER;
            } else if (null != currentDevice && (currentDevice.isTablet() || currentDevice.isMobile())) {
                device = Device.MOBILE;
            }

            if (device != null) {
                request.getSession().setAttribute(SESSION_KEY_DEVICE_TYPE, CacheUtil.doJacksonSerialize(device));
            }
        }
        return device;
    }



    /**
     * 获取 User-Agent ;先从session中取,如果session中没有,则从request的header中获取,最后存入session中
     * add by chenxz
     *
     * @param request
     * @return
     */
    public static String getUserAgent(HttpServletRequest request) {
        String userAgent = null;
        Object sessionUserAgentObj = request.getSession().getAttribute(SESSION_KEY_USER_AGENT_HEADER);
        if (sessionUserAgentObj != null) {
            userAgent = String.valueOf(sessionUserAgentObj);
        } else {
            if (StringUtils.isNotBlank(request.getHeader(USER_AGENT_HEADER))) {
                userAgent = ClientTypeUtil.getUserAgent(request.getHeader(USER_AGENT_HEADER));
                if (isOldAndroid(userAgent)) {
                    userAgent = AndroidUserAgentHeader.DEFAULT_BELOW14_ANDROID_USER_AGENT;
                }
            } else {
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    if (USER_AGENT_HEADER.equalsIgnoreCase(name)) {
                        userAgent = request.getHeader(name);
                    }
                }
            }
            if (StringUtils.isNotBlank(userAgent)) {
                request.getSession().setAttribute(SESSION_KEY_USER_AGENT_HEADER, userAgent);
            }
        }

        return userAgent;
    }

    private static boolean isOldAndroid(String userAgent) {
        return userAgent.contains("Android") && userAgent.contains("Dalvik");
    }


}
