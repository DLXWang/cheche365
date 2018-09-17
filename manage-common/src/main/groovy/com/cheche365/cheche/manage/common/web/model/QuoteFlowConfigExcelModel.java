package com.cheche365.cheche.manage.common.web.model;

/**
 * Created by chenxiangyin on 2018/3/21.
 */
public class QuoteFlowConfigExcelModel {
    private String cityName;//城市名称
    private String insuranceComp;//保险公司
    private String type;//类型
    private String channel;//渠道
    private String channelName;//渠道名
    private String status;//状态
    private String quoteWay;//报价方式
    private String excelErr;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getInsuranceComp() {
        return insuranceComp;
    }

    public void setInsuranceComp(String insuranceComp) {
        this.insuranceComp = insuranceComp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuoteWay() {
        return quoteWay;
    }

    public void setQuoteWay(String quoteWay) {
        this.quoteWay = quoteWay;
    }

    public String getExcelErr() {
        return excelErr;
    }

    public void setExcelErr(String excelErr) {
        this.excelErr = excelErr;
    }

}
