package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.PurchaseOrderImageStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderImageStatusRepository extends PagingAndSortingRepository<PurchaseOrderImageStatus, Long> {

    PurchaseOrderImageStatus findFirstByPurchaseOrder(PurchaseOrder purchaseOrder);

    @Query(value = "select * from purchase_order_image_status where purchase_order = ?1 limit 1", nativeQuery = true)
    PurchaseOrderImageStatus findFirstByPurchaseOrderId(Long purchaseOrderId);
}
