package com.cheche365.cheche.ordercenter.web.controller.order;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.OrderOperationInfo;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.service.InternalUserService;
import com.cheche365.cheche.core.service.OrderOperationInfoService;
import com.cheche365.cheche.ordercenter.service.order.OrderRedistributionService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.ordercenter.web.model.OrderReAssignViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.web.model.order.OrderOperationInfoViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by wangfei on 2015/6/15.
 */
@RestController
@RequestMapping("/orderCenter/order")
public class OrderRedistributionController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderRedistributionService orderRedistributionService;

    @Autowired
    private InternalUserService internalUserService;

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;


    @RequestMapping(value = "/findByOperator", method = RequestMethod.GET)
    public OrderReAssignViewModel findByOperator(@RequestParam(value = "operatorId", required = true) Long operatorId) {
        logger.debug("check operator before redistribute order by operator, param operatorId = {}",
            operatorId);

        if (orderRedistributionService.getOrderCountByAssigner(operatorId) < 1)
            return new OrderReAssignViewModel(false, "所选操作人没有可分配订单，请重新选择");

        List<InternalUser> internalUserList = orderRedistributionService.getAssignersByOperatorId(operatorId);
        return createViewModelByOperator(operatorId, internalUserList);
    }

    @RequestMapping(value = "/findByOrder", method = RequestMethod.GET)
    public OrderReAssignViewModel findByOrder(@RequestParam(value = "orderNo", required = true) String orderNo) {
        logger.debug("check order before redistribute order by orderNo, param orderNo = {}",
            orderNo);

        PurchaseOrder purchaseOrder = orderRedistributionService.findByOrderNo(orderNo);
        if (purchaseOrder == null)
            return new OrderReAssignViewModel(false, "订单不存在");

        return createViewModelByOrder(purchaseOrder);
    }

    @RequestMapping(value = "/ReassignByOrder", method = RequestMethod.GET)
    @VisitorPermission("or0104")
    public OrderReAssignViewModel reAssignByOrder(@RequestParam(value = "orderNo", required = true) String orderNo,
                                                  @RequestParam(value = "newOperatorId", required = true) Long newOperatorId) {
        logger.debug("redistribute order by orderNo, param orderNo = {}, newOperatorId = {}",
            orderNo, newOperatorId);

        PurchaseOrder purchaseOrder = orderRedistributionService.redistributeByOrder(orderNo, newOperatorId);
        return createViewModelByOrder(purchaseOrder);
    }

    @RequestMapping(value = "/ReassignByOperator", method = RequestMethod.GET)
    @VisitorPermission("or0104")
    public ResultModel reAssignByOperator(@RequestParam(value = "oldOperatorId", required = true) Long oldOperatorId,
                                                     @RequestParam(value = "newOperatorId", required = false) Long newOperatorId,
                                                     @RequestParam(value = "distributionMethod", required = true) String distributionMethod,
                                                     @RequestParam(value = "checkedIds") String[] checkedIds) {
        logger.debug("redistribute order by operator, param oldOperatorId = {}, newOperatorId = {}, distributionMethod = {},checkedIds = {}",
            oldOperatorId, newOperatorId, distributionMethod, checkedIds);
        return orderRedistributionService.redistributeByOperator(oldOperatorId, newOperatorId, distributionMethod, checkedIds);
    }

    public OrderReAssignViewModel createViewModelByOperator(Long operatorId, List<InternalUser> internalUserList) {
        OrderReAssignViewModel OrderReAssignViewModel = new OrderReAssignViewModel();
        InternalUser internalUser = internalUserService.getInternalUserById(operatorId);
        OrderReAssignViewModel.setOldOperatorName(internalUser.getName());
        OrderReAssignViewModel.setOldOperatorId(operatorId);
        OrderReAssignViewModel.setNewOperatorList(internalUserList);
        return OrderReAssignViewModel;
    }

    public OrderReAssignViewModel createViewModelByOrder(PurchaseOrder purchaseOrder) {
        OrderReAssignViewModel OrderReAssignViewModel = new OrderReAssignViewModel();
        OrderOperationInfo orderOperationInfo = orderOperationInfoService.getByPurchaseOrder(purchaseOrder);
        if (orderOperationInfo != null) {
            OrderReAssignViewModel.setOldOperatorName(orderOperationInfo.getAssigner().getName());
            OrderReAssignViewModel.setNewOperatorList(orderRedistributionService.listAllEnableCustomerExceptOne(orderOperationInfo.getAssigner()));
        } else if (purchaseOrder.getOperator() != null) {
            OrderReAssignViewModel.setOldOperatorName(purchaseOrder.getOperator().getName());
            OrderReAssignViewModel.setNewOperatorList(orderRedistributionService.listAllEnableCustomerExceptOne(purchaseOrder.getOperator()));
        } else {
            OrderReAssignViewModel.setOldOperatorName("");
            OrderReAssignViewModel.setNewOperatorList(orderRedistributionService.listAllEnableCustomer());
        }
        return OrderReAssignViewModel;
    }

    @VisitorPermission("or0603")
    @RequestMapping(value = "/redistributionList", method = RequestMethod.GET)
    public DataTablePageViewModel<OrderOperationInfoViewModel> getDataList(@RequestParam(value = "operatorId", required = true) Long operatorId,
                                                                           @RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                                           @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                                           @RequestParam(value = "draw") Integer draw) {
        DataTablePageViewModel<OrderOperationInfoViewModel> dataView = orderRedistributionService.findOperationInfoByOperator(operatorId, currentPage, pageSize, draw);
        return dataView;
    }


}
