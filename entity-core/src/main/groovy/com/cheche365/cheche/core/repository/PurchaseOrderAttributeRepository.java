package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.AttributeType;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.PurchaseOrderAttribute;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderAttributeRepository extends PagingAndSortingRepository<PurchaseOrderAttribute, Long> {

    PurchaseOrderAttribute findByPurchaseOrderAndType(PurchaseOrder purchaseOrder, AttributeType type);


    PurchaseOrderAttribute findFirstByPurchaseOrder(PurchaseOrder purchaseOrder);

}
