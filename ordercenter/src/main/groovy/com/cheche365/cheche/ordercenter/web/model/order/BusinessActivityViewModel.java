package com.cheche365.cheche.ordercenter.web.model.order;

import javax.validation.constraints.NotNull;

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
    @NotNull
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

    private String city;//活动支持的城市

    private String linkMan;//联系人
    private String mobile;//联系人手机号
    private String email;//联系人邮箱
    private Integer frequency = 1;//发送频率，1-每周；2-每月
    private boolean enable = false;//是否使用优惠券,true-使用，false-不使用
    private boolean display = false;//是否显示"回到首页"、"我的"等按钮 true-显示  false-不显示

    private String code;//活动编号

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
}
