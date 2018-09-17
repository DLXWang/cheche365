package com.cheche365.cheche.scheduletask.model;

/**
 * 微车报价信息报表实体
 * Created by chenxiangyin on 2017/4/27.
 */
public class DailyInsuranceBasicReportModel extends AttachmentData{
    private String orderNum;
    private String allPaidAmount;
    private String licensePaidAmount;
    private String stoppedNum;
    private String unstoppedNum;
    private String stoppingNum;
    private String allStopDays;
    private String allStopTimes;
    private String allReturnMoney;
    private String allRestartDays;

    public String getAllReturnMoney() {
        return allReturnMoney;
    }

    public void setAllReturnMoney(String allReturnMoney) {
        this.allReturnMoney = allReturnMoney;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getAllPaidAmount() {
        return allPaidAmount;
    }

    public void setAllPaidAmount(String allPaidAmount) {
        this.allPaidAmount = allPaidAmount;
    }

    public String getLicensePaidAmount() {
        return licensePaidAmount;
    }

    public void setLicensePaidAmount(String licensePaidAmount) {
        this.licensePaidAmount = licensePaidAmount;
    }

    public String getStoppedNum() {
        return stoppedNum;
    }

    public void setStoppedNum(String stoppedNum) {
        this.stoppedNum = stoppedNum;
    }

    public String getUnstoppedNum() {
        return unstoppedNum;
    }

    public void setUnstoppedNum(String unstoppedNum) {
        this.unstoppedNum = unstoppedNum;
    }

    public String getStoppingNum() {
        return stoppingNum;
    }

    public void setStoppingNum(String stoppingNum) {
        this.stoppingNum = stoppingNum;
    }

    public String getAllStopDays() {
        return allStopDays;
    }

    public void setAllStopDays(String allStopDays) {
        this.allStopDays = allStopDays;
    }

    public String getAllStopTimes() {
        return allStopTimes;
    }

    public void setAllStopTimes(String allStopTimes) {
        this.allStopTimes = allStopTimes;
    }

    public String getAllRestartDays() {
        return allRestartDays;
    }

    public void setAllRestartDays(String allRestartDays) {
        this.allRestartDays = allRestartDays;
    }
}
