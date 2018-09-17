package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.ordercenter.constants.ReasonEnum;
import com.cheche365.cheche.ordercenter.model.NationwideOrderQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.InstitutionQuoteRecordService;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderCooperationInfoExpandService;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderCooperationStatusHandler;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.InstitutionQuoteRecordViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.OrderCooperationInfoViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangfei on 2015/11/13.
 */
@RestController
@RequestMapping(value = "/orderCenter/orderCooperationInfos")
public class OrderCooperationInfoController {
    private Logger logger = LoggerFactory.getLogger(OrderCooperationInfoController.class);

    @Autowired
    private OrderCooperationInfoExpandService orderCooperationInfoExpandService;

    @Autowired
    private BaseService baseService;
    
    @Autowired
    private OrderCooperationStatusHandler orderCooperationStatusHandler;

    @Autowired
    private InstitutionQuoteRecordService institutionQuoteRecordService;

    @Autowired
    private ResourceService resourceService;

    @RequestMapping(value = "",method = RequestMethod.GET)
    public PageViewModel<OrderCooperationInfoViewModel> getOrders(
                             @RequestParam(value = "currentPage",required = true) Integer currentPage,
                             @RequestParam(value = "pageSize",required = true) Integer pageSize,
                             NationwideOrderQuery query) {
        if (logger.isDebugEnabled()) {
            logger.debug("list orderCooperationInfo by page, currentPage -> {}", currentPage);
        }
        Page<OrderCooperationInfo> page = orderCooperationInfoExpandService.getOrdersByPage(currentPage, pageSize, query);
        List<OrderCooperationInfoViewModel> modelList = new ArrayList<>();
        page.getContent().forEach(orderCooperationInfo -> modelList.add(createViewModel(orderCooperationInfo)));
        return new PageViewModel<>(baseService.createPageInfo(page), modelList);
    }

    @RequestMapping(value = "/console",method = RequestMethod.GET)
    @VisitorPermission("or070101")
    public PageViewModel<OrderCooperationInfoViewModel> getConsoleOrders(
                            @RequestParam(value = "currentPage",required = true) Integer currentPage,
                            @RequestParam(value = "pageSize",required = true) Integer pageSize,
                            NationwideOrderQuery query) {
        if (logger.isDebugEnabled()) {
            logger.debug("list console orderCooperationInfo by page, currentPage -> {}", currentPage);
        }
        Page<OrderCooperationInfo> page = orderCooperationInfoExpandService.getOrdersByPage(currentPage, pageSize, query);
        List<OrderCooperationInfoViewModel> modelList = new ArrayList<>();
        page.getContent().forEach(orderCooperationInfo -> modelList.add(createViewModel(orderCooperationInfo)));
        return new PageViewModel<>(baseService.createPageInfo(page), modelList);
    }

    private OrderCooperationInfoViewModel createViewModel(OrderCooperationInfo orderCooperationInfo) {
        OrderCooperationInfoViewModel viewModel = OrderCooperationInfoViewModel.createViewModel(orderCooperationInfo,resourceService);
        PaymentStatus paymentStatus = orderCooperationInfoExpandService.getPaymentStatus(orderCooperationInfo.getPurchaseOrder());
        viewModel.setPaymentStatus(orderCooperationInfoExpandService.getPaymentComment(paymentStatus, orderCooperationInfo.getPurchaseOrder()));
        InstitutionQuoteRecord institutionQuoteRecord = institutionQuoteRecordService.getByPurchaseOrder(orderCooperationInfo.getPurchaseOrder());
        viewModel.setQuoteRecord(InstitutionQuoteRecordViewModel.createViewModel(institutionQuoteRecord));
        return viewModel;
    }

    @RequestMapping(value = "/status",method = RequestMethod.GET)
    public List<OrderCooperationStatus> getAllStatus() {
        return orderCooperationInfoExpandService.getAllStatus();
    }

