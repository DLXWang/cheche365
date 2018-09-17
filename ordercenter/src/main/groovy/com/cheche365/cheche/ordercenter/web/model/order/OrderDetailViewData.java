package com.cheche365.cheche.ordercenter.web.model.order;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 订单详情实体
 * Created by wangfei on 2015/5/16.
 */
public class OrderDetailViewData {
    private String licenseNo;//车牌号
    private String vinNo;//车架号
    private String engineNo;//发动机号
    private String enrollDate;//初登日期
    private String userMobile;//用户手机号
    private String nickName;//微信昵称
    private String modelName;//车型
    private String insuranceCompany;//保险公司名称
    private String orderNo;//订单号
    private String commercialPolicyNo;//商业险保单号
    private String commercialPolicyEffectiveDate;//商业险生效日期
    private String commercialPolicyExpireDate;//商业险失效日期
    private String compulsoryPolicyNo;//交强险保单号
    private String compulsoryPolicyEffectiveDate;//交强险生效日期
    private String compulsoryPolicyExpireDate;//交强险失效日期
    private String orderStatus;//订单状态
    private Double payableAmount;//应付金额
    private Double paidAmount;//已付金额
    private Double compulsoryPremium;//交强险保费
    private Double autoTax;//车船税
    private Double commercialPremium;//商业险保费
    private Double thirdPartyPremium;//三者险保费
    private Double thirdPartyAmount;//三者险保额
    private Double scratchPremium;//划痕险保费
    private Double scratchAmount;//划痕险保额
    private Double damagePremium;//车损险保费
    private Double damageAmount;//车损险保额
    private Double driverPremium;//车上人员(司机)保费
    private Double driverAmount;//车上人员(司机)保额
    private Double passengerPremium;//车上人员(乘客)保费
    private Double passengerAmount;//车上人员(乘客)保额
    private Double theftPremium;//盗抢险保费
    private Double theftAmount;//盗抢险保额
    private Double spontaneousLossPremium;//自燃险保费
    private Double spontaneousLossAmount;//自燃险保额
    private Double enginePremium;//发动机特别损失险保费
    private Double glassPremium;//玻璃单独破碎险保费
    private String glassType;//玻璃类型
    private Double unableFindThirdPartyPremium;//机动车损失保险无法找到第三方特约险保费
    private Double iop;//不计免赔保费
    private Double designatedRepairShopPremium;//指定专修厂险
    private String ownerName;//车主姓名
    private String ownerIdentityType;//车主证件类型
    private String ownerIdentity;//车主证件号码
    private String ownerMobile;//车主手机号
    private String address;//送单地址
    private String paymentChannel;//支付方式
    private String payStatus;//支付状态
    private String receiver;//收件人
    private String receiverMobile;//收件人手机号
    private String sendDate;//配送日期
    private String timePeriod;//配送时间段
    private String giftDetails;//礼品详情
    private String insuredName;//被保险人姓名
    private String insuredIdentityType;//被保险人证件类型
    private Long insuredIdentityTypeId;//被保险人证件类型id
    private String insuredIdentity;//被保险人证件号
    private String source;//来源
    private Long sourceId;
    private String platform;//平台
    private String expressCompany;//快递公司
    private String trackingNo;
    private String insuranceImage;//商业险保单扫描文件地址
    private String compulsoryInsuranceImage;//交强险保单扫描文件地址
    private String compulsoryStamp;//交強标识地址
    private String applicantName;//投保人姓名	String	否	投保人姓名（汉字）
    private String applicantIdentityType;//投保人证件类型
    private Long applicantIdentityTypeId;//投保人证件类型id
    private String applicantIdNo;//投保人证件号码	String	否	身份证号
    private List<Map> supplementInfos;//车辆补充信息
    private String seats;//车座数
    private String code;//品牌型号
    private List<RebateModel> rebates;//费率佣金信息
    private Long areaId;
    private Long currentStatus;//出单状态

    private String orderImageStatus;//照片审核状态
    private Address addressInfo;

    private Boolean dailyInsurance;// 是否在订单详情页显示车辆停复驶记录按钮

    private Long insuranceCompanyId;
    private String specialRemarks;//针对特殊情况的特殊标记
    private String statusDisplay;//状态展示


    private String agentName;
    private Double commercialRebate=0.0;//商业险费率
    private Double compulsoryRebate=0.0;//交强险费率
    private String cardNum;//銀行卡号

    private String quoteType;
    private boolean supportAmend;
    private Long quoteSourceId;
    private String mongoSeats;
    private String mongoCompulsoryEndDate;
    private String mongoCommercialEndDate;

    private String inviter; //邀请人
    private String indirectInviter; //间接邀请人

