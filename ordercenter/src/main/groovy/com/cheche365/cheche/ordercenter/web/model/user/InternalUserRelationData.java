package com.cheche365.cheche.ordercenter.web.model.user;

import javax.validation.constraints.NotNull;

/**
 * Created by wangfei on 2015/5/26.
 */
public class InternalUserRelationData {
    private Long id;
    @NotNull
    private Long customerUserId;
    @NotNull
    private Long internalUserId;
    @NotNull
    private Long externalUserId;
    private String customerUserName;
    private String internalUserName;
    private String externalUserName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerUserId() {
        return customerUserId;
    }

    public void setCustomerUserId(Long customerUserId) {
        this.customerUserId = customerUserId;
    }

    public Long getInternalUserId() {
        return internalUserId;
    }

    public void setInternalUserId(Long internalUserId) {
        this.internalUserId = internalUserId;
    }

    public Long getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(Long externalUserId) {
        this.externalUserId = externalUserId;
    }

    public String getCustomerUserName() {
        return customerUserName;
    }

    public void setCustomerUserName(String customerUserName) {
        this.customerUserName = customerUserName;
    }

    public String getInternalUserName() {
        return internalUserName;
    }

    public void setInternalUserName(String internalUserName) {
        this.internalUserName = internalUserName;
    }

    public String getExternalUserName() {
        return externalUserName;
    }

    public void setExternalUserName(String externalUserName) {
        this.externalUserName = externalUserName;
    }
}
