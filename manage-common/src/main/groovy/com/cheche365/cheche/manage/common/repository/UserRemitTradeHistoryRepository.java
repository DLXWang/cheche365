package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.UserRemitTradeHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yinJianBin on 2018/4/4.
 */
@Repository
public interface UserRemitTradeHistoryRepository extends JpaSpecificationExecutor<UserRemitTradeHistory>, PagingAndSortingRepository<UserRemitTradeHistory, Long> {

    List<UserRemitTradeHistory> findByMerchantSeqNo(String merchantSeqNo);
}
