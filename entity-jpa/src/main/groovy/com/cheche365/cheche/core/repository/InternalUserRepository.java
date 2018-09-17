package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.InternalUser;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface InternalUserRepository extends PagingAndSortingRepository<InternalUser, Long>, JpaSpecificationExecutor<InternalUser> {
    InternalUser findFirstByName(String name);

    InternalUser findFirstByNameAndDisable(String name, boolean disable);

    InternalUser findFirstByUserId(String userId);

    InternalUser findFirstByEmail(String email);

    InternalUser findFirstByEmailAndDisable(String email, boolean disable);

    InternalUser findFirstByEmailAndPasswordAndDisable(String email, String password, boolean disable);

    List<InternalUser> findByDisable(boolean disable);

    @Query(value = "select distinct iu.id from internal_user_role iur left join internal_user iu on iur.internal_user = iu.id where iu.disable = 0 and iur.role = ?1", nativeQuery = true)
    List<BigInteger> listEnableCustomer(Long roleId);

    @Query(value = "select count(distinct iu.id) from internal_user iu, " +
            "(select internal_user from internal_user_role where role in (select role from role_permission where permission = ?1))iur " +
            "where iu.email != 'superman@cheche365.com' and iu.id = iur.internal_user and iu.name <> 'system' and iu.disable = 0 " +
            "and case when ?2 is not null then (iu.name like CONCAT(?2,'%') or iu.mobile like CONCAT(?2,'%')) end ", nativeQuery = true)
    Integer countOrderCenterEnableLogin(Long permissionId, String keyword);

    @Query(value = "select distinct iu.* from internal_user_role iur left join internal_user iu on iur.internal_user = iu.id" +
            " where iu.disable = 0 and iur.role = ?1 order by iu.id limit 1", nativeQuery = true)
    InternalUser findFirstByRoleId(Long roleId);

    InternalUser findByName(String name);

    @Query(value = "select * from internal_user where id in (select internal_user from internal_user_role where role in(?1)) and email not in (?2) and `disable` = 0 group by id", nativeQuery = true)
    List<InternalUser> getUserByRolesAndEmails(List<Long> roleId, List<String> email);

    @Query(value = "select * from internal_user where id in (select internal_user from internal_user_role where role in(?1)) and name like CONCAT('%',?2,'%') and `disable` = 0 group by id", nativeQuery = true)
    List<InternalUser> getUserByRolesAndName(List<Long> roleId, String name);

    @Query(value = " select * from internal_user  " +
     " where id =(select operator from tel_marketing_center where mobile = ?1) ", nativeQuery = true)
    InternalUser getEmailWithPhone(String mobile);
}
