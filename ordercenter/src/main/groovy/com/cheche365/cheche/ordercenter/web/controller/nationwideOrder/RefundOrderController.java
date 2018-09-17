package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.core.model.InstitutionQuoteRecord;
import com.cheche365.cheche.core.model.OrderCooperationInfo;
import com.cheche365.cheche.core.model.PurchaseOrderRefund;
import com.cheche365.cheche.ordercenter.constants.RefundTypeEnum;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.ordercenter.model.NationwideOrderQuery;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.InstitutionQuoteRecordService;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderCooperationInfoManageService;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.InstitutionQuoteRecordViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.OrderCooperationInfoViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.PurchaseOrderRefundViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 退款订单
 * Created by sunhuazhong on 2015/11/20.
 */
@RestController
@RequestMapping("/orderCenter/nationwide/refund")
public class RefundOrderController  extends NationalWideOrderController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderCooperationInfoManageService orderCooperationInfoManageService;

    @Autowired
    private InstitutionQuoteRecordService institutionQuoteRecordService;

    @RequestMapping(value = "", method = RequestMethod.GET)
//    @VisitorPermission("op0102")
    public PageViewModel<OrderCooperationInfoViewModel> find(@RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                             @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                             NationwideOrderQuery query) {
        if (currentPage == null || currentPage < 1) {
            throw new FieldValidtorException("list refund order, currentPage can not be null or less than 1");
        }
        if (pageSize == null || pageSize < 1) {
            throw new FieldValidtorException("list refund order, pageSize can not be null or less than 1");
        }
        Page<OrderCooperationInfo> orderCooperationInfoPage =
                orderCooperationInfoManageService.listRefundOrder(currentPage, pageSize, query);
        return createPageViewModel(orderCooperationInfoPage);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
//    @VisitorPermission("op0102")
    public OrderCooperationInfoViewModel refundToUser(@PathVariable Long id) {
        if (id == null || id < 1) {
            throw new FieldValidtorException("refund to user, id can not be null or less than 1");
        }

        return refundTo(id, RefundTypeEnum.TO_USER);
    }

    @RequestMapping(value = "/cheche/{id}", method = RequestMethod.PUT)
//    @VisitorPermission("op0102")
    public OrderCooperationInfoViewModel refundToCheche(@PathVariable Long id) {
        if (id == null || id < 1) {
            throw new FieldValidtorException("refund to cheche, id can not be null or less than 1");
        }

        return refundTo(id, RefundTypeEnum.TO_CHECHE);
    }

    private OrderCooperationInfoViewModel refundTo(Long id, RefundTypeEnum refundTypeEnum) {
        OrderCooperationInfo orderCooperationInfo = orderCooperationInfoManageService.refundTo(id, refundTypeEnum);
        return createViewModel(orderCooperationInfo);
    }

    public void setProperties(OrderCooperationInfo orderCooperationInfo, OrderCooperationInfoViewModel viewModel) {
        PurchaseOrderRefund purchaseOrderRefund = orderCooperationInfoManageService
                .getPurchaseOrderRefund(orderCooperationInfo.getPurchaseOrder());
        InstitutionQuoteRecord institutionQuoteRecord =institutionQuoteRecordService.getByPurchaseOrder(orderCooperationInfo.getPurchaseOrder());
        viewModel.setRefund(PurchaseOrderRefundViewModel.createViewModel(purchaseOrderRefund));
        viewModel.setQuoteRecord(InstitutionQuoteRecordViewModel.createViewModel(institutionQuoteRecord));
    }
}
