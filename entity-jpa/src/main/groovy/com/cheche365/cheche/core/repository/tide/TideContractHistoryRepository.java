package com.cheche365.cheche.core.repository.tide;

import com.cheche365.cheche.core.model.tide.TideContractHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by yinJianBin on 2018/4/19.
 */
public interface TideContractHistoryRepository extends PagingAndSortingRepository<TideContractHistory, Long>, JpaSpecificationExecutor<TideContractHistory> {

    Iterable<TideContractHistory> findAllByTideContractId(Long tideContract_id);

}
