package com.cheche365.cheche.core.repository.tide;

import com.cheche365.cheche.core.model.tide.TideContract;
import com.cheche365.cheche.core.model.tide.TideContractRebate;
import com.cheche365.cheche.core.model.tide.TideRebateRecord;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

/**
 * Created by yinJianBin on 2018/4/19.
 */
public interface TideContractRebateRepository extends PagingAndSortingRepository<TideContractRebate, Long>, JpaSpecificationExecutor<TideContractRebate> {

    Iterable<TideContractRebate> findAllByEffectiveDateLessThanAndStatus(Date effectiveDate, Integer status);

    Iterable<TideContractRebate> findAllByExpireDateLessThanAndStatus(Date expireDate, Integer status);

    Iterable<TideContractRebate> findAllByTideContractId(Long tideContract_id);
}
