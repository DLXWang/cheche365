package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.core.model.DeliveryInfo;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.DeliveryInfoExpandService;
import com.cheche365.cheche.ordercenter.service.order.OrderManageService;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.DeliveryInfoViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wangfei on 2015/11/21.
 */
@RestController
@RequestMapping(value = "/orderCenter/deliveryInfos")
public class DeliveryInfoController {
    private Logger logger = LoggerFactory.getLogger(DeliveryInfoController.class);

    @Autowired
    private OrderManageService orderManageService;

    @Autowired
    private DeliveryInfoExpandService deliveryInfoExpandService;

    @RequestMapping(value = "/purchaseOrder/{purchaseOrderId}", method = RequestMethod.POST)
    public DeliveryInfo updateInsuranceInfo(@PathVariable Long purchaseOrderId, DeliveryInfoViewModel viewModel) {
        if (logger.isDebugEnabled()) {
            logger.debug("add or update DeliveryInfo for purchaseOrder id -> {}", purchaseOrderId);
        }
        PurchaseOrder purchaseOrder = orderManageService.getPurchaseOrder(purchaseOrderId);
        AssertUtil.notNull(purchaseOrder, "can not find purchaseOrder by purchaseOrderId -> " + purchaseOrderId);
        DeliveryInfo deliveryInfo = DeliveryInfoViewModel.createModel(viewModel);
        return deliveryInfoExpandService.updateInsuranceInfo(viewModel.getCommercialPolicyNo(), viewModel.getCompulsoryPolicyNo(),
                purchaseOrder, deliveryInfo);
    }

}
