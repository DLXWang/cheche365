package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * 该业务类负责出单状态变更以及获取新状态下的出单信息
 * Created by sunhuazhong on 2015/5/8.
 */
@Service
public class OrderManageService {

    private Logger logger = LoggerFactory.getLogger(OrderManageService.class);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;


    public void updateStatus(PurchaseOrder purchaseOrder, OrderStatus orderStatus) {
        purchaseOrder.setOperator(internalUserManageService.getCurrentInternalUser());
        purchaseOrder.setStatus(orderStatus);
        purchaseOrder.setUpdateTime(Calendar.getInstance().getTime());
        purchaseOrderRepository.save(purchaseOrder);
    }

    public PurchaseOrder getPurchaseOrder(Long purchaseOrderId) {
        return purchaseOrderRepository.findOne(purchaseOrderId);
    }

    public QuoteRecord getQuoteRecordByPurchaseOrder(PurchaseOrder purchaseOrder) {
        return quoteRecordRepository.findOne(purchaseOrder.getObjId());
    }

}