    private Double inviterAward; //直接邀请人奖励
    private Double indirectInviterAward; //间接邀请人奖励
    private boolean supportChangeStatus; //是否支持 <修改订单>

    public boolean isSupportChangeStatus() {
        return supportChangeStatus;
    }
    public void setSupportChangeStatus(boolean supportChangeStatus) {
        this.supportChangeStatus = supportChangeStatus;
    }

    public Double getInviterAward() { return inviterAward; }

    public void setInviterAward(Double inviterAward) {  this.inviterAward = inviterAward; }

    public Double getIndirectInviterAward() { return indirectInviterAward; }

    public void setIndirectInviterAward(Double indirectInviterAward) { this.indirectInviterAward = indirectInviterAward; }

    public String getInviter() { return inviter; }

    public void setInviter(String inviter) { this.inviter = inviter; }

    public String getIndirectInviter() { return indirectInviter; }

    public void setIndirectInviter(String indirectInviter) { this.indirectInviter = indirectInviter; }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
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

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public Long getInsuranceCompanyId() {
        return insuranceCompanyId;
    }

    public void setInsuranceCompanyId(Long insuranceCompanyId) {
        this.insuranceCompanyId = insuranceCompanyId;
    }

    public Address getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(Address addressInfo) {
        this.addressInfo = addressInfo;
    }

    private List<PaymentInfoViewModel> paymentInfos;

    private boolean isThirdPart;

    public List<PaymentInfoViewModel> getPaymentInfos() {
        return paymentInfos;
    }

