package com.cheche365.cheche.ordercenter.web.model.order;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.ordercenter.web.model.InsuranceCompanyData;
import com.cheche365.cheche.ordercenter.web.model.area.AreaViewData;
import com.cheche365.cheche.ordercenter.web.model.auto.AutoViewModel;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by wangfei on 2015/12/16.
 */
public class OrderOperationInfoViewModel {
    private Long orderOperationInfoId;
    private Long purchaseOrderId;
    private String purchaseOrderIdBak;//审计重组的序号
    private String orderNo;
    private OrderTransmissionStatus currentStatus;
    private OrderTransmissionStatus originalStatus;
    private String createTime;
    private String updateTime;
    private AutoViewModel auto;
    private InsuranceCompanyData insuranceCompany;
    private AreaViewData area;
    private PaymentStatus paymentStatus;
    private PaymentChannel paymentChannel;
    private Double paidAmount;
    private String assignerName;//指定人
    private String operatorName;//最后操作人
    private String paymentNo;
    private Double payableAmount;
    private String gift;
    private String confirmNo;
    private OrderStatus orderStatus;
    private boolean onlinePay;
    private Long channelId;
    private QuoteSource quoteSource;
    private String channelIcon;
    private boolean innerPay;
    private String latestComment;
    private String orderImageStatus;//照片审核状态
    private boolean isPaid;

    private boolean isThirdPart;
    private boolean isFanhua = false;
    private boolean isAgentChannel =false;
    private boolean supportAmend;
    private boolean supportChangeStatus;

    public boolean isSupportChangeStatus() {
        return supportChangeStatus;
    }

    public void setSupportChangeStatus(boolean supportChangeStatus) {
        this.supportChangeStatus = supportChangeStatus;
    }

    public String getOrderImageStatus() {
        return orderImageStatus;
    }

    public void setOrderImageStatus(String orderImageStatus) {
        this.orderImageStatus = orderImageStatus;
    }


    public Long getOrderOperationInfoId() {
        return orderOperationInfoId;
    }

    public void setOrderOperationInfoId(Long orderOperationInfoId) {
        this.orderOperationInfoId = orderOperationInfoId;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public OrderTransmissionStatus getOriginalStatus() { return originalStatus; }

    public void setOriginalStatus(OrderTransmissionStatus originalStatus) { this.originalStatus = originalStatus; }

    public OrderTransmissionStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(OrderTransmissionStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public AutoViewModel getAuto() {
        return auto;
    }

    public void setAuto(AutoViewModel auto) {
        this.auto = auto;
    }

    public InsuranceCompanyData getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompanyData insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public AreaViewData getArea() {
        return area;
    }

    public void setArea(AreaViewData area) {
        this.area = area;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getAssignerName() {
        return assignerName;
    }

    public void setAssignerName(String assignerName) {
        this.assignerName = assignerName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public Double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentChannel getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(PaymentChannel paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public String getConfirmNo() {
        return confirmNo;
    }

    public void setConfirmNo(String confirmNo) {
        this.confirmNo = confirmNo;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isOnlinePay() {
        return onlinePay;
    }

    public void setOnlinePay(boolean onlinePay) {
        this.onlinePay = onlinePay;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getPurchaseOrderIdBak() {
        return purchaseOrderIdBak;
    }

    public void setPurchaseOrderIdBak(String purchaseOrderIdBak) {
        this.purchaseOrderIdBak = purchaseOrderIdBak;
    }

    public QuoteSource getQuoteSource() {
        return quoteSource;
    }

    public void setQuoteSource(QuoteSource quoteSource) {
        this.quoteSource = quoteSource;
    }

    public String getChannelIcon() {
        return channelIcon;
    }

    public void setChannelIcon(String channelIcon) {
        this.channelIcon = channelIcon;
    }

    public boolean isInnerPay() {
        return innerPay;
    }

    public void setInnerPay(boolean innerPay) {
        this.innerPay = innerPay;
    }

    public boolean isPaid() {  return isPaid; }

    public void setPaid(boolean paid) {  isPaid = paid; }

    public boolean isThirdPart() { return isThirdPart; }

    public void setThirdPart(boolean thirdPart) { isThirdPart = thirdPart; }

    public boolean isFanhua() {   return isFanhua;  }

    public void setFanhua(boolean fanhua) { isFanhua = fanhua;  }

    public boolean isAgentChannel() {
        return isAgentChannel;
    }

    public void setAgentChannel(boolean agentChannel) {
        isAgentChannel = agentChannel;
    }

    public String getLatestComment() {
        return latestComment;
    }

    public void setLatestComment(String latestComment) {
        this.latestComment = latestComment;
    }


    public boolean isSupportAmend() {
        return supportAmend;
    }

    public void setSupportAmend(boolean supportAmend) {
        this.supportAmend = supportAmend;
    }

    public static OrderOperationInfoViewModel createViewModel(OrderOperationInfo orderOperationInfo) {
        if (null == orderOperationInfo) return null;

        PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
        Auto auto = purchaseOrder.getAuto();

        OrderOperationInfoViewModel viewModel = new OrderOperationInfoViewModel();
        viewModel.setOrderOperationInfoId(orderOperationInfo.getId());
        viewModel.setPurchaseOrderId(purchaseOrder.getId());
        viewModel.setOrderNo(purchaseOrder.getOrderNo());
        viewModel.setOriginalStatus((orderOperationInfo.getOriginalStatus() == null)?new OrderTransmissionStatus():orderOperationInfo.getOriginalStatus());
        viewModel.setCurrentStatus(orderOperationInfo.getCurrentStatus());
        viewModel.setCreateTime(DateUtils.getDateString(orderOperationInfo.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(orderOperationInfo.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setAuto(AutoViewModel.createViewModel(auto));
        viewModel.setArea(AreaViewData.createViewModel(purchaseOrder.getArea()));
        viewModel.setPaidAmount(purchaseOrder.getPaidAmount());
        viewModel.setPayableAmount(purchaseOrder.getPayableAmount());
        viewModel.setAssignerName(null == orderOperationInfo.getAssigner() ? "" : orderOperationInfo.getAssigner().getName());
        viewModel.setOperatorName(null == orderOperationInfo.getOperator() ? "" : orderOperationInfo.getOperator().getName());
        viewModel.setConfirmNo(StringUtils.trimToEmpty(orderOperationInfo.getConfirmNo()));
        viewModel.setOrderStatus(orderOperationInfo.getPurchaseOrder().getStatus());
        viewModel.setChannelId(null == orderOperationInfo.getPurchaseOrder().getSourceChannel() ? null :
            orderOperationInfo.getPurchaseOrder().getSourceChannel().getId());
        viewModel.setSupportAmend(orderOperationInfo.suppleAmend());
        return viewModel;
    }
}
