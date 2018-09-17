package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.constants.WebConstants
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.apache.commons.lang3.time.DateUtils

import javax.persistence.*
import javax.ws.rs.core.UriBuilder

/**
 * 商务活动
 * Created by sunhuazhong on 2015/8/25.
 */
@Entity
@JsonIgnoreProperties(value = ["partner", "paymentChannels"])
class BusinessActivity implements Serializable {
    private static final long serialVersionUID = 1L
    private Long id;
    private String name;//商务活动名称
    private Partner partner;//合作商
    private CooperationMode cooperationMode;//合作方式
    private Double rebate;//佣金
    private Double budget;//预算
    private Date startTime;//活动开始时间
    private Date endTime;//活动结束时间
    private String landingPage="";//落地页
    private String originalUrl; //原始链接
    private String comment;//备注
    private Date createTime;//创建时间
    private Date updateTime;//修改时间
    private InternalUser operator;//操作人
    private Date refreshTime;//数据更新时间
    private boolean refreshFlag = true;//是否可刷新标记，默认为false

    private String linkMan;//联系人
    private String mobile;//联系人手机号
    private String email;//联系人邮箱
    private Integer frequency = 1;//发送频率，1-每周；2-每月，3-不发送
    private boolean enable = false;//是否使用优惠券,默认为false，true-使用，false-不使用
    private boolean display = false;//是否显示"回到首页"、"我的"等按钮，默认为false，true-显示，false-不显示
    private boolean home = false;//是否可以回到首页，默认为false，true-可以，false-不可以
    private boolean mine = false;//是否可以显示我的，默认为false，true-显示，false-不显示
    private boolean footer = true;//是否显示底部公司标识，默认为true，true-显示，false-不显示
    private boolean btn = false;//成功提交订单后是否显示按钮，默认为false，true-显示，false-不显示
    private boolean app = false;//成功提交订单后是否显示公司微信二维码，默认为false，true-显示，false-不显示
    private List<PaymentChannel> paymentChannels;//支持的支付方式

    private String code;//商务活动编号

    private String objTable;//对象表名
    private String objId;//对象id

    private Integer landingPageType;//落地页类型，1-M站首页；2-M站购买页；3-M端活动页；4-PC端活动页

