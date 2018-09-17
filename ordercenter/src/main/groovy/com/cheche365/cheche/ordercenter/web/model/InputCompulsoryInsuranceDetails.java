package com.cheche365.cheche.ordercenter.web.model;

import javax.validation.constraints.NotNull;

/**
 * Created by sunhuazhong on 2015/2/28.
 */
public class InputCompulsoryInsuranceDetails {
    @NotNull
    private String identity;//身份证号
    @NotNull
    private String licenseNo;//车牌号
    @NotNull
    private Long insuranceCompanyId;//保险公司
    @NotNull
    private String proposalNo;//投保单号
    @NotNull
    private String policyNo;//保单号
    @NotNull
    private String effectiveDate;//生效日期
    @NotNull
    private String expireDate;//失效日期
    private String originalPolicyNo;//原保单号（续保时用）
    @NotNull
    private Double compulsoryPremium;//交通强制险
    private Double autoTax;//车船使用税
    private Integer effectiveHour; //生效小时
    private Integer expireHour; //过期小时
    private String insuredName;//被保险人姓名
    private String operatorName;//操作人
    private Double compulsoryDiscount;//浮动系数
    private Long forceId;

    private String expressCompany;//快递公司
    private String trackingNo;//快递单号
    private String compulsoryInsuranceImage;//交强险保单扫描文件地址


    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public Long getInsuranceCompanyId() {
        return insuranceCompanyId;
    }

    public void setInsuranceCompanyId(Long insuranceCompanyId) {
        this.insuranceCompanyId = insuranceCompanyId;
    }

    public String getProposalNo() {
        return proposalNo;
    }

    public void setProposalNo(String proposalNo) {
        this.proposalNo = proposalNo;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

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

    public String getOriginalPolicyNo() {
        return originalPolicyNo;
    }

    public void setOriginalPolicyNo(String originalPolicyNo) {
        this.originalPolicyNo = originalPolicyNo;
    }

    public Double getCompulsoryPremium() {
        return compulsoryPremium;
    }

    public void setCompulsoryPremium(Double compulsoryPremium) {
        this.compulsoryPremium = compulsoryPremium;
    }

    public Double getAutoTax() {
        return autoTax;
    }

    public void setAutoTax(Double autoTax) {
        this.autoTax = autoTax;
    }

    public Integer getEffectiveHour() {
        return effectiveHour;
    }

    public void setEffectiveHour(Integer effectiveHour) {
        this.effectiveHour = effectiveHour;
    }

    public Integer getExpireHour() {
        return expireHour;
    }

    public void setExpireHour(Integer expireHour) {
        this.expireHour = expireHour;
    }

    public String getInsuredName() {
        return insuredName;
    }

    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Double getCompulsoryDiscount() {
        return compulsoryDiscount;
    }

    public void setCompulsoryDiscount(Double compulsoryDiscount) {
        this.compulsoryDiscount = compulsoryDiscount;
    }

    public Long getForceId() {
        return forceId;
    }

    public void setForceId(Long forceId) {
        this.forceId = forceId;
    }

    public String getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getCompulsoryInsuranceImage() {
        return compulsoryInsuranceImage;
    }

    public void setCompulsoryInsuranceImage(String compulsoryInsuranceImage) {
        this.compulsoryInsuranceImage = compulsoryInsuranceImage;
    }
}
