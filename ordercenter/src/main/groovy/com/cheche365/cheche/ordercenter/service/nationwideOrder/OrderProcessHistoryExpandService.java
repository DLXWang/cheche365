package com.cheche365.cheche.ordercenter.service.nationwideOrder;

import com.cheche365.cheche.core.model.OrderProcessHistory;
import com.cheche365.cheche.core.model.OrderProcessType;
import com.cheche365.cheche.core.repository.OrderProcessHistoryRepository;
import com.cheche365.cheche.core.service.OrderProcessHistoryService;
import com.cheche365.cheche.ordercenter.service.order.OrderManageService;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * Created by wangfei on 2015/11/17.
 */
@Service
public class OrderProcessHistoryExpandService extends OrderProcessHistoryService {
    private Logger logger = LoggerFactory.getLogger(OrderProcessHistoryExpandService.class);

    @Autowired
    private OrderProcessHistoryRepository orderProcessHistoryRepository;

    @Autowired
    private OrderManageService orderManageService;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    public OrderProcessHistory saveOrderProcessHistory(OrderProcessHistory orderProcessHistory) {
        orderProcessHistory.setOrderProcessType(OrderProcessType.Enum.COOPERATION);
        orderProcessHistory.setPurchaseOrder(orderManageService.getPurchaseOrder(orderProcessHistory.getPurchaseOrder().getId()));
        orderProcessHistory.setCreateTime(Calendar.getInstance().getTime());
        orderProcessHistory.setOperator(orderCenterInternalUserManageService.getCurrentInternalUser());
        return orderProcessHistoryRepository.save(orderProcessHistory);
    }


}
