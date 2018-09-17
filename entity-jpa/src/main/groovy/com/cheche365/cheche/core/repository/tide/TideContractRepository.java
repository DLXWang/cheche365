package com.cheche365.cheche.core.repository.tide;

import com.cheche365.cheche.core.model.tide.TideContract;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

/**
 * Created by yinJianBin on 2018/4/19.
 */
public interface TideContractRepository extends PagingAndSortingRepository<TideContract, Long>, JpaSpecificationExecutor<TideContract> {

    Iterable<TideContract> findAllByContractName(String contractName);

    Integer countAllByContractCode(String contractCode);

    Integer countByContractName(String contractCode);

    Iterable<TideContract> findAllByEffectiveDateLessThanAndStatus(Date effectiveDate, Integer status);

    Iterable<TideContract> findAllByExpireDateLessThanAndStatus(Date expireDate, Integer status);
}
