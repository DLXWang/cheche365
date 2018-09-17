package com.cheche365.cheche.scheduletask.model;

/**
 * 小米日报表
 * Created by chenxy on 2018/6/25.
 */
public class XiaomiDailyReportInfo extends AttachmentData {
    private String marketingNum;//名单量
    private String dialNum;//拨打量
    private String connectNum;//接通数
    private String orderNum;//成单数
    private String paid;//成交金额

    public String getMarketingNum() {
        return marketingNum;
    }

    public void setMarketingNum(String marketingNum) {
        this.marketingNum = marketingNum;
    }

    public String getDialNum() {
        return dialNum;
    }

    public void setDialNum(String dialNum) {
        this.dialNum = dialNum;
    }

    public String getConnectNum() {
        return connectNum;
    }

    public void setConnectNum(String connectNum) {
        this.connectNum = connectNum;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }
}
