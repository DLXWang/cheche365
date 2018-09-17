package com.cheche365.cheche.admin.service;

import com.cheche365.cheche.admin.exception.OperationNotAllowedException;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.Permission;
import com.cheche365.cheche.core.model.Role;
import com.cheche365.cheche.core.repository.PermissionRepository;
import com.cheche365.cheche.core.repository.RoleRepository;
import com.cheche365.cheche.core.service.InternalUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wangfei on 2016/5/31.
 */
@Component
public class RoleAndPermissionChecker {
    private final int specialLevelValue = 1;//特殊的权限和角色
    private final int normalLevelValue = 0;//普通的权限和角色
    private final String defaultPrefix = ",";

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private InternalUserService internalUserService;

    /**
     * 校验新建的角色
     * @param internalUser
     * @param newRole
     */
    public void checkNewRole(InternalUser internalUser, Role newRole) {
        if (null != newRole.getLevel()) {
            if (specialLevelValue == newRole.getLevel() && !internalUserService.isSuperMan(internalUser)) {
                throw new OperationNotAllowedException("只有超级用户才可创建特殊角色");
            }
        }
    }

    /**
     * 校验将角色分配给用户
     * @param internalUser
     * @param roles
     */
    public void checkAssignedRolesToUser(InternalUser internalUser, String roles) {
        Set<Long> roleIdSet = getIdSetByPrefix(roles, defaultPrefix);
        if (!CollectionUtils.isEmpty(roleIdSet)) {
            List<Integer> levelList = roleRepository.getByIds(roleIdSet);
            if (!CollectionUtils.isEmpty(levelList)) {
                if (hasSpecial(levelList)) {
                    if (!internalUserService.isSuperMan(internalUser)) {//只有超级用户才可分配特殊角色
                        throw new OperationNotAllowedException("只有超级用户才可分配特殊角色");
                    }
                }
            }
        }
    }

    /**
     * 校验将某些角色附上具体的权限
     * @param internalUser
     * @param roles
     * @param permission
     */
    public void checkAssignedRolesToPermission(InternalUser internalUser, String roles, Permission permission) {
        Set<Long> roleIdSet = getIdSetByPrefix(roles, defaultPrefix);
        if (!CollectionUtils.isEmpty(roleIdSet)) {
            List<Integer> levelList = roleRepository.getByIds(roleIdSet);
            if (!CollectionUtils.isEmpty(levelList)) {
                switch (permission.getLevel()) {
                    case normalLevelValue://普通权限
                        if (hasSpecial(levelList)) {//普通权限不能分配给特殊角色
                            throw new OperationNotAllowedException("普通权限不能分配给特殊角色");
                        }
                        break;
                    case specialLevelValue://特殊权限
                        if (!internalUserService.isSuperMan(internalUser)) {//只有超级用户才可分配特殊权限
                            throw new OperationNotAllowedException("只有超级用户才可分配特殊权限");
                        } else {
                            if (hasNormal(levelList)) {//特殊权限不能分配给普通角色
                                throw new OperationNotAllowedException("特殊权限不能分配给普通角色");
                            }
                        }
                        break;
                    default:
                }
            }
        }
    }

    /**
     * 校验将某些权限分配给具体的角色
     * @param internalUser
     * @param permissions
     * @param role
     */
    public void checkAssignedPermissionsToRole(InternalUser internalUser, String permissions, Role role) {
        Set<Long> permissionIdSet = getIdSetByPrefix(permissions, defaultPrefix);
        if (!CollectionUtils.isEmpty(permissionIdSet)) {
            List<Integer> levelList = permissionRepository.getByIds(permissionIdSet);
            if (!CollectionUtils.isEmpty(levelList)) {
                switch (role.getLevel()) {
                    case normalLevelValue://普通角色
                        if (hasSpecial(levelList)) {//普通角色不能被分配特殊权限
                            throw new OperationNotAllowedException("普通角色不能被分配特殊权限");
                        }
                        break;
                    case specialLevelValue://特殊角色
                        if (!internalUserService.isSuperMan(internalUser)) {//只有超级管理员才可分配特殊权限
                            throw new OperationNotAllowedException("只有超级管理员才可分配特殊权限");
                        } else {
                            if (hasNormal(levelList)) {//特殊角色不能被分配普通权限
                                throw new OperationNotAllowedException("特殊角色不能被分配普通权限");
                            }
                        }
                        break;
                    default:
                }
            }
        }
    }

    private boolean hasSpecial(List<Integer> levelList) {
        return !CollectionUtils.isEmpty(levelList) && levelList.contains(specialLevelValue);
    }

    private boolean hasNormal(List<Integer> levelList) {
        return !CollectionUtils.isEmpty(levelList) && levelList.contains(normalLevelValue);
    }

    private Set<Long> getIdSetByPrefix(String strIds, String prefix) {
        if (StringUtils.isBlank(prefix)) prefix = defaultPrefix;
        Set<Long> idSet = new HashSet<>();
        if (StringUtils.isNotBlank(strIds)) {
            String[] ids = strIds.split(prefix);
            for (String id : ids) {
                idSet.add(Long.parseLong(id));
            }
        }
        return idSet;
    }
}
