package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Permission;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Created by liqiang on 3/17/15.
 */
@Repository
public interface PermissionRepository extends PagingAndSortingRepository<Permission,Long>, JpaSpecificationExecutor<Permission> {
    Permission findFirstByCode(String code);

    @Query(value = "select distinct(p.level) from permission p where p.id in (?1)",nativeQuery = true)
    List<Integer> getByIds(Set<Long> ids);

    List<Permission> findByLevel(Integer level);
}
