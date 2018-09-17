package com.cheche365.cheche.core.repository.tide;

import com.cheche365.cheche.core.model.tide.TideContractSupportArea;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by yinJianBin on 2018/4/19.
 */
public interface TideContractSupportAreaRepository extends PagingAndSortingRepository<TideContractSupportArea, Long>, JpaSpecificationExecutor<TideContractSupportArea> {

    TideContractSupportArea findByTideContractIdAndSupportAreaId(Long tideContract_id, Long supportArea_id);

    Iterable<TideContractSupportArea> findAllByTideContractId(Long tideContract_id);

}