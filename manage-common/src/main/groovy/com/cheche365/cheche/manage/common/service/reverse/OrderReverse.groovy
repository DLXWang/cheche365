package com.cheche365.cheche.manage.common.service.reverse

import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.manage.common.web.model.InsurancePurchaseOrderRebateViewModel
import com.fasterxml.jackson.annotation.JsonFormat
import org.apache.commons.lang3.StringUtils
import org.springframework.format.annotation.DateTimeFormat

import javax.validation.constraints.NotNull

/**
 * Created by yellow on 2017/11/30.
 */
class OrderReverse {
    final static Integer REVERSE_TYPE_USER = 1
    final static Integer REVERSE_TYPE_AGENT = 2

    private Long purchaseOrderId;
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
    private Long recommender;
    @NotNull
    private Long area;

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

    private Long institution;//出单机构
    private String institutionName;//出单机构名称
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

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date applicantDate;//投保日期
    private String confirmOrderDate;//确认出单日期
    private Boolean isNewCar;
    @NotNull
    private Long channel;//保单回录、支付有问题时，需要选择渠道
    private String compulsoryStampFile;//交强险标识
    private Double commercialDiscount;
    private Double compulsoryDiscount;
    private List<Map<String, String>> resendGiftList;
    private String mobile;
    private Integer orderType;
    private ReverseSource reverseSource;

    private InsurancePurchaseOrderRebateViewModel insurancePurchaseOrderRebateViewModel;

    private String agentIdentity
    private String agentName
    private Integer agentType
    private OrderSourceType orderSourceType
    private InternalUser operator
    private String comment
    private Integer institutionType
    private UserType userType

    final static enum ReverseSource {
        RESERVE_INPUT(1, "反向录入")
        , OFFLINE_IMPORT(2, "线下导入")
        , FAN_HUA_SYNC(3, "泛华同步")
        , AGENT_INPUT(4, "代理人")
        , USER_INPUT(5, "普通用户")
        , TOA_INPUT(6, "TOA渠道")

        private Integer id
        private String name

        ReverseSource(Integer id, String name) {
            this.id = id
            this.name = name
        }

        Integer getId() {
            return id
        }

        void setId(Integer id) {
            this.id = id
        }

        String getName() {
            return name
        }

        void setName(String name) {
            this.name = name
        }
    }


    String getLicensePlateNo() {
        return licensePlateNo
    }

