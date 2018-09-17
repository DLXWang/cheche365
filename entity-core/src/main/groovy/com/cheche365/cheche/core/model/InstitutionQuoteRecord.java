package com.cheche365.cheche.core.model;

import com.cheche365.cheche.common.util.DoubleUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * 出单机构报价表
 * Created by sunhuazhong on 2015/11/13.
 */
@Entity
public class InstitutionQuoteRecord {
    private Long id;
    private PurchaseOrder purchaseOrder;//订单
    private InsurancePackage insurancePackage;//套餐类型
    private Institution institution;//出单机构

    private Double commercialRebate;//商业险佣金
    private Double compulsoryRebate;//交强险佣金

    private String commercialPolicyNo;//商业险保单号
    private String compulsoryPolicyNo;//交强险保单号

    private Double commercialPremium = 0.0;//商业险保费
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

    private Double thirdPartyIop = 0.0;//三者不计免赔
    private Double theftIop = 0.0;//盗抢不计免赔
    private Double damageIop = 0.0;//车损不计免赔
    private Double driverIop = 0.0;//车上人员（司机）不计免赔
    private Double passengerIop = 0.0;//车上人员（乘客）不计免赔
    private Double engineIop = 0.0;//发动机特别险不计免赔
    private Double scratchIop = 0.0; //划痕险不计免赔保费

