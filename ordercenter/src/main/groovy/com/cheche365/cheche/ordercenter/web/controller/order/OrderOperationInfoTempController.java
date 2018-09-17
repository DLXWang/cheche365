package com.cheche365.cheche.ordercenter.web.controller.order;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.CompulsoryInsuranceService;
import com.cheche365.cheche.core.service.InsuranceService;
import com.cheche365.cheche.core.service.PurchaseOrderGiftService;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderProcessHistoryExpandService;
import com.cheche365.cheche.ordercenter.service.order.OrderManageService;
import com.cheche365.cheche.ordercenter.service.order.OrderOperationInfoExpandTempService;
import com.cheche365.cheche.ordercenter.service.order.OrderTransmissionStatusHandler;
import com.cheche365.cheche.ordercenter.web.model.InsuranceCompanyData;
import com.cheche365.cheche.ordercenter.web.model.order.OrderOperationInfoViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * Created by wangfei on 2015/12/10.
 */
@RestController
@RequestMapping(value = "/orderCenter/orderOperationInfos/temp")
public class OrderOperationInfoTempController {
    private Logger logger = LoggerFactory.getLogger(OrderOperationInfoTempController.class);

    @Autowired
    @Qualifier("orderOperationInfoExpandTempService")
    private OrderOperationInfoExpandTempService orderOperationInfoExpandTempService;

    @Autowired
    private OrderManageService orderManageService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private OrderTransmissionStatusHandler orderTransmissionStatusHandler;

    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;

    @Autowired
    private OrderProcessHistoryExpandService orderProcessHistoryExpandService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private InsuranceService insuranceService;

    @Autowired
    private CompulsoryInsuranceService compulsoryInsuranceService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @VisitorPermission("or0107")
    public PageViewModel getOrders(PublicQuery query) {
        if (logger.isDebugEnabled()) {
            logger.debug("list orderOperationInfo by page, currentPage -> {}", query.getCurrentPage());
        }
        Page<OrderOperationInfo> page = orderOperationInfoExpandTempService.getOrdersByPage(query);
        List<OrderOperationInfoViewModel> modelList = new ArrayList<>();
        page.getContent().forEach(orderOperationInfo -> modelList.add(createViewModel(orderOperationInfo)));
        return new PageViewModel<>(baseService.createPageInfo(page), modelList);
    }


    @RequestMapping(value = "/transferStation", method = RequestMethod.GET)
    @VisitorPermission("or0107")
    public ModelAndView transfer(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderService.findById(purchaseOrderId);
        //status不为5继续走以前的
        if (!OrderStatus.Enum.FINISHED_5.getId().equals(purchaseOrder.getStatus().getId())) {
            return new ModelAndView("redirect:/page/order/order_detail.html?no=" + purchaseOrder.getOrderNo() + "&id=" + purchaseOrderId);
        }

        //订单时间大于5月8日并且是先上支付的订单数据继续走以前的
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 5, 8);
        if (purchaseOrder.getCreateTime().after(calendar.getTime())) {
            return new ModelAndView("redirect:/page/order/order_detail.html?no=" + purchaseOrder.getOrderNo() + "&id=" + purchaseOrderId);
        }

        //非车险的订单继续走以前的
        if (!OrderType.Enum.INSURANCE.getId().equals(purchaseOrder.getType().getId())) {
            return new ModelAndView("redirect:/page/order/order_detail.html?no=" + purchaseOrder.getOrderNo() + "&id=" + purchaseOrderId);
        }
        //audit!=1走新的流程
        if (1 != purchaseOrder.getAudit()) {
            return new ModelAndView("redirect:/page/order/order_detail.html?id=" + orderOperationInfoExpandTempService.getNewPurchaseOrderId(purchaseOrder));
        }

