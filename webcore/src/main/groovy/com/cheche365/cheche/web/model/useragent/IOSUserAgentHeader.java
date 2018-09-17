package com.cheche365.cheche.web.model.useragent;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.util.UserDeviceUtil;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * Created by zhengwei on 6/4/15.
 * IOS_4 User-Agent header example:
 * User-Agent : Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Mobile/14E8301 checheApp/(cc.app:cheche;cc.dev:Simulator;cc.ver:2.1.4;cc.osver:iOS 10.3.1;cc.screen:375x667;cc.appId:1055592103;cc.os:ios;cc.uuid:F36A5CB6-0A95-4E27-A9D3-7B8B98B642ED;cc.build:020104)
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class IOSUserAgentHeader extends UserAgentHeader {

    public static String IOS = "ios";
    public static boolean isIOSHeader(HttpServletRequest request) {
        String userAgentValue = getUserAgentValue(request);
        if (StringUtils.isBlank(userAgentValue)) {
            return false;
        }

        return isIOSHeader(userAgentValue);
    }

    public static boolean isIOSHeader(String userAgentValue) {

        return StringUtils.isNotBlank(userAgentValue)
            && userAgentValue.toLowerCase().contains("cc.uuid")
            && userAgentValue.toLowerCase().contains(IOS);
    }

    public static Channel getChannel(HttpServletRequest request){
        String userAgentValue = getUserAgentValue(request);
        if(userAgentValue.toLowerCase().contains(CLIENT_TYPE)){
            return UserAgentHeader.getAppChannel(userAgentValue);
        }else {
            return Channel.Enum.IOS_4;
        }
    }
}
