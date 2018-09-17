package com.cheche365.cheche.operationcenter.web.model.marketing;

import com.alibaba.common.lang.StringUtil;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.MarketingRule;
import com.cheche365.cheche.core.model.MarketingShared;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiangyin on 2016/8/1.
 */
public class MarketingRuleViewData {
    private Long id;//活动id
    private String title;//主标题
    private String subTitle;//副标题
    private String description;//活动政策
    private String userGuide;//使用规则
    private String activityType;//活动类别
    private String activityTypeInfo;//多选框
    private String channelId;
    private String insuranceCompanyId;
    private String areaId;
    private String channel;//活动平台
    private String insuranceCompany;//活动保险公司
    private String area;//活动支持城市
    private String insuranceMust;//需购买哪几种险种才能享受优惠
    private String fullIncludes;//满额包含的险种

    private Long status;//状态
    private String statusDesc;//状态
    private String createTime;
    private String updateTime;
    private String effectiveDate;//生效时间
    private String expireDate;//失效时间
    private List<DiscountByMoney> discountByMoneyList;//满减
    private List<Present> extraPresentList;//其他礼物
    private List<Present> presentList;//满送礼物
    private Integer isAccumulate;//是否累计
    private Double notMoreThan;//不超过
    private MarketingShared marketingShared;
    private List<Integer> areaList;
    private DiscountByInsurance discountByInsurance;
    private List<String> activityInfo;
    private String topImage;
    private Integer version;
    private DiscountGift discountGift;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserGuide() {  return userGuide; }

    public void setUserGuide(String userGuide) { this.userGuide = userGuide;  }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityTypeInfo() { return activityTypeInfo;   }

    public void setActivityTypeInfo(String activityTypeInfo) { this.activityTypeInfo = activityTypeInfo;  }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
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

    public String getInsuranceMust() {
        return insuranceMust;
    }

    public void setInsuranceMust(String insuranceMust) {
        this.insuranceMust = insuranceMust;
    }

    public String getFullIncludes() {
        return fullIncludes;
    }

    public void setFullIncludes(String fullIncludes) {
        this.fullIncludes = fullIncludes;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
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

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public static MarketingRuleViewData createViewModel(MarketingRule marketingRule){
        MarketingRuleViewData marketingRuleViewData = new MarketingRuleViewData();
        marketingRuleViewData.setId(marketingRule.getId());
        marketingRuleViewData.setTitle(marketingRule.getTitle());
        marketingRuleViewData.setSubTitle(marketingRule.getSubTitle());
        marketingRuleViewData.setDescription(StringUtils.substringBefore(marketingRule.getDescription(),"|"));
        marketingRuleViewData.setUserGuide(StringUtils.substringAfter(marketingRule.getDescription(),"|"));
        marketingRuleViewData.setActivityType(marketingRule.getActivityType().getId().toString());
        marketingRuleViewData.setActivityTypeInfo(marketingRule.getActivityType().getDescription());
        marketingRuleViewData.setChannel(marketingRule.getChannel().getDescription());
        marketingRuleViewData.setInsuranceCompany(marketingRule.getInsuranceCompany().getName());
        marketingRuleViewData.setArea(marketingRule.getArea().getName());
        marketingRuleViewData.setStatusDesc(marketingRule.getStatus().getDescription());
        marketingRuleViewData.setCreateTime(DateUtils.getDateString(marketingRule.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        marketingRuleViewData.setUpdateTime(DateUtils.getDateString(marketingRule.getUpdate_time(), DateUtils.DATE_LONGTIME24_PATTERN));
        marketingRuleViewData.setEffectiveDate(DateUtils.getDateString(marketingRule.getEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        marketingRuleViewData.setExpireDate(DateUtils.getDateString(marketingRule.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        marketingRuleViewData.setTopImage(marketingRule.getTopImage());
        return marketingRuleViewData;
    }

    public String getStatusDesc() { return statusDesc; }

    public void setStatusDesc(String statusDesc) {  this.statusDesc = statusDesc; }

    public List<DiscountByMoney> getDiscountByMoneyList() {
        return discountByMoneyList;
    }

    public void setDiscountByMoneyList(List<DiscountByMoney> discountByMoneyList) {
        this.discountByMoneyList = discountByMoneyList;
    }

    public List<Present> getExtraPresentList() {
        return extraPresentList;
    }

    public void setExtraPresentList(List<Present> extraPresentList) {
        this.extraPresentList = extraPresentList;
    }

    public void setExtraPresentList(String ruleValue){
        this.extraPresentList = convertToPresentList(ruleValue);
    }

    public List<Present> getPresentList() {
        return presentList;
    }

    public void setPresentList(List<Present> presentList) {
        this.presentList = presentList;
    }

    public void setPresentList(String ruleValue){
        this.presentList = convertToPresentList(ruleValue);
    }

    //其他礼物拆解
    private List<Present> convertToPresentList(String ruleValue){
        List<Present> extraPresentList = new ArrayList<>();
        String[] ruleList = StringUtil.split(ruleValue,"&&");
        for(String rule:ruleList){
            Present present = new Present();
            String full = StringUtils.substringBefore(rule,"_");
            String discount = StringUtils.substringAfter(rule,"|");
            String giftIdStr = StringUtils.substringBetween(rule,"_","|");
            if(giftIdStr.equals("null")){
                continue;
            }
            Long giftId = Long.parseLong(StringUtils.substringBetween(rule,"_","|"));
            present.setFull(Double.parseDouble(full));
            present.setDiscount(Double.parseDouble(discount));
            present.setPresent(giftId);
            extraPresentList.add(present);
        }
        return extraPresentList;
    }

    public Integer getIsAccumulate() {
        return isAccumulate;
    }

    public void setIsAccumulate(Integer isAccumulate) {
        this.isAccumulate = isAccumulate;
    }

    public Double getNotMoreThan() {
        return notMoreThan;
    }

    public void setNotMoreThan(Double notMoreThan) {  this.notMoreThan = notMoreThan;   }

    public MarketingShared getMarketingShared() {  return marketingShared;  }

    public void setMarketingShared(MarketingShared marketingShared) { this.marketingShared = marketingShared; }

    public List<Integer> getAreaList() {  return areaList; }

    public void setAreaList(List<Integer> areaList) {  this.areaList = areaList; }

    public DiscountByInsurance getDiscountByInsurance() {  return discountByInsurance;  }

    public void setDiscountByInsurance(DiscountByInsurance discountByInsurance) {  this.discountByInsurance = discountByInsurance;  }

    public List<String> getActivityInfo() {  return activityInfo; }

    public void setActivityInfo(List<String> activityInfo) {  this.activityInfo = activityInfo;  }

    public String getTopImage() {  return topImage; }

    public void setTopImage(String topImage) {  this.topImage = topImage; }

    public Integer getVersion() { return version;  }

    public void setVersion(Integer version) {  this.version = version;  }

    public String getChannelId() {  return channelId;  }

    public void setChannelId(String channelId) {  this.channelId = channelId;  }

    public String getAreaId() {   return areaId;  }

    public void setAreaId(String areaId) {  this.areaId = areaId; }

    public String getInsuranceCompanyId() {  return insuranceCompanyId;  }

    public void setInsuranceCompanyId(String insuranceCompanyId) {  this.insuranceCompanyId = insuranceCompanyId; }

    public DiscountGift getDiscountGift() {
        return discountGift;
    }

    public void setDiscountGift(DiscountGift discountGift) {
        this.discountGift = discountGift;
    }

}
