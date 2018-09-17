package com.cheche365.cheche.admin.web.model.account;

import com.cheche365.cheche.core.model.Gender;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.util.BeanUtil;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by liyh on 2015/9/9
 */
public class AccountViewModel {
    private Long id;
    private String userId;//liqiang@cheche365.com
    @NotNull
    private String name;
    @NotNull
    private String mobile;
    @NotNull
    private String email;
    private String gender;
    @NotNull
    private String password;
    private boolean disable;
    private String disableValue;//是否禁用
    private Date createTime;
    private Date updateTime;
    private Integer internalUserType;//系统用户类型，1-内部用户，2-外部用户
    private String roleIds;
    private String roleName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public String getDisableValue() {
        return disableValue;
    }

    public void setDisableValue(String disableValue) {
        this.disableValue = disableValue;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getInternalUserType() {
        return internalUserType;
    }

    public void setInternalUserType(Integer internalUserType) {
        this.internalUserType = internalUserType;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public static AccountViewModel createViewModel(InternalUser internalUser) {
        if(internalUser == null) {
            return null;
        }
        AccountViewModel viewModel = new AccountViewModel();
        String[] properties = {
            "id", "userId", "name", "mobile", "email", "disable"
        };
        BeanUtil.copyPropertiesContain(internalUser, viewModel, properties);
        return viewModel;
    }
}
