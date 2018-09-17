package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.InsurancePurchaseOrderRebate;
import com.cheche365.cheche.core.model.PurchaseOrder;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sunhuazhong on 2016/5/26.
 */
@Repository
public interface InsurancePurchaseOrderRebateRepository extends PagingAndSortingRepository<InsurancePurchaseOrderRebate, Long> {
    InsurancePurchaseOrderRebate findFirstByPurchaseOrder(PurchaseOrder purchaseOrder);
}
