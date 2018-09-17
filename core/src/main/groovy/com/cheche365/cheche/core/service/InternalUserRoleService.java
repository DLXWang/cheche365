package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.InternalUserRole;
import com.cheche365.cheche.core.model.Permission;
import com.cheche365.cheche.core.model.Role;
import com.cheche365.cheche.core.repository.InternalUserRoleRepository;
import com.cheche365.cheche.core.repository.RoleRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wangfei on 2016/2/19.
 */
@Service
public class InternalUserRoleService {
    public static final String ROLE_PROPERTY_ID = "id";
    public static final String ROLE_PROPERTY_NAME = "name";

    @Autowired
    private InternalUserRoleRepository internalUserRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    public List<InternalUserRole> getRolesByInternalUser(InternalUser internalUser) {
        return internalUserRoleRepository.findByInternalUser(internalUser);
    }

    public void deleteAllInternalUserRoles(InternalUser internalUser) {
        internalUserRoleRepository.deleteByInternalUser(internalUser);
    }

    public void deletePartInternalUserRoles(InternalUser internalUser, Permission permission) {
        if (Permission.Enum.ORDER_CENTER.getId().equals(permission.getId())) {
            List<Role> roleList = roleService.getOrderCenterRoleList(Permission.Enum.ORDER_CENTER);
            if (!CollectionUtils.isEmpty(roleList)) {
                internalUserRoleRepository.deleteByInternalUserAndRoleIn(internalUser, roleList);
            }
        }
    }

    public void saveByStrRoleIdList(InternalUser internalUser, String roleIdList) {
        internalUserRoleRepository.save(createByStrRoleIdList(internalUser, roleIdList));
    }

    public List<InternalUserRole> createByStrRoleIdList(InternalUser internalUser, String roleIdList) {
        if (StringUtils.isBlank(roleIdList)) {
            return null;
        }

        List<InternalUserRole> userRoleList = new ArrayList<>();

        String[] roleIdArray = roleIdList.split(",");
        for (String roleId : roleIdArray) {
            InternalUserRole userRole = new InternalUserRole();
            userRole.setRole(roleRepository.findOne(Long.parseLong(roleId)));
            userRole.setInternalUser(internalUser);
            userRoleList.add(userRole);
        }

        return userRoleList;
    }

    public String getStrRoleProperty(List<InternalUserRole> internalUserRoleList, String property) {
        if (CollectionUtils.isEmpty(internalUserRoleList)) {
            return "";
        }

        StringBuffer strProperty = new StringBuffer("");
        internalUserRoleList.forEach(internalUserRole -> {
            switch (property) {
                case ROLE_PROPERTY_NAME:
                    strProperty.append(internalUserRole.getRole().getName()).append("，");
                    break;
                case ROLE_PROPERTY_ID:
                    strProperty.append(internalUserRole.getRole().getId().toString()).append(",");
                    break;
            }
        });

        return strProperty.substring(0, strProperty.length() - 1);
    }

    /**
     * 验证用户是否拥有相应的角色
     *
     * @param internalUser
     * @param roles
     * @return
     */
    public Boolean isHaveOne(InternalUser internalUser, Role... roles) {
        List<Role> roleList = Arrays.asList(roles);
        List<Role> internalUserRoles = this.getRolesByInternalUser(internalUser).stream().map(InternalUserRole::getRole).collect(Collectors.toList());
        for (Role role : roleList) {
            if (internalUserRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证用户是否拥有其所有角色
     * @param internalUser
     * @param roles
     * @return
     */
    public Boolean isHaveAll(InternalUser internalUser, Role... roles) {
        List<Role> roleList = Arrays.asList(roles);
        List<Role> internalUserRoles = this.getRolesByInternalUser(internalUser).stream().map(InternalUserRole::getRole).collect(Collectors.toList());
        return internalUserRoles.containsAll(roleList);
    }
}
