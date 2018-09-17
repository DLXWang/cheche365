package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ChannelRebate;
import com.cheche365.cheche.core.model.ChannelRebateHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by yinJianBin on 2017/6/12.
 */
@Repository
public interface ChannelRebateHistoryRepository extends JpaSpecificationExecutor<ChannelRebateHistory>, PagingAndSortingRepository<ChannelRebateHistory, Long> {

    Page<ChannelRebateHistory> findByChannelRebateId(Long channelRebate_id, Pageable pageable);
}
