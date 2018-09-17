package com.cheche365.cheche.admin.web.model.permission;

/**
 * Created by wangfei on 2015/9/14.
 */
public class PermissionViewModel {
    private Long id;
    private String firstMenu;
    private String secondMenu;
    private String thirdMenu;
    private String name;

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

    public String getFirstMenu() {
        return firstMenu;
    }

    public void setFirstMenu(String firstMenu) {
        this.firstMenu = firstMenu;
    }

    public String getSecondMenu() {
        return secondMenu;
    }

    public void setSecondMenu(String secondMenu) {
        this.secondMenu = secondMenu;
    }

    public String getThirdMenu() {
        return thirdMenu;
    }

    public void setThirdMenu(String thirdMenu) {
        this.thirdMenu = thirdMenu;
    }
}
