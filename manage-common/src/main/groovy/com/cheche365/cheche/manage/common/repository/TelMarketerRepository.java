package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.TelMarketer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelMarketerRepository extends PagingAndSortingRepository<TelMarketer, Long>, JpaSpecificationExecutor<TelMarketer> {

    @Query(value = " SELECT iu.id,iu.`name`,IFNULL(utci.bind_tel,''),IFNULL(utci.cno,'') " +
        "FROM internal_user iu " +
        "JOIN internal_user_role iur ON iur.internal_user = iu.id  " +
        "JOIN role_permission rp ON rp.role = iur.role AND rp.permission = 185  " +
        "LEFT JOIN tel_marketer utci ON utci.`user` = iu.id " +
        "WHERE iu.`disable` = 0 " +
        "GROUP BY iur.internal_user  LIMIT ?1 OFFSET ?2 ", nativeQuery = true)
    List<Object[]> findAllTelMarketer(Integer pageSize, Integer startIndex);
    @Query(value = " SELECT count(DISTINCT(iu.id)) " +
        "FROM internal_user iu " +
        "JOIN internal_user_role iur ON iur.internal_user = iu.id " +
        "JOIN role_permission rp ON rp.role = iur.role " +
        "AND rp.permission = 185 " +
        "WHERE iu.`disable` = 0 ", nativeQuery = true)
    Long countAllTelMarketer();

    TelMarketer findFirstByUser(Long user);

}
