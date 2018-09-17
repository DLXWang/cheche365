package com.cheche365.cheche.operationcenter.web.model.quoteFlowConfig;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.QuoteFlowConfig;

/**
 * Created by chenxiangyin on 2017/7/13.
 */
public class QuoteFlowConfigViewData {
    private Long id;
    private String insuranceCompany;
    private String area;
    private String clientType;
    private String channelType;
    private String channel;
    private Boolean enable;
    private Long configValue;
    public static QuoteFlowConfigViewData createViewModel(QuoteFlowConfig quoteFlowConfig){
        QuoteFlowConfigViewData data = new QuoteFlowConfigViewData();
        data.setId(quoteFlowConfig.getId());
        data.setInsuranceCompany(quoteFlowConfig.getInsuranceCompany().getName());
        data.setArea(quoteFlowConfig.getArea().getName());
        Channel channel = quoteFlowConfig.getChannel();
        data.setClientType(channel.isAgentChannel()?"ToA":"ToC");
        data.setChannelType(channel.isSelf()?"自有":"第三方");
        data.setChannel(channel.getDescription());
        data.setEnable(quoteFlowConfig.getEnable());
        data.setConfigValue(quoteFlowConfig.getConfigValue());
        return data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Long getConfigValue() {
        return configValue;
    }

    public void setConfigValue(Long configValue) {
        this.configValue = configValue;
    }
}
