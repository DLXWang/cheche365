package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.InsuranceType
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface InsuranceTypeRepository extends PagingAndSortingRepository<InsuranceType, Long> {
    InsuranceType findFirstByName(String name)
    @Query(value = "select * from insurance_type ", nativeQuery = true)
    List<InsuranceType> findAll();
}
