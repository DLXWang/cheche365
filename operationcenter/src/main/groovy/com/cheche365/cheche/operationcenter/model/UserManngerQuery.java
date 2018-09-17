package com.cheche365.cheche.operationcenter.model;

public class UserManngerQuery {

    private Long id;
    private Long[] area;
    private Long channelId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long[] getArea() {
        return area;
    }

    public void setArea(Long[] area) {
        this.area = area;
    }

    public Long getChannelId() { return channelId; }

    public void setChannelId(Long channelId) { this.channelId = channelId; }
}
