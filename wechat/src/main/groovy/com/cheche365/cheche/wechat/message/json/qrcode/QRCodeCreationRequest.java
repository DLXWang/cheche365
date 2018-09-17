package com.cheche365.cheche.wechat.message.json.qrcode;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by liqiang on 7/15/15.
 */
public class QRCodeCreationRequest {
    @JsonProperty("action_name")
    private String actionName;

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public ActionInfo getActionInfo() {
        return actionInfo;
    }

    public void setActionInfo(ActionInfo actionInfo) {
        this.actionInfo = actionInfo;
    }

    @JsonProperty("expire_seconds")
    private long expireSeconds;

    @JsonProperty("action_info")
    private ActionInfo actionInfo;


}
