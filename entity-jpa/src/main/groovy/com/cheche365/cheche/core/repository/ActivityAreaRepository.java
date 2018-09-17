package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ActivityArea;
import com.cheche365.cheche.core.model.BusinessActivity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/8/25.
 */
@Repository
public interface ActivityAreaRepository extends PagingAndSortingRepository<ActivityArea, Long> , JpaSpecificationExecutor<ActivityArea> {
    List<ActivityArea> findByBusinessActivity(BusinessActivity businessActivity);

    @Query(value = "select distinct area.name from activity_area act, area area where act.area = area.id and act.business_activity = ?1", nativeQuery = true)
    List<String> findAreaNameByBusinessActivity(Long businessActivityId);
}
