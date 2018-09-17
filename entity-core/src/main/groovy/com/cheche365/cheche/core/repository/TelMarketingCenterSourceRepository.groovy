package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.TelMarketingCenterSource
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface TelMarketingCenterSourceRepository extends PagingAndSortingRepository<TelMarketingCenterSource, Long> {

    List<TelMarketingCenterSource> findByEnable(boolean enable);

    TelMarketingCenterSource findFirstByName(String name);
}
