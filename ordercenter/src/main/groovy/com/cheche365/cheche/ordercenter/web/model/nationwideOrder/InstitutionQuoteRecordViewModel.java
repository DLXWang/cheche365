package com.cheche365.cheche.ordercenter.web.model.nationwideOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.model.InstitutionQuoteRecord;
import com.cheche365.cheche.core.model.InsurancePackage;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.ordercenter.web.model.InsuranceCompanyData;

/**
 * Created by sunhuazhong on 2015/11/18.
 */
public class InstitutionQuoteRecordViewModel {
    private Long id;
    private Long purchaseOrderId;
    private String orderNo;//订单号
    private InsurancePackage insurancePackage;//套餐类型
    private InstitutionViewModel institution;//出单机构

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

    private String createTime;//创建时间
    private String updateTime;//修改时间
    private String operator;//操作人

    private Double premium;//保险总金额
    private Double iop;//不计免赔总金额
    private Double rebate;//出单机构佣金金额

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public InsurancePackage getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(InsurancePackage insurancePackage) {
        this.insurancePackage = insurancePackage;
    }

    public InstitutionViewModel getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionViewModel institution) {
        this.institution = institution;
    }

    public Double getCommercialRebate() {
        return commercialRebate;
    }

    public void setCommercialRebate(Double commercialRebate) {
        this.commercialRebate = commercialRebate;
    }

    public Double getCompulsoryRebate() {
        return compulsoryRebate;
    }

    public void setCompulsoryRebate(Double compulsoryRebate) {
        this.compulsoryRebate = compulsoryRebate;
    }

    public String getCommercialPolicyNo() {
        return commercialPolicyNo;
    }

    public void setCommercialPolicyNo(String commercialPolicyNo) {
        this.commercialPolicyNo = commercialPolicyNo;
    }

    public String getCompulsoryPolicyNo() {
        return compulsoryPolicyNo;
    }

    public void setCompulsoryPolicyNo(String compulsoryPolicyNo) {
        this.compulsoryPolicyNo = compulsoryPolicyNo;
    }

    public Double getCommercialPremium() {
        return commercialPremium;
    }

    public void setCommercialPremium(Double commercialPremium) {
        this.commercialPremium = commercialPremium;
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

    public Double getPremium() {
        return DoubleUtils.displayDoubleValue(
            DoubleUtils.doubleValue(this.commercialPremium)
                + DoubleUtils.doubleValue(this.compulsoryPremium)
                + DoubleUtils.doubleValue(this.autoTax));
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }

    public Double getIop() {
        return DoubleUtils.displayDoubleValue(
            DoubleUtils.doubleValue(this.getDamageIop())
                + DoubleUtils.doubleValue(this.getDriverIop())
                + DoubleUtils.doubleValue(this.getPassengerIop())
                + DoubleUtils.doubleValue(this.getThirdPartyIop())
                + DoubleUtils.doubleValue(this.getTheftIop())
                + DoubleUtils.doubleValue(this.getScratchIop())
                + DoubleUtils.doubleValue(this.getEngineIop())
        );
    }

    public void setIop(Double iop) {
        this.iop = iop;
    }

    public Double getRebate() {
        return DoubleUtils.displayDoubleValue(
            DoubleUtils.doubleValue(this.commercialPremium) * (DoubleUtils.doubleValue(this.commercialRebate) / 100)
                + DoubleUtils.doubleValue(this.compulsoryPremium) * (DoubleUtils.doubleValue(this.compulsoryRebate) / 100)
        );
    }

    public void setRebate(Double rebate) {
        this.rebate = rebate;
    }

    public static InstitutionQuoteRecordViewModel createViewModel(InstitutionQuoteRecord institutionQuoteRecord) {
        if (null == institutionQuoteRecord) {
            return null;
        }
        InstitutionQuoteRecordViewModel viewModel = new InstitutionQuoteRecordViewModel();
        String[] properties = new String[]{
            "id", "commercialRebate", "compulsoryRebate", "commercialPolicyNo", "compulsoryPolicyNo",
            "commercialPremium", "compulsoryPremium", "autoTax",
            "thirdPartyPremium", "thirdPartyAmount", "theftPremium", "theftAmount", "damagePremium", "damageAmount",
            "driverPremium", "driverAmount", "passengerPremium", "passengerAmount", "passengerCount", "enginePremium",
            "glassPremium", "scratchPremium", "scratchAmount", "spontaneousLossPremium", "spontaneousLossAmount"
        };
        BeanUtil.copyPropertiesContain(institutionQuoteRecord, viewModel, properties);

        viewModel.setPurchaseOrderId(institutionQuoteRecord.getPurchaseOrder().getId());
        viewModel.setOrderNo(institutionQuoteRecord.getPurchaseOrder().getOrderNo());

        viewModel.setInsurancePackage(institutionQuoteRecord.getInsurancePackage());
        viewModel.setInstitution(InstitutionViewModel.createViewModel(institutionQuoteRecord.getInstitution()));

        viewModel.setCreateTime(DateUtils.getDateString(institutionQuoteRecord.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(institutionQuoteRecord.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setOperator(institutionQuoteRecord.getOperator() == null ? "" : institutionQuoteRecord.getOperator().getName());
        viewModel.setDamageIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getDamageIop()));
        viewModel.setThirdPartyIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getThirdPartyIop()));
        viewModel.setTheftIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getTheftIop()));
        viewModel.setEngineIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getEngineIop()));
        viewModel.setDriverIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getDriverIop()));
        viewModel.setPassengerIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getPassengerIop()));
        viewModel.setScratchIop(DoubleUtils.positiveDouble(institutionQuoteRecord.getScratchIop()));
        return viewModel;
    }

}
