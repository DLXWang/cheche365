package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Institution;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/13.
 */
@Repository
public interface InstitutionRepository extends PagingAndSortingRepository<Institution, Long>, JpaSpecificationExecutor<Institution> {
    List<Institution> findByEnable(boolean disable);

    Institution findFirstByName(String name);

    @Query(value = "select count(distinct ins.id) from institution_rebate rebate left join area area on rebate.area = area.id left join institution ins on ins.id = rebate.institution " +
        "where ins.name like ?1 or area.name like ?1", nativeQuery = true)
    Integer countAssignedNameAndAreaName(String condition);
}
