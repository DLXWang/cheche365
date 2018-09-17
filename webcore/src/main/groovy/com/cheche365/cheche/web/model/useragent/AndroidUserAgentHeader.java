package com.cheche365.cheche.web.model.useragent;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.web.util.UserDeviceUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by mahong on 2015/9/16.
 * Android User-Agent header example:
 * User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; H60-L01 Build/HDH60-L01) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/39.0.0.0 Mobile Safari/537.36 checheApp/(cc.app:cheche;cc.dev:H60-L01;cc.ver:2.1.1;cc.osver:Android5.1.1;cc.os:Android;cc.build:211;cc.uuid:ee3b71ef-a6d9-3a92-a017-aa16a53d7e36)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AndroidUserAgentHeader extends UserAgentHeader {

    public static final String DEFAULT_BELOW14_ANDROID_USER_AGENT = "{\"cc.app\":\"cheche\",\"cc.dev\":\"Default device\",\"cc.ver\":\"1.3.1\",\"cc.osver\":\"Android 4.1.2\",\"cc.os\":\"Android\",\"cc.uuid\":\"98f10382-3909-37ce-9f03-1e4eb2f9541b\",\"cc.other\":\"00000\",\"cc.screen\":\"320x568\"}";

    public static String ANDROID = "Android";
    public static boolean isAndroidHeader(HttpServletRequest request) {
        String userAgentValue = getUserAgentValue(request);
        if (StringUtils.isBlank(userAgentValue)) {
            return false;
        }

        return isAndroidHeader(userAgentValue);
    }

    public static boolean isAndroidHeader(String userAgentValue) {
        return StringUtils.isNotBlank(userAgentValue)
            && userAgentValue.toLowerCase().contains("cc.uuid")
            && userAgentValue.toLowerCase().contains("android");
    }

    public static Channel getChannel(HttpServletRequest request){
        String userAgentValue = getUserAgentValue(request);
        if(userAgentValue.toLowerCase().contains(CLIENT_TYPE)){
            return UserAgentHeader.getAppChannel(userAgentValue);
        }else {
            return Channel.Enum.ANDROID_6;
        }
    }
}
