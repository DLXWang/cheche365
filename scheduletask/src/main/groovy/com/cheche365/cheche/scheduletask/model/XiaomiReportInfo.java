package com.cheche365.cheche.scheduletask.model;

/**
 * 微车报价信息报表实体
 * Created by chenxy on 2018/5/29.
 */
public class XiaomiReportInfo extends AttachmentData {
    private String iPolicyNo;//商业险保单号
    private String ciPolicyNo;//交强险保单号
    private String allPremium;//总保费
    private String iPremium;//商业险保费
    private String ciPremium;//交强险保费
    private String autoTax;//车船税
    private String iEffectiveDate;//商业险起保日期
    private String iExpireDate;//商业险截止日期
    private String ciEffectiveDate;//交强险起保日期
    private String ciExpireDate;//交强险截止日期
    private String licensePlateNo;//车牌号

    private String area;//投保城市id
    private String insuredMobile;//被保人手机号
    private String insuredName;//被保人姓名
    private String insuredIdNo;//被保险人身份证
    private String insuredIdNo2;//被保险人身份证2
    private String applicantName;//投保人姓名
    private String applicantMobile;//投保人手机号
    private String applicantIdNo;//投保人身份证号
    private String payTime;//支付日期
    private String xiaomiId;//小米ID
    private String confirmOrderDate;//出单时间
    private String paidAmount;//实付金额
    private String subtraction; //差额

    private String insureComp;//保险公司
    private String owner;//车主
    private String ownerIdNo;//车主身份号
    private String orderNo;//订单号
    private String null1;//活动优惠
    private String ciPoint;//交强险点位
    private String iPoint;//商业点位
    private String null2;//点位优惠
    private String payChannel;//付款方式
    private String userMobile;//客户电话（客户下单手机号）
    private String null3;//null

    private String vinNo;//车架号
    private String engineNo;//发动机号
    private String enrollDate;//车辆注册日期
    private String carType;//品牌车型
    private String address;//地址
    private String addrName;//收件人
    private String addrMobile;//电话
    private String createTime;//订单创建时间
    public String getIPolicyNo() {
        return iPolicyNo;
    }

    public void setiPolicyNo(String iPolicyNo) {
        this.iPolicyNo = iPolicyNo;
    }

    public String getCiPolicyNo() {
        return ciPolicyNo;
    }

    public void setCiPolicyNo(String ciPolicyNo) {
        this.ciPolicyNo = ciPolicyNo;
    }

    public String getAllPremium() {
        return allPremium;
    }

    public void setAllPremium(String allPremium) {
        this.allPremium = allPremium;
    }

    public String getIPremium() {
        return iPremium;
    }

    public void setiPremium(String iPremium) {
        this.iPremium = iPremium;
    }

    public String getCiPremium() {
        return ciPremium;
    }

    public void setCiPremium(String ciPremium) {
        this.ciPremium = ciPremium;
    }

    public String getAutoTax() {
        return autoTax;
    }

    public void setAutoTax(String autoTax) {
        this.autoTax = autoTax;
    }

    public String getIEffectiveDate() {
        return iEffectiveDate;
    }

    public void setiEffectiveDate(String iEffectiveDate) {
        this.iEffectiveDate = iEffectiveDate;
    }

    public String getIExpireDate() {
        return iExpireDate;
    }

    public void setiExpireDate(String iExpireDate) {
        this.iExpireDate = iExpireDate;
    }

    public String getCiEffectiveDate() {
        return ciEffectiveDate;
    }

    public void setCiEffectiveDate(String ciEffectiveDate) {
        this.ciEffectiveDate = ciEffectiveDate;
    }

    public String getCiExpireDate() {
        return ciExpireDate;
    }

    public void setCiExpireDate(String ciExpireDate) {
        this.ciExpireDate = ciExpireDate;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getInsuredMobile() {
        return insuredMobile;
    }

    public void setInsuredMobile(String insuredMobile) {
        this.insuredMobile = insuredMobile;
    }

    public String getInsuredName() {
        return insuredName;
    }

    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    public String getInsuredIdNo() {
        return insuredIdNo;
    }

    public void setInsuredIdNo(String insuredIdNo) {
        this.insuredIdNo = insuredIdNo;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantMobile() {
        return applicantMobile;
    }

    public void setApplicantMobile(String applicantMobile) {
        this.applicantMobile = applicantMobile;
    }

    public String getApplicantIdNo() {
        return applicantIdNo;
    }

    public void setApplicantIdNo(String applicantIdNo) {
        this.applicantIdNo = applicantIdNo;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getXiaomiId() {
        return xiaomiId;
    }

    public void setXiaomiId(String xiaomiId) {
        this.xiaomiId = xiaomiId;
    }

    public String getConfirmOrderDate() {
        return confirmOrderDate;
    }

    public void setConfirmOrderDate(String confirmOrderDate) {
        this.confirmOrderDate = confirmOrderDate;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getSubtraction() {
        return subtraction;
    }

    public void setSubtraction(String subtraction) {
        this.subtraction = subtraction;
    }

    public String getInsureComp() {
        return insureComp;
    }

    public void setInsureComp(String insureComp) {
        this.insureComp = insureComp;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerIdNo() {
        return ownerIdNo;
    }

    public void setOwnerIdNo(String ownerIdNo) {
        this.ownerIdNo = ownerIdNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getNull1() {
        return null1;
    }

    public void setNull1(String null1) {
        this.null1 = null1;
    }

    public String getCiPoint() {
        return ciPoint;
    }

    public void setCiPoint(String ciPoint) {
        this.ciPoint = ciPoint;
    }

    public String getIPoint() {
        return iPoint;
    }

    public void setiPoint(String iPoint) {
        this.iPoint = iPoint;
    }

    public String getNull2() {
        return null2;
    }

    public void setNull2(String null2) {
        this.null2 = null2;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getNull3() {
        return null3;
    }

    public void setNull3(String null3) {
        this.null3 = null3;
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

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddrName() {
        return addrName;
    }

    public void setAddrName(String addrName) {
        this.addrName = addrName;
    }

    public String getAddrMobile() {
        return addrMobile;
    }

    public void setAddrMobile(String addrMobile) {
        this.addrMobile = addrMobile;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getInsuredIdNo2() {
        return insuredIdNo2;
    }

    public void setInsuredIdNo2(String insuredIdNo2) {
        this.insuredIdNo2 = insuredIdNo2;
    }

}
