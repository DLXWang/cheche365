package com.cheche365.cheche.ordercenter.web.model.nationwideOrder;

import com.cheche365.cheche.core.model.InsurancePackage;

/**
 * Created by xu.yelong on 2015/11/19.
 */
public class InsurancePackageViewModel {
    private Long id;
    private Long type;//套餐类型:基本,大众,全面,自定义
    private Boolean compulsory;//是否购买交通强制险
    private Boolean autoTax;//是否购买车船使用税
    private Double thirdPartyAmount;//三者险金额：空表示不投保
    private Boolean thirdPartyIop;//是否购买三者不计免赔
    private Boolean damage;//是否购买车损险
    private Boolean damageIop;//是否购买车损不计免赔
    private Boolean theft;//是否购买盗抢险
    private Boolean theftIop;//是否购买盗抢不计免赔
    private Boolean engine;//是否购买发动机特别损失险
    private Boolean engineIop;//是否购买发动机特别险不计免赔
    private Boolean glass;//是否购买玻璃险
    private Long glassType;//玻璃类型:1:国产;2:进口
    private Double driverAmount;//车上人员（司机）责任险保额：null：不投保
    private Boolean driverIop;//是否购买车上人员（司机）不计免赔
    private Double passengerAmount;//车上人员（乘客）责任险,null表示不投保
    private Boolean passengerIop;//是否购买车上人员（乘客）不计免赔
    private Boolean spontaneousLoss;//是否购买自燃险
    private Double scratchAmount;//划痕险保额,null表示不投保
    private Boolean scratchIop;//是否购买划痕险不计免赔

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Boolean getCompulsory() {
        return compulsory;
    }

    public void setCompulsory(Boolean compulsory) {
        this.compulsory = compulsory;
    }

    public Boolean getAutoTax() {
        return autoTax;
    }

    public void setAutoTax(Boolean autoTax) {
        this.autoTax = autoTax;
    }

    public Double getThirdPartyAmount() {
        return thirdPartyAmount;
    }

    public void setThirdPartyAmount(Double thirdPartyAmount) {
        this.thirdPartyAmount = thirdPartyAmount;
    }

    public Boolean getThirdPartyIop() {
        return thirdPartyIop;
    }

    public void setThirdPartyIop(Boolean thirdPartyIop) {
        this.thirdPartyIop = thirdPartyIop;
    }

    public Boolean getDamage() {
        return damage;
    }

    public void setDamage(Boolean damage) {
        this.damage = damage;
    }

    public Boolean getDamageIop() {
        return damageIop;
    }

    public void setDamageIop(Boolean damageIop) {
        this.damageIop = damageIop;
    }

    public Boolean getTheft() {
        return theft;
    }

    public void setTheft(Boolean theft) {
        this.theft = theft;
    }

    public Boolean getTheftIop() {
        return theftIop;
    }

    public void setTheftIop(Boolean theftIop) {
        this.theftIop = theftIop;
    }

    public Boolean getEngine() {
        return engine;
    }

    public void setEngine(Boolean engine) {
        this.engine = engine;
    }

    public Boolean getEngineIop() {
        return engineIop;
    }

    public void setEngineIop(Boolean engineIop) {
        this.engineIop = engineIop;
    }

    public Boolean getGlass() {
        return glass;
    }

    public void setGlass(Boolean glass) {
        this.glass = glass;
    }

    public Long getGlassType() {
        return glassType;
    }

    public void setGlassType(Long glassType) {
        this.glassType = glassType;
    }

    public Double getDriverAmount() {
        return driverAmount;
    }

    public void setDriverAmount(Double driverAmount) {
        this.driverAmount = driverAmount;
    }

    public Boolean getDriverIop() {
        return driverIop;
    }

    public void setDriverIop(Boolean driverIop) {
        this.driverIop = driverIop;
    }

    public Double getPassengerAmount() {
        return passengerAmount;
    }

    public void setPassengerAmount(Double passengerAmount) {
        this.passengerAmount = passengerAmount;
    }

    public Boolean getPassengerIop() {
        return passengerIop;
    }

    public void setPassengerIop(Boolean passengerIop) {
        this.passengerIop = passengerIop;
    }

    public Boolean getSpontaneousLoss() {
        return spontaneousLoss;
    }

    public void setSpontaneousLoss(Boolean spontaneousLoss) {
        this.spontaneousLoss = spontaneousLoss;
    }

    public Double getScratchAmount() {
        return scratchAmount;
    }

    public void setScratchAmount(Double scratchAmount) {
        this.scratchAmount = scratchAmount;
    }

    public Boolean getScratchIop() {
        return scratchIop;
    }

    public void setScratchIop(Boolean scratchIop) {
        this.scratchIop = scratchIop;
    }

    public static InsurancePackageViewModel createViewModel(InsurancePackage insurancePackage){

        return null;
    }
}
