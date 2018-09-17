package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.abao.InsuranceProductType
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Created by mahong on 2016/11/25.
 */
@Repository
public interface InsuranceProductTypeRepository extends PagingAndSortingRepository<InsuranceProductType, Long> {
    InsuranceProductType findFirstByName(String name);
}
