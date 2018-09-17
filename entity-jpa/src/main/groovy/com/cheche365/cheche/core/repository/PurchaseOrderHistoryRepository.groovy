package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.PurchaseOrderHistory
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Created by mahong on 2016/9/22.
 */
@Repository
public interface PurchaseOrderHistoryRepository extends PagingAndSortingRepository<PurchaseOrderHistory, Long> {

    @Query(value = "select * from purchase_order_history where purchase_order = ?1 order by id desc limit 1", nativeQuery = true)
    PurchaseOrderHistory findLastOrderHis(PurchaseOrder purchaseOrder)

    @Query(value = "select * from purchase_order_history where purchase_order = ?1 and obj_id = ?2", nativeQuery = true)
    PurchaseOrderHistory findByPurchaseOrderAndObjId(Long orderId, Long recordId)

}
