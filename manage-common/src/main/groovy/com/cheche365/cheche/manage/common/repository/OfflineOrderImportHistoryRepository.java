package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.OfflineOrderImportHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by yinJianBin on 2017/11/5.
 */
@Repository
public interface OfflineOrderImportHistoryRepository extends PagingAndSortingRepository<OfflineOrderImportHistory, Long>, JpaSpecificationExecutor<OfflineOrderImportHistory> {

    OfflineOrderImportHistory findFirstByStatus(Boolean status);
}
