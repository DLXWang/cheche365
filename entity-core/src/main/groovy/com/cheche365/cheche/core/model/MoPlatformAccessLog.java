package com.cheche365.cheche.core.model;

import java.util.Date;

/**
 * Created by wangshaobin on 2017/8/16.
 */
public class MoPlatformAccessLog {
    private String id;
    private String ip;
    private String requestType;
    private String url;
    private String param;
    private String platform;
    private Date requestTime;
    /**
     * internalUser关联的属性太多，在批量保存到Mongodb时，会出现内存泄露的问题，因此只保存ID
     *
     * **/
    private Long internalUser;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Long getInternalUser() {
        return internalUser;
    }

    public void setInternalUser(Long internalUser) {
        this.internalUser = internalUser;
    }

    public static class PLATEFORM {
        public static final String ORDER_CENTER="orderCenter";
        public static final String ADMINISTER="administer";
        public static final String OPERATION_CENTER="operationCenter";
    }

}
