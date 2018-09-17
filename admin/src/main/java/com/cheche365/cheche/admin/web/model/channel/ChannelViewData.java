package com.cheche365.cheche.admin.web.model.channel;
import javax.validation.constraints.NotNull;
/**
 * Created by wangfei on 2015/5/22.
 */
public class ChannelViewData {
    private Long id;//channel id
    @NotNull
    private String name;//渠道名称
    @NotNull
    private String channelNo;//渠道编号
    @NotNull
    private Double rebate;//返点
    @NotNull
    private String wapUrl;//wapURL
    @NotNull
    private String startDate;//合作开始日期
    @NotNull
    private String endDate;//合作结束日期
    @NotNull
    private String linkMan;//联系人
    @NotNull
    private String mobile;//联系人手机号
    @NotNull
    private String email;//联系人邮箱
    @NotNull
    private Integer frequency;//发送频率，1-每周；2-每月
    @NotNull
    private boolean enable;//是否使用优惠券
    @NotNull
    private boolean display;//是否展示链接按钮
    private String createTime;//创建时间
    private String updateTime;//更新时间
    private String operator;//操作人
    private String description;//描述

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

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public Double getRebate() {
        return rebate;
    }

    public void setRebate(Double rebate) {
        this.rebate = rebate;
    }

    public String getWapUrl() {
        return wapUrl;
    }

    public void setWapUrl(String wapUrl) {
        this.wapUrl = wapUrl;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
        this .email = email;
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

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
