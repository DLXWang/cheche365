package com.cheche365.cheche.scheduletask.model;

/**
 * 按天买车险停复驶车辆报表实体
 * Created by mujiguang on 2017/7/26.
 */
public class DailyInsuranceStopReport extends AttachmentData {
    private String reportType;          //报表类型
    private String id;                  //编号
    private String commercialPolicyNo;  //商业险保单号
    private String stopBeginDate;       //停驶开始日期
    private String stopEndDate;         //停驶截至日期
    private String reStartDate;         //复驶日期
    private String licensePlateNo;      //车牌号
    private String refundAmt;           //返还金额
    private String rePayAmt;            //复驶支付金额
    private String insuranceDetail;     //险种详情
    private String optStopTime;         //操作停驶时间
    private String optReStartTime;      //操作复驶时间

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommercialPolicyNo() {
        return commercialPolicyNo;
    }

    public void setCommercialPolicyNo(String commercialPolicyNo) {
        this.commercialPolicyNo = commercialPolicyNo;
    }

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

    public String getReStartDate() {
        return reStartDate;
    }

    public void setReStartDate(String reStartDate) {
        this.reStartDate = reStartDate;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public String getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(String refundAmt) {
        this.refundAmt = refundAmt;
    }

    public String getRePayAmt() {
        return rePayAmt;
    }

    public void setRePayAmt(String rePayAmt) {
        this.rePayAmt = rePayAmt;
    }

    public String getInsuranceDetail() {
        return insuranceDetail;
    }

    public void setInsuranceDetail(String insuranceDetail) {
        this.insuranceDetail = insuranceDetail;
    }

    public String getOptStopTime() {
        return optStopTime;
    }

    public void setOptStopTime(String optStopTime) {
        this.optStopTime = optStopTime;
    }

    public String getOptReStartTime() {
        return optReStartTime;
    }

    public void setOptReStartTime(String optReStartTime) {
        this.optReStartTime = optReStartTime;
    }
}
