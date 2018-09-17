package com.cheche365.cheche.ordercenter.web.model.telMarketingCenter;

import java.util.List;

/**
 * Created by yinJianBin on 2016/11/11.
 */
public class QuoteHistoryDetailJsonObject {

    private String carNo;       //车牌号
    private String carVin;
    private String engineNo;
    private String enrollDate;
    private String autoTypeName;
    private String autoModel;

    private List<QuoteCompanyData> quoteCompanyDataList;      //报价公司的报价数据数据

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
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

    public List<QuoteCompanyData> getQuoteCompanyDataList() {
        return quoteCompanyDataList;
    }

    public void setQuoteCompanyDataList(List<QuoteCompanyData> quoteCompanyDataList) {
        this.quoteCompanyDataList = quoteCompanyDataList;
    }
}


