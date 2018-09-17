package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.AreaInsuranceTimeLimit
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Created by wangshaobin on 2016/7/25.
 */
@Repository
interface AreaInsuranceTimeLimitRepository extends PagingAndSortingRepository<AreaInsuranceTimeLimit, Long> {
}
