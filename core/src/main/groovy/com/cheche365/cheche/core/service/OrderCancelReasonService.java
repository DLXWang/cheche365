package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.OrderCancelReason;
import com.cheche365.cheche.core.model.OrderCancelReasonType;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.OrderCancelReasonRepository;
import com.cheche365.cheche.core.repository.OrderCancelReasonTypeRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaozhong on 2015/9/6.
 */

@Service
@Transactional
public class OrderCancelReasonService {

    @Autowired
    private OrderCancelReasonRepository orderCancelReasonRepository;
    @Autowired
    private OrderCancelReasonTypeRepository orderCancelReasonTypeRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public void saveCancelReason(String orderNo, Long orderCancelReasonId) {
        if (StringUtils.isBlank(orderNo) || orderCancelReasonId == null) {
            return;
        }
        OrderCancelReasonType reasonType = orderCancelReasonTypeRepository.findOne(orderCancelReasonId);
        if (reasonType != null) {
            PurchaseOrder order = purchaseOrderRepository.findFirstByOrderNo(orderNo);
            OrderCancelReason orderCancelReason = new OrderCancelReason();
            orderCancelReason.setOrderCancelReasonType(reasonType);
            orderCancelReason.setCancelTime(new Timestamp(System.currentTimeMillis()));
            orderCancelReason.setPurchaseOrder(order);
            orderCancelReasonRepository.save(orderCancelReason);
        }
    }

    public List<OrderCancelReasonType> listOrderCancelReasonType() {
        List<OrderCancelReasonType> list = new ArrayList<>();
        orderCancelReasonTypeRepository.findAll(new Sort(new Sort.Order("order"))).forEach(t -> list.add(t));
        return list;
    }
}
