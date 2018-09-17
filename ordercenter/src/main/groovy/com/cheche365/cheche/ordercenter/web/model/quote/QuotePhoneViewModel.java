package com.cheche365.cheche.ordercenter.web.model.quote;

import com.cheche365.cheche.core.model.IdentityType;

import javax.validation.constraints.NotNull;

/**
 * Created by wangfei on 2015/10/15.
 */

public class QuotePhoneViewModel {
    private Long id;
    @NotNull
    private String licensePlateNo;//车牌号
    private String owner;//车主
    private IdentityType identityType;
    private String identity;//车主身份证
    private String insuredName;//被保险人
    private IdentityType insuredIdType;
    private String insuredIdNo;//被保险人身份证
    private String vinNo;//车架号
    private String engineNo;//发动机号
    private String enrollDate;//初登日期
    private String model;//车型
    private String code;//品牌型号
    private String expireDate;//失效日期
    private boolean visited;//是否需回访 true已回访 false需回访
    private String comment;//备注
    private Long userId;//用户ID
    private String mobile;//手机号
    private String encyptMobile;//加密手机号
    private String createTime;//创建时间
    private String updateTime;//更新时间
    private Long sourceChannel;//数据来源
    private String transferDate;//过户日期
    private String channelIcon;//渠道LOGO

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public IdentityType getIdentityType() {
        return identityType;
    }

    public void setIdentityType(IdentityType identityType) {
        this.identityType = identityType;
    }


    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getInsuredName() {
        return insuredName;
    }

    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    public IdentityType getInsuredIdType() {
        return insuredIdType;
    }

    public void setInsuredIdType(IdentityType insuredIdType) {
        this.insuredIdType = insuredIdType;
    }

    public String getInsuredIdNo() {
        return insuredIdNo;
    }

    public void setInsuredIdNo(String insuredIdNo) {
        this.insuredIdNo = insuredIdNo;
    }

    public String getVinNo() {
        return vinNo;
    }

    public void setVinNo(String vinNo) {
        this.vinNo = vinNo;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEncyptMobile() {
        return encyptMobile;
    }

    public void setEncyptMobile(String encyptMobile) {
        this.encyptMobile = encyptMobile;
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

    public Long getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(Long sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    public String getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(String transferDate) {
        this.transferDate = transferDate;
    }

    public String getChannelIcon() {
        return channelIcon;
    }

    public void setChannelIcon(String channelIcon) {
        this.channelIcon = channelIcon;
    }
}
