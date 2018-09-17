package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.constants.WebConstants
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.*

import static com.cheche365.cheche.core.constants.TagConstants.MARKETING_TAGS

@Entity
@JsonIgnoreProperties(ignoreUnknown = false)
class Marketing extends DescribableEntity {
    private static final long serialVersionUID = 1L

    private String name;
    private String code;
    private Date beginDate;//活动开始时间
    private Date endDate;//活动结束时间
    private String channel;//渠道

    private String amount;//优惠券金额

    private String giftExpireDate;//gift过期时间
    private String fullLimit;//gift做满减条件计算
    private String giftClass;//写gift数据的处理类
    private String marketingType;//M端，Web端

    private Long tag; //渠道标志

    // Transient Fields
    private List<Map<String, String>> inputParam;
    private Boolean involved;

    private String shortName;

    private String marketingService;
    private ActivityType activityType;

    private String detailUrl;


    @Column(columnDefinition = "int(1)")
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getGiftClass() {
        return giftClass;
    }

    public void setGiftClass(String giftClass) {
        this.giftClass = giftClass;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    @Column(columnDefinition = "VARCHAR(100)")
    public String getGiftExpireDate() {
        return giftExpireDate;
    }

    public void setGiftExpireDate(String giftExpireDate) {
        this.giftExpireDate = giftExpireDate;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(9)")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Column(columnDefinition = "VARCHAR(500)")
    public String getFullLimit() {
        return fullLimit;
    }

    public void setFullLimit(String fullLimit) {
        this.fullLimit = fullLimit;
    }

    @Column(columnDefinition = "VARCHAR(10)")
    public String getMarketingType() {
        return marketingType;
    }

    public void setMarketingType(String marketingType) {
        this.marketingType = marketingType;
    }

    @Column
    public Long getTag() {
        return tag;
    }

    public void setTag(Long tag) {
        this.tag = tag;
    }

    public void setInputParam(List<Map<String, String>> inputParam) {
        this.inputParam = inputParam;
    }

    @Transient
    public Boolean getInvolved() {
        return involved;
    }

    public void setInvolved(Boolean involved) {
        this.involved = involved;
    }

    @Column
    public String getMarketingService() {
        return marketingService;
    }

    public void setMarketingService(String marketingService) {
        this.marketingService = marketingService;
    }

    @ManyToOne
    @JoinColumn(name = "activity_type", foreignKey = @ForeignKey(name = "FK_MARKETING_REF_ACTIVITY_TYPE", foreignKeyDefinition = "FOREIGN KEY (activity_type) REFERENCES activity_type(id)"))
    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }


    @Transient
    public String getDetailUrl() {
        return detailUrl;
    }

    @Transient
    public List<Channel> getChannelObj() {

        List<Channel> result = new ArrayList<>();
        if(null != this.getChannel()){
            for(String channelId : this.getChannel().split(";")){
                result.add(Channel.toChannel(Long.valueOf(channelId)));
            }
        }

        return result;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }


    public static Long generateCommonVirtualGiftId(User user) {
        return Long.valueOf(WebConstants.COMMON_MARKETING_ID + user.getId());
    }

    public static Boolean isCommonMarketing(Long giftId, User user) {
        return giftId != null && user != null && giftId.equals(generateCommonVirtualGiftId(user));
    }

    boolean attendWithoutLogin(){
        this.tag && (MARKETING_TAGS.ATTEND_WITHOUT_LOGIN.mask & this.tag)
    }

    boolean multiAttend(){
        this.tag && (MARKETING_TAGS.MULTI_ATTEND.mask & this.tag)
    }

    boolean withUUID(){
        this.tag && (MARKETING_TAGS.WITH_UUID.mask & this.tag)
    }

    boolean needWechatOAuth(){
        this.tag && (MARKETING_TAGS.NEED_WECHAT_OAUTH.mask & this.tag)
    }

    boolean attendCreateUser(){
        this.tag && (MARKETING_TAGS.ATTEND_CREATE_USER.mask & this.tag)
    }

}
