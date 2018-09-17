package com.cheche365.cheche.ordercenter.web.model.quote;

import com.cheche365.cheche.core.model.InsurancePackage;

/**
 * Created by xu.yelong on 2016/9/19.
 */
public class QuoteRecordViewModel {
    private Long purchaseOrderId;
    private InsurancePackage insurancePackage;//套餐类型
    private Long insuranceCompanyId;

    private Double premium = 0.0;//商业险保费
    private Double compulsoryPremium = 0.0;//交通险保费
    private Double autoTax = 0.0;//车船使用税

    private Double thirdPartyPremium = 0.0;//三者险保费
    private Double thirdPartyAmount = 0.0;//三者险保额

    private Double theftPremium = 0.0;//盗抢险保费
    private Double theftAmount = 0.0;//盗抢险保额

    private Double damagePremium = 0.0;//车损险保费
    private Double damageAmount = 0.0;// 车损险保额

    private Double driverPremium = 0.0;//车上人员（司机）保费
    private Double driverAmount = 0.0;//车上人员（司机）保额

    private Double passengerPremium = 0.0;//车上人员（乘客）保费
    private Double passengerAmount = 0.0;//车上人员（乘客）保额
    private Integer passengerCount = 0;//车上人员（乘客）数量

    private Double enginePremium = 0.0;//发动机特别险保费

    private Double glassPremium = 0.0;//玻璃单独破碎险保费

    private Double scratchPremium = 0.0; //划痕险保费
    private Double scratchAmount = 0.0; //划痕险保额

    private Double spontaneousLossPremium = 0.0;//自燃损失险保费
    private Double spontaneousLossAmount = 0.0;//自燃损失险保额
    private Double unableFindThirdPartyPremium = 0.0;//机动车损失保险无法找到第三方特约险保费

    private Double thirdPartyIop = 0.0;//三者不计免赔
    private Double theftIop = 0.0;//盗抢不计免赔
    private Double damageIop = 0.0;//车损不计免赔
    private Double driverIop = 0.0;//车上人员（司机）不计免赔
    private Double passengerIop = 0.0;//车上人员（乘客）不计免赔
    private Double engineIop = 0.0;//发动机特别险不计免赔
    private Double scratchIop = 0.0; //划痕险不计免赔保费
    private Double iopTotal = 0.0;//不计免赔总额
    private Double spontaneousLossAmountIop = 0.0;//自燃损失险不计免赔

    private String createTime;//创建时间
    private String updateTime;//修改时间
    private String operator;//操作人

    private Long logId;//被存在日志表中的对应日志id

    public void setUnableFindThirdPartyPremium(Double unableFindThirdPartyPremium) {
        this.unableFindThirdPartyPremium = unableFindThirdPartyPremium;
    }

    public void setSpontaneousLossAmountIop(Double spontaneousLossAmountIop) {
        this.spontaneousLossAmountIop = spontaneousLossAmountIop;
    }

    public Double getUnableFindThirdPartyPremium() {
        return unableFindThirdPartyPremium;
    }

    public Double getSpontaneousLossAmountIop() {
        return spontaneousLossAmountIop;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public InsurancePackage getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(InsurancePackage insurancePackage) {
        this.insurancePackage = insurancePackage;
    }

    public Double getPremium() {
        return premium;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
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

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }

    public Double getEnginePremium() {
        return enginePremium;
    }

    public void setEnginePremium(Double enginePremium) {
        this.enginePremium = enginePremium;
    }

    public Double getGlassPremium() {
        return glassPremium;
    }

    public void setGlassPremium(Double glassPremium) {
        this.glassPremium = glassPremium;
    }

    public Double getScratchPremium() {
        return scratchPremium;
    }

    public void setScratchPremium(Double scratchPremium) {
        this.scratchPremium = scratchPremium;
    }

    public Double getScratchAmount() {
        return scratchAmount;
    }

    public void setScratchAmount(Double scratchAmount) {
        this.scratchAmount = scratchAmount;
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

    public Double getDamageIop() {
        return damageIop;
    }

    public void setDamageIop(Double damageIop) {
        this.damageIop = damageIop;
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

    public Double getEngineIop() {
        return engineIop;
    }

    public void setEngineIop(Double engineIop) {
        this.engineIop = engineIop;
    }

    public Double getScratchIop() {
        return scratchIop;
    }

    public void setScratchIop(Double scratchIop) {
        this.scratchIop = scratchIop;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getInsuranceCompanyId() {
        return insuranceCompanyId;
    }

    public void setInsuranceCompanyId(Long insuranceCompanyId) {
        this.insuranceCompanyId = insuranceCompanyId;
    }

    public Double getIopTotal() {
        return iopTotal;
    }

    public void setIopTotal(Double iopTotal) {
        this.iopTotal = iopTotal;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }
}
