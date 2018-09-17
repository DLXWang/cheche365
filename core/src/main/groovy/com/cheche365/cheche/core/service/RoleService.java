package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.Permission;
import com.cheche365.cheche.core.model.Role;
import com.cheche365.cheche.core.model.RolePermission;
import com.cheche365.cheche.core.repository.RolePermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 2016/2/19.
 */
@Service
public class RoleService {

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    public List<Role> getOrderCenterRoleList(Permission permission) {
        List<RolePermission> rolePermissionList = rolePermissionRepository.findByPermission(permission);
        if (!CollectionUtils.isEmpty(rolePermissionList)) {
            List<Role> roleList = new ArrayList<>();
            rolePermissionList.forEach(rolePermission -> {
                if (!rolePermission.getRole().isDisable()) {
                    roleList.add(rolePermission.getRole());
                }
            });
            return roleList;
        }

        return null;
    }

}
