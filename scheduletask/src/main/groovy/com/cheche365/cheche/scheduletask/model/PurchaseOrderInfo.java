package com.cheche365.cheche.scheduletask.model;

/**
 * 订单信息
 * Created by sunhuazhong on 2015/6/10.
 */
public class PurchaseOrderInfo extends AttachmentData {
    private String orderNo;//订单编号
    private String licenseNo;//车牌
    private String owner;//车主
    private String area;//城市
    private String insuranceCompany;//保险公司
    private String payableAmount;//应付金额
    private String paidAmount; //实付金额
    private String amount;//金额
    private String orderStatus;//订单状态
    private String paymentStatus;//支付状态
    private String operateStatus;//操作状态
    private String hours;//多长时间未操作
    private String orderTime;//下单时间
    private String operateTime;//最后操作时间
    private String sendTime;//派送时间
    private String sendPeriod;//派送时间段
    private String reConfirmDate;//再次确认时间
    private String assigner;//指定人
    private String operator;//最后操作人
    private String linkMan;//联系人
    private String userType;//用户名称，区分出大客户，CPS渠道，代理，普通用户
    private String source;//来源
    private String paymentDate;//支付日期
    private String insuredName;//被保险人名称
    private String applicantName;//投保人姓名
    private String linkPhone;//电话
    private String compulsoryPremium;//交强险
    private String autoTax;//车船税
    private String commecialPremium;//商业险
    private String originalPremium;//原始保费
    private String activity;//活动政策
    private String jdCard;//京东卡
    private String subsidyRate;//补贴率
    private String paymentChannel;//支付方式
    private String comment;//备注
    private String mobile;//领取红包手机号
    private String marketingName;//领取红包活动名称
    private String time;//领取红包时间
    private String expireTime;//车险到期日
    private String submitTime;//提交时间
    private String channel;//产品平台
    private String giftSource;//领券渠道
    private String paymentPlatform;//支付平台
    private BusinessActivityInfo businessActivityInfo;
    private String discountAmount;//差价
    private String giftDetail;//礼品信息
    private String serialNumber;//序号
    private String deliveryAddress;//收货地址
    private String cityName;//城市
    private String paymentDiscountType;//减免优惠类型
    private String paymentDiscountAmount;//减免优惠类型（金额）
    private String giftDiscountType;//线下优惠类型
    private String giftDiscountAmount;//线下优惠类型（金额）
    private String downRebateChannel;//出单机构
    private String downRebateAmount;//服务费
    private String upRebateChannel;//代理（含渠道）
    private String upRebateAmount;//返佣
    private String result;//结果
    private String issueTime;//出单日期（订单状态变更为确认出单的日期）

    private String downRebateChannelRebate;//出单机构费率
    private String agentRebate; //代理人费率
    private String mchId;//商户ID

    private String autoType;//车型
    private String vinNo;//车架号
    private String engineNo;//发动机号
    private String enrollDate;//初登日期
    private String account;//第三方账号；如：百度账号
    private String offlineCashBackSum;//线下计算返现金额
    private String offlineCashBackBase;//线下返现基数
    private String isCashBack;//是否返现

    private String fieldName;//电销统计进库量拨打量名称
    private String inputAmount;//进库量
    private String callAmount;//拨打量

    private String days;   //停复驶天数

    private String bank;//银行
    private String bankNo;//银行卡
    private String premiumSum;//保费总额

    private String compulsoryPointLocation;//交强点位
    private String commecialPointLocation;//商业点位
    private String activityFavour;//活动优惠
    private String fuelCard;//加油卡
    private String damagePremium;//车损险
    private String isNewAuto;//是否新车

    private String registerChannel;

    private String deliveryMobile;

    public String getDeliveryMobile() {
        return deliveryMobile;
    }

