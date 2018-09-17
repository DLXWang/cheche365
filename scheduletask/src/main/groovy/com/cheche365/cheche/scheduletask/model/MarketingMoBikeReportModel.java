package com.cheche365.cheche.scheduletask.model;

/**
 * Created by yellow on 2017/9/13.
 */
public class MarketingMoBikeReportModel extends AttachmentData {
    private String mobile;
    private String licensePlateNo;
    private String marketingName;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public String getMarketingName() {
        return marketingName;
    }

    public void setMarketingName(String marketingName) {
        this.marketingName = marketingName;
    }
}
