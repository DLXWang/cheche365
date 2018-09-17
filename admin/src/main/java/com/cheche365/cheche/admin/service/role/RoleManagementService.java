package com.cheche365.cheche.admin.service.role;

import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.core.model.Role;
import com.cheche365.cheche.core.model.RolePermission;
import com.cheche365.cheche.core.model.RoleType;
import com.cheche365.cheche.core.repository.PermissionRepository;
import com.cheche365.cheche.core.repository.RolePermissionRepository;
import com.cheche365.cheche.core.repository.RoleRepository;
import com.cheche365.cheche.core.service.InternalUserService;
import com.cheche365.cheche.core.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by guoweifu on 2015/9/11.
 */
@Service("roleManagementService")
@Transactional
public class RoleManagementService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private InternalUserService internalUserService;

    /**
     * 获取所有角色
     *
     * @param currentPage
     * @param pageSize
     * @param keyword
     * @return
     */
    public Page<Role> findAllRoles(Integer currentPage, Integer pageSize, String keyword) {
        try {
            //查询全部或根据角色名查询
            return this.findBySpecAndPaginate(keyword, this.buildPageable(currentPage, pageSize));
        } catch (Exception e) {
            logger.error("list role by page has error", e);
        }
        return null;
    }

    /**
     * 改变角色状态
     *
     * @param roleId
     * @param disable
     */
    public void changeStatus(Long roleId, Integer disable) {
        // 角色
        Role role = roleRepository.findOne(roleId);
        AssertUtil.notNull(role, "can not find role by id -> " + roleId);
        role.setDisable(disable == 1);// 启用或禁用角色
        roleRepository.save(role);
    }

    private Page<Role> findBySpecAndPaginate(String keyword, Pageable pageable) {
        return roleRepository.findAll(new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<Role> criteriaQuery = cb.createQuery(Role.class);

                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(keyword)) {
                    // 角色名称
                    Path<String> namePath = root.get("name");
                    predicateList.add(cb.like(namePath, keyword + "%"));
                }

                Path<Integer> levelPath = root.get("level");
                Predicate predicate = cb.equal(levelPath, 0);
                //超级管理员能看到超级权限
                if (internalUserService.isSuperMan(internalUserManageService.getCurrentInternalUser())) {
                    predicate = cb.or(
                        predicate,
                        cb.equal(levelPath, 1)
                    );
                }
                predicateList.add(predicate);

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    /**
     * 构建分页信息 以ID倒序排序
     *
     * @param currentPage 当前页面
     * @param pageSize    每页显示数
     * @return Pageable
     */
    private Pageable buildPageable(int currentPage, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return new PageRequest(currentPage - 1, pageSize, sort);
    }

    public List<Role> listRoleByUserType(RoleType roleType) {
        if (internalUserService.isSuperMan(internalUserManageService.getCurrentInternalUser())) {
            return roleRepository.findByDisableAndTypeOrderByIdDesc(false, roleType);
        } else {
            return roleRepository.findByDisableAndTypeAndLevelOrderByIdDesc(false, roleType, 0);
        }
    }

    @Transactional
    public void addRole(String permissions, Role role) {
        role.setDisable(true);
        role = roleRepository.save(role);
        this.saveRolePermissions(permissions, role);
    }

    @Transactional
    public void saveRolePermissions(String permissions, Role role) {
        List<RolePermission> rolePermissionList = rolePermissionRepository.findByRole(role);
        if (!CollectionUtils.isEmpty(rolePermissionList)) {
            rolePermissionRepository.delete(rolePermissionList);
        }

        if (StringUtils.isNotBlank(permissions)) {
            Set<RolePermission> rolePermissionSet = new HashSet<>();
            String[] array = permissions.split(",");
            for (int i = 0; i < array.length; i++) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRole(role);
                rolePermission.setPermission(permissionRepository.findOne(Long.parseLong(array[i])));
                rolePermissionSet.add(rolePermission);
            }
            rolePermissionRepository.save(rolePermissionSet);
        }
    }

    public Role findRoleById(Long roleId) {
        return roleRepository.findOne(roleId);
    }

    public void updateRole(String permissions, Role role) {
        Role existedRole = this.findRoleById(role.getId());
        String[] properties = {"name", "type", "description"};
        BeanUtil.copyPropertiesContain(role, existedRole, properties);
        existedRole = roleRepository.save(existedRole);
        this.saveRolePermissions(permissions, existedRole);
    }

    public List<RolePermission> findByRole(Role role) {
        return rolePermissionRepository.findByRole(role);
    }
}
