package com.cheche365.cheche.wechat;


import com.cheche365.cheche.wechat.message.json.Result;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by liqiang on 3/20/15.
 */
public class AccessToken extends Result {

    @JsonProperty(value="access_token")
    private String token;
    @JsonProperty(value="expires_in")
    private long expiresIn;

    private long createdTime;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
