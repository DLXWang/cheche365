package com.cheche365.cheche.ordercenter.web.controller.order;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.model.PurchaseOrderModifyView;
import com.cheche365.cheche.ordercenter.service.order.OrderFinderService;
import com.cheche365.cheche.manage.common.service.sms.SMSHelper;
import com.cheche365.cheche.ordercenter.web.model.order.AmendQuoteRecordViewModel;
import com.cheche365.cheche.ordercenter.web.model.order.DailyInsuranceViewModel;
import com.cheche365.cheche.ordercenter.web.model.order.OrderDetailViewData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by wangfei on 2015/4/30.
 */
@RestController
@RequestMapping("/orderCenter/order")
public class OrderFinderController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderFinderService orderFinderService;

    @Autowired
    private SMSHelper smsHelper;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private BaseService baseService;

    /**
     * get order detail
     *
     * @param purchaseOrderId
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public OrderDetailViewData detail(@RequestParam(value = "purchaseOrderId", required = true) Long purchaseOrderId) {
        if (purchaseOrderId == null || purchaseOrderId < 1) {
            throw new FieldValidtorException("find order detail, id can not be null or less than 1");
        }
        return orderFinderService.getOrderDetail(purchaseOrderId);
    }

    /**
     * get order followInfo
     *
     * @param purchaseOrderId
     * @return
     */
    @RequestMapping(value = "/followInfo", method = RequestMethod.GET)
    public Map<String, String> followInfo(@RequestParam(value = "purchaseOrderId", required = true) Long purchaseOrderId) {
        return orderFinderService.getOrderFollowInfo(purchaseOrderId);
    }

    /**
     * 设置订单状态为已付款
     *
     * @param orderIds
     * @return
     */
    @RequestMapping(value = "/payment", method = RequestMethod.PUT)
    public ModelAndViewResult setOrderPaymentStatus(@RequestParam(value = "orderIds", required = true) String orderIds) {
        return orderFinderService.setOrderPaymentStatus(orderIds);
    }

    /**
     * 把线下订单支付方式置为线上订单（微信或者银联）
     *
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/payment/channel/change", method = RequestMethod.PUT)
    public ModelAndViewResult changeOrderPaymentChannel(@RequestParam(value = "orderId", required = true) Long orderId) {
        return orderFinderService.changeOrderPaymentChannel(orderId);
    }

    @RequestMapping(value = "/findAmendRecordsByOrderId", method = RequestMethod.GET)
    public List<AmendQuoteRecordViewModel> findAmendRecordsByOrderId(@RequestParam(value = "purchaseOrderId", required = true) Long orderId) {
        return orderFinderService.findAmendRecordsByOrderId(orderId);
    }

    @RequestMapping(value = "/findTotalByPurchaseOrderId", method = RequestMethod.GET)
    public DailyInsuranceViewModel findTotalByPurchaseOrderId(@RequestParam(value = "purchaseOrderId", required = true) Long orderId) {
        return orderFinderService.findTotalByPurchaseOrderId(orderId);
    }

    @RequestMapping(value = "/findDailyInsuranceByOrderId", method = RequestMethod.POST)
    public DataTablePageViewModel findDailyInsuranceByOrderId(PublicQuery query, @RequestParam(value = "orderId", required = true) Long orderId) {
        Page<DailyInsurance> page = orderFinderService.getDailyInsuranceOrdersByPage(query, orderId);
        List<DailyInsurance> dailyInsuranceList = page.getContent();
        Map<Long, List<DailyRestartInsurance>> dailyRestartMap = orderFinderService.getDailyRestartInsurance(dailyInsuranceList);
        List<DailyInsuranceViewModel> modelList = DailyInsuranceViewModel.createViewModel(dailyInsuranceList, dailyRestartMap);
        PageInfo pageInfo = baseService.createPageInfo(page);
        return new DataTablePageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), modelList);
    }

    @RequestMapping(value = "/findQuoteRecordByPurchaseOrderHistoryId", method = RequestMethod.GET)
    public OrderDetailViewData findQuoteRecordByPurchaseOrderHistoryId(@RequestParam(value = "orderHistoryId", required = true) Long orderHistoryId) {
        return orderFinderService.findQuoteRecordByPurchaseOrderHistoryId(orderHistoryId);
    }

    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public ResultModel sendMessage(@RequestParam(value = "quoteRecordId", required = true) Long recordId,
                                   @RequestParam(value = "paymentType", required = true) Long paymentType,
                                   @RequestParam(value = "orderNo", required = false) String orderNo) {
        QuoteRecord record = quoteRecordRepository.findOne(recordId);
        if (paymentType.equals(PaymentType.Enum.INITIALPAYMENT_1.getId()))//首次支付，发送短信与支付送短信是一样的
            smsHelper.sendCommitOrderMsg(orderNo);
        else if (paymentType.equals(PaymentType.Enum.ADDITIONALPAYMENT_2.getId()))//增补，发送短信内容为：由于险种调整、更换保险公司等原因致使保费增加，现需要您补充差额。如有疑问请致电4000-150-999
            smsHelper.sendAmendQuoteMsg(record);
        return new ResultModel(true, "短信发送成功");
    }

    @RequestMapping(value = "/updatePurchaseOrder", method = RequestMethod.POST)
    public ResultModel updatePurchaseOrder(@RequestBody PurchaseOrderModifyView purchaseOrder) {
        Address address = orderFinderService.updatePurchaseOrder(purchaseOrder);
        return new ResultModel(true, CacheUtil.doJacksonSerialize(address));
    }

    @RequestMapping(value = "/getDetailMessage", method = RequestMethod.GET)
    public List<String> getLogDetailMessage(@RequestParam(value = "purchaseOrderId", required = true) Long purchaseOrderId) {
        return orderFinderService.getDetailMessage(purchaseOrderId);
    }
}
