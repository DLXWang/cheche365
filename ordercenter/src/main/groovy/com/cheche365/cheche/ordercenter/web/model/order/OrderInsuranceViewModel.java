package com.cheche365.cheche.ordercenter.web.model.order;


import com.cheche365.cheche.core.model.GlassType;
import com.cheche365.cheche.core.model.IdentityType;
import com.cheche365.cheche.core.model.InsurancePackage;
import com.cheche365.cheche.manage.common.web.model.InsurancePurchaseOrderRebateViewModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Created by wangfei on 2015/8/18.
 */
public class OrderInsuranceViewModel {
    public static String[] copyCommercial = {
        "commercialPolicyNo",
        "commercialPremium",
        "commercialEffectiveDate",
        "commercialEffectiveHour",
        "commercialExpireDate",
        "commercialExpireHour",
        "damageAmount",
        "damagePremium",
        "thirdPartyAmount",
        "thirdPartyPremium",
        "driverAmount",
        "driverPremium",
        "passengerAmount",
        "passengerPremium",
        "passengerCount",
        "theftAmount",
        "theftPremium",
        "scratchAmount",
        "scratchPremium",
        "spontaneousLossAmount",
        "spontaneousLossPremium",
        "glassType",
        "glassTypeName",
        "glassPremium",
        "engineAmount",
        "unableFindThirdPartyPremium",
        "designatedRepairShopPremium",
        "enginePremium",
        "damageIop",
        "thirdPartyIop",
        "theftIop",
        "spontaneousLossIop",
        "engineIop",
        "driverIop",
        "passengerIop",
        "scratchIop",
        "iop"
    };
    public static String[] copyCompulsory = {
        "compulsoryPolicyNo",
        "compulsoryPremium",
        "autoTax",
        "compulsoryEffectiveDate",
        "compulsoryEffectiveHour",
        "compulsoryExpireDate",
        "compulsoryExpireHour"
    };
    private Long userId;
    private Long autoId;
    @NotNull
    private String licensePlateNo;
    @NotNull
    private String owner;
    @NotNull
    private IdentityType identityType;
    @NotNull
    private String identity;
    @NotNull
    private String vinNo;
    @NotNull
    private String engineNo;
    @NotNull
    private String enrollDate;
    @NotNull
    private String brand;
    @NotNull
    private String insuredIdNo;
    @NotNull
    private IdentityType insuredIdType;
    @NotNull
    private String insuredName;
    @NotNull
    private Long insuranceCompany;
    @NotNull
    private Double originalPremium;
    @NotNull
    private Double rebateExceptPremium;
    private String trackingNo;
    @NotNull
    private Long recommender;
    @NotNull
    private Long area;
    private String areaName;
    private String recommenderName;

    private String commercialPolicyNo;
    private Double commercialPremium;
    private String commercialEffectiveDate;
    private Integer commercialEffectiveHour;
    private String commercialExpireDate;
    private Integer commercialExpireHour;
    private Double damageAmount;
    private Double damagePremium;
    private Double thirdPartyAmount;
    private Double thirdPartyPremium;
    private Double driverAmount;
    private Double driverPremium;
    private Double passengerAmount;
    private Double passengerPremium;
    private Double passengerCount;//乘客人数
    private Double theftAmount;
    private Double theftPremium;
    private Double scratchAmount;
    private Double scratchPremium;
    private Double spontaneousLossAmount;
    private Double spontaneousLossPremium;
    private Long glassType;
    private String glassTypeName;//玻璃类型名称
    private Double glassPremium;
    private Double engineAmount;//发动机特别险保额
    private Double unableFindThirdPartyPremium;//机动车损失保险无法找到第三方特约险
    private Double designatedRepairShopPremium;//指定专修厂险
    private Double enginePremium;
    private Double damageIop = 0.0;
    private Double thirdPartyIop;
    private Double theftIop;
    private Double spontaneousLossIop;//自燃险不计免赔
    private Double engineIop;
    private Double driverIop;
    private Double passengerIop;
    private Double scratchIop;
    private Double iop;

