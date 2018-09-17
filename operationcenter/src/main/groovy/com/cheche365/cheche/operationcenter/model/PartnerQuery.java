package com.cheche365.cheche.operationcenter.model;

import com.cheche365.cheche.manage.common.model.PublicQuery;

/**
 * Created by chenxy on 2018/4/21.
 */
public class PartnerQuery extends PublicQuery {
    private String channel;//第三方渠道名称
    private String partner;//合作商
    private String operator;
    private String status;
    private String apiPartner;//渠道英文名称
    private PartnerType partnerType;// ToA/ToC
    private String landingPage;
    private String quoteWay;//报价方式

    private Long partnerId;
    private String logs;
    private Long id;
    //添加修改时需要
    private Boolean customer;//电话客服&在线客服//
    private Boolean thirdPartyManage;//是否支持三级管理
    private Boolean elecAgreement;//电子协议//
    private Boolean wallet;//我的钱包 //
    private Boolean award;//奖励显示//
    private Boolean isOrder;//出单中心是否可下单

    private Boolean isOrderCenter;//数据是否进电销
    private Boolean synchro;//是否支持订单同步//
    private String address;//第三方提供的同步订单的地址//
    private Boolean supplement;//是否支持增补

    //前端页面配置信息
    private Boolean reserve; //预约功能
    private Boolean supportPhoto; //拍照报价
    private Boolean showCustomService; //在线客服
    private String serviceTel; //客服电话
    private Boolean home; //落地页//
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


    private Boolean isAdd = false;//是否是添加 否是修改

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }


    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApiPartner() {
        return apiPartner;
    }

    public void setApiPartner(String apiPartner) {
        this.apiPartner = apiPartner;
    }

    public PartnerType getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(PartnerType partnerType) {
        this.partnerType = partnerType;
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

    public Boolean getCustomer() {
        return customer;
    }

    public void setCustomer(Boolean customer) {
        this.customer = customer;
    }

    public Boolean getThirdPartyManage() {
        return thirdPartyManage;
    }

    public void setThirdPartyManage(Boolean thirdPartyManage) {
        this.thirdPartyManage = thirdPartyManage;
    }

    public Boolean getElecAgreement() {
        return elecAgreement;
    }

    public void setElecAgreement(Boolean elecAgreement) {
        this.elecAgreement = elecAgreement;
    }

    public Boolean getAward() {
        return award;
    }

    public void setAward(Boolean award) {
        this.award = award;
    }

    public Boolean getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(Boolean order) {
        isOrder = order;
    }

    public Boolean getSynchro() {
        return synchro;
    }

    public void setSynchro(Boolean synchro) {
        this.synchro = synchro;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getWallet() {
        return wallet;
    }

    public void setWallet(Boolean wallet) {
        this.wallet = wallet;
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

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Boolean getAdd() {
        return isAdd;
    }

    public Boolean getSupplement() { return supplement; }

    public void setSupplement(Boolean supplement) { this.supplement = supplement; }

    public void setAdd(Boolean add) {
        isAdd = add;
    }

    public Boolean getIsOrderCenter() {
        return isOrderCenter;
    }

    public void setIsOrderCenter(Boolean orderCenter) {
        isOrderCenter = orderCenter;
    }
    public enum PartnerType {
        TOA(1, "ToA"),
        TOC(2, "ToC");
        private Integer index;
        private String name;

        PartnerType(Integer index, String name) {
            this.index = index;
            this.name = name;
        }

        public Integer getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

    }

    public enum QuoteType {
        MULTI(1, "比价"),
        SINGLE(2, "直投");
        private Integer index;
        private String name;

        QuoteType(Integer index, String name) {
            this.index = index;
            this.name = name;
        }

        public Integer getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }

    public enum State {
        ENABLE(1, "上线"),
        DISABLE(0, "下线");
        private Integer index;
        private String name;

        State(Integer index, String name) {
            this.index = index;
            this.name = name;
        }

        public Integer getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

    }
}
