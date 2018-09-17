package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.OrderProcessHistoryRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

/**
 * Created by wangfei on 2015/11/17.
 */
@Service
public class OrderProcessHistoryService {
    private Logger logger = LoggerFactory.getLogger(OrderProcessHistoryService.class);

    @Autowired
    private OrderProcessHistoryRepository orderProcessHistoryRepository;

    public OrderProcessHistory createHistory(InternalUser operator, PurchaseOrder purchaseOrder,
                                             Long oriStatusId,String oriStatusStr, Long newStatusId,String newStatusStr, OrderProcessType type,String comment) {
        OrderProcessHistory history = new OrderProcessHistory();
        history.setPurchaseOrder(purchaseOrder);
        history.setCurrentStatus(newStatusId);
        history.setOrderProcessType(type);
        history.setCreateTime(Calendar.getInstance().getTime());
        history.setOperator(operator);
        if(StringUtils.isEmpty(comment)){
            StringBuffer commentSb = new StringBuffer();
            commentSb.append("订单状态");
            if (null != oriStatusId) {
                commentSb.append("由").append("[").append(oriStatusStr).append("]");
                history.setOriginalStatus(oriStatusId);
            }
            commentSb.append("改变为").append("[").append(newStatusStr).append("]");
            comment=commentSb.toString();
        }
        history.setComment(comment.toString());
        return history;
    }

    public OrderProcessHistory saveChangeStatusHistory(InternalUser operator, PurchaseOrder purchaseOrder,
                                                       OrderCooperationStatus oriStatus, OrderCooperationStatus newStatus) {
        return orderProcessHistoryRepository.save(createHistory(operator,purchaseOrder,oriStatus.getId(),oriStatus.getStatus(),newStatus.getId(),newStatus.getStatus(),OrderProcessType.Enum.COOPERATION,null));
    }

    public OrderProcessHistory saveChangeStatusHistory(InternalUser operator, PurchaseOrder purchaseOrder,
                                                       OrderTransmissionStatus oriStatus, OrderTransmissionStatus newStatus) {
        return orderProcessHistoryRepository.save(createHistory(operator,purchaseOrder,oriStatus.getId(),oriStatus.getStatus(),newStatus.getId(),newStatus.getStatus(),OrderProcessType.Enum.INDEPENDENCE,null));
    }

    public OrderProcessHistory saveChangeStatusHistory(InternalUser operator, PurchaseOrder purchaseOrder,
                                                       OrderTransmissionStatus oriStatus, OrderTransmissionStatus newStatus, String comment) {
        return orderProcessHistoryRepository.save(createHistory(operator,purchaseOrder,
            oriStatus==null?null:oriStatus.getId(),
            oriStatus==null?null:oriStatus.getStatus(),
            newStatus==null?null:newStatus.getId(),
            newStatus==null?null:newStatus.getStatus(),
            OrderProcessType.Enum.INDEPENDENCE,comment));
    }
    public List<OrderProcessHistory> getHistoriesByPurchaseOrder(PurchaseOrder purchaseOrder) {
        if (null == purchaseOrder) {
            logger.warn("can not get any histories because purchaseOrder is null.");
            return null;
        }
        return orderProcessHistoryRepository.findByPurchaseOrderOrderByCreateTime(purchaseOrder);
    }

    public OrderProcessHistory getLatestHistoryByPurchaseOrder(PurchaseOrder purchaseOrder) {
        if (null == purchaseOrder) {
            logger.warn("can not get latest history because purchaseOrder is null.");
            return null;
        }
        return orderProcessHistoryRepository.findFirstByPurchaseOrderOrderByCreateTimeDesc(purchaseOrder);
    }

}
