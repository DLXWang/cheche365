package com.cheche365.cheche.operationcenter.web.model.red;

/**
 * Created by guoweifu on 2015/10/26.
 */
public class UserRedPacketViewModel {

    private Long id;
    private String user;   //对应用户名字
    private String mobile;//用户手机号
    private String nickName;//微信昵称
    private String openId;//微信的id
    private String licensePlateNo;  //车牌号
    private Integer status;//审核状态  默认为1
    private String operateDate;  //用户预约成功的日期

    private String quotePhotoCteateDate;//提交时间
    private Long quotePhotoId;//拍照信息序号
    private Integer smsFlag;//短信发送状态
    private String smsResult;//短信发送结果
    private Integer redFlag;//微信红包发放状态
    private String redResult;//微信红包发放结果

    private Boolean isSatisfied; //是否满足发红包的要求   false:不满足   true:满足
    private String description;//不能发送红包的原因

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
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

    public String getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(String operateDate) {
        this.operateDate = operateDate;
    }

    public String getQuotePhotoCteateDate() {
        return quotePhotoCteateDate;
    }

    public void setQuotePhotoCteateDate(String quotePhotoCteateDate) {
        this.quotePhotoCteateDate = quotePhotoCteateDate;
    }

    public Long getQuotePhotoId() {
        return quotePhotoId;
    }

    public void setQuotePhotoId(Long quotePhotoId) {
        this.quotePhotoId = quotePhotoId;
    }

    public Integer getSmsFlag() {
        return smsFlag;
    }

    public void setSmsFlag(Integer smsFlag) {
        this.smsFlag = smsFlag;
    }

    public String getSmsResult() {
        return smsResult;
    }

    public void setSmsResult(String smsResult) {
        this.smsResult = smsResult;
    }

    public Integer getRedFlag() {
        return redFlag;
    }

    public void setRedFlag(Integer redFlag) {
        this.redFlag = redFlag;
    }

    public String getRedResult() {
        return redResult;
    }

    public void setRedResult(String redResult) {
        this.redResult = redResult;
    }

    public Boolean getIsSatisfied() {
        return isSatisfied;
    }

    public void setIsSatisfied(Boolean isSatisfied) {
        this.isSatisfied = isSatisfied;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
