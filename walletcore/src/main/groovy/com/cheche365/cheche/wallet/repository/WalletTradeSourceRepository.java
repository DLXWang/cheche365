package com.cheche365.cheche.wallet.repository;

import com.cheche365.cheche.wallet.model.WalletTradeSource;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wangshaobin on 2017/6/13.
 */
@Repository
public interface WalletTradeSourceRepository extends PagingAndSortingRepository<WalletTradeSource, Long>, JpaSpecificationExecutor<WalletTradeSource> {
}
