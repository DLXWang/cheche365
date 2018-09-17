package com.cheche365.cheche.ordercenter.web.model.order;

/**
 * Created by wangfei on 2015/6/16.
 */
public class OrderFilterRequestParams {
    private Long insuranceCompany;//保险公司
    private Long assigner;//指定人
    private Long status;//出单状态
    private Long orderStatus;//订单状态
    private String orderStartDate;//出单开始日期
    private String orderEndDate;//出单结束日期
    private String operateStartDate;//操作开始日期
    private String operateEndDate;//操作结束日期
    private String owner;//车主姓名
    private String mobile;//手机
    private String licenseNo;//车牌
    private Long agent;//代理人
    private Long vipCompany;//大客户
    private String orderNo;//订单号
    private Long channel;//来源
    private Long quoteArea;//报价区域
    private Long cpsChannel;//CPS渠道
    private Long paymentChannel;//支付方式
    private Long institution;//出单机构
    private Integer currentPage;//当前页
    private Integer pageSize;//页大小
    private String countField;//统计字段
    private String receiveUser;//收件人
    private String insurance;//保险人
    private String insuranced;//被保险人
    private Long orderSourceType;//报价来源
    private Long orderSourceId;//广告来源

    private String stopBeginDate;//停驶开始日期
    private String stopEndDate;//停驶结束日期
    private String restartBeginDate;//复驶开始日期
    private String restartEndDate;//复驶结束日期
    private String inviter;//邀请人
    private String indirectionInviter;//间接邀请人

    public String getStopBeginDate() {
        return stopBeginDate;
    }

    public void setStopBeginDate(String stopBeginDate) {
        this.stopBeginDate = stopBeginDate;
    }

    public String getStopEndDate() {
        return stopEndDate;
    }

    public void setStopEndDate(String stopEndDate) {
        this.stopEndDate = stopEndDate;
    }

    public String getRestartBeginDate() {
        return restartBeginDate;
    }

    public void setRestartBeginDate(String restartBeginDate) {
        this.restartBeginDate = restartBeginDate;
    }

    public String getRestartEndDate() {
        return restartEndDate;
    }

    public void setRestartEndDate(String restartEndDate) {
        this.restartEndDate = restartEndDate;
    }
    public Long getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(Long insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public Long getAssigner() {
        return assigner;
    }

    public void setAssigner(Long assigner) {
        this.assigner = assigner;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Long orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStartDate() {
        return orderStartDate;
    }

    public void setOrderStartDate(String orderStartDate) {
        this.orderStartDate = orderStartDate;
    }

    public String getOrderEndDate() {
        return orderEndDate;
    }

    public void setOrderEndDate(String orderEndDate) {
        this.orderEndDate = orderEndDate;
    }

    public String getOperateStartDate() {
        return operateStartDate;
    }

    public void setOperateStartDate(String operateStartDate) {
        this.operateStartDate = operateStartDate;
    }

    public String getOperateEndDate() {
        return operateEndDate;
    }

    public void setOperateEndDate(String operateEndDate) {
        this.operateEndDate = operateEndDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public Long getAgent() {
        return agent;
    }

    public void setAgent(Long agent) {
        this.agent = agent;
    }

    public Long getVipCompany() {
        return vipCompany;
    }

    public void setVipCompany(Long vipCompany) {
        this.vipCompany = vipCompany;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getChannel() {
        return channel;
    }

    public void setChannel(Long channel) {
        this.channel = channel;
    }

    public Long getQuoteArea() {
        return quoteArea;
    }

    public void setQuoteArea(Long quoteArea) {
        this.quoteArea = quoteArea;
    }

    public Long getCpsChannel() {
        return cpsChannel;
    }

    public void setCpsChannel(Long cpsChannel) {
        this.cpsChannel = cpsChannel;
    }

    public Long getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(Long paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public Long getInstitution() {
        return institution;
    }

    public void setInstitution(Long institution) {
        this.institution = institution;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getCountField() {
        return countField;
    }

    public void setCountField(String countField) {
        this.countField = countField;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getInsuranced() {
        return insuranced;
    }

    public void setInsuranced(String insuranced) {
        this.insuranced = insuranced;
    }

    public String getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(String receiveUser) {
        this.receiveUser = receiveUser;
    }

    public Long getOrderSourceType() {
        return orderSourceType;
    }

    public void setOrderSourceType(Long orderSourceType) {
        this.orderSourceType = orderSourceType;
    }

    public Long getOrderSourceId() {
        return orderSourceId;
    }

    public void setOrderSourceId(Long orderSourceId) {
        this.orderSourceId = orderSourceId;
    }

    public String getInviter() { return inviter; }

    public void setInviter(String inviter) { this.inviter = inviter; }

    public String getIndirectionInviter() { return indirectionInviter; }

    public void setIndirectionInviter(String indirectionInviter) { this.indirectionInviter = indirectionInviter; }
}
