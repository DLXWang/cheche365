package com.cheche365.cheche.operationcenter.web.model.partner;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 商务活动
 * Created by sunhuazhong on 2015/8/26.
 */
public class BusinessActivityViewModel {
    private Long id;
    @NotNull
    private String name;//商务活动名称
    @NotNull
    private Long partner;//合作商
    private String partnerName;//合作商名称
    @NotNull
    private Long cooperationMode;//合作方式
    private String cooperationModeName;//合作方式名称
    private Double rebate;//佣金
    private Double budget;//预算
    @NotNull
    private String startTime;//活动开始时间，精确到分钟
    @NotNull
    private String endTime;//活动结束时间，精确到分钟
    private String status;//活动状态，未开始，进行中，已结束
    @NotNull
    private String landingPage;//落地页

    private String createTime;//创建时间
    private String updateTime;//修改时间
    private String operator;//操作人
    private String comment;//备注
    private String refreshTime;//数据更新时间
    private boolean refreshFlag;//是否可刷新标记

    private List<ActivityAreaViewModel> activityArea = new ArrayList<>();//活动支持的城市
    private List<CustomerFieldViewModel> customerField = new ArrayList<>();//自定义字段

    private String city;//活动支持的城市

    private ActivityMonitorDataViewModel lastMonitorData = new ActivityMonitorDataViewModel();//最新的数据更新时间监控数据
    private List<ActivityMonitorDataViewModel> monitorDataList = new ArrayList<>();//按时间区分的监控数据

    private String linkMan;//联系人
    private String mobile;//联系人手机号
    private String email;//联系人邮箱
    private Integer frequency = 1;//发送频率，1-每周；2-每月
    private boolean enable = false;//是否使用优惠券,true-使用，false-不使用
    private boolean display = false;//是否显示"回到首页"、"我的"等按钮 true-显示  false-不显示

    private String code;//活动编号

    private boolean footer = true;//是否显示底部公司标识，默认为false，true-显示，false-不显示
    private boolean btn = false;//成功提交订单后是否显示按钮，默认为false，true-显示，false-不显示
    private boolean app = false;//成功提交订单后是否显示公司微信二维码，默认为true，true-显示，false-不显示
    private List<PaymentChannelViewModel> paymentChannelList = new ArrayList<>();//活动支持的支付方式
    private String paymentChannels;//活动支持的支付方式

    private String marketingName;//关联Marketing名称
    private String objTable;//对象表名
    private String objId;//对象id
    private String beginDate;//推广活动开始日期
    private String endDate;//推广活动结束日期

    private Integer landingPageType;//落地页类型，1-M站首页；2-M站购买页；3-M端活动页；4-PC端活动页

    private boolean home = false;//是否可以回到首页，默认为true，true-可以，false-不可以
    private boolean mine = false;//是否可以显示我的，默认为true，true-显示，false-不显示

    /* M站首页配置项 */
    private boolean topBrand = true;//车车顶部品牌（涉及品牌）
    private boolean myCenter = true;//我的中心
    private boolean topCarousel = true;//顶部轮播图（涉及品牌）
    private boolean activityEntry = true;//活动入口
    private boolean ourCustomer = true;//我们的客户
    private boolean bottomCarousel = true;//底部轮播图（涉及品牌）
    private boolean bottomInfo = true;//底部信息（涉及品牌）
    private boolean bottomDownload = true;//底部下载（涉及品牌）

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPartner() {
        return partner;
    }

