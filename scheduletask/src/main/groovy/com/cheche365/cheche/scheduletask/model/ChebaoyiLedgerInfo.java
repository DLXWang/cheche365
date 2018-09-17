package com.cheche365.cheche.scheduletask.model;

/**
 * 车保易台账
 * Created by liulu on 2018/7/30.
 */
public class ChebaoyiLedgerInfo  extends AttachmentData{

    private String issueTime;//出单时间
    private String institution;//出单机构
    private String paymentName;//付款单位
    private String company;//保险公司
    private String platform;//平台
    private String owner;//车主
    private String plateNum;// 车牌号
    private String orderNo;//订单号

    private String premiumSum;//保费总额
    private String compulsoryPremium;//交强险
    private String autoTax;//车船税
    private String commecialPremium;//商业险
    private String compulsoryPointLocation;//交强点位
    private String commecialPointLocation;//商业点位
    private String PointLocationSum;//点位优惠金额
    private String activityFavour;//活动优惠
    private String fuelCard;//加油卡金额
    private String jdCard;//京东卡金额
    private String paymentChannel;//付款方式
    private String giftDetail;//实物礼品
    private String comment;//备注

    private String salesDirector;//销售总监姓名
    private String directorCompulsoryPointLocation;//销售总监交强点位
    private String directorCommecialPointLocation;//销售总监商业点位
    private String salesDirectorBillingSum;//销售总监钱包入账金额
    private String salesManager;//销售经理姓名
    private String managerCompulsoryPointLocation;//销售经理交强点位
    private String managerCommecialPointLocation;//销售经理商业点位
    private String salesManagerBillingSum;//销售经理钱包入账金额
    private String salesman;//业务员姓名
    private String salesmanCompulsoryPointLocation;//业务员交强点位
    private String salesmanCommecialPointLocation;//业务员商业点位
    private String salesmanBillingSum;//业务员钱包入账金额

    private String area;//出单城市
    private String orderAccount;//出单账号
    private String agentAccount;//代理人登录账号
    private String jiaoshangNo;//交强险保单号
    private String insuranceNo;//商业险保单号

    private String getMailed;//收件人
    private String getAddress;//收货地址
    private String customerPhone;//收件人电话
    private String mailAddress;//电子邮箱


    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPlateNum() {
        return plateNum;
    }

    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPremiumSum() {
        return premiumSum;
    }

    public void setPremiumSum(String premiumSum) {
        this.premiumSum = premiumSum;
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

    public String getPointLocationSum() {
        return PointLocationSum;
    }

    public void setPointLocationSum(String pointLocationSum) {
        PointLocationSum = pointLocationSum;
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

    public String getJdCard() {
        return jdCard;
    }

    public void setJdCard(String jdCard) {
        this.jdCard = jdCard;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public String getGiftDetail() {
        return giftDetail;
    }

    public void setGiftDetail(String giftDetail) {
        this.giftDetail = giftDetail;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSalesDirector() {
        return salesDirector;
    }

    public void setSalesDirector(String salesDirector) {
        this.salesDirector = salesDirector;
    }

    public String getDirectorCompulsoryPointLocation() {
        return directorCompulsoryPointLocation;
    }

    public void setDirectorCompulsoryPointLocation(String directorCompulsoryPointLocation) {
        this.directorCompulsoryPointLocation = directorCompulsoryPointLocation;
    }

    public String getDirectorCommecialPointLocation() {
        return directorCommecialPointLocation;
    }

    public void setDirectorCommecialPointLocation(String directorCommecialPointLocation) {
        this.directorCommecialPointLocation = directorCommecialPointLocation;
    }

    public String getSalesDirectorBillingSum() {
        return salesDirectorBillingSum;
    }

    public void setSalesDirectorBillingSum(String salesDirectorBillingSum) {
        this.salesDirectorBillingSum = salesDirectorBillingSum;
    }

    public String getSalesManager() {
        return salesManager;
    }

    public void setSalesManager(String salesManager) {
        this.salesManager = salesManager;
    }

    public String getManagerCompulsoryPointLocation() {
        return managerCompulsoryPointLocation;
    }

    public void setManagerCompulsoryPointLocation(String managerCompulsoryPointLocation) {
        this.managerCompulsoryPointLocation = managerCompulsoryPointLocation;
    }

    public String getManagerCommecialPointLocation() {
        return managerCommecialPointLocation;
    }

    public void setManagerCommecialPointLocation(String managerCommecialPointLocation) {
        this.managerCommecialPointLocation = managerCommecialPointLocation;
    }

    public String getSalesManagerBillingSum() {
        return salesManagerBillingSum;
    }

    public void setSalesManagerBillingSum(String salesManagerBillingSum) {
        this.salesManagerBillingSum = salesManagerBillingSum;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getSalesmanCompulsoryPointLocation() {
        return salesmanCompulsoryPointLocation;
    }

    public void setSalesmanCompulsoryPointLocation(String salesmanCompulsoryPointLocation) {
        this.salesmanCompulsoryPointLocation = salesmanCompulsoryPointLocation;
    }

    public String getSalesmanCommecialPointLocation() {
        return salesmanCommecialPointLocation;
    }

    public void setSalesmanCommecialPointLocation(String salesmanCommecialPointLocation) {
        this.salesmanCommecialPointLocation = salesmanCommecialPointLocation;
    }

    public String getSalesmanBillingSum() {
        return salesmanBillingSum;
    }

    public void setSalesmanBillingSum(String salesmanBillingSum) {
        this.salesmanBillingSum = salesmanBillingSum;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getOrderAccount() {
        return orderAccount;
    }

    public void setOrderAccount(String orderAccount) {
        this.orderAccount = orderAccount;
    }

    public String getAgentAccount() {
        return agentAccount;
    }

    public void setAgentAccount(String agentAccount) {
        this.agentAccount = agentAccount;
    }

    public String getJiaoshangNo() {
        return jiaoshangNo;
    }

    public void setJiaoshangNo(String jiaoshangNo) {
        this.jiaoshangNo = jiaoshangNo;
    }

    public String getInsuranceNo() {
        return insuranceNo;
    }

    public void setInsuranceNo(String insuranceNo) {
        this.insuranceNo = insuranceNo;
    }

    public String getGetMailed() {
        return getMailed;
    }

    public void setGetMailed(String getMailed) {
        this.getMailed = getMailed;
    }

    public String getGetAddress() {
        return getAddress;
    }

    public void setGetAddress(String getAddress) {
        this.getAddress = getAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }
}
