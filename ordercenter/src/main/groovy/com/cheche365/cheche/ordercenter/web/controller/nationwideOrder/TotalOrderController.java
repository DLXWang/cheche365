package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.core.model.OrderCooperationInfo;
import com.cheche365.cheche.core.model.PaymentStatus;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.ordercenter.model.NationwideOrderQuery;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderCooperationInfoManageService;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.OrderCooperationInfoViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 全部订单
 * Created by sunhuazhong on 2015/11/14.
 */
@RestController
@RequestMapping("/orderCenter/nationwide/total")
public class TotalOrderController extends NationalWideOrderController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderCooperationInfoManageService orderCooperationInfoManageService;

    @RequestMapping(value = "",method = RequestMethod.GET)
//    @VisitorPermission("op0102")
    public PageViewModel<OrderCooperationInfoViewModel> find(@RequestParam(value = "currentPage",required = true) Integer currentPage,
                                                             @RequestParam(value = "pageSize",required = true) Integer pageSize,
                                                             NationwideOrderQuery query) {
        if(currentPage == null || currentPage < 1 ){
            throw new FieldValidtorException("list total order, currentPage can not be null or less than 1");
        }
        if(pageSize == null || pageSize < 1 ){
            throw new FieldValidtorException("list total order, pageSize can not be null or less than 1");
        }
        Page<OrderCooperationInfo> orderCooperationInfoPage =
                orderCooperationInfoManageService.listTotalOrder(currentPage, pageSize, query);
        return createPageViewModel(orderCooperationInfoPage);
    }

    @RequestMapping(value = "/giveUpPay/{id}",method = RequestMethod.PUT)
//    @VisitorPermission("op0102")
    public OrderCooperationInfoViewModel giveUpPay(@PathVariable Long id) {
        if(id == null || id < 1){
            throw new FieldValidtorException("give up pay for order, id can not be null or less than 1");
        }
        OrderCooperationInfo orderCooperationInfo = orderCooperationInfoManageService.giveUpPay(id);
        return createViewModel(orderCooperationInfo);
    }

    public void setProperties(OrderCooperationInfo orderCooperationInfo, OrderCooperationInfoViewModel viewModel) {
        PurchaseOrder purchaseOrder = orderCooperationInfo.getPurchaseOrder();
        PaymentStatus paymentStatus = orderCooperationInfoManageService.getPaymentStatus(purchaseOrder);
        setPaymentStatus(orderCooperationInfo, paymentStatus, viewModel);
    }
}
