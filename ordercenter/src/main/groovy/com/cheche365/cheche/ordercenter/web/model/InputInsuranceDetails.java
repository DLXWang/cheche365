package com.cheche365.cheche.ordercenter.web.model;

import javax.validation.constraints.NotNull;

/**
 * Created by sunhuazhong on 2015/2/28.
 */
public class InputInsuranceDetails {
    @NotNull
    private String identity;//身份证号
    @NotNull
    private String licenseNo;//车牌号
    @NotNull
    private Long insuranceCompanyId;//保险公司
    private String proposalNo;//投保单号
    private String policyNo;//保单号
    private String effectiveDate;//生效日期
    private String expireDate;//失效日期
    private Double premium;//商业险保费
    private String originalPolicyNo;//原保单号（续保时用）
    private Double thirdPartyPremium;//三者险保费
    private Double thirdPartyAmount;//三者险保额
    private Double damagePremium;//车损险保费
    private Double damageAmount;// 车损险保额
    private Double theftPremium;//盗抢险保费
    private Double theftAmount;//盗抢险保额
    private Double enginePremium;//发动机特别险保费
    private Double engineAmount;//发动机特别险保额
    private Double driverPremium;//车上人员（司机）保费
    private Double driverAmount;//车上人员（司机）保额
    private Double passengerPremium;//车上人员（乘客）保费
    private Double passengerAmount;//车上人员（乘客）保额
    private Double passengerCount;//车上人员（乘客）数量
    private Double spontaneousLossPremium;//自燃损失险保费
    private Double spontaneousLossAmount;//自燃损失险保额
    private Double glassPremium;//玻璃单独破碎险保费
    private Double glassAmount;//玻璃单独破碎险保额
    private Long glassType;//玻璃险类型
    private Double scratchAmount; //划痕险保额
    private Double scratchPremium; //划痕险保费
    private Double damageIop;//车损不计免赔
    private Double thirdPartyIop;//三者不计免赔
    private Double theftIop;//盗抢不计免赔
    private Double engineIop;//发动机特别险不计免赔
    private Double driverIop;//车上人员（司机）不计免赔
    private Double passengerIop;//车上人员（乘客）不计免赔
    private Double scratchIop; //划痕险不计免赔
    private Integer effectiveHour; //生效小时
    private Integer expireHour; //过期小时
    private String insuredName;//被保险人
    private String operatorName;//操作人
    private Double discount;//浮动系数
    private Long purchaseOrderId;//purchase_order id

    private String proposalNoCI;//交强险投保单号
    private String policyNoCI;//交强险保单号
    private String effectiveDateCI;//交强险起保日期
    private String expireDateCI;//交强险终保日期
    private Double compulsoryPremium;//交强险保费
    private Double autoTax;//车船税
    private Double discountCI;//交强险折扣

    private String thirdPartyPaymentNo;//银行流水号

    private String expressCompany;//快递公司
    private String trackingNo;//快递单号
    private String insuranceImage;//商业险保单扫描文件地址
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

    public Double getPremium() {
        return premium;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }

    public String getOriginalPolicyNo() {
        return originalPolicyNo;
    }

    public void setOriginalPolicyNo(String originalPolicyNo) {
        this.originalPolicyNo = originalPolicyNo;
    }

    public Double getThirdPartyPremium() {
        return thirdPartyPremium;
    }

    public void setThirdPartyPremium(Double thirdPartyPremium) {
        this.thirdPartyPremium = thirdPartyPremium;
    }

    public Double getThirdPartyAmount() {
        return thirdPartyAmount;
    }

    public void setThirdPartyAmount(Double thirdPartyAmount) {
        this.thirdPartyAmount = thirdPartyAmount;
    }

    public Double getDamagePremium() {
        return damagePremium;
    }

    public void setDamagePremium(Double damagePremium) {
        this.damagePremium = damagePremium;
    }

    public Double getDamageAmount() {
        return damageAmount;
    }

    public void setDamageAmount(Double damageAmount) {
        this.damageAmount = damageAmount;
    }

    public Double getTheftPremium() {
        return theftPremium;
    }

    public void setTheftPremium(Double theftPremium) {
        this.theftPremium = theftPremium;
    }

    public Double getTheftAmount() {
        return theftAmount;
    }

    public void setTheftAmount(Double theftAmount) {
        this.theftAmount = theftAmount;
    }

    public Double getEnginePremium() {
        return enginePremium;
    }

    public void setEnginePremium(Double enginePremium) {
        this.enginePremium = enginePremium;
    }

    public Double getEngineAmount() {
        return engineAmount;
    }

    public void setEngineAmount(Double engineAmount) {
        this.engineAmount = engineAmount;
    }

    public Double getDriverPremium() {
        return driverPremium;
    }

