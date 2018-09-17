package com.cheche365.cheche.ordercenter.web.model.healthOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.model.abao.InsurancePerson;
import com.cheche365.cheche.core.model.abao.InsurancePolicy;
import com.cheche365.cheche.core.model.abao.InsuranceProduct;
import com.cheche365.cheche.ordercenter.web.model.area.AreaViewData;
import com.cheche365.cheche.ordercenter.web.model.auto.AutoViewModel;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by chenxiangyin on 2016/12/26.
 */
public class InsurancePolicyViewModel {
    private Long orderOperationInfoId;
    private Long insurancePolicyId;
    private Long purchaseOrderId;
    private String orderNo;
    private OrderTransmissionStatus currentStatus;
    private String createTime;
    private String updateTime;
    private AutoViewModel auto;
    private InsuranceCompany insuranceCompany;
    private AreaViewData area;
    private PaymentStatus paymentStatus;
    private PaymentChannel paymentChannel;
    private Double paidAmount;
    private String assignerName;//指定人
    private String operatorName;//最后操作人
    private Double payableAmount;
    private String gift;
    private String confirmNo;
    private OrderStatus orderStatus;
    private QuoteSource quoteSource;
    private Channel channel;
    private boolean isPaid;
    private InsurancePerson insurancePerson;
    private String insureStatus;
    private String effectiveDate;
    private String expireDate;
    private InsurancePerson applicantPerson; // 投保人姓名
    private String policyNo;//保单编号
    private Double premium; // 保费
    private BigDecimal amount;//保额
    private String waitingDays;//等待期
    private String relation;//与被保人关系
    private InsuranceProduct insuranceProduct;//保险产品

    private String agentName;
    private String cardNum;
    private Double rebate=0.0;


    public static InsurancePolicyViewModel createAllViewMdel(InsurancePolicy insurancePolicy){
        InsurancePolicyViewModel viewModel = createBaseViewModel(insurancePolicy);
        viewModel.setInsureStatus(getInsurancePolicyStatus(insurancePolicy));
        viewModel.setEffectiveDate(DateUtils.getDateString(insurancePolicy.getEffectiveDate(),DateUtils.DATE_SHORTDATE_PATTERN));
        viewModel.setExpireDate(DateUtils.getDateString(insurancePolicy.getExpireDate(),DateUtils.DATE_SHORTDATE_PATTERN));
        viewModel.setPolicyNo(insurancePolicy.getPolicyNo());
        viewModel.setPremium(insurancePolicy.getInsuranceQuote().getPremium().doubleValue() * 1);
        if(insurancePolicy.getApplicantPerson().getRelationship() != null){
            viewModel.setRelation(insurancePolicy.getApplicantPerson().getRelationship().getName());
        }
        return viewModel;
    }

    public static InsurancePolicyViewModel createBaseViewModel(InsurancePolicy insurancePolicy) {
        InsurancePolicyViewModel viewModel = new InsurancePolicyViewModel();
        viewModel.setOrderNo(insurancePolicy.getPurchaseOrder().getOrderNo());
        viewModel.setOrderStatus(insurancePolicy.getPurchaseOrder().getStatus());
        viewModel.setChannel(insurancePolicy.getPurchaseOrder().getSourceChannel());
        viewModel.setInsuranceCompany(insurancePolicy.getInsuranceCompany());
        viewModel.setApplicantPerson(insurancePolicy.getApplicantPerson());
        viewModel.setInsurancePerson(insurancePolicy.getInsuredPerson());
        viewModel.setPaidAmount(insurancePolicy.getPurchaseOrder().getPaidAmount());
        viewModel.setInsurancePolicyId(insurancePolicy.getId());
        viewModel.setPaidAmount(insurancePolicy.getPurchaseOrder().getPaidAmount());
        viewModel.setCreateTime(DateUtils.getDateString(insurancePolicy.getPurchaseOrder().getCreateTime(),DateUtils.DATE_LONGTIME24_PATTERN));
        return viewModel;
    }

    //待付款 保障中 已失效
    public static String getInsurancePolicyStatus(InsurancePolicy insurancePolicy){
        Date current = new Date();
        if(insurancePolicy.getPurchaseOrder().getStatus().getId().equals(OrderStatus.Enum.PENDING_PAYMENT_1.getId())){
            return "待付款";
        }
        if(DateUtils.compareDate(current,insurancePolicy.getEffectiveDate()) && DateUtils.compareDate(insurancePolicy.getExpireDate(),current)){
            return "保障中";
        }
        return "已失效";
    }

    public Long getOrderOperationInfoId() {
        return orderOperationInfoId;
    }

    public void setOrderOperationInfoId(Long orderOperationInfoId) {
        this.orderOperationInfoId = orderOperationInfoId;
    }

    public Long getInsurancePolicyId() {  return insurancePolicyId;  }

    public void setInsurancePolicyId(Long insurancePolicyId) { this.insurancePolicyId = insurancePolicyId; }

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

    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
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

    public QuoteSource getQuoteSource() {
        return quoteSource;
    }

    public void setQuoteSource(QuoteSource quoteSource) {
        this.quoteSource = quoteSource;
    }

    public boolean isPaid() {  return isPaid; }

    public void setPaid(boolean paid) {  isPaid = paid; }

    public Channel getChannel() {  return channel;  }

    public void setChannel(Channel channel) {   this.channel = channel; }

    public InsurancePerson getInsurancePerson() { return insurancePerson; }

    public void setInsurancePerson(InsurancePerson insurancePerson) { this.insurancePerson = insurancePerson;  }

    public String getInsureStatus() {  return insureStatus;  }

    public void setInsureStatus(String insureStatus) {    this.insureStatus = insureStatus;  }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public InsurancePerson getApplicantPerson() {
        return applicantPerson;
    }

    public void setApplicantPerson(InsurancePerson applicantPerson) {
        this.applicantPerson = applicantPerson;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public Double getPremium() {
        return premium;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getWaitingDays() {
        return waitingDays;
    }

    public void setWaitingDays(String waitingDays) {
        this.waitingDays = waitingDays;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setInsuranceProduct(final InsuranceProduct insuranceProduct) {
        this.insuranceProduct = insuranceProduct;
    }

    public InsuranceProduct getInsuranceProduct() {
        return insuranceProduct;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public Double getRebate() {
        return rebate;
    }

    public void setRebate(Double rebate) {
        this.rebate = rebate;
    }
}
