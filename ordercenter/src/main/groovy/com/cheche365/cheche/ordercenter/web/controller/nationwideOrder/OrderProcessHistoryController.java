package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.core.model.OrderProcessHistory;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderProcessHistoryExpandService;
import com.cheche365.cheche.ordercenter.service.order.OrderManageService;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.OrderProcessHistoryViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by wangfei on 2015/11/20.
 */
@RestController
@RequestMapping(value = "/orderCenter/orderProcessHistories")
public class OrderProcessHistoryController {
    private Logger logger = LoggerFactory.getLogger(OrderProcessHistoryController.class);

    @Autowired
    private OrderProcessHistoryExpandService orderProcessHistoryExpandService;

    @Autowired
    private OrderManageService orderManageService;

    @RequestMapping(value = "/purchaseOrder/{purchaseOrderId}")
    public Map<String, Object> getOrderHistories(@PathVariable Long purchaseOrderId) {
        if (logger.isDebugEnabled()) {
            logger.debug("get orderProcessHistories for purchaseOrder id -> {}", purchaseOrderId);
        }
        PurchaseOrder purchaseOrder = orderManageService.getPurchaseOrder(purchaseOrderId);
        AssertUtil.notNull(purchaseOrder, "can not find purchaseOrder by id -> " + purchaseOrderId);
        Map<String, Object> historyMap = new HashMap<>();
        List<OrderProcessHistory> orderProcessHistoryList = orderProcessHistoryExpandService.getHistoriesByPurchaseOrder(purchaseOrder);
        List<OrderProcessHistoryViewModel> viewModelList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderProcessHistoryList)) {
            orderProcessHistoryList.forEach(orderProcessHistory -> viewModelList.add(OrderProcessHistoryViewModel.createViewModel(orderProcessHistory)));
        }
        historyMap.put("histories", viewModelList);
        return historyMap;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public OrderProcessHistoryViewModel saveOrderHistory(@RequestBody OrderProcessHistory orderProcessHistory) {
        if (logger.isDebugEnabled()) {
            logger.debug("save new orderProcessHistory for purchaseOrder id -> {}", orderProcessHistory.getPurchaseOrder().getId());
        }
        return OrderProcessHistoryViewModel.createViewModel(orderProcessHistoryExpandService.saveOrderProcessHistory(orderProcessHistory));
    }

}
