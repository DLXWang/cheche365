package com.cheche365.cheche.core.repository.tide;

import com.cheche365.cheche.core.model.tide.TideContractRebateHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by mjg on 2018/4/19.
 */
public interface TideContractRebateHistoryRepository extends PagingAndSortingRepository<TideContractRebateHistory, Long>, JpaSpecificationExecutor<TideContractRebateHistory> {

}
