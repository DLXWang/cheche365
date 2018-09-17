package com.cheche365.cheche.admin.service.permission;

import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Role;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.core.model.Permission;
import com.cheche365.cheche.core.model.Resource;
import com.cheche365.cheche.core.model.RolePermission;
import com.cheche365.cheche.core.repository.PermissionRepository;
import com.cheche365.cheche.core.repository.ResourceRepository;
import com.cheche365.cheche.core.repository.RolePermissionRepository;
import com.cheche365.cheche.core.repository.RoleRepository;
import com.cheche365.cheche.core.service.InternalUserService;
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

import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by liyha on 2015/9/15.
 */
@Service
public class PermissionManagementService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private InternalUserManageService internalUserManageService;

    @Autowired
    private InternalUserService internalUserService;

    /**
     * 获取list
     *
     * @param currentPage
     * @param pageSize
     * @param keyword
     * @return
     */
    public Page<Permission> findList(Integer currentPage, Integer pageSize, String keyword) {
        try {
            //查询全部或根据角色名查询
            return this.findBySpecAndPaginate(keyword, this.buildPageable(currentPage, pageSize));
        } catch (Exception e) {
            logger.error("list Permission by page has error", e);
        }
             return null;
    }

    private Page<Permission> findBySpecAndPaginate(String keyword, Pageable pageable) {
        return permissionRepository.findAll(new Specification<Permission>() {
            @Override
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<Permission> criteriaQuery = cb.createQuery(Permission.class);
                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                Path<String> namePath = root.get("name");
                predicateList.add(cb.like(namePath, keyword + "%"));

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


    @Transactional
    public boolean save(String roleList, Long permissionId) {
        try {
            Permission permission = permissionRepository.findOne(permissionId);
            List<RolePermission> rolePermissionList = rolePermissionRepository.findByPermission(permission);
            for (RolePermission rolePerRel : rolePermissionList) {
                rolePermissionRepository.delete(rolePerRel);
            }
            if(!StringUtil.isNull(roleList)) {
                String[] resultArray = roleList.split(",");
                for(String roleId : resultArray){
                    Role role=roleRepository.findOne(Long.valueOf(roleId));
                    if(role == null){
                        throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST,"未知的角色：角色ID ->"+roleId);
                    }
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setPermission(permission);
                    rolePermission.setRole(role);
                    rolePermissionRepository.save(rolePermission);
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("save role for permission has error", e);
            return false;
        }
    }

    public String getRoleIdByPermission(Long permissionId) {
        Permission permission = permissionRepository.findOne(permissionId);
        List<RolePermission> rolePermissionList = rolePermissionRepository.findByPermission(permission);
        String rolePermissionRel = "";
        for (RolePermission rolePerRel : rolePermissionList) {
            rolePermissionRel += rolePerRel.getRole().getId() + ",";
        }
        return rolePermissionRel;
    }

    public Permission findOne(Long permissionId) {
        return permissionRepository.findOne(permissionId);
    }

    public List<Permission> findByLevel(Integer level) {
        return permissionRepository.findByLevel(level);
    }

    public Map<Long,Resource> listResource(){
        Iterable<Resource> iterable = resourceRepository.findAll();
        Iterator<Resource> iterator = iterable.iterator();
        Map<Long,Resource> resourceMap = new HashMap<>();
        while (iterator.hasNext()){
            Resource resource = iterator.next();
            resourceMap.put(resource.getId(),resource);
        }
        return resourceMap;
    }
}
