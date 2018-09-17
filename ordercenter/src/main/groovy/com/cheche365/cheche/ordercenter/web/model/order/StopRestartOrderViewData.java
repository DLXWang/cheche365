package com.cheche365.cheche.ordercenter.web.model.order;

/**
 * Created by Luly on 2017/4/24.
 */
public class StopRestartOrderViewData {
    private Long id;//purchase_order id
    private String createTime;//下单时间
    private String orderNo;//订单编号
    private String owner;//车主
    private String licenseNo;//车牌号
    private String quoteArea;//报价区域
    private String channel;//平台
    private String orderSource;//订单来源
    private double paidAmount;//保费实付金额
    private double premium;//商业险保费
    private int stopNum;//累计停驶次数
    private int totalStopDays;//累计停驶天数
    private int totalRestartDays;//累计复驶天数
    private double totalRefundAmount;//累计退还保费
    private String status;//当前停复驶状态
    private String lastStopBeginDate;//最近一次停驶开始日期
    private int lastStopDays;//最近一次停驶天数
    private String lastRestartBeginDate;//最近一次停驶的复驶时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getQuoteArea() {
        return quoteArea;
    }

    public void setQuoteArea(String quoteArea) {
        this.quoteArea = quoteArea;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getPremium() {
        return premium;
    }

    public void setPremium(double premium) {
        this.premium = premium;
    }

    public int getStopNum() {
        return stopNum;
    }

    public void setStopNum(int stopNum) {
        this.stopNum = stopNum;
    }

    public int getTotalStopDays() {
        return totalStopDays;
    }

    public void setTotalStopDays(int totalStopdays) {
        this.totalStopDays = totalStopdays;
    }

    public int getTotalRestartDays() {
        return totalRestartDays;
    }

    public void setTotalRestartDays(int totalRestartDays) {
        this.totalRestartDays = totalRestartDays;
    }

    public double getTotalRefundAmount() {
        return totalRefundAmount;
    }

    public void setTotalRefundAmount(double totalRefundAmount) {
        this.totalRefundAmount = totalRefundAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastStopBeginDate() {
        return lastStopBeginDate;
    }

    public void setLastStopBeginDate(String lastStopBeginDate) {
        this.lastStopBeginDate = lastStopBeginDate;
    }

    public int getLastStopDays() {
        return lastStopDays;
    }

    public void setLastStopDays(int lastStopDays) {
        this.lastStopDays = lastStopDays;
    }

    public String getLastRestartBeginDate() {
        return lastRestartBeginDate;
    }

    public void setLastRestartBeginDate(String lastRestartBeginDate) {
        this.lastRestartBeginDate = lastRestartBeginDate;
    }
}

