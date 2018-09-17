package com.cheche365.cheche.operationcenter.web.model.thirdParty.mapToBean;

public class ChannelPageConfigModel {

    //前端页面配置信息
    private Boolean reserve; //预约功能
    private Boolean supportPhoto; //拍照报价
    private Boolean showCustomService; //在线客服
    private String  serviceTel; //客服电话
    private Boolean home; //落地页
    private Boolean homeFixBottom; //吸底按钮
    private Boolean showPartner; //合作伙伴
    private Boolean singleCompany;//报价方式

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

    private Boolean showAgentLicense; //电子协议
    private Boolean showAgentReward; //奖励显示


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

    public Boolean getHasWallet() {
        return hasWallet;
    }

    public void setHasWallet(Boolean hasWallet) {
        this.hasWallet = hasWallet;
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

    public Boolean getSingleCompany() {
        return singleCompany;
    }

    public void setSingleCompany(Boolean singleCompany) {
        this.singleCompany = singleCompany;
    }

}
