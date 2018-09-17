package com.cheche365.cheche.scheduletask.model;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.model.CustomerField;
import com.cheche365.cheche.web.util.UrlUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/12/4.
 */
public class BusinessActivityInfo {
    private Long id;
    private String name;//商务活动名称
    private String partnerName;//合作商名称
    private String cooperationModeName;//合作方式名称
    private String city;//活动支持的城市
    private String rebate;//佣金
    private String budget;//预算
    private String startTime;//活动开始时间，精确到分钟
    private String endTime;//活动结束时间，精确到分钟
    private String landingPage;//落地页
    private String comment;//备注
    private String refreshTime;//数据更新时间
    private ActivityMonitorDataInfo sumMonitorData;//汇总监控数
    private List<ActivityMonitorDataInfo> monitorDataList = new ArrayList<>();//按时间区分的监控数据
    private List<CustomerField> customerFieldList = new ArrayList<>();//自定义字段

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

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getCooperationModeName() {
        return cooperationModeName;
    }

    public void setCooperationModeName(String cooperationModeName) {
        this.cooperationModeName = cooperationModeName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRebate() {
        return rebate;
    }

    public void setRebate(String rebate) {
        this.rebate = rebate;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
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

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
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

    public List<ActivityMonitorDataInfo> getMonitorDataList() {
        return monitorDataList;
    }

    public void setMonitorDataList(List<ActivityMonitorDataInfo> monitorDataList) {
        this.monitorDataList = monitorDataList;
    }

    public List<CustomerField> getCustomerFieldList() {
        return customerFieldList;
    }

    public void setCustomerFieldList(List<CustomerField> customerFieldList) {
        this.customerFieldList = customerFieldList;
    }

    public ActivityMonitorDataInfo getSumMonitorData() {
        return sumMonitorData;
    }

    public void setSumMonitorData(ActivityMonitorDataInfo sumMonitorData) {
        this.sumMonitorData = sumMonitorData;
    }

    public static BusinessActivityInfo createViewModel(BusinessActivity businessActivity) {
        BusinessActivityInfo businessActivityInfo = new BusinessActivityInfo();
        businessActivityInfo.setId(businessActivity.getId());
        businessActivityInfo.setName(businessActivity.getName());//商务活动名称
        businessActivityInfo.setPartnerName(businessActivity.getPartner().getName());//合作商名称
        businessActivityInfo.setCooperationModeName(businessActivity.getCooperationMode().getName());//合作方式名称
        businessActivityInfo.setRebate(businessActivity.getRebate()==null?"":String.valueOf(businessActivity.getRebate()));//佣金
        businessActivityInfo.setBudget(businessActivity.getBudget()==null?"":String.valueOf(businessActivity.getBudget()));//预算
        businessActivityInfo.setLandingPage(UrlUtil.toFullUrl(businessActivity.getLandingPage()));//落地页
        businessActivityInfo.setComment(businessActivity.getComment());//备注
        businessActivityInfo.setStartTime(
                DateUtils.getDateString(businessActivity.getStartTime(), "yyyy-MM-dd HH:mm"));//活动开始时间，精确到分钟
        businessActivityInfo.setEndTime(
                DateUtils.getDateString(businessActivity.getEndTime(), "yyyy-MM-dd HH:mm"));//活动结束时间，精确到分钟
        businessActivityInfo.setRefreshTime(businessActivity.getRefreshTime() == null ?
                "" : DateUtils.getDateString(businessActivity.getRefreshTime(), DateUtils.DATE_LONGTIME24_PATTERN));//数据更新时间
        return businessActivityInfo;
    }
}