    private String compulsoryPolicyNo;
    private Double compulsoryPremium;
    private Double autoTax;
    private String compulsoryEffectiveDate;
    private Integer compulsoryEffectiveHour;
    private String compulsoryExpireDate;
    private Integer compulsoryExpireHour;

    private String orderNo;
    private String insuranceInputter;
    private String insuranceOperator;
    private Long purchaseOrderId;
    private String createTime;
    private String updateTime;

    private Long institution;//出单机构
    private String insuranceImage;//商业险保单扫描文件地址
    private String compulsoryInsuranceImage;//交强险保单扫描文件地址
    private String expressCompany;//快递公司
    private String quoteCompulsoryPolicyNo;//报价交强险保单号
    private String quoteCommercialPolicyNo;//报价商业险保单号

    private Double discount;//商业险浮动系数
    private Double discountCI;//交强险浮动系数

    private String applicantName;//投保人姓名
    private IdentityType applicantIdType;//投保人证件类型
    private String applicantIdNo;//投保人证件号

    private String applicantDate;//投保日期
    private String confirmOrderDate;//确认出单日期
    private Boolean isNewCar;
    private String source;//保单回录：answern
    @NotNull
    private Long channel;//保单回录、支付有问题时，需要选择渠道
    private String compulsoryStampFile;//交强险标识
    private Double commercialDiscount;
    private Double compulsoryDiscount;
    private List<Map<String, String>> resendGiftList;
    private String mobile;
    private Integer orderType;
    private String giftInfo;
    private Integer channelType;

    private InsurancePurchaseOrderRebateViewModel insurancePurchaseOrderRebateViewModel;

    public Long getChannel() {
        return channel;
    }

