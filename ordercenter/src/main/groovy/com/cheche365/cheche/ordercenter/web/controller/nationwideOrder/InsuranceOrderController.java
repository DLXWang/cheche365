package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.core.model.InstitutionQuoteRecord;
import com.cheche365.cheche.core.model.OrderCooperationInfo;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.ordercenter.model.NationwideOrderQuery;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.InstitutionQuoteRecordService;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderCooperationInfoManageService;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.DeliveryInfoViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.InstitutionQuoteRecordViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.OrderCooperationInfoViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 已出单订单
 * Created by sunhuazhong on 2015/11/21.
 */
@RestController
@RequestMapping("/orderCenter/nationwide/done")
public class InsuranceOrderController extends NationalWideOrderController {

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
            throw new FieldValidtorException("list insurance order, currentPage can not be null or less than 1");
        }
        if (pageSize == null || pageSize < 1) {
            throw new FieldValidtorException("list insurance order, pageSize can not be null or less than 1");
        }
        Page<OrderCooperationInfo> orderCooperationInfoPage =
            orderCooperationInfoManageService.listDoneOrder(currentPage, pageSize, query);
        return createPageViewModel(orderCooperationInfoPage);
    }

    public void setProperties(OrderCooperationInfo orderCooperationInfo, OrderCooperationInfoViewModel viewModel) {
        if(logger.isDebugEnabled()) {
            logger.debug("set properties include quote record and delivery info for insurance order");
        }
        InstitutionQuoteRecord institutionQuoteRecord = institutionQuoteRecordService.getByPurchaseOrder(orderCooperationInfo.getPurchaseOrder());
        viewModel.setDeliveryInfo(DeliveryInfoViewModel.createViewModel(orderCooperationInfo.getPurchaseOrder().getDeliveryInfo()));
        viewModel.setQuoteRecord(InstitutionQuoteRecordViewModel.createViewModel(institutionQuoteRecord));
    }
}
