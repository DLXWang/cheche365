package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.core.model.OrderCooperationInfo;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.PaymentStatus;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.ordercenter.constants.OrderCooperationInfoConstants;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.OrderCooperationInfoViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/14.
 */
public abstract class NationalWideOrderController {

    @Autowired
    private ResourceService resourceService;

    public PageViewModel<OrderCooperationInfoViewModel> createPageViewModel(Page page) {
        PageViewModel model = new PageViewModel<OrderCooperationInfoViewModel>();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPage(page.getTotalPages());
        model.setPageInfo(pageInfo);
        List<OrderCooperationInfoViewModel> pageViewDataList = new ArrayList<>();
        for (OrderCooperationInfo orderCooperationInfo : (List<OrderCooperationInfo>) page.getContent()) {
            OrderCooperationInfoViewModel viewModel = createViewModel(orderCooperationInfo);
            pageViewDataList.add(viewModel);
        }
        model.setViewList(pageViewDataList);
        return model;
    }

    public OrderCooperationInfoViewModel createViewModel(OrderCooperationInfo orderCooperationInfo) {
        OrderCooperationInfoViewModel viewModel = OrderCooperationInfoViewModel.createViewModel(orderCooperationInfo,resourceService);
        setProperties(orderCooperationInfo, viewModel);
        return viewModel;
    }

    protected void setPaymentStatus(OrderCooperationInfo orderCooperationInfo, PaymentStatus paymentStatus,
                                 OrderCooperationInfoViewModel viewModel) {
        PurchaseOrder purchaseOrder = orderCooperationInfo.getPurchaseOrder();
        OrderStatus orderStatus = purchaseOrder.getStatus();
        if(paymentStatus == null) {
            viewModel.setPaymentStatus(OrderCooperationInfoConstants.NO_PAYMENT_STATUS);
            viewModel.setCooperationStatus(null);
        } else if(paymentStatus.getId().equals(PaymentStatus.Enum.PAYMENTSUCCESS_2.getId())) {
            viewModel.setPaymentStatus(OrderCooperationInfoConstants.PAID_STATUS);
            viewModel.setCooperationStatus(orderCooperationInfo.getStatus());
        } else {
            if(orderStatus.getId().equals(OrderStatus.Enum.CANCELED_6.getId())
                || orderStatus.getId().equals(OrderStatus.Enum.EXPIRED_8.getId())) {
                viewModel.setPaymentStatus(OrderCooperationInfoConstants.GIVEUP_PAYMENT_STATUS);
                viewModel.setCooperationStatus(null);
            } else {
                viewModel.setPaymentStatus(OrderCooperationInfoConstants.NO_PAYMENT_STATUS);
                viewModel.setCooperationStatus(null);
            }
        }
    }

    public abstract void setProperties(OrderCooperationInfo orderCooperationInfo, OrderCooperationInfoViewModel viewModel);
}
