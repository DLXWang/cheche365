package com.cheche365.cheche.web.model;

import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.agent.ChannelAgent;

/**
 * Created by taichangwei on 2018/4/18.
 */
public class UserCallbackInfo {

    private User user;
    private ChannelAgent channelAgent;
    private Object objId; //uuid绑定的唯一对象id
    private String uuid;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChannelAgent getChannelAgent() {
        return channelAgent;
    }

    public void setChannelAgent(ChannelAgent channelAgent) {
        this.channelAgent = channelAgent;
    }

    public Object getObjId() {
        return objId;
    }

    public void setObjId(Object objId) {
        this.objId = objId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
