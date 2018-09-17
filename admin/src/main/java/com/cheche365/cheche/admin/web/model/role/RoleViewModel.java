package com.cheche365.cheche.admin.web.model.role;

import javax.validation.constraints.NotNull;

/**
 * Created by guoweifu on 2015/9/11.
 */
public class RoleViewModel {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String roleType;//角色类型 内部用户、外部用户
    private String description;
    private boolean disable;//是否禁用
    private String permissions;//权限集合
    private Integer level;//角色类型 0普通 1特殊

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

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
