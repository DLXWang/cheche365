package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.OfflineDataHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by yinjianbin on 2018/1/19.
 */
@Repository
public interface OfflineDataHistoryRepository extends PagingAndSortingRepository<OfflineDataHistory, Long>, JpaSpecificationExecutor<OfflineDataHistory> {
    OfflineDataHistory findByPurchaseOrderIdAndDataSource(Long orderId, Integer dataSource);
}
