package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.InsurancePackage;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsurancePackageRepository extends PagingAndSortingRepository<InsurancePackage, Long> {

    InsurancePackage findFirstByUniqueString(String uniqueString);
}
