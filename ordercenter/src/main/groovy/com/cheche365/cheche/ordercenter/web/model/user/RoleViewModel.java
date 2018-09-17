package com.cheche365.cheche.ordercenter.web.model.user;

import com.cheche365.cheche.core.model.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/2/1.
 */
public class RoleViewModel {
    private Long id;
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

    public static List<RoleViewModel> createViewModel(List<Role> roleList) {
        if (roleList == null)
            return null;

        List<RoleViewModel> viewModelList = new ArrayList<>();
        roleList.forEach(role -> {
            RoleViewModel viewData = new RoleViewModel();
            viewData.setId(role.getId());
            viewData.setName(role.getName());
            viewModelList.add(viewData);
        });

        return viewModelList;
    }
}