    public void setDriverPremium(Double driverPremium) {
        this.driverPremium = driverPremium;
    }

    public Double getDriverAmount() {
        return driverAmount;
    }

    public void setDriverAmount(Double driverAmount) {
        this.driverAmount = driverAmount;
    }

    public Double getPassengerPremium() {
        return passengerPremium;
    }

    public void setPassengerPremium(Double passengerPremium) {
        this.passengerPremium = passengerPremium;
    }

    public Double getPassengerAmount() {
        return passengerAmount;
    }

    public void setPassengerAmount(Double passengerAmount) {
        this.passengerAmount = passengerAmount;
    }

    public Double getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Double passengerCount) {
        this.passengerCount = passengerCount;
    }

    public Double getSpontaneousLossPremium() {
        return spontaneousLossPremium;
    }

    public void setSpontaneousLossPremium(Double spontaneousLossPremium) {
        this.spontaneousLossPremium = spontaneousLossPremium;
    }

    public Double getSpontaneousLossAmount() {
        return spontaneousLossAmount;
    }

    public void setSpontaneousLossAmount(Double spontaneousLossAmount) {
        this.spontaneousLossAmount = spontaneousLossAmount;
    }

    public Double getGlassPremium() {
        return glassPremium;
    }

    public void setGlassPremium(Double glassPremium) {
        this.glassPremium = glassPremium;
    }

    public Double getGlassAmount() {
        return glassAmount;
    }

    public void setGlassAmount(Double glassAmount) {
        this.glassAmount = glassAmount;
    }

    public Double getScratchAmount() {
        return scratchAmount;
    }

    public void setScratchAmount(Double scratchAmount) {
        this.scratchAmount = scratchAmount;
    }

    public Double getScratchPremium() {
        return scratchPremium;
    }

    public void setScratchPremium(Double scratchPremium) {
        this.scratchPremium = scratchPremium;
    }

    public Double getDamageIop() {
        return damageIop;
    }

    public void setDamageIop(Double damageIop) {
        this.damageIop = damageIop;
    }

    public Double getThirdPartyIop() {
        return thirdPartyIop;
    }

    public void setThirdPartyIop(Double thirdPartyIop) {
        this.thirdPartyIop = thirdPartyIop;
    }

    public Double getTheftIop() {
        return theftIop;
    }

    public void setTheftIop(Double theftIop) {
        this.theftIop = theftIop;
    }

    public Double getEngineIop() {
        return engineIop;
    }

    public void setEngineIop(Double engineIop) {
        this.engineIop = engineIop;
    }

    public Double getDriverIop() {
        return driverIop;
    }

    public void setDriverIop(Double driverIop) {
        this.driverIop = driverIop;
    }

    public Double getPassengerIop() {
        return passengerIop;
    }

    public void setPassengerIop(Double passengerIop) {
        this.passengerIop = passengerIop;
    }

    public Double getScratchIop() {
        return scratchIop;
    }

    public void setScratchIop(Double scratchIop) {
        this.scratchIop = scratchIop;
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

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getProposalNoCI() {
        return proposalNoCI;
    }

    public void setProposalNoCI(String proposalNoCI) {
        this.proposalNoCI = proposalNoCI;
    }

    public String getPolicyNoCI() {
        return policyNoCI;
    }

    public void setPolicyNoCI(String policyNoCI) {
        this.policyNoCI = policyNoCI;
    }

    public String getEffectiveDateCI() {
        return effectiveDateCI;
    }

    public void setEffectiveDateCI(String effectiveDateCI) {
        this.effectiveDateCI = effectiveDateCI;
    }

    public String getExpireDateCI() {
        return expireDateCI;
    }

    public void setExpireDateCI(String expireDateCI) {
        this.expireDateCI = expireDateCI;
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

    public Double getDiscountCI() {
        return discountCI;
    }

    public void setDiscountCI(Double discountCI) {
        this.discountCI = discountCI;
    }

    public String getThirdPartyPaymentNo() {
        return thirdPartyPaymentNo;
    }

    public void setThirdPartyPaymentNo(String thirdPartyPaymentNo) {
        this.thirdPartyPaymentNo = thirdPartyPaymentNo;
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

    public String getInsuranceImage() {
        return insuranceImage;
    }

    public void setInsuranceImage(String insuranceImage) {
        this.insuranceImage = insuranceImage;
    }

    public String getCompulsoryInsuranceImage() {
        return compulsoryInsuranceImage;
    }

    public void setCompulsoryInsuranceImage(String compulsoryInsuranceImage) {
        this.compulsoryInsuranceImage = compulsoryInsuranceImage;
    }

    public Long getGlassType() {
        return glassType;
    }

    public void setGlassType(Long glassType) {
        this.glassType = glassType;
    }
}
