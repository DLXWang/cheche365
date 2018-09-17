package com.cheche365.cheche.marketing.model;

import java.io.Serializable;

/**
 * Created by cheche on 2015/11/19.
 */
public class AssistInfo implements Serializable{

    private static final long serialVersionUID = 565956866929921160L;
    private String nickName;
    private String openId;
    private String dateTime;

    public AssistInfo() {
    }

    public AssistInfo(String nickName, String openId, String dateTime) {
        this.nickName = nickName;
        this.openId = openId;
        this.dateTime = dateTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