    public void setDeliveryMobile(String deliveryMobile) {
        this.deliveryMobile = deliveryMobile;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getDamagePremium() {
        return damagePremium;
    }

    public void setDamagePremium(String damagePremium) {
        this.damagePremium = damagePremium;
    }

    public String getIsNewAuto() {
        return isNewAuto;
    }

    public void setIsNewAuto(String isNewAuto) {
        this.isNewAuto = isNewAuto;
    }

    public String getCompulsoryPointLocation() {
        return compulsoryPointLocation;
    }

    public void setCompulsoryPointLocation(String compulsoryPointLocation) {
        this.compulsoryPointLocation = compulsoryPointLocation;
    }

    public String getCommecialPointLocation() {
        return commecialPointLocation;
    }

    public void setCommecialPointLocation(String commecialPointLocation) {
        this.commecialPointLocation = commecialPointLocation;
    }

    public String getActivityFavour() {
        return activityFavour;
    }

    public void setActivityFavour(String activityFavour) {
        this.activityFavour = activityFavour;
    }

    public String getFuelCard() {
        return fuelCard;
    }

    public void setFuelCard(String fuelCard) {
        this.fuelCard = fuelCard;
    }

    public String getPremiumSum() {
        return premiumSum;
    }

    public void setPremiumSum(String premiumSum) {
        this.premiumSum = premiumSum;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getAutoType() {
        return autoType;
    }

    public void setAutoType(String autoType) {
        this.autoType = autoType;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOfflineCashBackSum() {
        return offlineCashBackSum;
    }

    public void setOfflineCashBackSum(String offlineCashBackSum) {
        this.offlineCashBackSum = offlineCashBackSum;
    }

    public String getOfflineCashBackBase() {
        return offlineCashBackBase;
    }

    public void setOfflineCashBackBase(String offlineCashBackBase) {
        this.offlineCashBackBase = offlineCashBackBase;
    }

    public String getIsCashBack() {
        return isCashBack;
    }

    public void setIsCashBack(String isCashBack) {
        this.isCashBack = isCashBack;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    public String getInputAmount() {
        return inputAmount;
    }

    public void setInputAmount(String inputAmount) {
        this.inputAmount = inputAmount;
    }

    public String getCallAmount() {
        return callAmount;
    }

    public void setCallAmount(String callAmount) {
        this.callAmount = callAmount;
    }


    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(String payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOperateStatus() {
        return operateStatus;
    }

    public void setOperateStatus(String operateStatus) {
        this.operateStatus = operateStatus;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendPeriod() {
        return sendPeriod;
    }

    public void setSendPeriod(String sendPeriod) {
        this.sendPeriod = sendPeriod;
    }

    public String getReConfirmDate() {
        return reConfirmDate;
    }

    public void setReConfirmDate(String reConfirmDate) {
        this.reConfirmDate = reConfirmDate;
    }

    public String getAssigner() {
        return assigner;
    }

    public void setAssigner(String assigner) {
        this.assigner = assigner;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getLinkMan() {
        return linkMan;
    }

    public void setLinkMan(String linkMan) {
        this.linkMan = linkMan;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getInsuredName() {
        return insuredName;
    }

    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    public String getLinkPhone() {
        return linkPhone;
    }

    public void setLinkPhone(String linkPhone) {
        this.linkPhone = linkPhone;
    }

    public String getCompulsoryPremium() {
        return compulsoryPremium;
    }

    public void setCompulsoryPremium(String compulsoryPremium) {
        this.compulsoryPremium = compulsoryPremium;
    }

    public String getAutoTax() {
        return autoTax;
    }

    public void setAutoTax(String autoTax) {
        this.autoTax = autoTax;
    }

    public String getCommecialPremium() {
        return commecialPremium;
    }

    public void setCommecialPremium(String commecialPremium) {
        this.commecialPremium = commecialPremium;
    }

    public String getOriginalPremium() {
        return originalPremium;
    }

    public void setOriginalPremium(String originalPremium) {
        this.originalPremium = originalPremium;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getJdCard() {
        return jdCard;
    }

    public void setJdCard(String jdCard) {
        this.jdCard = jdCard;
    }

    public String getSubsidyRate() {
        return subsidyRate;
    }

    public void setSubsidyRate(String subsidyRate) {
        this.subsidyRate = subsidyRate;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMarketingName() {
        return marketingName;
    }

    public void setMarketingName(String marketingName) {
        this.marketingName = marketingName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getGiftSource() {
        return giftSource;
    }

    public void setGiftSource(String giftSource) {
        this.giftSource = giftSource;
    }

    public String getPaymentPlatform() {
        return paymentPlatform;
    }

    public void setPaymentPlatform(String paymentPlatform) {
        this.paymentPlatform = paymentPlatform;
    }

    public BusinessActivityInfo getBusinessActivityInfo() {
        return businessActivityInfo;
    }

    public void setBusinessActivityInfo(BusinessActivityInfo businessActivityInfo) {
        this.businessActivityInfo = businessActivityInfo;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getGiftDetail() {
        return giftDetail;
    }

    public void setGiftDetail(String giftDetail) {
        this.giftDetail = giftDetail;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPaymentDiscountType() {
        return paymentDiscountType;
    }

    public void setPaymentDiscountType(String paymentDiscountType) {
        this.paymentDiscountType = paymentDiscountType;
    }

    public String getPaymentDiscountAmount() {
        return paymentDiscountAmount;
    }

    public void setPaymentDiscountAmount(String paymentDiscountAmount) {
        this.paymentDiscountAmount = paymentDiscountAmount;
    }

    public String getGiftDiscountType() {
        return giftDiscountType;
    }

    public void setGiftDiscountType(String giftDiscountType) {
        this.giftDiscountType = giftDiscountType;
    }

    public String getGiftDiscountAmount() {
        return giftDiscountAmount;
    }

    public void setGiftDiscountAmount(String giftDiscountAmount) {
        this.giftDiscountAmount = giftDiscountAmount;
    }

    public String getDownRebateChannel() {
        return downRebateChannel;
    }

    public void setDownRebateChannel(String downRebateChannel) {
        this.downRebateChannel = downRebateChannel;
    }

    public String getDownRebateAmount() {
        return downRebateAmount;
    }

    public void setDownRebateAmount(String downRebateAmount) {
        this.downRebateAmount = downRebateAmount;
    }

    public String getUpRebateChannel() {
        return upRebateChannel;
    }

    public void setUpRebateChannel(String upRebateChannel) {
        this.upRebateChannel = upRebateChannel;
    }

    public String getUpRebateAmount() {
        return upRebateAmount;
    }

    public void setUpRebateAmount(String upRebateAmount) {
        this.upRebateAmount = upRebateAmount;
    }

    public String getDownRebateChannelRebate() {
        return downRebateChannelRebate;
    }

    public void setDownRebateChannelRebate(String downRebateChannelRebate) {
        this.downRebateChannelRebate = downRebateChannelRebate;
    }

    public String getAgentRebate() {
        return agentRebate;
    }

    public void setAgentRebate(String agentRebate) {
        this.agentRebate = agentRebate;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getRegisterChannel() {
        return registerChannel;
    }

    public void setRegisterChannel(String registerChannel) {
        this.registerChannel = registerChannel;
    }
}

