package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.PurchaseOrderRefund;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sunhuazhong on 2015/11/20.
 */
@Repository
public interface PurchaseOrderRefundRepository extends PagingAndSortingRepository<PurchaseOrderRefund, Long> {
    PurchaseOrderRefund findFirstByPurchaseOrder(PurchaseOrder purchaseOrder);
}
