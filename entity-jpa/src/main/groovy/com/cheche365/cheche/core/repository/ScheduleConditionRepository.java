package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ScheduleCondition;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lyh on 2015/10/13.
 */
@Repository
public interface ScheduleConditionRepository extends PagingAndSortingRepository<ScheduleCondition, Long>, JpaSpecificationExecutor<ScheduleCondition> {
    ScheduleCondition findFirstByName(String name);

    @Query(value = " SELECT * FROM schedule_condition where marketing is not null ", nativeQuery = true)
    List<ScheduleCondition> findMarketingScheduleConditions();
}
