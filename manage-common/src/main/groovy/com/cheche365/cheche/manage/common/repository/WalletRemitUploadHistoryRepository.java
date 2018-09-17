package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.WalletRemitUploadHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by yinJianBin on 2018/4/5.
 */
@Repository
public interface WalletRemitUploadHistoryRepository extends PagingAndSortingRepository<WalletRemitUploadHistory, Long>, JpaSpecificationExecutor<WalletRemitUploadHistory> {
    WalletRemitUploadHistory findFirstByFileName(String fileName);
}
