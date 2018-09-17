package com.cheche365.cheche.ordercenter.web.model.order;

/**
 * Created by wangfei on 2015/5/5.
 */
public class OrderViewData {
    private Long id;
    private Long orderOperationId;//order_operation_info id
    private Long purchaseOrderId;//purchase_order id
    private String orderNo;//订单编号
    private String owner;//车主
    private String licenseNo;//车牌号
    private String modelName;//车型
    private String insuranceCompanyCode;//保险公司代码
    private String insuranceCompany;//保险公司
    private double sumPremium;//总保费
    private double payableAmount;//原始金额
    private double rebate = 0.00;//返点
    private String createTime;//下单时间
    private String assignerName;//指定人
    private String operatorName;//最后操作人
    private String updateTime;//最后操作时间
    private Long statusId;//订单操作状态ID
    private String currentStatus;//当前状态
    private String optionValue;//操作内容
    private String payStatus;//支付状态  已支付  未支付
    private String remark;//备注
    private String payTime;//上门收费时间
    private String quoteArea;//报价区域
    private String inviter;//邀请人
    private String indirectionInviter;//间接邀请人

    private Long didiInsuranceId;//滴滴专车id
    private String mobile;//手机号
    private String thirdPartyAmount;//三者险保额
    private String driverAmount;//车上人员（司机）保额
    private String passengerAmount;//车上人员（乘客）保额

    private String userSource;//用户来源
    private Long channel;//订单来源
    private String channelIcon;//渠道logo

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderOperationId() {
        return orderOperationId;
    }

    public void setOrderOperationId(Long orderOperationId) {
        this.orderOperationId = orderOperationId;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public double getSumPremium() {
        return sumPremium;
    }

    public void setSumPremium(double sumPremium) {
        this.sumPremium = sumPremium;
    }

    public double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public double getRebate() {
        return rebate;
    }

    public void setRebate(double rebate) {
        this.rebate = rebate;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAssignerName() {
        return assignerName;
    }

    public void setAssignerName(String assignerName) {
        this.assignerName = assignerName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getInsuranceCompanyCode() {
        return insuranceCompanyCode;
    }

    public void setInsuranceCompanyCode(String insuranceCompanyCode) {
        this.insuranceCompanyCode = insuranceCompanyCode;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public Long getDidiInsuranceId() {
        return didiInsuranceId;
    }

    public void setDidiInsuranceId(Long didiInsuranceId) {
        this.didiInsuranceId = didiInsuranceId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getThirdPartyAmount() {
        return thirdPartyAmount;
    }

    public void setThirdPartyAmount(String thirdPartyAmount) {
        this.thirdPartyAmount = thirdPartyAmount;
    }

    public String getDriverAmount() {
        return driverAmount;
    }

    public void setDriverAmount(String driverAmount) {
        this.driverAmount = driverAmount;
    }

    public String getPassengerAmount() {
        return passengerAmount;
    }

    public void setPassengerAmount(String passengerAmount) {
        this.passengerAmount = passengerAmount;
    }

    public String getQuoteArea() {
        return quoteArea;
    }

    public void setQuoteArea(String quoteArea) {
        this.quoteArea = quoteArea;
    }

    public String getUserSource() {
        return userSource;
    }

    public void setUserSource(String userSource) {
        this.userSource = userSource;
    }

    public Long getChannel() {
        return channel;
    }

    public void setChannel(Long channel) {
        this.channel = channel;
    }

    public String getChannelIcon() {
        return channelIcon;
    }

    public void setChannelIcon(String channelIcon) {
        this.channelIcon = channelIcon;
    }

    public String getInviter() { return inviter; }

    public void setInviter(String inviter) { this.inviter = inviter; }

    public String getIndirectionInviter() { return indirectionInviter; }

    public void setIndirectionInviter(String indirectionInviter) { this.indirectionInviter = indirectionInviter; }
}
