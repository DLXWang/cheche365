package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Role;
import com.cheche365.cheche.core.model.RoleType;
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
public interface RoleRepository extends PagingAndSortingRepository<Role,Long> , JpaSpecificationExecutor<Role> {
    Role findFirstByNameAndType(String name, RoleType type);
    List<Role> findByType( RoleType type);
    List<Role> findByDisableAndTypeOrderByIdDesc(boolean disable, RoleType roleType);
    List<Role> findByDisableAndTypeAndLevelOrderByIdDesc(boolean disable, RoleType roleType, Integer level);
    Role findByName(String name);

    @Query(value = "select distinct(r.level) from role r where r.id in (?1)",nativeQuery = true)
    List<Integer> getByIds(Set<Long> ids);
}