        return new ModelAndView("redirect:/page/order/order_detail.html?no=" + purchaseOrder.getOrderNo() + "&id=" + orderOperationInfoExpandTempService.getNewPurchaseOrderId(purchaseOrder));
    }

    private OrderOperationInfoViewModel createViewModel(OrderOperationInfo orderOperationInfo) {
        PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
        Payment payment = purchaseOrderService.getPaymentByPurchaseOrder(purchaseOrder);
        QuoteRecord quoteRecord = orderManageService.getQuoteRecordByPurchaseOrder(purchaseOrder);
        OrderOperationInfoViewModel viewModel = OrderOperationInfoViewModel.createViewModel(orderOperationInfo);
        viewModel.setInsuranceCompany(InsuranceCompanyData.createViewModel(quoteRecord.getInsuranceCompany()));
        viewModel.setPaymentChannel(purchaseOrder.getChannel());
        viewModel.setPaymentStatus(null != payment ? payment.getStatus() : null);
        viewModel.setGift(purchaseOrderGiftService.getGiftDetail(purchaseOrder));
        OrderProcessHistory orderProcessHistory = orderProcessHistoryExpandService.getLatestHistoryByPurchaseOrder(purchaseOrder);
        //序号重组
        viewModel.setPurchaseOrderIdBak(String.valueOf(purchaseOrder.getCreateTime().getTime())
                + purchaseOrder.getOrderNo().substring(purchaseOrder.getOrderNo().length() - 6, purchaseOrder.getOrderNo().length()));
        return viewModel;
    }

    @RequestMapping(value = "/{orderOperationInfoId}", method = RequestMethod.GET)
    public OrderOperationInfoViewModel getOrderOperationInfo(@PathVariable Long orderOperationInfoId) {
        if (logger.isDebugEnabled()) {
            logger.debug("get orderOperationInfo by orderOperationInfoId -> {}", orderOperationInfoId);
        }
        OrderOperationInfo orderOperationInfo = orderOperationInfoExpandTempService.getById(orderOperationInfoId);
        AssertUtil.notNull(orderOperationInfo, "can not find orderOperationInfo by id: " + orderOperationInfoId);
        return createViewModel(orderOperationInfo);
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public List<OrderTransmissionStatus> getAllOrderStatus() {
        return OrderTransmissionStatus.Enum.getNewStatusList();
    }

    @RequestMapping(value = "/{orderOperationInfoId}/status/enable", method = RequestMethod.GET)
    public Map<String, Object> getAllOrderStatus(@PathVariable Long orderOperationInfoId) {
        if (logger.isDebugEnabled()) {
            logger.debug("get all enable status by orderOperationInfoId -> {}", orderOperationInfoId);
        }
        OrderOperationInfo orderOperationInfo = orderOperationInfoExpandTempService.getById(orderOperationInfoId);
        AssertUtil.notNull(orderOperationInfo, "can not find orderOperationInfo by id -> " + orderOperationInfoId);
        Payment payment = purchaseOrderService.getPaymentByPurchaseOrder(orderOperationInfo.getPurchaseOrder());
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("oriStatus", orderOperationInfo.getCurrentStatus().getId());
        statusMap.put("switchStatus", orderOperationInfo.getCurrentStatus().next());
        return statusMap;
    }

    @RequestMapping(value = "/{orderOperationInfoId}/status", method = RequestMethod.PUT)
    public ResultModel updateStatus(@PathVariable Long orderOperationInfoId,
                                    @RequestParam(value = "newStatus", required = true) Long newStatus,
                                    @RequestParam(value = "confirmNo", required = false) String confirmNo,
                                    @RequestParam(value = "owner", required = false) Long owner) {
        logger.debug("update order status by orderOperationInfoId -> {}, newStatus -> {}, confirmNo -> {}, owner -> {}",
                orderOperationInfoId, newStatus, confirmNo, owner);
        OrderTransmissionStatus transmissionStatus = OrderTransmissionStatus.Enum.format(newStatus);
        AssertUtil.notNull(transmissionStatus, "illegal argument newStatus: " + newStatus);
        OrderOperationInfo orderOperationInfo = orderOperationInfoExpandTempService.getById(orderOperationInfoId);
        AssertUtil.notNull(orderOperationInfo, "can not find orderOperationInfo by id -> " + orderOperationInfoId);
        if (!allow(orderOperationInfo)) {
            return new ResultModel(false, "更改状态失败，请先录入保单");
        }
        orderTransmissionStatusHandler.request(orderOperationInfo, transmissionStatus, confirmNo, owner);
        return new ResultModel();
    }

    private boolean allow(OrderOperationInfo orderOperationInfo) {
        Long objId = orderOperationInfo.getPurchaseOrder().getObjId();
        if (insuranceService.findByQuoteRecordId(objId) != null || compulsoryInsuranceService.findByQuoteRecordId(objId) != null) {
            return true;
        }
        return false;
    }

}
