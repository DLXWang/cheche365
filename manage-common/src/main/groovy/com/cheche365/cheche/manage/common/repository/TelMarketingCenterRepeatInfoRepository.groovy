package com.cheche365.cheche.manage.common.repository

import com.cheche365.cheche.manage.common.model.TelMarketingCenterRepeatInfo
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface TelMarketingCenterRepeatInfoRepository extends PagingAndSortingRepository<TelMarketingCenterRepeatInfo, Long>, JpaSpecificationExecutor<TelMarketingCenterRepeatInfo> {

    @Query(value = "select * from tel_marketing_center_repeat_info t where t.source_table = ?1 and t.repeat_id = ?2", nativeQuery = true)
    List<TelMarketingCenterRepeatInfo> findBySourceTableAndRepeatId(String sourceTable, Long repeatId);

    TelMarketingCenterRepeatInfo findBySourceId(String sourceId)

}
