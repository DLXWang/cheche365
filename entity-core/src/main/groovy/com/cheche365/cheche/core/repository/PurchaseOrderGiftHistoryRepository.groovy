package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.PurchaseOrderGiftHistory
import com.cheche365.cheche.core.model.PurchaseOrderHistory
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Created by mahong on 2016/9/22.
 */
@Repository
public interface PurchaseOrderGiftHistoryRepository extends PagingAndSortingRepository<PurchaseOrderGiftHistory, Long> {
    List<PurchaseOrderGiftHistory> findByPurchaseOrderHistory(PurchaseOrderHistory purchaseOrderHistory);
}
