package com.cheche365.cheche.manage.common.repository

import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface TelMarketingCenterStatusRepository extends PagingAndSortingRepository<TelMarketingCenterStatus, Long> ,
        JpaSpecificationExecutor<TelMarketingCenterStatus> {
    TelMarketingCenterStatus findFirstByName(String name);
}
