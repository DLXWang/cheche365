package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.PurchaseOrderAmendStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
@Repository
public interface PurchaseOrderAmendStatusRepository extends PagingAndSortingRepository<PurchaseOrderAmendStatus,Long> {

    PurchaseOrderAmendStatus findById(Long id);

}
