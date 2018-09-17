package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.MarketingInsuranceType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketingInsuranceTypeRepository extends PagingAndSortingRepository<MarketingInsuranceType, Long> {
}
