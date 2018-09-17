package com.cheche365.cheche.web.model.useragent;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.swing.event.ChangeEvent;
import java.util.HashMap;

/**
 * Created by mahong on 2015/9/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAgentHeader {
    public static final String USER_AGENT_HEADER_NAME = "User-Agent";

    public static final String CHE_CHE_SIGN = "checheApp/(";

    public static final String CLIENT_TYPE = "cc.client";

    @JsonProperty("cc.app")
    private String app;

    @JsonProperty("cc.dev")
    private String dev;

    @JsonProperty("cc.ver")
    private String ver;

    @JsonProperty("cc.osver")
    private String osver;

    @JsonProperty("cc.os")
    private String os;

    @JsonProperty("cc.uuid")
    private String uuid;

    @JsonProperty("cc.other")
    private String other;

    @JsonProperty("cc.screen")
    private String screen;

    @JsonProperty("cc.appId")
    private String appId;

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getOsver() {
        return osver;
    }

    public void setOsver(String osver) {
        this.osver = osver;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public static String getUserAgentValue(HttpServletRequest request) {
        String userAgentHeader = ClientTypeUtil.getUserAgent(request.getHeader(USER_AGENT_HEADER_NAME));
        if (StringUtils.isNotBlank(userAgentHeader)) {
            return userAgentHeader;
        }

        return null;
    }

    public static Channel getAppChannel(String userAgentValue){
        HashMap hashMap = CacheUtil.doJacksonDeserialize(userAgentValue, HashMap.class);
        return Channel.toChannel(Long.parseLong(hashMap.get(CLIENT_TYPE).toString()));
    }
}
