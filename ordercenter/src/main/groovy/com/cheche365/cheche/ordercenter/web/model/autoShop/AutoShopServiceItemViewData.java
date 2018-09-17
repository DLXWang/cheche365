package com.cheche365.cheche.ordercenter.web.model.autoShop;

/**
 * Created by wangfei on 2015/6/3.
 */
public class AutoShopServiceItemViewData {
    private Long id;
    private String name;
    private boolean disabled;
    private String comments;
    private Integer serviceKind;//1:基本服务.2:特色服务

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getServiceKind() {
        return serviceKind;
    }

    public void setServiceKind(Integer serviceKind) {
        this.serviceKind = serviceKind;
    }
}
