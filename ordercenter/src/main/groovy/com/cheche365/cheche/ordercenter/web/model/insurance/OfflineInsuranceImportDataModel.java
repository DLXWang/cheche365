package com.cheche365.cheche.ordercenter.web.model.insurance;


import java.math.BigDecimal;

/**
 * 财务对账查询model
 * Created by cxy on 2017/12/9.
 */
public class OfflineInsuranceImportDataModel {
    private String area;
    private String institution;
    private String insuranceComp;
    private String balanceStartTime;
    private String balanceEndTime;
    private String policyNo;
    private String issueStartTime;
    private String issueEndTime;
    private int pageSize;
    private int currentPage;
    private String vinNo;
    private String orderNo;
    private Integer draw;
    private Integer status;
    private String licensePlateNo;
    private String commercialRebate;
    private String compulsoryRebate;
    private BigDecimal payableAmount;
    private BigDecimal paidAmount;
    private Long purchaseOrderId;
    private Long rebateId;
    private Integer insuranceType;//0商业险1交强险
    private BigDecimal differ;

    private String createDate;

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    private Integer num;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getInsuranceComp() {
        return insuranceComp;
    }

    public void setInsuranceComp(String insuranceComp) {
        this.insuranceComp = insuranceComp;
    }

    public String getBalanceStartTime() {
        return balanceStartTime;
    }

    public void setBalanceStartTime(String balanceStartTime) {
        this.balanceStartTime = balanceStartTime;
    }

    public String getBalanceEndTime() {
        return balanceEndTime;
    }

    public void setBalanceEndTime(String balanceEndTime) {
        this.balanceEndTime = balanceEndTime;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getIssueStartTime() {
        return issueStartTime;
    }

    public void setIssueStartTime(String issueStartTime) {
        this.issueStartTime = issueStartTime;
    }

    public String getIssueEndTime() {
        return issueEndTime;
    }

    public void setIssueEndTime(String issueEndTime) {
        this.issueEndTime = issueEndTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getVinNo() {
        return vinNo;
    }

    public void setVinNo(String vinNo) {
        this.vinNo = vinNo;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public String getCompulsoryRebate() {
        return compulsoryRebate;
    }

    public void setCompulsoryRebate(String compulsoryRebate) {
        this.compulsoryRebate = compulsoryRebate;
    }
    public String getCommercialRebate() {
        return commercialRebate;
    }

    public void setCommercialRebate(String commercialRebate) {
        this.commercialRebate = commercialRebate;
    }

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public Long getRebateId() {
        return rebateId;
    }

    public void setRebateId(Long rebateId) {
        this.rebateId = rebateId;
    }

    public Integer getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(Integer insuranceType) {
        this.insuranceType = insuranceType;
    }

    public BigDecimal getDiffer() {
        return differ;
    }

    public void setDiffer(BigDecimal differ) {
        this.differ = differ;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
