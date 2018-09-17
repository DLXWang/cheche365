package com.cheche365.cheche.operationcenter.web.model.appointmentInsurance;

/**
 * Created by zhangshitao on 2015/10/31.
 */
public class AppointmentInsuranceViewModel
{

    private Long id;
    private Long user;//预约用户id
    private String name;//用户名
    private String mobile;
    private String licensePlateNo;//车牌号
    private String expireBefore;//车险到期日期
    private String createTime;//提交时间
    private String count;//购买单数
    private String totalMoney;//总金额
    private Long qrCodeChannelId;//二维码渠道ID
    private String qrCodeChannelCode;//二维码渠道号
    private String qrCodeChannelName;//二维码渠道名
    private String actionName;//二维码渠道类型
    private String status;//处理状态，1-未处理，2-已处理
    private String comment;//备注

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getExpireBefore() {
        return expireBefore;
    }

    public void setExpireBefore(String expireBefore) {
        this.expireBefore = expireBefore;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Long getQrCodeChannelId() {
        return qrCodeChannelId;
    }

    public void setQrCodeChannelId(Long qrCodeChannelId) {
        this.qrCodeChannelId = qrCodeChannelId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getQrCodeChannelCode() {
        return qrCodeChannelCode;
    }

    public void setQrCodeChannelCode(String qrCodeChannelCode) {
        this.qrCodeChannelCode = qrCodeChannelCode;
    }

    public String getQrCodeChannelName() {
        return qrCodeChannelName;
    }

    public void setQrCodeChannelName(String qrCodeChannelName) {
        this.qrCodeChannelName = qrCodeChannelName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