    @RequestMapping(value = "/{id}/status",method = RequestMethod.GET)
    public Map<String, Object> getOrderStatus(@PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("get order status by id -> {}", id);
        }
        OrderCooperationInfo orderCooperationInfo = orderCooperationInfoExpandService.getById(id);
        AssertUtil.notNull(orderCooperationInfo, "can not find orderCooperationInfo by id: " + id);
        Map<String, Object> statusMap = new HashMap<>();
        /*statusMap.put("status", orderCooperationInfo.getStatus());*/
        statusMap.put("switchStatus", OrderCooperationStatus.Enum.getSwitchStatus(orderCooperationInfo.getStatus()));
        /*ReasonEnum reasonEnum = ReasonEnum.formatByReason(orderCooperationInfo.getReason());
        statusMap.put("reasonId", null != reasonEnum ? reasonEnum.getIndex() : null);
        PurchaseOrderRefund purchaseOrderRefund = purchaseOrderRefundService.findByPurchaseOrder(orderCooperationInfo.getPurchaseOrder());
        statusMap.put("refundObject", PurchaseOrderRefund.convertCheckToString(purchaseOrderRefund));*/
        return statusMap;
    }

    @RequestMapping(value = "/{id}/status",method = RequestMethod.PUT)
    //@VisitorPermission("or07010101")
    public OrderCooperationInfoViewModel updateStatus(@PathVariable Long id,
                                                      @RequestParam(value = "newStatus",required = true) Long newStatus,
                                                      @RequestParam(value = "reasonId",required = false) Integer reasonId,
                                                      @RequestParam(value = "refundObject",required = false) String refundObject) {
        if (logger.isDebugEnabled()) {
            logger.debug("update order status by id -> {}, newStatus -> {}", id, newStatus);
        }
        OrderCooperationStatus cooperationStatus = OrderCooperationStatus.Enum.format(newStatus);
        AssertUtil.notNull(cooperationStatus, "illegal argument newStatus: " + newStatus);
        OrderCooperationInfo orderCooperationInfo = orderCooperationInfoExpandService.getById(id);
        AssertUtil.notNull(orderCooperationInfo, "can not find orderCooperationInfo by id: " + id);
        orderCooperationInfo.setRefundObject(StringUtils.isNotBlank(refundObject) ? refundObject : "");
        if(reasonId == null || reasonId == 0) {
            return createViewModel(orderCooperationStatusHandler.request(orderCooperationInfo, cooperationStatus, null));
        }
        return createViewModel(orderCooperationStatusHandler.request(orderCooperationInfo, cooperationStatus,
            ReasonEnum.format(reasonId).getContent()));
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public OrderCooperationInfoViewModel getOrderCooperationInfo(@PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("get orderCooperationInfo by id -> {}", id);
        }
        OrderCooperationInfo orderCooperationInfo = orderCooperationInfoExpandService.getById(id);
        AssertUtil.notNull(orderCooperationInfo, "can not find orderCooperationInfo by id: " + id);
        return createViewModel(orderCooperationInfo);
    }

    @RequestMapping(value = "/{id}/insurancePackage",method = RequestMethod.GET)
    public Map<String, Object> getInsurancePackage(@PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("get orderCooperation insurancePackage by orderCooperation id -> {}", id);
        }
        Map<String, Object> insurancePackageMap = new HashMap<>();
        OrderCooperationInfo orderCooperationInfo = orderCooperationInfoExpandService.getById(id);
        AssertUtil.notNull(orderCooperationInfo, "can not find orderCooperationInfo by id: " + id);
        InstitutionQuoteRecord institutionQuoteRecord = institutionQuoteRecordService.getByPurchaseOrder(orderCooperationInfo.getPurchaseOrder());
        AssertUtil.notNull(institutionQuoteRecord, "can not find institutionQuoteRecord by institution id -> " + orderCooperationInfo.getInstitution().getId());
        insurancePackageMap.put("institutionInsurancePackage", institutionQuoteRecord.getInsurancePackage());
        return insurancePackageMap;
    }

}
