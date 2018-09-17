package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.InstitutionTemp;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/13.
 */
@Repository
public interface InstitutionTempRepository extends PagingAndSortingRepository<InstitutionTemp, Long>, JpaSpecificationExecutor<InstitutionTemp> {
    List<InstitutionTemp> findByEnable(boolean disable);

    InstitutionTemp findFirstByName(String name);

    @Query(value = "select count(distinct ins.id) from institution_rebate_temp rebate left join area area on rebate.area = area.id left join institution_temp ins on ins.id = rebate.institution_temp " +
        "where ins.name like ?1 or area.name like ?1", nativeQuery = true)
    Integer countAssignedNameAndAreaName(String condition);
}
