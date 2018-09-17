package com.cheche365.cheche.operationcenter.web.model.thirdParty;

import com.cheche365.cheche.core.model.ApiPartner;
import com.cheche365.cheche.core.model.ApiPartnerProperties;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Partner;
import com.cheche365.cheche.operationcenter.web.model.thirdParty.mapToBean.ChannelPageConfigModel;
import com.cheche365.cheche.operationcenter.web.model.thirdParty.mapToBean.ParamsModel;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class TocDetailsViewModel {

    //渠道信息
    private Long id;
    private String partner; //合作商
    private Date createdTime; //创建时间
    private Boolean buttJoint; //对接车车
    private String remark; //备注


    //ParamsMap
    private String channelCode; //渠道英文名称
    private String channelName; //第三方渠道名称
    private Boolean agent; //是否ToA
    private Boolean singleCompany; //是否直投
    private Boolean levelAgent; //是否支持三级管理
    private Boolean rebateIntoWallet; //返点进钱包
    private Boolean disabledChannel; //渠道禁用
    private Boolean supportAmend;//是否支持增补

    //前端页面配置信息
    private Boolean reserve; //预约功能
    private Boolean supportPhoto; //拍照报价
    private Boolean showCustomService; //在线客服
    private String serviceTel; //客服电话
    private Boolean home; //落地页
    private Boolean homeFixBottom; //吸底按钮
    private Boolean showPartner; //合作伙伴

    //基本信息页配置
    private Boolean baseLogin; //手机号输入框
    private Boolean baseCustomAndPhoto; //基本信息页拍照报价
    private Boolean baseBanner; //顶部banner
    private Boolean baseOrder; //订单中心下拉框 订单中心
    private Boolean baseMine; //订单中心下拉框 个人中心
    private Boolean hasWallet; //个人中心是否展示钱包
    private Boolean cheWallet; //钱包提现时验证方式  true是验证码 false 是密码
    private Boolean orderGift; //优惠券模块
    private Boolean orderInsuredCar; // 投保车辆展示附加页

    //支付完成页配置
    private Boolean successOrder; //查看订单按钮
    private String orderUrl; //订单页链接地址
    private String homeUrl; //首页链接地址

    //参数配置
    private String googleTrackId; //Google统计位
    private String themeColor; //页面底色色值
    //后台配置
    private Boolean hasOrderCenter; //出单中心是否可下单  通用
    private Boolean isTelemarketing; //出单中心是否进电销

    //订单相关配置
    private Boolean needSyncOrder; //是否支持订单同步  通用
    private String syncOrderUrl; //第三方订单同步url  通用

    //提供给第三方的配置项
    private String signature; //签名方法

    //个人中心配置
    private Boolean showAgentLicense;//电子协议
    private Boolean showAgentReward;//奖励显示

    private String logoImage;


    /**
     * 设置toc详情页展示信息
     *
     * @param channel
     * @param partner
     * @param paramsModel
     * @param configModel
     * @param apiPartner
     * @return
     */
    public static TocDetailsViewModel createPageInfo(Channel channel, Partner partner, ParamsModel paramsModel,
                                                     ChannelPageConfigModel configModel, ApiPartner apiPartner,
                                                     ApiPartnerProperties properties, Boolean isTelemarketing) {
        TocDetailsViewModel model = new TocDetailsViewModel();
        //渠道
        if (null != channel) {
            if (channel.getDescription() != null) {
                model.setChannelName(channel.getDescription());
                model.setRemark(channel.getDescription());
            }
            if (channel.getId() != null) {
                model.setId(channel.getId());
            }
            if (channel.getCreateTime() != null) {
                model.setCreatedTime(channel.getCreateTime());
            }
        }
        //合作商详情
        if (null != apiPartner) {
            if (apiPartner.getCode() != null) {
                model.setChannelCode(apiPartner.getCode());
            }
        }
        //合作商
        if (null != partner) {
            if (partner.getName() != null) {
                model.setPartner(partner.getName());
            }
        }
        //数据是否进电销
        if (null != isTelemarketing) {
            model.setIsTelemarketing(isTelemarketing);
        }
        //渠道配置部分参数
        if (null != paramsModel) {
            if (paramsModel.getAgent() != null) {
                model.setButtJoint(paramsModel.getAgent());
            }
            if (paramsModel.getHasOrderCenter() != null) {
                model.setHasOrderCenter(paramsModel.getHasOrderCenter());
            }
            if (paramsModel.getNeedSyncOrder() != null) {
                model.setNeedSyncOrder(paramsModel.getNeedSyncOrder());
            }
            if (paramsModel.getSyncOrderUrl() != null) {
                model.setSyncOrderUrl(paramsModel.getSyncOrderUrl());
            }
            if (paramsModel.getDisabledChannel() != null) {
                model.setDisabledChannel(paramsModel.getDisabledChannel());
            }
            if (paramsModel.getLevelAgent() != null) {
                model.setLevelAgent(paramsModel.getLevelAgent());
            }
            if (paramsModel.getRebateIntoWallet() != null) {
                model.setRebateIntoWallet(paramsModel.getRebateIntoWallet());
            }
            if (paramsModel.getSupportAmend() != null) {
                model.setSupportAmend(paramsModel.getSupportAmend());
            }

        }
        //前端页面配置参数
        if (null != configModel) {
            if (configModel.getReserve() != null) {
                model.setReserve(configModel.getReserve());
            }
            if (configModel.getSupportPhoto() != null) {
                model.setSupportPhoto(configModel.getSupportPhoto());
            }
            if (configModel.getShowCustomService() != null) {
                model.setShowCustomService(configModel.getShowCustomService());
            }
            if (configModel.getServiceTel() != null) {
                model.setServiceTel(configModel.getServiceTel());
            }
            if (configModel.getHome() != null) {
                model.setHome(configModel.getHome());
            }
            if (configModel.getHomeFixBottom() != null) {
                model.setHomeFixBottom(configModel.getHomeFixBottom());
            }
            if (configModel.getShowPartner() != null) {
                model.setShowPartner(configModel.getShowPartner());
            }
            if (configModel.getBaseLogin() != null) {
                model.setBaseLogin(configModel.getBaseLogin());
            }
            if (configModel.getBaseCustomAndPhoto() != null) {
                model.setBaseCustomAndPhoto(configModel.getBaseCustomAndPhoto());
            }
            if (configModel.getBaseBanner() != null) {
                model.setBaseBanner(configModel.getBaseBanner());
            }
            if (configModel.getBaseOrder() != null) {
                model.setBaseOrder(configModel.getBaseOrder());
            }
            if (configModel.getBaseMine() != null) {
                model.setBaseMine(configModel.getBaseMine());
            }
            if (configModel.getHasWallet() != null) {
                model.setHasWallet(configModel.getHasWallet());
            }
            if (configModel.getCheWallet() != null) {
                model.setCheWallet(configModel.getCheWallet());
            }
            if (configModel.getOrderGift() != null) {
                model.setOrderGift(configModel.getOrderGift());
            }
            if (configModel.getOrderInsuredCar() != null) {
                model.setOrderInsuredCar(configModel.getOrderInsuredCar());
            }
            if (configModel.getSuccessOrder() != null) {
                model.setSuccessOrder(configModel.getSuccessOrder());
            }
            if (configModel.getOrderUrl() != null) {
                model.setOrderUrl(configModel.getOrderUrl());
            }
            if (configModel.getHomeUrl() != null) {
                model.setHomeUrl(configModel.getHomeUrl());
            }
            if (configModel.getGoogleTrackId() != null) {
                model.setGoogleTrackId(configModel.getGoogleTrackId());
            }
            if (configModel.getThemeColor() != null) {
                model.setThemeColor(configModel.getThemeColor());
            }
            if (configModel.getShowAgentLicense() != null) {
                model.setShowAgentLicense(configModel.getShowAgentLicense());
            }
            if (configModel.getShowAgentReward() != null) {
                model.setShowAgentReward(configModel.getShowAgentReward());
            }
            if(configModel.getSingleCompany() != null){
                model.setSingleCompany(configModel.getSingleCompany());
            }
        }
        model.setSignature("HMAC-SHA1");
//        if (properties.getValue() != null) {
//            model.setSignature(properties.getValue());
//        }
        return model;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Boolean getButtJoint() {
        return buttJoint;
    }

    public void setButtJoint(Boolean buttJoint) {
        this.buttJoint = buttJoint;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getReserve() {
        return reserve;
    }

    public void setReserve(Boolean reserve) {
        this.reserve = reserve;
    }

    public Boolean getSupportPhoto() {
        return supportPhoto;
    }

    public void setSupportPhoto(Boolean supportPhoto) {
        this.supportPhoto = supportPhoto;
    }

    public Boolean getShowCustomService() {
        return showCustomService;
    }

    public void setShowCustomService(Boolean showCustomService) {
        this.showCustomService = showCustomService;
    }

    public String getServiceTel() {
        return serviceTel;
    }

    public void setServiceTel(String serviceTel) {
        this.serviceTel = serviceTel;
    }

    public Boolean getHome() {
        return home;
    }

    public void setHome(Boolean home) {
        this.home = home;
    }

    public Boolean getHomeFixBottom() {
        return homeFixBottom;
    }

    public void setHomeFixBottom(Boolean homeFixBottom) {
        this.homeFixBottom = homeFixBottom;
    }

    public Boolean getShowPartner() {
        return showPartner;
    }

    public void setShowPartner(Boolean showPartner) {
        this.showPartner = showPartner;
    }

    public Boolean getBaseLogin() {
        return baseLogin;
    }

    public void setBaseLogin(Boolean baseLogin) {
        this.baseLogin = baseLogin;
    }

    public Boolean getBaseCustomAndPhoto() {
        return baseCustomAndPhoto;
    }

    public void setBaseCustomAndPhoto(Boolean baseCustomAndPhoto) {
        this.baseCustomAndPhoto = baseCustomAndPhoto;
    }

    public Boolean getBaseBanner() {
        return baseBanner;
    }

    public void setBaseBanner(Boolean baseBanner) {
        this.baseBanner = baseBanner;
    }

    public Boolean getBaseOrder() {
        return baseOrder;
    }

    public void setBaseOrder(Boolean baseOrder) {
        this.baseOrder = baseOrder;
    }

    public Boolean getBaseMine() {
        return baseMine;
    }

    public void setBaseMine(Boolean baseMine) {
        this.baseMine = baseMine;
    }

    public Boolean getCheWallet() {
        return cheWallet;
    }

    public void setCheWallet(Boolean cheWallet) {
        this.cheWallet = cheWallet;
    }

    public Boolean getOrderGift() {
        return orderGift;
    }

    public void setOrderGift(Boolean orderGift) {
        this.orderGift = orderGift;
    }

    public Boolean getOrderInsuredCar() {
        return orderInsuredCar;
    }

    public void setOrderInsuredCar(Boolean orderInsuredCar) {
        this.orderInsuredCar = orderInsuredCar;
    }

    public Boolean getSuccessOrder() {
        return successOrder;
    }

    public void setSuccessOrder(Boolean successOrder) {
        this.successOrder = successOrder;
    }

    public String getOrderUrl() {
        return orderUrl;
    }

    public void setOrderUrl(String orderUrl) {
        this.orderUrl = orderUrl;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public String getGoogleTrackId() {
        return googleTrackId;
    }

    public void setGoogleTrackId(String googleTrackId) {
        this.googleTrackId = googleTrackId;
    }

    public String getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }

    public Boolean getHasOrderCenter() {
        return hasOrderCenter;
    }

    public void setHasOrderCenter(Boolean hasOrderCenter) {
        this.hasOrderCenter = hasOrderCenter;
    }

    public Boolean getIsTelemarketing() {
        return isTelemarketing;
    }

    public void setIsTelemarketing(Boolean telemarketing) {
        isTelemarketing = telemarketing;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Boolean getHasWallet() {
        return hasWallet;
    }

    public void setHasWallet(Boolean hasWallet) {
        this.hasWallet = hasWallet;
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

    public Boolean getShowAgentLicense() {
        return showAgentLicense;
    }

    public void setShowAgentLicense(Boolean showAgentLicense) {
        this.showAgentLicense = showAgentLicense;
    }

    public Boolean getShowAgentReward() {
        return showAgentReward;
    }

    public void setShowAgentReward(Boolean showAgentReward) {
        this.showAgentReward = showAgentReward;
    }

    public Boolean getSupportAmend() { return supportAmend; }

    public void setSupportAmend(Boolean supportAmend) { this.supportAmend = supportAmend; }

    public String getLogoImage() { return logoImage; }

    public void setLogoImage(String logoImage) { this.logoImage = logoImage; }
}
