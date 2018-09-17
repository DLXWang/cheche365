package com.cheche365.cheche.manage.common.repository;


import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.manage.common.model.ActivityMonitorUrl;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chenxiangyin on 2017/6/7.
 */
@Repository
public interface ActivityMonitorUrlRepository extends PagingAndSortingRepository<ActivityMonitorUrl,Long>, JpaSpecificationExecutor<ActivityMonitorUrl> {

    @Query(value = "select * from purchase_order po where po.obj_id=?1 and  (po.audit=1 or po.audit=10 or po.audit=20)  and po.type = 1 ", nativeQuery = true)
    List<ActivityMonitorUrl> findByQuoteRecordId(Long quoteRecordId);

    List<ActivityMonitorUrl> findByEnable(Boolean enable);

    @Query(value = "select amu.* from activity_monitor_url amu join marketing_success ms on ms.business_activity = amu.business_activity where ms.id = ?1", nativeQuery = true)
    ActivityMonitorUrl findByMarketingSuccessId(Long successId);

    ActivityMonitorUrl findFirstByBusinessActivity(BusinessActivity businessActivity);
}
