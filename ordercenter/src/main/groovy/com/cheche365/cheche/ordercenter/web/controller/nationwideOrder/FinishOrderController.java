package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.core.model.InstitutionQuoteRecord;
import com.cheche365.cheche.core.model.OrderCooperationInfo;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.ordercenter.model.NationwideOrderQuery;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.InstitutionQuoteRecordService;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderCooperationInfoManageService;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.DeliveryInfoViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.InstitutionQuoteRecordViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.OrderCooperationInfoViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * Created by xu.yelong on 2015/11/20.
 */
@RestController
@RequestMapping("/orderCenter/nationwide/finished")
public class FinishOrderController extends NationalWideOrderController {
    @Autowired
    private OrderCooperationInfoManageService orderCooperationInfoManageService;

    @Autowired
    private InstitutionQuoteRecordService institutionQuoteRecordService;


    @RequestMapping(value = "",method = RequestMethod.GET)
    public PageViewModel<OrderCooperationInfoViewModel> find(@RequestParam(value = "currentPage",required = true) Integer currentPage,
                                                             @RequestParam(value = "pageSize",required = true) Integer pageSize,
                                                             NationwideOrderQuery query) {
        if(currentPage == null || currentPage < 1 ){
            throw new FieldValidtorException("list finished order, currentPage can not be null or less than 1");
        }
        if(pageSize == null || pageSize < 1 ){
            throw new FieldValidtorException("list finished order, pageSize can not be null or less than 1");
        }
        Page<OrderCooperationInfo> orderCooperationInfoPage = orderCooperationInfoManageService.listFinishedOrder(currentPage, pageSize, query);

        return createPageViewModel(orderCooperationInfoPage);
    }

    @Override
    public void setProperties(OrderCooperationInfo orderCooperationInfo, OrderCooperationInfoViewModel viewModel) {
        PurchaseOrder purchaseOrder=orderCooperationInfo.getPurchaseOrder();
        InstitutionQuoteRecord institutionQuoteRecord=institutionQuoteRecordService.getByPurchaseOrder(purchaseOrder);
        viewModel.setQuoteRecord(InstitutionQuoteRecordViewModel.createViewModel(institutionQuoteRecord));
        viewModel.setDeliveryInfo(DeliveryInfoViewModel.createViewModel(purchaseOrder.getDeliveryInfo()));
    }

    @RequestMapping(value = "/{id}/rebate",method = RequestMethod.PUT)
    public ResultModel rebateStatus(@PathVariable Long id){
        try{
            OrderCooperationInfo orderCooperationInfo=orderCooperationInfoManageService.findById(id);
            orderCooperationInfo.setRebateStatus(true);
            orderCooperationInfoManageService.save(orderCooperationInfo);
            return new ResultModel(true,"保存成功");
        }catch(Exception ex){
            return new ResultModel(false,"保存失败");
        }
    }

    @RequestMapping(value = "/{id}/audit",method = RequestMethod.PUT)
    public ResultModel auditStatus(@PathVariable Long id,@RequestParam(value = "auditStatus",required = true) Boolean auditStatus){
        try{
            OrderCooperationInfo orderCooperationInfo=orderCooperationInfoManageService.findById(id);
            orderCooperationInfo.setAuditStatus(auditStatus);
            orderCooperationInfoManageService.save(orderCooperationInfo);
            return new ResultModel(true,"保存成功");
        }catch(Exception ex){
            return new ResultModel(false,"保存失败");
        }
    }



}
