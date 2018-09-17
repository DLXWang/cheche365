package com.cheche365.cheche.ordercenter.web.model.telMarketingCenter;

import com.cheche365.cheche.core.model.QuoteRecord;

/**
 * Created by yinJianBin on 2016/11/11.
 */
public class QuoteRecordExt extends QuoteRecord {
    private String logId;
    private String carNo;
    private String insuranceCompanyName;
    private String quoteDetailString;
    private Double totalAmout;
    private Integer quoteKindNum;
    private String createTimeString;
    private Long userId;
    private String channelName;
    private String carVin;
    private String engineNo;
    private String enrollDate;
    private String autoTypeName;
    private String autoModel;
    private String quoteKind;


    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getInsuranceCompanyName() {
        return insuranceCompanyName;
    }

    public void setInsuranceCompanyName(String insuranceCompanyName) {
        this.insuranceCompanyName = insuranceCompanyName;
    }

    public String getQuoteDetailString() {
        return quoteDetailString;
    }

    public void setQuoteDetailString(String quoteDetailString) {
        this.quoteDetailString = quoteDetailString;
    }

    public Double getTotalAmout() {
        return totalAmout;
    }

    public void setTotalAmout(Double totalAmout) {
        this.totalAmout = totalAmout;
    }

    public Integer getQuoteKindNum() {
        return quoteKindNum;
    }

    public void setQuoteKindNum(Integer quoteKindNum) {
        this.quoteKindNum = quoteKindNum;
    }

    public String getCreateTimeString() {
        return createTimeString;
    }

    public void setCreateTimeString(String createTimeString) {
        this.createTimeString = createTimeString;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getCarVin() {
        return carVin;
    }

    public void setCarVin(String carVin) {
        this.carVin = carVin;
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

    public String getAutoTypeName() {
        return autoTypeName;
    }

    public void setAutoTypeName(String autoTypeName) {
        this.autoTypeName = autoTypeName;
    }

    public String getAutoModel() {
        return autoModel;
    }

    public void setAutoModel(String autoModel) {
        this.autoModel = autoModel;
    }

    public String getQuoteKind() {
        return quoteKind;
    }

    public void setQuoteKind(String quoteKind) {
        this.quoteKind = quoteKind;
    }

    @Override
    public String toString() {
        return "QuoteRecordExt{" +
                "logId=" + logId +
                ", carNo='" + carNo + '\'' +
                ", insuranceCompanyName='" + insuranceCompanyName + '\'' +
                ", quoteDetailString='" + quoteDetailString + '\'' +
                ", totalAmout=" + totalAmout +
                ", quoteKindNum=" + quoteKindNum +
                ", createTimeString='" + createTimeString + '\'' +
                ", userId=" + userId +
                ", channelName='" + channelName + '\'' +
                ", carVin='" + carVin + '\'' +
                ", engineNo='" + engineNo + '\'' +
                ", enrollDate='" + enrollDate + '\'' +
                ", autoTypeName='" + autoTypeName + '\'' +
                ", autoModel='" + autoModel + '\'' +
                ", quoteKind='" + quoteKind + '\'' +
                '}';
    }
}
