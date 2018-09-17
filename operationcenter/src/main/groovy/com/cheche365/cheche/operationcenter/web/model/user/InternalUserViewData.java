package com.cheche365.cheche.operationcenter.web.model.user;

import javax.validation.constraints.NotNull;

/**
 * Created by wangfei on 2015/5/26.
 */
public class InternalUserViewData {
    private Long id;
    @NotNull
    private String email;
    @NotNull
    private String name;
    @NotNull
    private String mobile;
    private String password;
    private String confirmPassword;
    private Long gender;
    private Integer disable;
    private String roles;
    private String createTime;
    private String updateTime;
    private String statusChange;
    private String permissionCode;
    private Boolean resetPasswordFlag = false;
    private Boolean resetPasswordLockFlag = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Long getGender() {
        return gender;
    }

    public void setGender(Long gender) {
        this.gender = gender;
    }

    public Integer getDisable() {
        return disable;
    }

    public void setDisable(Integer disable) {
        this.disable = disable;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatusChange() {
        return statusChange;
    }

    public void setStatusChange(String statusChange) {
        this.statusChange = statusChange;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public Boolean getResetPasswordFlag() {
        return resetPasswordFlag;
    }

    public void setResetPasswordFlag(Boolean resetPasswordFlag) {
        this.resetPasswordFlag = resetPasswordFlag;
    }

    public Boolean getResetPasswordLockFlag() {
        return resetPasswordLockFlag;
    }

    public void setResetPasswordLockFlag(Boolean resetPasswordLockFlag) {
        this.resetPasswordLockFlag = resetPasswordLockFlag;
    }
}
