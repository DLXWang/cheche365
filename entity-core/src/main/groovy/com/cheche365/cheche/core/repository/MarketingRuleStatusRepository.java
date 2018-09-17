package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.MarketingRuleStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by mahong on 2016/8/15.
 */
@Repository
public interface MarketingRuleStatusRepository extends PagingAndSortingRepository<MarketingRuleStatus, Long> {
}