    public void setChannel(Long channel) {
        this.channel = channel;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Double getSpontaneousLossIop() {
        return spontaneousLossIop;
    }

    public void setSpontaneousLossIop(Double spontaneousLossIop) {
        this.spontaneousLossIop = spontaneousLossIop;
    }

    public Double getUnableFindThirdPartyPremium() {
        return unableFindThirdPartyPremium;
    }

    public void setUnableFindThirdPartyPremium(Double unableFindThirdPartyPremium) {
        this.unableFindThirdPartyPremium = unableFindThirdPartyPremium;
    }

    public Double getDesignatedRepairShopPremium() { return designatedRepairShopPremium; }

    public void setDesignatedRepairShopPremium(Double designatedRepairShopPremium) {
        this.designatedRepairShopPremium = designatedRepairShopPremium;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public IdentityType getIdentityType() {
        return identityType;
    }

    public void setIdentityType(IdentityType identityType) {
        this.identityType = identityType;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getVinNo() {
        return vinNo;
    }

    public void setVinNo(String vinNo) {
        this.vinNo = vinNo;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getInsuredIdNo() {
        return insuredIdNo;
    }

    public void setInsuredIdNo(String insuredIdNo) {
        this.insuredIdNo = insuredIdNo;
    }

    public IdentityType getInsuredIdType() {
        return insuredIdType;
    }

    public void setInsuredIdType(IdentityType insuredIdType) {
        this.insuredIdType = insuredIdType;
    }

    public String getInsuredName() {
        return insuredName;
    }

    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    public Long getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(Long insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public Double getOriginalPremium() {
        return originalPremium;
    }

    public void setOriginalPremium(Double originalPremium) {
        this.originalPremium = originalPremium;
    }

    public Double getRebateExceptPremium() {
        return rebateExceptPremium;
    }

    public void setRebateExceptPremium(Double rebateExceptPremium) {
        this.rebateExceptPremium = rebateExceptPremium;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public Long getRecommender() {
        return recommender;
    }

    public void setRecommender(Long recommender) {
        this.recommender = recommender;
    }

    public String getCommercialPolicyNo() {
        return commercialPolicyNo;
    }

    public void setCommercialPolicyNo(String commercialPolicyNo) {
        this.commercialPolicyNo = commercialPolicyNo;
    }

    public Double getCommercialPremium() {
        return commercialPremium;
    }

    public void setCommercialPremium(Double commercialPremium) {
        this.commercialPremium = commercialPremium;
    }

    public String getCommercialEffectiveDate() {
        return commercialEffectiveDate;
    }

    public void setCommercialEffectiveDate(String commercialEffectiveDate) {
        this.commercialEffectiveDate = commercialEffectiveDate;
    }

    public Integer getCommercialEffectiveHour() {
        return commercialEffectiveHour;
    }

    public void setCommercialEffectiveHour(Integer commercialEffectiveHour) {
        this.commercialEffectiveHour = commercialEffectiveHour;
    }

    public String getCommercialExpireDate() {
        return commercialExpireDate;
    }

    public void setCommercialExpireDate(String commercialExpireDate) {
        this.commercialExpireDate = commercialExpireDate;
    }

    public Integer getCommercialExpireHour() {
        return commercialExpireHour;
    }

    public void setCommercialExpireHour(Integer commercialExpireHour) {
        this.commercialExpireHour = commercialExpireHour;
    }

    public Double getDamageAmount() {
        return damageAmount;
    }

    public void setDamageAmount(Double damageAmount) {
        this.damageAmount = damageAmount;
    }

    public Double getDamagePremium() {
        return damagePremium;
    }

    public void setDamagePremium(Double damagePremium) {
        this.damagePremium = damagePremium;
    }

    public Double getThirdPartyAmount() {
        return thirdPartyAmount;
    }

    public void setThirdPartyAmount(Double thirdPartyAmount) {
        this.thirdPartyAmount = thirdPartyAmount;
    }

    public Double getThirdPartyPremium() {
        return thirdPartyPremium;
    }

    public void setThirdPartyPremium(Double thirdPartyPremium) {
        this.thirdPartyPremium = thirdPartyPremium;
    }

    public Double getDriverAmount() {
        return driverAmount;
    }

    public void setDriverAmount(Double driverAmount) {
        this.driverAmount = driverAmount;
    }

    public Double getDriverPremium() {
        return driverPremium;
    }

    public void setDriverPremium(Double driverPremium) {
        this.driverPremium = driverPremium;
    }

    public Double getPassengerAmount() {
        return passengerAmount;
    }

    public void setPassengerAmount(Double passengerAmount) {
        this.passengerAmount = passengerAmount;
    }

    public Double getPassengerPremium() {
        return passengerPremium;
    }

    public void setPassengerPremium(Double passengerPremium) {
        this.passengerPremium = passengerPremium;
    }

    public Double getTheftAmount() {
        return theftAmount;
    }

    public void setTheftAmount(Double theftAmount) {
        this.theftAmount = theftAmount;
    }

    public Double getTheftPremium() {
        return theftPremium;
    }

    public void setTheftPremium(Double theftPremium) {
        this.theftPremium = theftPremium;
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

    public Double getSpontaneousLossAmount() {
        return spontaneousLossAmount;
    }

    public void setSpontaneousLossAmount(Double spontaneousLossAmount) {
        this.spontaneousLossAmount = spontaneousLossAmount;
    }

    public Double getSpontaneousLossPremium() {
        return spontaneousLossPremium;
    }

    public void setSpontaneousLossPremium(Double spontaneousLossPremium) {
        this.spontaneousLossPremium = spontaneousLossPremium;
    }

    public Long getGlassType() {
        return glassType;
    }

    public void setGlassType(Long glassType) {
        this.glassType = glassType;
    }

    public String getGlassTypeName() {
        return glassTypeName;
    }

    public void setGlassTypeName(String glassTypeName) {
        this.glassTypeName = glassTypeName;
    }

    public Double getGlassPremium() {
        return glassPremium;
    }

    public void setGlassPremium(Double glassPremium) {
        this.glassPremium = glassPremium;
    }

    public Double getEnginePremium() {
        return enginePremium;
    }

    public void setEnginePremium(Double enginePremium) {
        this.enginePremium = enginePremium;
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

    public Double getIop() {
        return iop;
    }

    public void setIop(Double iop) {
        this.iop = iop;
    }

    public String getCompulsoryPolicyNo() {
        return compulsoryPolicyNo;
    }

    public void setCompulsoryPolicyNo(String compulsoryPolicyNo) {
        this.compulsoryPolicyNo = compulsoryPolicyNo;
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

    public String getCompulsoryEffectiveDate() {
        return compulsoryEffectiveDate;
    }

    public void setCompulsoryEffectiveDate(String compulsoryEffectiveDate) {
        this.compulsoryEffectiveDate = compulsoryEffectiveDate;
    }

    public Integer getCompulsoryEffectiveHour() {
        return compulsoryEffectiveHour;
    }

    public void setCompulsoryEffectiveHour(Integer compulsoryEffectiveHour) {
        this.compulsoryEffectiveHour = compulsoryEffectiveHour;
    }

    public String getCompulsoryExpireDate() {
        return compulsoryExpireDate;
    }

    public void setCompulsoryExpireDate(String compulsoryExpireDate) {
        this.compulsoryExpireDate = compulsoryExpireDate;
    }

    public Integer getCompulsoryExpireHour() {
        return compulsoryExpireHour;
    }

    public void setCompulsoryExpireHour(Integer compulsoryExpireHour) {
        this.compulsoryExpireHour = compulsoryExpireHour;
    }

    public Long getArea() {
        return area;
    }

    public void setArea(Long area) {
        this.area = area;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getInsuranceInputter() {
        return insuranceInputter;
    }

    public void setInsuranceInputter(String insuranceInputter) {
        this.insuranceInputter = insuranceInputter;
    }

    public String getInsuranceOperator() {
        return insuranceOperator;
    }

    public void setInsuranceOperator(String insuranceOperator) {
        this.insuranceOperator = insuranceOperator;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
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

    public Long getInstitution() {
        return institution;
    }

    public void setInstitution(Long institution) {
        this.institution = institution;
    }

    public String getInsuranceImage() {
        return insuranceImage;
    }

    public void setInsuranceImage(String insuranceImage) {
        this.insuranceImage = insuranceImage;
    }

    public String getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }

    public String getConfirmOrderDate() {
        return confirmOrderDate;
    }

    public void setConfirmOrderDate(String confirmOrderDate) {
        this.confirmOrderDate = confirmOrderDate;
    }

    public String getCompulsoryStampFile() {
        return compulsoryStampFile;
    }

    public void setCompulsoryStampFile(String compulsoryStampFile) {
        this.compulsoryStampFile = compulsoryStampFile;
    }

    public Double getCommercialDiscount() {
        return commercialDiscount;
    }

    public void setCommercialDiscount(Double commercialDiscount) {
        this.commercialDiscount = commercialDiscount;
    }

    public Double getCompulsoryDiscount() {
        return compulsoryDiscount;
    }

    public void setCompulsoryDiscount(Double compulsoryDiscount) {
        this.compulsoryDiscount = compulsoryDiscount;
    }

    public List<Map<String, String>> getResendGiftList() {
        return resendGiftList;
    }

    public void setResendGiftList(List<Map<String, String>> resendGiftList) {
        this.resendGiftList = resendGiftList;
    }

    public InsurancePackage getInsurancePackage() {
        InsurancePackage insurancePackage = new InsurancePackage();
        insurancePackage.setCompulsory(this.compulsoryPremium != null && this.compulsoryPremium != 0);
        insurancePackage.setAutoTax(this.autoTax != null && this.autoTax != 0);
        insurancePackage.setThirdPartyAmount(this.thirdPartyAmount);
        insurancePackage.setThirdPartyIop(this.thirdPartyIop != null && this.thirdPartyIop != 0);
        insurancePackage.setDamage(this.damageAmount != null && this.damageAmount != 0);
        insurancePackage.setDamageIop(this.damageIop != null && this.damageIop != 0);
        insurancePackage.setTheft(this.theftAmount != null && this.theftAmount != 0);
        insurancePackage.setTheftIop(this.theftIop != null && this.theftIop != 0);
        insurancePackage.setEngine(this.enginePremium != null && this.enginePremium != 0);
        insurancePackage.setEngineIop(this.engineIop != null && this.engineIop != 0);
        insurancePackage.setGlass(this.glassPremium != null && this.glassPremium != 0);
        insurancePackage.setGlassType(this.glassType == null ? null : GlassType.Enum.findById(this.glassType));
        insurancePackage.setDriverAmount(this.driverAmount);
        insurancePackage.setDriverIop(this.driverIop != null && this.driverIop != 0);
        insurancePackage.setPassengerAmount(this.passengerAmount);
        insurancePackage.setPassengerIop(this.passengerIop != null && this.passengerIop != 0);
        insurancePackage.setSpontaneousLoss(this.spontaneousLossAmount != null && this.spontaneousLossAmount != 0);
        insurancePackage.setScratchAmount(this.scratchAmount);
        insurancePackage.setScratchIop(this.scratchIop != null && this.scratchIop != 0);
        return insurancePackage;
    }

    public String getQuoteCompulsoryPolicyNo() {
        return quoteCompulsoryPolicyNo;
    }

    public void setQuoteCompulsoryPolicyNo(String quoteCompulsoryPolicyNo) {
        this.quoteCompulsoryPolicyNo = quoteCompulsoryPolicyNo;
    }

    public String getQuoteCommercialPolicyNo() {
        return quoteCommercialPolicyNo;
    }

    public void setQuoteCommercialPolicyNo(String quoteCommercialPolicyNo) {
        this.quoteCommercialPolicyNo = quoteCommercialPolicyNo;
    }

    public String getCompulsoryInsuranceImage() {
        return compulsoryInsuranceImage;
    }

    public void setCompulsoryInsuranceImage(String compulsoryInsuranceImage) {
        this.compulsoryInsuranceImage = compulsoryInsuranceImage;
    }

    public Double getEngineAmount() {
        return engineAmount;
    }

    public void setEngineAmount(Double engineAmount) {
        this.engineAmount = engineAmount;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getDiscountCI() {
        return discountCI;
    }

    public void setDiscountCI(Double discountCI) {
        this.discountCI = discountCI;
    }

    public Double getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Double passengerCount) {
        this.passengerCount = passengerCount;
    }

    public String getRecommenderName() {
        return recommenderName;
    }

    public void setRecommenderName(String recommenderName) {
        this.recommenderName = recommenderName;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public IdentityType getApplicantIdType() {
        return applicantIdType;
    }

    public void setApplicantIdType(IdentityType applicantIdType) {
        this.applicantIdType = applicantIdType;
    }

    public String getApplicantIdNo() {
        return applicantIdNo;
    }

    public void setApplicantIdNo(String applicantIdNo) {
        this.applicantIdNo = applicantIdNo;
    }

    public String getApplicantDate() {
        return applicantDate;
    }

    public void setApplicantDate(String applicantDate) {
        this.applicantDate = applicantDate;
    }

    public InsurancePurchaseOrderRebateViewModel getInsurancePurchaseOrderRebateViewModel() {
        return insurancePurchaseOrderRebateViewModel;
    }

    public void setInsurancePurchaseOrderRebateViewModel(InsurancePurchaseOrderRebateViewModel insurancePurchaseOrderRebateViewModel) {
        this.insurancePurchaseOrderRebateViewModel = insurancePurchaseOrderRebateViewModel;
    }

    public Boolean getNewCar() {
        return isNewCar;
    }

    public void setNewCar(Boolean newCar) {
        isNewCar = newCar;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getGiftInfo() {
        return giftInfo;
    }

    public void setGiftInfo(String giftInfo) {
        this.giftInfo = giftInfo;
    }

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }
}
