package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.core.model.OrderCooperationInfo;
import com.cheche365.cheche.core.model.PaymentStatus;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.ordercenter.constants.ReasonEnum;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.ordercenter.model.NationwideOrderQuery;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderCooperationInfoManageService;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.AbnormityReasonViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.OrderCooperationInfoViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 异常订单
 * Created by sunhuazhong on 2015/11/16.
 */
@RestController
@RequestMapping("/orderCenter/nationwide/abnormity")
public class AbnormityOrderController extends NationalWideOrderController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderCooperationInfoManageService orderCooperationInfoManageService;

    @RequestMapping(value = "",method = RequestMethod.GET)
//    @VisitorPermission("op0102")
    public PageViewModel<OrderCooperationInfoViewModel> find(@RequestParam(value = "currentPage",required = true) Integer currentPage,
                                                             @RequestParam(value = "pageSize",required = true) Integer pageSize,
                                                             NationwideOrderQuery query) {
        if(currentPage == null || currentPage < 1 ){
            throw new FieldValidtorException("list abnormity order, currentPage can not be null or less than 1");
        }
        if(pageSize == null || pageSize < 1 ){
            throw new FieldValidtorException("list abnormity order, pageSize can not be null or less than 1");
        }
        Page<OrderCooperationInfo> orderCooperationInfoPage =
                orderCooperationInfoManageService.listAbnormityOrder(currentPage, pageSize, query);
        return createPageViewModel(orderCooperationInfoPage);
    }

    @RequestMapping(value = "/refund/{id}",method = RequestMethod.PUT)
//    @VisitorPermission("op0102")
    public ResultModel refund(@PathVariable Long id, @RequestParam(value = "refundTo",required = true) String refundTo) {
        if(id == null || id < 1){
            throw new FieldValidtorException("refund order, id can not be null or less than 1");
        }
        boolean isSuccess = orderCooperationInfoManageService.refund(id, refundTo);
        if(isSuccess) {
            return new ResultModel();
        } else {
            return new ResultModel(false, "订单退款失败");
        }
    }

    @RequestMapping(value = "/resend/{id}",method = RequestMethod.PUT)
//    @VisitorPermission("op0102")
    public ResultModel resend(@PathVariable Long id) {
        if(id == null || id < 1){
            throw new FieldValidtorException("resend order, id can not be null or less than 1");
        }
        boolean isSuccess = orderCooperationInfoManageService.resend(id);
        if(isSuccess) {
            return new ResultModel();
        } else {
            return new ResultModel(false, "订单重发失败");
        }
    }

    @RequestMapping(value = "/reasons",method = RequestMethod.GET)
//    @VisitorPermission("op0102")
    public List<AbnormityReasonViewModel> reason() {
        ReasonEnum[] reasonEnums = ReasonEnum.values();
        return createReasonViewModel(reasonEnums);
    }

    private List<AbnormityReasonViewModel> createReasonViewModel(ReasonEnum[] reasonEnums) {
        List<AbnormityReasonViewModel> viewModelList = new ArrayList<>();
        for(ReasonEnum reasonEnum : reasonEnums) {
            AbnormityReasonViewModel viewModel = new AbnormityReasonViewModel();
            viewModel.setIndex(reasonEnum.getIndex());
            viewModel.setContent(reasonEnum.getContent());
            viewModelList.add(viewModel);
        }
        return viewModelList;
    }

    public void setProperties(OrderCooperationInfo orderCooperationInfo, OrderCooperationInfoViewModel viewModel) {
        PurchaseOrder purchaseOrder = orderCooperationInfo.getPurchaseOrder();
        PaymentStatus paymentStatus = orderCooperationInfoManageService.getPaymentStatus(purchaseOrder);
        setPaymentStatus(orderCooperationInfo, paymentStatus, viewModel);
    }
}