    private Date createTime;//创建时间
    private Date updateTime;//修改时间
    private InternalUser operator;//操作人

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name="purchaseOrder", foreignKey=@ForeignKey(name="FK_INSTITUTION_QUOTE_RECORD_REF_PURCHASE_ORDER", foreignKeyDefinition="FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @ManyToOne
    @JoinColumn(name="insurancePackage", foreignKey=@ForeignKey(name="FK_INSTITUTION_QUOTE_RECORD_REF_INSURANCE_PACKAGE", foreignKeyDefinition="FOREIGN KEY (insurance_package) REFERENCES insurance_package(id)"))
    public InsurancePackage getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(InsurancePackage insurancePackage) {
        this.insurancePackage = insurancePackage;
    }

    @ManyToOne
    @JoinColumn(name="institution", foreignKey=@ForeignKey(name="FK_INSTITUTION_QUOTE_RECORD_REF_INSTITUTION", foreignKeyDefinition="FOREIGN KEY (institution) REFERENCES institution(id)"))
    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getCommercialRebate() {
        return commercialRebate;
    }

    public void setCommercialRebate(Double commercialRebate) {
        this.commercialRebate = commercialRebate;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getCompulsoryRebate() {
        return compulsoryRebate;
    }

    public void setCompulsoryRebate(Double compulsoryRebate) {
        this.compulsoryRebate = compulsoryRebate;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getCommercialPolicyNo() {
        return commercialPolicyNo;
    }

    public void setCommercialPolicyNo(String commercialPolicyNo) {
        this.commercialPolicyNo = commercialPolicyNo;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getCompulsoryPolicyNo() {
        return compulsoryPolicyNo;
    }

    public void setCompulsoryPolicyNo(String compulsoryPolicyNo) {
        this.compulsoryPolicyNo = compulsoryPolicyNo;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getCommercialPremium() {
        return commercialPremium;
    }

    public void setCommercialPremium(Double commercialPremium) {
        this.commercialPremium = commercialPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getCompulsoryPremium() {
        return compulsoryPremium;
    }

    public void setCompulsoryPremium(Double compulsoryPremium) {
        this.compulsoryPremium = compulsoryPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getAutoTax() {
        return autoTax;
    }

    public void setAutoTax(Double autoTax) {
        this.autoTax = autoTax;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getThirdPartyPremium() {
        return thirdPartyPremium;
    }

    public void setThirdPartyPremium(Double thirdPartyPremium) {
        this.thirdPartyPremium = thirdPartyPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getThirdPartyAmount() {
        return thirdPartyAmount;
    }

    public void setThirdPartyAmount(Double thirdPartyAmount) {
        this.thirdPartyAmount = thirdPartyAmount;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getTheftPremium() {
        return theftPremium;
    }

    public void setTheftPremium(Double theftPremium) {
        this.theftPremium = theftPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getTheftAmount() {
        return theftAmount;
    }

    public void setTheftAmount(Double theftAmount) {
        this.theftAmount = theftAmount;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDamagePremium() {
        return damagePremium;
    }

    public void setDamagePremium(Double damagePremium) {
        this.damagePremium = damagePremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDamageAmount() {
        return damageAmount;
    }

    public void setDamageAmount(Double damageAmount) {
        this.damageAmount = damageAmount;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDriverPremium() {
        return driverPremium;
    }

    public void setDriverPremium(Double driverPremium) {
        this.driverPremium = driverPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDriverAmount() {
        return driverAmount;
    }

    public void setDriverAmount(Double driverAmount) {
        this.driverAmount = driverAmount;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getPassengerPremium() {
        return passengerPremium;
    }

    public void setPassengerPremium(Double passengerPremium) {
        this.passengerPremium = passengerPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getPassengerAmount() {
        return passengerAmount;
    }

    public void setPassengerAmount(Double passengerAmount) {
        this.passengerAmount = passengerAmount;
    }

    @Column(columnDefinition = "tinyint(3)")
    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getEnginePremium() {
        return enginePremium;
    }

    public void setEnginePremium(Double enginePremium) {
        this.enginePremium = enginePremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getGlassPremium() {
        return glassPremium;
    }

    public void setGlassPremium(Double glassPremium) {
        this.glassPremium = glassPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getScratchPremium() {
        return scratchPremium;
    }

    public void setScratchPremium(Double scratchPremium) {
        this.scratchPremium = scratchPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getScratchAmount() {
        return scratchAmount;
    }

    public void setScratchAmount(Double scratchAmount) {
        this.scratchAmount = scratchAmount;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getSpontaneousLossPremium() {
        return spontaneousLossPremium;
    }

    public void setSpontaneousLossPremium(Double spontaneousLossPremium) {
        this.spontaneousLossPremium = spontaneousLossPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getSpontaneousLossAmount() {
        return spontaneousLossAmount;
    }

    public void setSpontaneousLossAmount(Double spontaneousLossAmount) {
        this.spontaneousLossAmount = spontaneousLossAmount;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getThirdPartyIop() {
        return thirdPartyIop;
    }

    public void setThirdPartyIop(Double thirdPartyIop) {
        this.thirdPartyIop = thirdPartyIop;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getTheftIop() {
        return theftIop;
    }

    public void setTheftIop(Double theftIop) {
        this.theftIop = theftIop;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDamageIop() {
        return damageIop;
    }

    public void setDamageIop(Double damageIop) {
        this.damageIop = damageIop;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDriverIop() {
        return driverIop;
    }

    public void setDriverIop(Double driverIop) {
        this.driverIop = driverIop;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getPassengerIop() {
        return passengerIop;
    }

    public void setPassengerIop(Double passengerIop) {
        this.passengerIop = passengerIop;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getEngineIop() {
        return engineIop;
    }

    public void setEngineIop(Double engineIop) {
        this.engineIop = engineIop;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getScratchIop() {
        return scratchIop;
    }

    public void setScratchIop(Double scratchIop) {
        this.scratchIop = scratchIop;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @ManyToOne
    @JoinColumn(name="operator", foreignKey=@ForeignKey(name="FK_INSTITUTION_QUOTE_RECORD_REF_OPERATOR", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    public Double calculateTotalPremium(){
        return DoubleUtils.displayDoubleValue(doubleValue(this.commercialPremium) + doubleValue(this.compulsoryPremium) + doubleValue(this.autoTax));
    }

    public Double calculateRebateablePremium(){
        return DoubleUtils.displayDoubleValue(calculateTotalPremium() - doubleValue(this.autoTax));
    }

    //不计免赔总额
    public Double calculateIopTotal(){
        return DoubleUtils.displayDoubleValue(
            doubleValue(this.getDamageIop())
                + doubleValue(this.getDriverIop())
                + doubleValue(this.getPassengerIop())
                + doubleValue(this.getThirdPartyIop())
                + doubleValue(this.getTheftIop())
                + doubleValue(this.getScratchIop())
                + doubleValue(this.getEngineIop())
        );
    }

    private double doubleValue(Double doubleObj){
        return DoubleUtils.doubleValue(doubleObj);
    }

    public void clearAllData(){
        this.setInsurancePackage(null);//套餐类型
        this.setInstitution(null);//出单机构

        this.setCommercialRebate(null);//商业险佣金
        this.setCompulsoryRebate(null);//交强险佣金

        this.setCommercialPolicyNo(null);//商业险保单号
        this.setCompulsoryPolicyNo(null);//交强险保单号

        this.setCommercialPremium(null);//商业险保费
        this.setCompulsoryPremium(null);//交通强制险
        this.setAutoTax(null);//车船使用税

        this.setThirdPartyPremium(null);//三者险保费
        this.setThirdPartyAmount(null);//三者险保额
        this.setThirdPartyIop(null);//三者不计免赔

        this.setDamagePremium(null);//车损险保费
        this.setDamageAmount(null);//车损险保额
        this.setDamageIop(null);//车损不计免赔

        this.setTheftPremium(null);//盗抢险保费
        this.setTheftAmount(null);//盗抢险保额
        this.setTheftIop(null);//盗抢不计免赔

        this.setEnginePremium(null);//发动机特别险保费
        this.setEngineIop(null);//发动机特别险不计免赔

        this.setDriverPremium(null);//车上人员（司机）保费
        this.setDriverAmount(null);//车上人员（司机）保额
        this.setDriverIop(null);//车上人员（司机）不计免赔

        this.setPassengerPremium(null);//车上人员（乘客）保费
        this.setPassengerAmount(null);//车上人员（乘客）保额
        this.setPassengerIop(null);//车上人员（乘客）不计免赔

        this.setSpontaneousLossPremium(null);//自燃损失险保费
        this.setSpontaneousLossAmount(null);//自燃损失险保额

        this.setGlassPremium(null);//玻璃单独破碎险保费

        this.setScratchPremium(null);//划痕险保额
        this.setScratchAmount(null);//划痕险保费
        this.setScratchIop(null);//划痕险不计免赔保费
    }

    public void formatEmptyPremium(){
        if(null == this.getCommercialPremium()) {
            this.setCommercialPremium(0.0);//商业险保费
        }
        if(null == this.getCompulsoryPremium()) {
            this.setCompulsoryPremium(0.0);//交通强制险
        }
        if(null == this.getAutoTax()) {
            this.setAutoTax(0.0);//车船使用税
        }
        if(null == this.getThirdPartyPremium()) {
            this.setThirdPartyPremium(0.0);//三者险保费
        }
        if(null == this.getThirdPartyAmount())
            this.setThirdPartyAmount(0.0);//三者险保额
        if(null == this.getThirdPartyIop())
            this.setThirdPartyIop(0.0);//三者不计免赔

        if(null == this.getDamagePremium()) {
            this.setDamagePremium(0.0);//车损险保费
        }
        if(null == this.getDamageAmount())
            this.setDamageAmount(0.0);//车损险保额
        if(null == this.getDamageIop())
            this.setDamageIop(0.0);//车损不计免赔

        if(null == this.getTheftPremium())
            this.setTheftPremium(0.0);//盗抢险保费
        if(null == this.getTheftAmount())
            this.setTheftAmount(0.0);//盗抢险保额
        if(null == this.getTheftIop())
            this.setTheftIop(0.0);//盗抢不计免赔

        if(null == this.getEnginePremium())
            this.setEnginePremium(0.0);//发动机特别险保费
        if(null == this.getEngineIop())
            this.setEngineIop(0.0);//发动机特别险不计免赔

        if(null == this.getDriverPremium())
            this.setDriverPremium(0.0);//车上人员（司机）保费
        if(null == this.getDriverAmount())
            this.setDriverAmount(0.0);//车上人员（司机）保额
        if(null == this.getDriverIop())
            this.setDriverIop(0.0);//车上人员（司机）不计免赔

        if(null == this.getPassengerPremium())
            this.setPassengerPremium(0.0);//车上人员（乘客）保费
        if(null == this.getPassengerAmount())
            this.setPassengerAmount(0.0);//车上人员（乘客）保额
        if(null == this.getPassengerIop())
            this.setPassengerIop(0.0);//车上人员（乘客）不计免赔

        if(null == this.getSpontaneousLossPremium())
            this.setSpontaneousLossPremium(0.0);//自燃损失险保费
        if(null == this.getSpontaneousLossAmount())
            this.setSpontaneousLossAmount(0.0);//自燃损失险保额

        if(null == this.getGlassPremium())
            this.setGlassPremium(0.0);//玻璃单独破碎险保费

        if(null == this.getScratchPremium())
            this.setScratchPremium(0.0);//划痕险保额
        if(null == this.getScratchAmount())
            this.setScratchAmount(0.0);//划痕险保费
        if(null == this.getScratchIop())
            this.setScratchIop(0.0);//划痕险不计免赔保费

        if(null == this.getPassengerCount()){
            this.setPassengerCount(0);//车上人员（乘客）数量
        }
    }
}
