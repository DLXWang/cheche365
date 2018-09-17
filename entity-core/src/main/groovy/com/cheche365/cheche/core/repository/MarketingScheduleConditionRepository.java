package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.MarketingScheduleCondition;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by mahong on 2016/6/2.
 */
@Repository
public interface MarketingScheduleConditionRepository extends PagingAndSortingRepository<MarketingScheduleCondition, Long> {
}
