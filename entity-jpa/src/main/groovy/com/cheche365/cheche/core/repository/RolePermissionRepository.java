package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Permission;
import com.cheche365.cheche.core.model.Role;
import com.cheche365.cheche.core.model.RolePermission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by liqiang on 3/20/15.
 */
@Repository
public interface RolePermissionRepository extends PagingAndSortingRepository<RolePermission,Long> {

    List<RolePermission> findByRole(Role role);

    RolePermission findByRoleAndPermission(Role role, Permission permission);

    List<RolePermission> findByPermission(Permission permission);

    @Query(value = "select distinct role from role_permission where permission = ?1", nativeQuery = true)
    List<BigInteger> findOrderCenterEnableLoginRole(Long permissionId);
}
