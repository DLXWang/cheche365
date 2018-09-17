package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.OperationType
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.PurchaseOrderHistory
import com.cheche365.cheche.core.repository.PurchaseOrderHistoryRepository
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by mahong on 2016/9/22.
 */
@Service
public class PurchaseOrderHistoryService {

    @Autowired
    private PurchaseOrderHistoryRepository orderHistoryRepository;

    @Transactional
    public PurchaseOrderHistory saveOrderHistory(PurchaseOrder purchaseOrder, OperationType operationType) {
        PurchaseOrderHistory orderHistory = new PurchaseOrderHistory()
        BeanUtils.copyProperties(purchaseOrder, orderHistory, "id", "metaClass")

        orderHistory.setPurchaseOrder(purchaseOrder)
        orderHistory.setHistoryCreateTime(new Date())
        orderHistory.setOperationType(operationType)

        orderHistoryRepository.save(orderHistory)
    }

}