    /* M站首页配置项 */
    private boolean topBrand = true;//车车顶部品牌（涉及品牌）
    private boolean myCenter = true;//我的中心
    private boolean topCarousel = true;//顶部轮播图（涉及品牌）
    private boolean activityEntry = true;//活动入口
    private boolean ourCustomer = true;//我们的客户
    private boolean bottomCarousel = true;//底部轮播图（涉及品牌）
    private boolean bottomInfo = true;//底部信息（涉及品牌）
    private boolean bottomDownload = true;//底部下载（涉及品牌）

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "partner", foreignKey=@ForeignKey(name="FK_BUSINESS_ACTIVITY_REF_PARTNER", foreignKeyDefinition="FOREIGN KEY (partner) REFERENCES partner(id)"))
    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    @ManyToOne
    @JoinColumn(name = "cooperationMode", foreignKey=@ForeignKey(name="FK_BUSINESS_ACTIVITY_REF_COOPERATION_MODE", foreignKeyDefinition="FOREIGN KEY (cooperation_mode) REFERENCES cooperation_mode(id)"))
    public CooperationMode getCooperationMode() {
        return cooperationMode;
    }

    public void setCooperationMode(CooperationMode cooperationMode) {
        this.cooperationMode = cooperationMode;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getRebate() {
        return rebate;
    }

    public void setRebate(Double rebate) {
        this.rebate = rebate;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    @Column(columnDefinition = "varchar(100)")
    public String getLandingPage() {
        return landingPage;
    }
    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }


    @Column(columnDefinition = "varchar(100)")
    public String getOriginalUrl() {
        return originalUrl;
    }

    void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_BUSINESS_ACTIVITY_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(Date refreshTime) {
        this.refreshTime = refreshTime;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isRefreshFlag() {
        return refreshFlag;
    }

    public void setRefreshFlag(boolean refreshFlag) {
        this.refreshFlag = refreshFlag;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getLinkMan() {
        return linkMan;
    }

    public void setLinkMan(String linkMan) {
        this.linkMan = linkMan;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(columnDefinition = "varchar(200)")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isHome() {
        return home;
    }

    public void setHome(boolean home) {
        this.home = home;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isFooter() {
        return footer;
    }

    public void setFooter(boolean footer) {
        this.footer = footer;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isBtn() {
        return btn;
    }

    public void setBtn(boolean btn) {
        this.btn = btn;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isApp() {
        return app;
    }

    public void setApp(boolean app) {
        this.app = app;
    }

    @ManyToMany
    @JoinTable(name="activity_payment_channel",
        inverseJoinColumns =  @JoinColumn (name =  "payment_channel"),
        joinColumns =  @JoinColumn (name =  "business_activity" ))
    public List<PaymentChannel> getPaymentChannels() {
        return paymentChannels;
    }

    public void setPaymentChannels(List<PaymentChannel> paymentChannels) {
        this.paymentChannels = paymentChannels;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getObjTable() {
        return objTable;
    }

    public void setObjTable(String objTable) {
        this.objTable = objTable;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Integer getLandingPageType() {
        return landingPageType;
    }

    public void setLandingPageType(Integer landingPageType) {
        this.landingPageType = landingPageType;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isTopBrand() {
        return topBrand;
    }

    public void setTopBrand(boolean topBrand) {
        this.topBrand = topBrand;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isMyCenter() {
        return myCenter;
    }

    public void setMyCenter(boolean myCenter) {
        this.myCenter = myCenter;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isTopCarousel() {
        return topCarousel;
    }

    public void setTopCarousel(boolean topCarousel) {
        this.topCarousel = topCarousel;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isActivityEntry() {
        return activityEntry;
    }

    public void setActivityEntry(boolean activityEntry) {
        this.activityEntry = activityEntry;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isOurCustomer() {
        return ourCustomer;
    }

    public void setOurCustomer(boolean ourCustomer) {
        this.ourCustomer = ourCustomer;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isBottomCarousel() {
        return bottomCarousel;
    }

    public void setBottomCarousel(boolean bottomCarousel) {
        this.bottomCarousel = bottomCarousel;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isBottomInfo() {
        return bottomInfo;
    }

    public void setBottomInfo(boolean bottomInfo) {
        this.bottomInfo = bottomInfo;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isBottomDownload() {
        return bottomDownload;
    }

    public void setBottomDownload(boolean bottomDownload) {
        this.bottomDownload = bottomDownload;
    }

    public boolean checkActivityDate() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        Date beginDate, endDate;
        beginDate = this.getStartTime();
        endDate = DateUtils.addDays(this.getEndTime(), 1);
        if (!(now.after(beginDate) && now.before(endDate))) {
            return false;
        }
        return true;
    }

    @Transient
    String assembleCpsUrl(String uriStr) {
        def uri= uriStr.trim().toURI()
        "/"+UriBuilder
                .fromUri(uri)
                .replacePath([WebConstants.WEB_DEFAULT_CPS_MARK,this.getCode()].join('/'))
                .queryParam("utm",true)
                .build().toString()
    }

    @Transient
    public String assembleCpsUrl() {
        if (this.getLandingPage().contains("/m/channel/")) {
            String tempLandingPage = this.getLandingPage();
            return tempLandingPage.replace("/m/channel/" + this.getCode(), "/m/index.html?cps=" + this.getCode());
        } else {
            return this.getLandingPage();
        }
    }

    @Transient
    public String assembleWebMarketingUrl() {
        boolean isWebUrl = this.getLandingPage().contains("/marketing/web/");
        boolean lowerUrlRule = this.getLandingPage().contains("/p1/") || this.getLandingPage().contains("/p2/") || this.getLandingPage().contains("/p3/");
        String url = this.getLandingPage().substring(0, this.getLandingPage().lastIndexOf("/"));

        if (isWebUrl && lowerUrlRule) {
            return url.replace("/marketing/web/", "/web/c/") + ".html";
        } else if (isWebUrl && !lowerUrlRule) {
            return url + "/index.html";
        } else {
            return this.getLandingPage();
        }
    }

    @Transient
    public String assembleWebHomeUrl() {
        if (this.getLandingPage().contains("/web/channel/")) {
            String tempLandingPage = this.getLandingPage();
            return tempLandingPage.replace("/web/channel/" + this.getCode(), WebConstants.WEB_ROOT_PATH + "?cps=" + this.getCode());
        } else {
            return this.getLandingPage();
        }
    }

    Map constructionSessionMap(BusinessActivity cpsBusinessActivity){
        def baMap=["id":cpsBusinessActivity.getId(),"startTime":cpsBusinessActivity.getStartTime(),"endTime":cpsBusinessActivity.getEndTime(),"enable":cpsBusinessActivity.isEnable()]
    }
}
