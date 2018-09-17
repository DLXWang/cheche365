package com.cheche365.cheche.ordercenter.web.controller.order;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.web.service.InsurancePurchaseOrderRebateService;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by wangfei on 2016/6/8.
 */
@RestController
@RequestMapping("/orderCenter/insuranceOrderRebate")
public class InsuranceOrderRebateController {
    private Logger logger = LoggerFactory.getLogger(InsuranceOrderRebateController.class);

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private InsurancePurchaseOrderRebateService orderRebateService;

    @VisitorPermission("or0108")
    @RequestMapping(value = "/purchaseOrder/{purchaseOrderId}",method = RequestMethod.PUT)
    public ResultModel modifyOrderRebate(@PathVariable Long purchaseOrderId,
                                         @RequestParam(value = "compulsoryRebate", required = true) Double compulsoryRebate,
                                         @RequestParam(value = "commercialRebate", required = true) Double commercialRebate,
                                         @RequestParam(value = "type", required = true) String type) {
        InternalUser current = orderCenterInternalUserManageService.getCurrentInternalUser();
        logger.debug("用户{}调整订单{}费率，调整后：商业险费率：{}，交强险费率：{}，调整费率类型：{}", current.getEmail(),
            purchaseOrderId, commercialRebate, compulsoryRebate, type);
        orderRebateService.updateChannelRebate(type, compulsoryRebate, commercialRebate, purchaseOrderService.findById(purchaseOrderId));
        return new ResultModel();
    }
}
