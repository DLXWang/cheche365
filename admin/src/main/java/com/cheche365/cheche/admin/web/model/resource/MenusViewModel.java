package com.cheche365.cheche.admin.web.model.resource;

import com.cheche365.cheche.admin.web.model.permission.PermissionViewModel;

import java.util.List;

/**
 * Created by wangfei on 2015/9/12.
 */
public class MenusViewModel {
    private List<ResourceViewModel> firstMenus;
    private List<ResourceViewModel> secondMenus;
    private List<ResourceViewModel> thirdMenus;
    private ResourceViewModel current;
    private ResourceViewModel parent;
    private List<PermissionViewModel> permissions;

    public List<ResourceViewModel> getFirstMenus() {
        return firstMenus;
    }

    public void setFirstMenus(List<ResourceViewModel> firstMenus) {
        this.firstMenus = firstMenus;
    }

    public List<ResourceViewModel> getSecondMenus() {
        return secondMenus;
    }

    public void setSecondMenus(List<ResourceViewModel> secondMenus) {
        this.secondMenus = secondMenus;
    }

    public List<ResourceViewModel> getThirdMenus() {
        return thirdMenus;
    }

    public void setThirdMenus(List<ResourceViewModel> thirdMenus) {
        this.thirdMenus = thirdMenus;
    }

    public ResourceViewModel getCurrent() {
        return current;
    }

    public void setCurrent(ResourceViewModel current) {
        this.current = current;
    }

    public ResourceViewModel getParent() {
        return parent;
    }

    public void setParent(ResourceViewModel parent) {
        this.parent = parent;
    }

    public List<PermissionViewModel> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionViewModel> permissions) {
        this.permissions = permissions;
    }
}
