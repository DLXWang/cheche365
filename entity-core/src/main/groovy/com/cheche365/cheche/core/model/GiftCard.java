package com.cheche365.cheche.core.model;

import javax.validation.constraints.NotNull;

/**
 * Created by zhengwei on 6/3/15.
 */
public class GiftCard {

    @NotNull
    String id;

    Long channelId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