    public void setCurrentStatus(Long currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Long getCurrentStatus() {
        return currentStatus;
    }

    public void setPaymentInfos(List<PaymentInfoViewModel> paymentInfos) {
        this.paymentInfos = paymentInfos;
    }

    public String getOrderImageStatus() {
        return orderImageStatus;
    }

    public void setOrderImageStatus(String orderImageStatus) {
        this.orderImageStatus = orderImageStatus;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
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

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCommercialPolicyNo() {
        return commercialPolicyNo;
    }

    public void setCommercialPolicyNo(String commercialPolicyNo) {
        this.commercialPolicyNo = commercialPolicyNo;
    }

    public String getCommercialPolicyEffectiveDate() {
        return commercialPolicyEffectiveDate;
    }

    public void setCommercialPolicyEffectiveDate(String commercialPolicyEffectiveDate) {
        this.commercialPolicyEffectiveDate = commercialPolicyEffectiveDate;
    }

    public String getCommercialPolicyExpireDate() {
        return commercialPolicyExpireDate;
    }

    public void setCommercialPolicyExpireDate(String commercialPolicyExpireDate) {
        this.commercialPolicyExpireDate = commercialPolicyExpireDate;
    }

    public String getCompulsoryPolicyNo() {
        return compulsoryPolicyNo;
    }

    public void setCompulsoryPolicyNo(String compulsoryPolicyNo) {
        this.compulsoryPolicyNo = compulsoryPolicyNo;
    }

    public String getCompulsoryPolicyEffectiveDate() {
        return compulsoryPolicyEffectiveDate;
    }

    public void setCompulsoryPolicyEffectiveDate(String compulsoryPolicyEffectiveDate) {
        this.compulsoryPolicyEffectiveDate = compulsoryPolicyEffectiveDate;
    }

    public String getCompulsoryPolicyExpireDate() {
        return compulsoryPolicyExpireDate;
    }

    public void setCompulsoryPolicyExpireDate(String compulsoryPolicyExpireDate) {
        this.compulsoryPolicyExpireDate = compulsoryPolicyExpireDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
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

    public Double getCommercialPremium() {
        return commercialPremium;
    }

    public void setCommercialPremium(Double commercialPremium) {
        this.commercialPremium = commercialPremium;
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

    public Double getIop() {
        return iop;
    }

    public void setIop(Double iop) {
        this.iop = iop;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerIdentityType() {
        return ownerIdentityType;
    }

    public void setOwnerIdentityType(String ownerIdentityType) {
        this.ownerIdentityType = ownerIdentityType;
    }

    public String getOwnerIdentity() {
        return ownerIdentity;
    }

    public void setOwnerIdentity(String ownerIdentity) {
        this.ownerIdentity = ownerIdentity;
    }

    public String getOwnerMobile() {
        return ownerMobile;
    }

    public void setOwnerMobile(String ownerMobile) {
        this.ownerMobile = ownerMobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getGiftDetails() {
        return giftDetails;
    }

    public void setGiftDetails(String giftDetails) {
        this.giftDetails = giftDetails;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getGlassType() {
        return glassType;
    }

    public void setGlassType(String glassType) {
        this.glassType = glassType;
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

    public String getInsuredName() {
        return insuredName;
    }

    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    public String getInsuredIdentityType() {
        return insuredIdentityType;
    }

    public void setInsuredIdentityType(String insuredIdentityType) {
        this.insuredIdentityType = insuredIdentityType;
    }

    public String getInsuredIdentity() {
        return insuredIdentity;
    }

    public void setInsuredIdentity(String insuredIdentity) {
        this.insuredIdentity = insuredIdentity;
    }

    public Long getInsuredIdentityTypeId() {
        return insuredIdentityTypeId;
    }

    public void setInsuredIdentityTypeId(Long insuredIdentityTypeId) {
        this.insuredIdentityTypeId = insuredIdentityTypeId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
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

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantIdentityType() {
        return applicantIdentityType;
    }

    public void setApplicantIdentityType(String applicantIdentityType) {
        this.applicantIdentityType = applicantIdentityType;
    }

    public Long getApplicantIdentityTypeId() {
        return applicantIdentityTypeId;
    }

    public void setApplicantIdentityTypeId(Long applicantIdentityTypeId) {
        this.applicantIdentityTypeId = applicantIdentityTypeId;
    }

    public String getApplicantIdNo() {
        return applicantIdNo;
    }

    public void setApplicantIdNo(String applicantIdNo) {
        this.applicantIdNo = applicantIdNo;
    }

    public List<Map> getSupplementInfos() {
        return supplementInfos;
    }

    public void setSupplementInfos(List<Map> supplementInfos) {
        this.supplementInfos = supplementInfos;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public List<RebateModel> getRebates() {
        return rebates;
    }

    public void setRebates(List<RebateModel> rebates) {
        this.rebates = rebates;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public boolean isThirdPart() {
        return isThirdPart;
    }

    public void setThirdPart(boolean thirdPart) {
        isThirdPart = thirdPart;
    }

    public Boolean getDailyInsurance() {
        return dailyInsurance;
    }

    public void setDailyInsurance(Boolean dailyInsurance) {
        this.dailyInsurance = dailyInsurance;
    }

    public String getSpecialRemarks() {
        return specialRemarks;
    }

    public void setSpecialRemarks(String specialRemarks) {
        this.specialRemarks = specialRemarks;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public boolean isSupportAmend() {
        return supportAmend;
    }

    public void setSupportAmend(boolean supportAmend) {
        this.supportAmend = supportAmend;
    }

    public String getCompulsoryStamp() {
        return compulsoryStamp;
    }

    public void setCompulsoryStamp(String compulsoryStamp) {
        this.compulsoryStamp = compulsoryStamp;
    }


    public Long getQuoteSourceId() {
        return quoteSourceId;
    }

    public void setQuoteSourceId(Long quoteSourceId) {
        this.quoteSourceId = quoteSourceId;
    }

    public String getMongoSeats() {
        return mongoSeats;
    }

    public void setMongoSeats(String mongoSeats) {
        this.mongoSeats = mongoSeats;
    }

    public String getMongoCompulsoryEndDate() {
        return mongoCompulsoryEndDate;
    }

    public void setMongoCompulsoryEndDate(String mongoCompulsoryEndDate) {
        this.mongoCompulsoryEndDate = mongoCompulsoryEndDate;
    }

    public String getMongoCommercialEndDate() {
        return mongoCommercialEndDate;
    }

    public void setMongoCommercialEndDate(String mongoCommercialEndDate) {
        this.mongoCommercialEndDate = mongoCommercialEndDate;
    }

    public static List<RebateModel> organizeRebateList(InsurancePurchaseOrderRebate rebate, Agent agent, Institution institution) {
        List<RebateModel> rebateList = new ArrayList<RebateModel>();
        //上游
        if (null != rebate.getUpRebateChannel()) {
            RebateModel rebateModel = new RebateModel(rebate.getUpRebateChannel().getName(), null != agent ? agent.getName() : "",
                rebate.getUpCommercialAmount(), rebate.getUpCompulsoryAmount(), rebate.getUpCommercialRebate(), rebate.getUpCompulsoryRebate(), "up");
            rebateList.add(rebateModel);
        }
        //下游
        if (null != rebate.getDownRebateChannel()) {
            RebateModel rebateModel = new RebateModel(rebate.getDownRebateChannel().getName(), null != institution ? institution.getName() : "",
                rebate.getDownCommercialAmount(), rebate.getDownCompulsoryAmount(), rebate.getDownCommercialRebate(), rebate.getDownCompulsoryRebate(), "down");
            rebateList.add(rebateModel);
        }
        return rebateList;
    }

    static class RebateModel {
        private String categoryName;//类别
        private String name;//名称
        private Double commercialAmount;//商业险佣金
        private Double compulsoryAmount;//交强险佣金
        private Double commercialRebate;//商业险费率
        private Double compulsoryRebate;//交强险费率
        private Double sumAmount;//佣金合计
        private String type;//类型 up or down

        public RebateModel() {
        }

        public RebateModel(String categoryName, String name, Double commercialAmount, Double compulsoryAmount,
                           Double commercialRebate, Double compulsoryRebate, String type) {
            this.categoryName = categoryName;
            this.name = name;
            this.commercialAmount = commercialAmount;
            this.compulsoryAmount = compulsoryAmount;
            this.commercialRebate = commercialRebate;
            this.compulsoryRebate = compulsoryRebate;
            this.type = type;
            this.sumAmount = DoubleUtils.displayDoubleValue(DoubleUtils.add(this.commercialAmount, this.compulsoryAmount));
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getCommercialAmount() {
            return commercialAmount;
        }

        public void setCommercialAmount(Double commercialAmount) {
            this.commercialAmount = commercialAmount;
        }

        public Double getCompulsoryAmount() {
            return compulsoryAmount;
        }

        public void setCompulsoryAmount(Double compulsoryAmount) {
            this.compulsoryAmount = compulsoryAmount;
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

        public Double getSumAmount() {
            return sumAmount;
        }

        public void setSumAmount(Double sumAmount) {
            this.sumAmount = sumAmount;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

    public static void getRecordInsuranceDetail(OrderDetailViewData orderDetailViewData, QuoteRecord quoteRecord) {
        /* 报价记录 */
        orderDetailViewData.setCommercialPolicyNo("");
        orderDetailViewData.setCommercialPremium(quoteRecord.getPremium());
        orderDetailViewData.setCompulsoryPremium(quoteRecord.getCompulsoryPremium());
        orderDetailViewData.setAutoTax(quoteRecord.getAutoTax());
        orderDetailViewData.setThirdPartyAmount(quoteRecord.getThirdPartyAmount());
        orderDetailViewData.setThirdPartyPremium(quoteRecord.getThirdPartyPremium());
        orderDetailViewData.setScratchAmount(quoteRecord.getScratchAmount());
        orderDetailViewData.setScratchPremium(quoteRecord.getScratchPremium());
        orderDetailViewData.setDamageAmount(quoteRecord.getDamageAmount());
        orderDetailViewData.setDamagePremium(quoteRecord.getDamagePremium());
        orderDetailViewData.setTheftAmount(quoteRecord.getTheftAmount());
        orderDetailViewData.setTheftPremium(quoteRecord.getTheftPremium());
        orderDetailViewData.setDriverAmount(quoteRecord.getDriverAmount());
        orderDetailViewData.setDriverPremium(quoteRecord.getDriverPremium());
        orderDetailViewData.setPassengerAmount(quoteRecord.getPassengerAmount());
        orderDetailViewData.setPassengerPremium(quoteRecord.getPassengerPremium());
        orderDetailViewData.setSpontaneousLossAmount(quoteRecord.getSpontaneousLossAmount());
        orderDetailViewData.setSpontaneousLossPremium(quoteRecord.getSpontaneousLossPremium());
        orderDetailViewData.setEnginePremium(quoteRecord.getEnginePremium());
        orderDetailViewData.setGlassPremium(quoteRecord.getGlassPremium());
        orderDetailViewData.setGlassType(quoteRecord.getInsurancePackage().getGlassType() == null ? "" :
            quoteRecord.getInsurancePackage().getGlassType().getName());
        orderDetailViewData.setUnableFindThirdPartyPremium(quoteRecord.getUnableFindThirdPartyPremium());
        orderDetailViewData.setIop(quoteRecord.getIopTotal());
        orderDetailViewData.setDesignatedRepairShopPremium(quoteRecord.getDesignatedRepairShopPremium());
        orderDetailViewData.setCompulsoryPolicyNo("");
        orderDetailViewData.setCompulsoryPremium(quoteRecord.getCompulsoryPremium());
    }

    public static void createByPurchaseOrder(OrderDetailViewData orderDetailViewData, PurchaseOrderHistory history, String insuranceCompanyName) {
        orderDetailViewData.setInsuranceCompany(insuranceCompanyName);
        orderDetailViewData.setOrderNo(history.getOrderNo());
        orderDetailViewData.setOrderStatus(null == history.getStatus() ? "" : history.getStatus().getStatus());
        orderDetailViewData.setPayableAmount(history.getPayableAmount());
        orderDetailViewData.setPaidAmount(history.getPaidAmount());
    }
}
