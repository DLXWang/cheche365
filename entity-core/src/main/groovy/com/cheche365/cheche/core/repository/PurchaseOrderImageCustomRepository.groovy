package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.PurchaseOrderImageCustom
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface PurchaseOrderImageCustomRepository extends PagingAndSortingRepository<PurchaseOrderImageCustom, Long> {

    PurchaseOrderImageCustom findFirstByPurchaseOrderOrderByIdDesc(PurchaseOrder purchaseOrder)

}
