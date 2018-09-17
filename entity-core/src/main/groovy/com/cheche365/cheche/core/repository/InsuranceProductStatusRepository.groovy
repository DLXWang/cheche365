package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.abao.InsuranceProductStatus
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Created by mahong on 2016/11/25.
 */
@Repository
public interface InsuranceProductStatusRepository extends PagingAndSortingRepository<InsuranceProductStatus, Long> {
}
