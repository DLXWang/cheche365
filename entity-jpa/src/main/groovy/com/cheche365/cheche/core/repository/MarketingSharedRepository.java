package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.MarketingRule;
import com.cheche365.cheche.core.model.MarketingShared;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by xu.yelong on 2016/8/17.
 */
@Repository
public interface MarketingSharedRepository  extends PagingAndSortingRepository<MarketingShared, Long>, JpaSpecificationExecutor<MarketingShared> {
    MarketingShared findFirstByMarketingRule(MarketingRule marketingRule);
}
