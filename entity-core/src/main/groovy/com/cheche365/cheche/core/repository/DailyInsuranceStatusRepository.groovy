package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.DailyInsuranceStatus
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
public interface DailyInsuranceStatusRepository extends PagingAndSortingRepository<DailyInsuranceStatus, Long> {

}
