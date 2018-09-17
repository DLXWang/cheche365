package com.cheche365.cheche.operationcenter.web.model.thirdParty;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Partner;

import java.util.Date;
import java.util.Map;

public class ChannelManagerViewModel {

    private Long id;
    private String enName; //渠道英文名称
    private String name; //合作商
    private String channelName; //第三方渠道名称
    private String landingPage; //落地页
    private String quoteWay; //报价方式
    private String productLink; //生产环境链接
    private Boolean startOrDisable; //启用或禁用
    private Date createdTime; //创建时间
    private Date effTime; //生效时间
    private String remark; //备注
    private Map configuration; //配置信息
    private Boolean status; //状态
    private Boolean disabledChannel; //渠道禁用
    private Boolean singleCompany; //是否直投



    public static ChannelManagerViewModel createTocManagerViewModel(Channel channel, Partner partner, String generatedBaseUrl) {
        ChannelManagerViewModel model = new ChannelManagerViewModel();
        model.setId(channel.getId());
        model.setChannelName(channel.getDescription());
        model.setEnName(channel.getName());
        model.setProductLink(generatedBaseUrl);
        if (partner != null && partner.getName() != null) {
            model.setName(partner.getName());
        }
        return model;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    public String getQuoteWay() {
        return quoteWay;
    }

    public void setQuoteWay(String quoteWay) {
        this.quoteWay = quoteWay;
    }

    public String getProductLink() {
        return productLink;
    }

    public void setProductLink(String productLink) {
        this.productLink = productLink;
    }

    public Boolean getStartOrDisable() {
        return startOrDisable;
    }

    public void setStartOrDisable(Boolean startOrDisable) {
        this.startOrDisable = startOrDisable;
    }

    public Date getCreatedTime() { return createdTime; }

    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }

    public Date getEffTime() { return effTime; }

    public void setEffTime(Date effTime) { this.effTime = effTime;  }

    public String getRemark() { return remark;  }

    public void setRemark(String remark) { this.remark = remark; }

    public Map getConfiguration() { return configuration; }

    public void setConfiguration(Map configuration) { this.configuration = configuration; }

    public Boolean getStatus() { return status; }

    public void setStatus(Boolean status) { this.status = status; }
    public Boolean getDisabledChannel() {
        return disabledChannel;
    }

    public void setDisabledChannel(Boolean disabledChannel) {
        this.disabledChannel = disabledChannel;
    }

    public Boolean getSingleCompany() {
        return singleCompany;
    }

    public void setSingleCompany(Boolean singleCompany) {
        this.singleCompany = singleCompany;
    }
}
