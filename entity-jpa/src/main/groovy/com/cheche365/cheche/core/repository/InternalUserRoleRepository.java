package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.InternalUserRole;
import com.cheche365.cheche.core.model.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by liqiang on 3/20/15.
 */
@Repository
public interface InternalUserRoleRepository extends PagingAndSortingRepository<InternalUserRole, Long> {
    List<InternalUserRole> findByInternalUser(InternalUser internalUser);

    void deleteByInternalUser(InternalUser internalUser);

    void deleteByInternalUserAndRoleIn(InternalUser internalUser, List<Role> roleList);

    InternalUserRole findFirstByInternalUser(InternalUser internalUser);

    List<InternalUserRole> findByRole(Role role);

    InternalUserRole findFirstByInternalUserAndRole(InternalUser user, Role role);

    @Query(value="select count(*) from internal_user_role iur, role role, role_permission rp " +
        "where iur.role = role.id and role.id = rp.role " +
        "and role.disable = 0 and iur.internal_user = ?1 and rp.permission = ?2" ,nativeQuery = true)
    Integer countByInternalUserAndPermission(Long internalUserId, Long permissionId);

    @Query(value="select role.name from internal_user_role iur, role role " +
        "where iur.role = role.id and role.disable = 0 and iur.internal_user = ?1" ,nativeQuery = true)
    List<String> listRoleNameByInternalUser(Long internalUserId);

    @Query(value="select per.code from internal_user_role iur, role role, role_permission rp, permission per " +
        "where iur.role = role.id and role.id = rp.role and rp.permission = per.id " +
        "and role.disable = 0 and iur.internal_user = ?1 and per.code like ?2" ,nativeQuery = true)
    List<String> listPermissionCodeByInternalUserAndPermission(Long internalUserId, String permissionCode);

    @Query("select iu from InternalUserRole iur, InternalUser iu where iur.internalUser = iu.id and iur.role = ?1 and iu.disable = ?2 and iu.internalUserType<>3")
    List<InternalUser> getInternalUsersByRoleAndDisable(Role role, boolean disable);

    @Query(value="select count(distinct role.id) from internal_user_role iur, role role, role_permission rp " +
        "where iur.role = role.id and role.id = rp.role " +
        "and role.disable = 0 and iur.internal_user = ?1 and rp.permission in (?2)" ,nativeQuery = true)
    BigInteger countByInternalUserAndPermission(Long internalUserId, List<Long> permissionIdList);
}