    public void setPartner(Long partner) {
        this.partner = partner;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public Long getCooperationMode() {
        return cooperationMode;
    }

    public void setCooperationMode(Long cooperationMode) {
        this.cooperationMode = cooperationMode;
    }

    public String getCooperationModeName() {
        return cooperationModeName;
    }

    public void setCooperationModeName(String cooperationModeName) {
        this.cooperationModeName = cooperationModeName;
    }

    public Double getRebate() {
        return rebate;
    }

    public void setRebate(Double rebate) {
        this.rebate = rebate;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(String refreshTime) {
        this.refreshTime = refreshTime;
    }

    public boolean isRefreshFlag() {
        return refreshFlag;
    }

    public void setRefreshFlag(boolean refreshFlag) {
        this.refreshFlag = refreshFlag;
    }

    public List<ActivityAreaViewModel> getActivityArea() {
        return activityArea;
    }

    public void setActivityArea(List<ActivityAreaViewModel> activityArea) {
        this.activityArea = activityArea;
    }

    public List<CustomerFieldViewModel> getCustomerField() {
        return customerField;
    }

    public void setCustomerField(List<CustomerFieldViewModel> customerField) {
        this.customerField = customerField;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ActivityMonitorDataViewModel getLastMonitorData() {
        return lastMonitorData;
    }

    public void setLastMonitorData(ActivityMonitorDataViewModel lastMonitorData) {
        this.lastMonitorData = lastMonitorData;
    }

    public List<ActivityMonitorDataViewModel> getMonitorDataList() {
        return monitorDataList;
    }

    public void setMonitorDataList(List<ActivityMonitorDataViewModel> monitorDataList) {
        this.monitorDataList = monitorDataList;
    }

    public String getLinkMan() {
        return linkMan;
    }

    public void setLinkMan(String linkMan) {
        this.linkMan = linkMan;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isHome() {
        return home;
    }

    public void setHome(boolean home) {
        this.home = home;
    }

    public boolean isFooter() {
        return footer;
    }

    public void setFooter(boolean footer) {
        this.footer = footer;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public boolean isBtn() {
        return btn;
    }

    public void setBtn(boolean btn) {
        this.btn = btn;
    }

    public boolean isApp() {
        return app;
    }

    public void setApp(boolean app) {
        this.app = app;
    }

    public List<PaymentChannelViewModel> getPaymentChannelList() {
        return paymentChannelList;
    }

    public void setPaymentChannelList(List<PaymentChannelViewModel> paymentChannelList) {
        this.paymentChannelList = paymentChannelList;
    }

    public String getPaymentChannels() {
        return paymentChannels;
    }

    public void setPaymentChannels(String paymentChannels) {
        this.paymentChannels = paymentChannels;
    }

    public String getMarketingName() {
        return marketingName;
    }

    public void setMarketingName(String marketingName) {
        this.marketingName = marketingName;
    }

    public String getObjTable() {
        return objTable;
    }

    public void setObjTable(String objTable) {
        this.objTable = objTable;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getLandingPageType() {
        return landingPageType;
    }

    public void setLandingPageType(Integer landingPageType) {
        this.landingPageType = landingPageType;
    }

    public boolean isTopBrand() {
        return topBrand;
    }

    public void setTopBrand(boolean topBrand) {
        this.topBrand = topBrand;
    }

    public boolean isMyCenter() {
        return myCenter;
    }

    public void setMyCenter(boolean myCenter) {
        this.myCenter = myCenter;
    }

    public boolean isTopCarousel() {
        return topCarousel;
    }

    public void setTopCarousel(boolean topCarousel) {
        this.topCarousel = topCarousel;
    }

    public boolean isActivityEntry() {
        return activityEntry;
    }

    public void setActivityEntry(boolean activityEntry) {
        this.activityEntry = activityEntry;
    }

    public boolean isOurCustomer() {
        return ourCustomer;
    }

    public void setOurCustomer(boolean ourCustomer) {
        this.ourCustomer = ourCustomer;
    }

    public boolean isBottomCarousel() {
        return bottomCarousel;
    }

    public void setBottomCarousel(boolean bottomCarousel) {
        this.bottomCarousel = bottomCarousel;
    }

    public boolean isBottomInfo() {
        return bottomInfo;
    }

    public void setBottomInfo(boolean bottomInfo) {
        this.bottomInfo = bottomInfo;
    }

    public boolean isBottomDownload() {
        return bottomDownload;
    }

    public void setBottomDownload(boolean bottomDownload) {
        this.bottomDownload = bottomDownload;
    }
}
