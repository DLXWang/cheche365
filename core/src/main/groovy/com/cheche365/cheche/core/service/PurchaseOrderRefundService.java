package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.PurchaseOrderRefund;
import com.cheche365.cheche.core.repository.PurchaseOrderRefundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wangfei on 2015/11/30.
 */
@Service
@Transactional
public class PurchaseOrderRefundService {
    private Logger logger = LoggerFactory.getLogger(PurchaseOrderRefundService.class);

    @Autowired
    private PurchaseOrderRefundRepository purchaseOrderRefundRepository;

    public PurchaseOrderRefund findByPurchaseOrder(PurchaseOrder purchaseOrder) {
        if (null == purchaseOrder) return null;
        return purchaseOrderRefundRepository.findFirstByPurchaseOrder(purchaseOrder);
    }
}
