package com.cheche365.cheche.core.model;

import javax.persistence.*;

/**
 * Created by xu.yelong on 2016/8/17.
 * 活动分享规则
 */
@Entity
public class MarketingShared{
    private Long id;
    private MarketingRule marketingRule; //活动规则ID
    private Boolean wechatShared; //是否微信分享
    private String wechatMainTitle;//微信分项主标题
    private String wechatSubTitle;//微信分享副标题
    private Boolean alipayShared;//是否支付宝分享
    private String alipayMainTitle;//支付宝分享主标题
    private String alipaySubTitle;//支付宝分享副标题
    private String sharedIcon;//分享图标

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(name = "marketing_rule", foreignKey = @ForeignKey(name = "FK_MARKETING_SHARED_REF_MARKETING_RULE", foreignKeyDefinition = "FOREIGN KEY (marketing_rule) REFERENCES channel(id)"))
    public MarketingRule getMarketingRule() {
        return marketingRule;
    }

    public void setMarketingRule(MarketingRule marketingRule) {
        this.marketingRule = marketingRule;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getWechatShared() {
        return wechatShared;
    }

    public void setWechatShared(Boolean wechatShared) {
        this.wechatShared = wechatShared;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getWechatMainTitle() {
        return wechatMainTitle;
    }

    public void setWechatMainTitle(String wechatMainTitle) {
        this.wechatMainTitle = wechatMainTitle;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getWechatSubTitle() {
        return wechatSubTitle;
    }

    public void setWechatSubTitle(String wechatSubTitle) {
        this.wechatSubTitle = wechatSubTitle;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getAlipayShared() {
        return alipayShared;
    }

    public void setAlipayShared(Boolean alipayShared) {
        this.alipayShared = alipayShared;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getAlipayMainTitle() {
        return alipayMainTitle;
    }

    public void setAlipayMainTitle(String alipayMainTitle) {
        this.alipayMainTitle = alipayMainTitle;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getAlipaySubTitle() {
        return alipaySubTitle;
    }

    public void setAlipaySubTitle(String alipaySubTitle) {
        this.alipaySubTitle = alipaySubTitle;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getSharedIcon() {
        return sharedIcon;
    }

    public void setSharedIcon(String sharedIcon) {
        this.sharedIcon = sharedIcon;
    }
}
