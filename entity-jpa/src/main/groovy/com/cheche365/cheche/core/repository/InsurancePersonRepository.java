package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.abao.InsurancePerson;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by yinjianbin on 16-12-28.
 */
@Repository
public interface InsurancePersonRepository extends PagingAndSortingRepository<InsurancePerson, Long>, JpaSpecificationExecutor<InsurancePerson> {
}
