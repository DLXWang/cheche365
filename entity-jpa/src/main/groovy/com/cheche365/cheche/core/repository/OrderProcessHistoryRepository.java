package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.OrderProcessHistory;
import com.cheche365.cheche.core.model.PurchaseOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/13.
 */
@Repository
public interface OrderProcessHistoryRepository extends PagingAndSortingRepository<OrderProcessHistory, Long> {
    List<OrderProcessHistory> findByPurchaseOrder(PurchaseOrder purchaseOrder);

    List<OrderProcessHistory> findByPurchaseOrderOrderByCreateTime(PurchaseOrder purchaseOrder);

    OrderProcessHistory findFirstByPurchaseOrderOrderByCreateTimeDesc(PurchaseOrder purchaseOrder);

    @Query(value = "select oph.* from purchase_order po join order_process_history oph on po.id = oph.purchase_order where po.id = ?1 order by oph.id", nativeQuery = true)
    List<OrderProcessHistory> findByOrderIdOrderByCreateTime(Long orderId);
}
