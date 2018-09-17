package com.cheche365.cheche.operationcenter.web.model.thirdParty.mapToBean;

public class ParamsModel {

    private String channelCode; //渠道英文名称
    private String channelName; //第三方渠道名称
    private Boolean agent; //是否ToA
    private Boolean singleCompany; //是否直投
    private Boolean levelAgent; //是否支持三级管理
    private Boolean rebateIntoWallet; //返点进钱包
    private Boolean disabledChannel; //渠道禁用
    private Boolean hasOrderCenter; //出单中心是否可下单
    private Boolean needSyncOrder; //是否支持订单同步
    private String syncOrderUrl; //第三方订单同步url
    private Boolean supportAmend;//是否支持增补


    public String getChannelCode() { return channelCode; }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Boolean getAgent() {
        return agent;
    }

    public void setAgent(Boolean agent) {
        this.agent = agent;
    }

    public Boolean getSingleCompany() {
        return singleCompany;
    }

    public void setSingleCompany(Boolean singleCompany) {
        this.singleCompany = singleCompany;
    }

    public Boolean getLevelAgent() {
        return levelAgent;
    }

    public void setLevelAgent(Boolean levelAgent) {
        this.levelAgent = levelAgent;
    }

    public Boolean getRebateIntoWallet() {
        return rebateIntoWallet;
    }

    public void setRebateIntoWallet(Boolean rebateIntoWallet) {
        this.rebateIntoWallet = rebateIntoWallet;
    }

    public Boolean getDisabledChannel() {
        return disabledChannel;
    }

    public void setDisabledChannel(Boolean disabledChannel) {
        this.disabledChannel = disabledChannel;
    }

    public Boolean getHasOrderCenter() {
        return hasOrderCenter;
    }

    public void setHasOrderCenter(Boolean hasOrderCenter) {
        this.hasOrderCenter = hasOrderCenter;
    }

    public Boolean getNeedSyncOrder() {
        return needSyncOrder;
    }

    public void setNeedSyncOrder(Boolean needSyncOrder) {
        this.needSyncOrder = needSyncOrder;
    }

    public String getSyncOrderUrl() {
        return syncOrderUrl;
    }

    public void setSyncOrderUrl(String syncOrderUrl) {
        this.syncOrderUrl = syncOrderUrl;
    }

    public Boolean getSupportAmend() { return supportAmend; }

    public void setSupportAmend(Boolean supportAmend) { this.supportAmend = supportAmend; }
}
