package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.OrderAgent;
import com.cheche365.cheche.core.model.PurchaseOrder;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by xu.yelong on 2016/3/10.
 */
@Repository
public interface OrderAgentRepository extends PagingAndSortingRepository<OrderAgent,Long> {
    OrderAgent findByPurchaseOrder(PurchaseOrder purchaseOrder);
}
