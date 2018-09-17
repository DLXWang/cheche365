package com.cheche365.cheche.ordercenter.web.controller.order;

import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.service.order.OrderManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 出单中心：状态转换控制器类
 * Created by sunhuazhong on 2015/5/8.
 */
@RestController
@RequestMapping("/")
public class OrderManageController {

    private Logger logger = LoggerFactory.getLogger(OrderManageController.class);

    @Autowired
    private OrderManageService orderManageService;

    @RequestMapping(value = "/orderCenter/order/{purchaseOrderId}/insurancePackage")
    public Map<String, Object> getInsurancePackage(@PathVariable Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = orderManageService.getPurchaseOrder(purchaseOrderId);
        AssertUtil.notNull(purchaseOrder, "can not find purchaseOrder by id -> " + purchaseOrderId);
        QuoteRecord quoteRecord = orderManageService.getQuoteRecordByPurchaseOrder(purchaseOrder);
        Map<String, Object> insurancePackageMap = new HashMap<>();
        insurancePackageMap.put("orderInsurancePackage", quoteRecord.getInsurancePackage());
        return insurancePackageMap;
    }

    @RequestMapping(value = "/orderCenter/order/{purchaseOrderId}/status", method = RequestMethod.PUT)
    public ResultModel resetInsuranceFailureStatus(@PathVariable Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = orderManageService.getPurchaseOrder(purchaseOrderId);
        logger.debug("update order {} status to {} for letting user to pay the order.", purchaseOrder.getOrderNo(), OrderStatus.Enum.PENDING_PAYMENT_1.getStatus());
        AssertUtil.notNull(purchaseOrder, "can not find purchaseOrder by id -> " + purchaseOrderId);
        purchaseOrder.appendDescription("订单由核保失败重置为创建状态-出单中心北京订单列表让用户可支付功能修改");
        orderManageService.updateStatus(purchaseOrder, OrderStatus.Enum.PENDING_PAYMENT_1);
        return new ResultModel();
    }
}
