package com.cheche365.cheche.ordercenter.model;

/**
 * Created by wangfei on 2015/11/13.
 */
public class NationwideOrderQuery {
    private String orderNo;//订单号
    private String owner;//车主
    private String licensePlateNo;//车牌号
    private Long areaId;//地区
    private Long statusId;//订单状态ID
    private String institutionName;//出单机构
    private Integer paymentStatusId;//支付状态
    private Integer warningReasonId;//异常原因
    private String trackingNo;//快递单号
    private String policyNo;//保单号
    private Integer auditStatus;//审核状态
    private Boolean quoteTime;//20分钟内尚未报价
    private Boolean sourceChannel;//订单来源渠道 true-支付宝

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public Integer getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(Integer paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public Integer getWarningReasonId() {
        return warningReasonId;
    }

    public void setWarningReasonId(Integer warningReasonId) {
        this.warningReasonId = warningReasonId;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Boolean getQuoteTime() {
        return quoteTime;
    }

    public void setQuoteTime(Boolean quoteTime) {
        this.quoteTime = quoteTime;
    }

    public Boolean getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(Boolean sourceChannel) {
        this.sourceChannel = sourceChannel;
    }
}
