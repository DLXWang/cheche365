package com.cheche365.cheche.web.util

import com.cheche365.cheche.core.constants.Device
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.repository.ChannelRepository
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.web.model.useragent.AndroidUserAgentHeader
import com.cheche365.cheche.web.model.useragent.IOSUserAgentHeader
import com.cheche365.cheche.web.model.useragent.UserAgentHeader
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.constants.WebConstants.NON_AUTO_USER_AGENT_KEY
import static com.cheche365.cheche.core.constants.WebConstants.ORDER_CENTER_USER_AGENT_KEY
import static com.cheche365.cheche.core.constants.WebConstants.WECHAT_USER_AGENT_KEY
import static com.cheche365.cheche.core.constants.WebConstants.ALIPAY_USER_AGENT_KEY
import static com.cheche365.cheche.core.model.Channel.Enum.*

/**
 * Created by zhengwei on 7/17/15.
 */
public class ClientTypeUtil {

    private static Logger logger = LoggerFactory.getLogger(ClientTypeUtil.class);

    static void cacheChannel(HttpServletRequest request) {
        Channel clientType = getChannel(request);
        if (null == clientType) {
            logger.warn("无法判断客户端类型，User-Agent: {}", UserDeviceUtil.getUserAgent(request));
            return;
        }

        cacheChannel(request, clientType);
    }

    static void cacheChannel(HttpServletRequest request, Channel channel) {
        request.getSession().setAttribute(WebConstants.SESSION_KEY_CLIENT_TYPE, channel.getId());
    }

    static Channel getCachedChannel(HttpServletRequest request) {
        Object channel = request.getSession().getAttribute(WebConstants.SESSION_KEY_CLIENT_TYPE);
        if (null != channel) {
            return Channel.toChannel(Long.valueOf(channel.toString()));
        }
        return null;
    }

    static Channel getChannel(HttpServletRequest request) {

        Channel channel = getCachedChannel(request);
        if (null != channel) {
            return channel;
        }

        Device device = UserDeviceUtil.getDeviceType(request);
        String userAgent = UserDeviceUtil.getUserAgent(request);
        if (StringUtils.isNotBlank(userAgent)) {
            if (userAgent.startsWith(NON_AUTO_USER_AGENT_KEY)) {
                channel = ApplicationContextHolder.applicationContext.getBean(ChannelRepository).findOne(userAgent.split('\\.').last() as Long)
            } else if (userAgent.startsWith(ORDER_CENTER_USER_AGENT_KEY)) {
                channel = Channel.toChannel(userAgent.split('\\.').last() as Long)
            } else if (IOSUserAgentHeader.isIOSHeader(request)) {
                channel = IOSUserAgentHeader.getChannel(request)
            } else if (AndroidUserAgentHeader.isAndroidHeader(request)) {
                channel = AndroidUserAgentHeader.getChannel(request)
            } else if (headerContains(request, WebConstants.WECHAT_APP_HEADER)) {
                channel = WE_CHAT_APP_39;  //小程序和公众号user agent相同，没法通过这个值区分客户端，小程序用自定义的header来区分客户端，这个判断一定要写在公众号判断之前
            } else if (headerContains(request, WebConstants.CLAIM_APP_HEADER)) {
                channel = CLAIM_APP_214
            } else if (userAgent.contains(WECHAT_USER_AGENT_KEY)) {
                channel = WE_CHAT_3;
            } else if (userAgent.contains(ALIPAY_USER_AGENT_KEY)) {
                channel = ALIPAY_21;
            } else if (device == Device.COMPUTER) {
                channel = WEB_5
            }else {
                channel = WAP_8;
            }
        } else {
            channel = WAP_8
        }
        cacheChannel(request, channel);
        return channel;
    }

    static boolean headerContains(HttpServletRequest request, String headerName) {
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            if (headerName.equalsIgnoreCase(headerNames.nextElement().toString())) {
                return true;
            }
        }
        return false;
    }

    static String extractUUID(HttpServletRequest request) {

        UserAgentHeader userAgentHeader = getUserAgentHeaderByRequest(request);

        if (userAgentHeader == null || StringUtils.isBlank(userAgentHeader.getUuid())) {
            return null;
        }

        return userAgentHeader.getUuid();
    }

    static boolean isMobileClientType(HttpServletRequest request) {
        String userAgentValue = IOSUserAgentHeader.getUserAgentValue(request);
        return (userAgentValue != null) && userAgentValue.toLowerCase().contains("cc.uuid");
    }

    static UserAgentHeader getUserAgentHeaderByRequest(HttpServletRequest request) {
        String userAgentValue = getUserAgent(UserAgentHeader.getUserAgentValue(request))
        if (userAgentValue == null || !StringUtils.isNoneBlank(userAgentValue)) {
            return null;
        }
        if (IOSUserAgentHeader.isIOSHeader(userAgentValue)) {
            return CacheUtil.doJacksonDeserialize(userAgentValue, IOSUserAgentHeader.class);
        }
        if (AndroidUserAgentHeader.isAndroidHeader(userAgentValue)) {
            return CacheUtil.doJacksonDeserialize(userAgentValue, AndroidUserAgentHeader.class);
        }

        return null;
    }

    static String getUserAgent(String userAgent) {
        if (StringUtils.isNotBlank(userAgent) && userAgent.contains(UserAgentHeader.CHE_CHE_SIGN)) {
            String cheCheJson = userAgent.substring(userAgent.indexOf(UserAgentHeader.CHE_CHE_SIGN) + UserAgentHeader.CHE_CHE_SIGN.length()).with {
                it.substring(0, it.length() - 1)
            }
            List<String> iosList = Arrays.asList(cheCheJson.split(";"))
            HashMap map = [:]
            iosList.each { s -> map.put(s.split(":")[0], s.split(":")[1]) }
            return CacheUtil.doJacksonSerialize(map)
        }
        return userAgent
    }

    static Channel getMobileClientTypeByRequest(HttpServletRequest request) {
        if (IOSUserAgentHeader.isIOSHeader(request)) {
            return IOSUserAgentHeader.getChannel(request)
        }

        if (AndroidUserAgentHeader.isAndroidHeader(request)) {
            return AndroidUserAgentHeader.getChannel(request)
        }
        return null;
    }

    static boolean inWechat(HttpServletRequest request) {
        UserDeviceUtil.getUserAgent(request)?.contains(WECHAT_USER_AGENT_KEY)
    }

    static boolean isNonAutoUserAgent() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        UserDeviceUtil.getUserAgent(request)?.startsWith(NON_AUTO_USER_AGENT_KEY)
    }

    static boolean isNoAutoUser(HttpServletRequest request) {
        String userAgent = UserDeviceUtil.getUserAgent(request)
        return userAgent?.startsWith(NON_AUTO_USER_AGENT_KEY)
    }

    static Channel getChannel() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        getChannel(request)
    }

    static Channel getOauthChannel(HttpServletRequest request) {
        Object oauthChannel = request.getSession().getAttribute(WebConstants.OAUTH_CHANNEL)
        if (null != oauthChannel) {
            return Channel.toChannel(Long.valueOf(oauthChannel.toString()))
        }
        return null
    }

    static String getVersion(HttpServletRequest request){
        request.requestURI
            .split('/').find {
            it.startsWith('v') && (it - '.')[1..-1].every { version -> Character.isDigit(version as char) }
        }
    }
}
