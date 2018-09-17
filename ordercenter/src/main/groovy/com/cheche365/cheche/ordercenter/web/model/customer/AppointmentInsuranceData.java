package com.cheche365.cheche.ordercenter.web.model.customer;

/**
 * Created by sunhuazhong on 2015/7/24.
 */
public class AppointmentInsuranceData {
    private Long id;
    private Integer sourceChannel;//用户来源
    private String licensePlateNo;//车牌号
    private String expireBefore;//车辆到期日期
    private String contact;//联系人
    private String mobile;//客户手机
    private String createTime;//用户提交时间
    private Integer status;//处理状态，1-未处理，2-已处理
    private String comment;//备注
    private String channelIcon;//渠道图标


    public Integer getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(Integer sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getExpireBefore() {
        return expireBefore;
    }

    public void setExpireBefore(String expireBefore) {
        this.expireBefore = expireBefore;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getChannelIcon() {
        return channelIcon;
    }

    public void setChannelIcon(String channelIcon) {
        this.channelIcon = channelIcon;
    }
}
