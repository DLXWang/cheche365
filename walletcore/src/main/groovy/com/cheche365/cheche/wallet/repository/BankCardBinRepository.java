package com.cheche365.cheche.wallet.repository;

import com.cheche365.cheche.wallet.model.BankCardBin;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


/**
 * Created by mjg on 2017/6/6.
 */
@Repository
public interface BankCardBinRepository extends PagingAndSortingRepository<BankCardBin, Long> , JpaSpecificationExecutor<BankCardBin> {


}