    void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo
    }

    String getOwner() {
        return owner
    }

    void setOwner(String owner) {
        this.owner = owner
    }

    IdentityType getIdentityType() {
        return identityType
    }

    void setIdentityType(IdentityType identityType) {
        this.identityType = identityType
    }

    String getIdentity() {
        return StringUtils.upperCase(identity)
    }

    void setIdentity(String identity) {
        this.identity = identity
    }

    String getVinNo() {
        return vinNo
    }

    void setVinNo(String vinNo) {
        this.vinNo = vinNo
    }

    String getEngineNo() {
        return engineNo
    }

    void setEngineNo(String engineNo) {
        this.engineNo = engineNo
    }

    String getEnrollDate() {
        return enrollDate
    }

    void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate
    }

    String getBrand() {
        return brand
    }

    void setBrand(String brand) {
        this.brand = brand
    }

    String getInsuredIdNo() {
        return StringUtils.upperCase(insuredIdNo)
    }

    void setInsuredIdNo(String insuredIdNo) {
        this.insuredIdNo = insuredIdNo
    }

    IdentityType getInsuredIdType() {
        return insuredIdType
    }

    void setInsuredIdType(IdentityType insuredIdType) {
        this.insuredIdType = insuredIdType
    }

    String getInsuredName() {
        return insuredName
    }

    void setInsuredName(String insuredName) {
        this.insuredName = insuredName
    }

    Long getInsuranceCompany() {
        return insuranceCompany
    }

    void setInsuranceCompany(Long insuranceCompany) {
        this.insuranceCompany = insuranceCompany
    }

    Double getOriginalPremium() {
        return originalPremium
    }

    void setOriginalPremium(Double originalPremium) {
        this.originalPremium = originalPremium
    }

    Double getRebateExceptPremium() {
        return rebateExceptPremium
    }

    void setRebateExceptPremium(Double rebateExceptPremium) {
        this.rebateExceptPremium = rebateExceptPremium
    }

    String getTrackingNo() {
        return trackingNo
    }

    void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo
    }

    Long getRecommender() {
        return recommender
    }

    void setRecommender(Long recommender) {
        this.recommender = recommender
    }

    Long getArea() {
        return area
    }

    void setArea(Long area) {
        this.area = area
    }

    String getCommercialPolicyNo() {
        return commercialPolicyNo
    }

    void setCommercialPolicyNo(String commercialPolicyNo) {
        this.commercialPolicyNo = commercialPolicyNo
    }

    Double getCommercialPremium() {
        return commercialPremium
    }

    void setCommercialPremium(Double commercialPremium) {
        this.commercialPremium = commercialPremium
    }

    String getCommercialEffectiveDate() {
        return commercialEffectiveDate
    }

    void setCommercialEffectiveDate(String commercialEffectiveDate) {
        this.commercialEffectiveDate = commercialEffectiveDate
    }

    Integer getCommercialEffectiveHour() {
        return commercialEffectiveHour
    }

    void setCommercialEffectiveHour(Integer commercialEffectiveHour) {
        this.commercialEffectiveHour = commercialEffectiveHour
    }

    String getCommercialExpireDate() {
        return commercialExpireDate
    }

    void setCommercialExpireDate(String commercialExpireDate) {
        this.commercialExpireDate = commercialExpireDate
    }

    Integer getCommercialExpireHour() {
        return commercialExpireHour
    }

    void setCommercialExpireHour(Integer commercialExpireHour) {
        this.commercialExpireHour = commercialExpireHour
    }

    Double getDamageAmount() {
        return damageAmount
    }

    void setDamageAmount(Double damageAmount) {
        this.damageAmount = damageAmount
    }

    Double getDamagePremium() {
        return damagePremium
    }

    void setDamagePremium(Double damagePremium) {
        this.damagePremium = damagePremium
    }

    Double getThirdPartyAmount() {
        return thirdPartyAmount
    }

    void setThirdPartyAmount(Double thirdPartyAmount) {
        this.thirdPartyAmount = thirdPartyAmount
    }

    Double getThirdPartyPremium() {
        return thirdPartyPremium
    }

    void setThirdPartyPremium(Double thirdPartyPremium) {
        this.thirdPartyPremium = thirdPartyPremium
    }

    Double getDriverAmount() {
        return driverAmount
    }

    void setDriverAmount(Double driverAmount) {
        this.driverAmount = driverAmount
    }

    Double getDriverPremium() {
        return driverPremium
    }

    void setDriverPremium(Double driverPremium) {
        this.driverPremium = driverPremium
    }

    Double getPassengerAmount() {
        return passengerAmount
    }

    void setPassengerAmount(Double passengerAmount) {
        this.passengerAmount = passengerAmount
    }

    Double getPassengerPremium() {
        return passengerPremium
    }

    void setPassengerPremium(Double passengerPremium) {
        this.passengerPremium = passengerPremium
    }

    Double getPassengerCount() {
        return passengerCount
    }

    void setPassengerCount(Double passengerCount) {
        this.passengerCount = passengerCount
    }

    Double getTheftAmount() {
        return theftAmount
    }

    void setTheftAmount(Double theftAmount) {
        this.theftAmount = theftAmount
    }

    Double getTheftPremium() {
        return theftPremium
    }

    void setTheftPremium(Double theftPremium) {
        this.theftPremium = theftPremium
    }

    Double getScratchAmount() {
        return scratchAmount
    }

    void setScratchAmount(Double scratchAmount) {
        this.scratchAmount = scratchAmount
    }

    Double getScratchPremium() {
        return scratchPremium
    }

    void setScratchPremium(Double scratchPremium) {
        this.scratchPremium = scratchPremium
    }

    Double getSpontaneousLossAmount() {
        return spontaneousLossAmount
    }

    void setSpontaneousLossAmount(Double spontaneousLossAmount) {
        this.spontaneousLossAmount = spontaneousLossAmount
    }

    Double getSpontaneousLossPremium() {
        return spontaneousLossPremium
    }

    void setSpontaneousLossPremium(Double spontaneousLossPremium) {
        this.spontaneousLossPremium = spontaneousLossPremium
    }

    Long getGlassType() {
        return glassType
    }

    void setGlassType(Long glassType) {
        this.glassType = glassType
    }

    String getGlassTypeName() {
        return glassTypeName
    }

    void setGlassTypeName(String glassTypeName) {
        this.glassTypeName = glassTypeName
    }

    Double getGlassPremium() {
        return glassPremium
    }

    void setGlassPremium(Double glassPremium) {
        this.glassPremium = glassPremium
    }

    Double getEngineAmount() {
        return engineAmount
    }

    void setEngineAmount(Double engineAmount) {
        this.engineAmount = engineAmount
    }

    Double getUnableFindThirdPartyPremium() {
        return unableFindThirdPartyPremium
    }

    void setUnableFindThirdPartyPremium(Double unableFindThirdPartyPremium) {
        this.unableFindThirdPartyPremium = unableFindThirdPartyPremium
    }

    Double getDesignatedRepairShopPremium() {
        return designatedRepairShopPremium
    }

    void setDesignatedRepairShopPremium(Double designatedRepairShopPremium) {
        this.designatedRepairShopPremium = designatedRepairShopPremium
    }

    Double getEnginePremium() {
        return enginePremium
    }

    void setEnginePremium(Double enginePremium) {
        this.enginePremium = enginePremium
    }

    Double getDamageIop() {
        return damageIop
    }

    void setDamageIop(Double damageIop) {
        this.damageIop = damageIop
    }

    Double getThirdPartyIop() {
        return thirdPartyIop
    }

    void setThirdPartyIop(Double thirdPartyIop) {
        this.thirdPartyIop = thirdPartyIop
    }

    Double getTheftIop() {
        return theftIop
    }

    void setTheftIop(Double theftIop) {
        this.theftIop = theftIop
    }

    Double getSpontaneousLossIop() {
        return spontaneousLossIop
    }

    void setSpontaneousLossIop(Double spontaneousLossIop) {
        this.spontaneousLossIop = spontaneousLossIop
    }

    Double getEngineIop() {
        return engineIop
    }

    void setEngineIop(Double engineIop) {
        this.engineIop = engineIop
    }

    Double getDriverIop() {
        return driverIop
    }

    void setDriverIop(Double driverIop) {
        this.driverIop = driverIop
    }

    Double getPassengerIop() {
        return passengerIop
    }

    void setPassengerIop(Double passengerIop) {
        this.passengerIop = passengerIop
    }

    Double getScratchIop() {
        return scratchIop
    }

    void setScratchIop(Double scratchIop) {
        this.scratchIop = scratchIop
    }

    Double getIop() {
        return iop
    }

    void setIop(Double iop) {
        this.iop = iop
    }

    String getCompulsoryPolicyNo() {
        return compulsoryPolicyNo
    }

    void setCompulsoryPolicyNo(String compulsoryPolicyNo) {
        this.compulsoryPolicyNo = compulsoryPolicyNo
    }

    Double getCompulsoryPremium() {
        return compulsoryPremium
    }

    void setCompulsoryPremium(Double compulsoryPremium) {
        this.compulsoryPremium = compulsoryPremium
    }

    Double getAutoTax() {
        return autoTax
    }

    void setAutoTax(Double autoTax) {
        this.autoTax = autoTax
    }

    String getCompulsoryEffectiveDate() {
        return compulsoryEffectiveDate
    }

    void setCompulsoryEffectiveDate(String compulsoryEffectiveDate) {
        this.compulsoryEffectiveDate = compulsoryEffectiveDate
    }

    Integer getCompulsoryEffectiveHour() {
        return compulsoryEffectiveHour
    }

    void setCompulsoryEffectiveHour(Integer compulsoryEffectiveHour) {
        this.compulsoryEffectiveHour = compulsoryEffectiveHour
    }

    String getCompulsoryExpireDate() {
        return compulsoryExpireDate
    }

    void setCompulsoryExpireDate(String compulsoryExpireDate) {
        this.compulsoryExpireDate = compulsoryExpireDate
    }

    Integer getCompulsoryExpireHour() {
        return compulsoryExpireHour
    }

    void setCompulsoryExpireHour(Integer compulsoryExpireHour) {
        this.compulsoryExpireHour = compulsoryExpireHour
    }

    String getOrderNo() {
        return orderNo
    }

    void setOrderNo(String orderNo) {
        this.orderNo = orderNo
    }

    Long getInstitution() {
        return institution
    }

    void setInstitution(Long institution) {
        this.institution = institution
    }

    String getInsuranceImage() {
        return insuranceImage
    }

    void setInsuranceImage(String insuranceImage) {
        this.insuranceImage = insuranceImage
    }

    String getCompulsoryInsuranceImage() {
        return compulsoryInsuranceImage
    }

    void setCompulsoryInsuranceImage(String compulsoryInsuranceImage) {
        this.compulsoryInsuranceImage = compulsoryInsuranceImage
    }

    String getExpressCompany() {
        return expressCompany
    }

    void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany
    }

    String getQuoteCompulsoryPolicyNo() {
        return quoteCompulsoryPolicyNo
    }

    void setQuoteCompulsoryPolicyNo(String quoteCompulsoryPolicyNo) {
        this.quoteCompulsoryPolicyNo = quoteCompulsoryPolicyNo
    }

    String getQuoteCommercialPolicyNo() {
        return quoteCommercialPolicyNo
    }

    void setQuoteCommercialPolicyNo(String quoteCommercialPolicyNo) {
        this.quoteCommercialPolicyNo = quoteCommercialPolicyNo
    }

    Double getDiscount() {
        return discount
    }

    void setDiscount(Double discount) {
        this.discount = discount
    }

    Double getDiscountCI() {
        return discountCI
    }

    void setDiscountCI(Double discountCI) {
        this.discountCI = discountCI
    }

    String getApplicantName() {
        return applicantName
    }

    void setApplicantName(String applicantName) {
        this.applicantName = applicantName
    }

    IdentityType getApplicantIdType() {
        return applicantIdType
    }

    void setApplicantIdType(IdentityType applicantIdType) {
        this.applicantIdType = applicantIdType
    }

    String getApplicantIdNo() {
        return StringUtils.upperCase(applicantIdNo)
    }

    void setApplicantIdNo(String applicantIdNo) {
        this.applicantIdNo = applicantIdNo
    }

    Date getApplicantDate() {
        return applicantDate
    }

    String getConfirmOrderDate() {
        return confirmOrderDate
    }

    void setConfirmOrderDate(String confirmOrderDate) {
        this.confirmOrderDate = confirmOrderDate
    }

    Boolean getIsNewCar() {
        return isNewCar
    }

    void setIsNewCar(Boolean isNewCar) {
        this.isNewCar = isNewCar
    }

    Long getChannel() {
        return channel
    }

    void setChannel(Long channel) {
        this.channel = channel
    }

    String getCompulsoryStampFile() {
        return compulsoryStampFile
    }

    void setCompulsoryStampFile(String compulsoryStampFile) {
        this.compulsoryStampFile = compulsoryStampFile
    }

    Double getCommercialDiscount() {
        return commercialDiscount
    }

    void setCommercialDiscount(Double commercialDiscount) {
        this.commercialDiscount = commercialDiscount
    }

    Double getCompulsoryDiscount() {
        return compulsoryDiscount
    }

    void setCompulsoryDiscount(Double compulsoryDiscount) {
        this.compulsoryDiscount = compulsoryDiscount
    }

    List<Map<String, String>> getResendGiftList() {
        return resendGiftList
    }

    void setResendGiftList(List<Map<String, String>> resendGiftList) {
        this.resendGiftList = resendGiftList
    }

    String getMobile() {
        return mobile
    }

    void setMobile(String mobile) {
        this.mobile = mobile
    }

    Integer getOrderType() {
        return orderType
    }

    void setOrderType(Integer orderType) {
        this.orderType = orderType
    }

    ReverseSource getReverseSource() {
        if (reverseSource != null) {
            return reverseSource
        }
        if (Channel.rebateToWallets().contains(Channel.toChannel(this.getChannel()))) {
            return ReverseSource.TOA_INPUT
        }
        if (orderType == REVERSE_TYPE_USER) {
            return ReverseSource.USER_INPUT
        }
        if (orderType == REVERSE_TYPE_AGENT) {
            return ReverseSource.AGENT_INPUT
        }
        reverseSource

    }

    void setReverseSource(ReverseSource reverseSource) {
        this.reverseSource = reverseSource
    }

    InsurancePurchaseOrderRebateViewModel getInsurancePurchaseOrderRebateViewModel() {
        return insurancePurchaseOrderRebateViewModel
    }

    void setInsurancePurchaseOrderRebateViewModel(InsurancePurchaseOrderRebateViewModel insurancePurchaseOrderRebateViewModel) {
        this.insurancePurchaseOrderRebateViewModel = insurancePurchaseOrderRebateViewModel
    }

    Long getPurchaseOrderId() {
        return purchaseOrderId
    }

    void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId
    }

    String getAgentIdentity() {
        return StringUtils.upperCase(agentIdentity)
    }

    void setAgentIdentity(String agentIdentity) {
        this.agentIdentity = agentIdentity
    }

    String getAgentName() {
        return agentName
    }

    void setAgentName(String agentName) {
        this.agentName = agentName
    }

    Integer getAgentType() {
        return agentType
    }

    void setAgentType(Integer agentType) {
        this.agentType = agentType
    }

    OrderSourceType getOrderSourceType() {
        return orderSourceType
    }

    void setOrderSourceType(OrderSourceType orderSourceType) {
        this.orderSourceType = orderSourceType
    }

    InternalUser getOperator() {
        return operator
    }

    void setOperator(InternalUser operator) {
        this.operator = operator
    }

    String getComment() {
        return comment
    }

    void setComment(String comment) {
        this.comment = comment
    }

    String getInstitutionName() {
        return institutionName
    }

    void setInstitutionName(String institutionName) {
        this.institutionName = institutionName
    }

    Integer getInstitutionType() {
        return institutionType
    }

    void setInstitutionType(Integer institutionType) {
        this.institutionType = institutionType
    }

    UserType getUserType() {
        return userType
    }

    void setUserType(UserType userType) {
        this.userType = userType
    }
}
