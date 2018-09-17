package com.cheche365.cheche.operationcenter.web.model.wechat.channel;

import javax.validation.constraints.NotNull;

/**
 * Created by wangfei on 2015/7/28.
 */
public class QRCodeChannelViewModel {
    private Long id;//二维码渠道id
    @NotNull
    private String code;//渠道号
    @NotNull
    private String name;//渠道名
    private String department;//所属部门
    private String status;//有效状态
    private String expireTime;//过期时间
    @NotNull
    private Double rebate;//返点
    private String comment;//备注
    private String createTime;//创建时间
    private String updateTime;//更新时间
    private String operator;//操作人信息
    private Integer newCount;//新建数量
    private Integer scanCount;//扫描数
    private Integer subscribeCount;//关注数
    private Integer bindingMobileCount;//绑定手机数
    private Long wechatQRCode;//二维码
    private String imageURL;//二维码图片存放路径
    private String downLoadFlag;//完成新建后下载二维码至本地 1勾选
    @NotNull
    private String qrCodeType;//临时二维码 or 永久二维码
    private Integer updateFlag;//更新标记，1-更新渠道号并保存，2-更新保存
    private Integer successOrderCount;//成交订单数
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public String getDepartment() {
        return department;
    }
    @SuppressWarnings("unused")
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public Double getRebate() {
        return rebate;
    }

    public void setRebate(Double rebate) {
        this.rebate = rebate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public Integer getNewCount() {
        return newCount;
    }

    public void setNewCount(Integer newCount) {
        this.newCount = newCount;
    }

    public Integer getScanCount() {
        return scanCount;
    }

    public void setScanCount(Integer scanCount) {
        this.scanCount = scanCount;
    }

    public Integer getSubscribeCount() {
        return subscribeCount;
    }

    public void setSubscribeCount(Integer subscribeCount) {
        this.subscribeCount = subscribeCount;
    }

    public Integer getBindingMobileCount(){
        return bindingMobileCount;
    }

    public void setBindingMobileCount(Integer bindingMobileCount){
        this.bindingMobileCount = bindingMobileCount;
    }

    public Long getWechatQRCode() {
        return wechatQRCode;
    }

    public void setWechatQRCode(Long wechatQRCode) {
        this.wechatQRCode = wechatQRCode;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDownLoadFlag() {
        return downLoadFlag;
    }

    public void setDownLoadFlag(String downLoadFlag) {
        this.downLoadFlag = downLoadFlag;
    }

    public String getQrCodeType() {
        return qrCodeType;
    }

    public void setQrCodeType(String qrCodeType) {
        this.qrCodeType = qrCodeType;
    }

    public Integer getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(Integer updateFlag) {
        this.updateFlag = updateFlag;
    }
    public Integer getSuccessOrderCount() {
        return successOrderCount;
    }

    public void setSuccessOrderCount(Integer successOrderCount) {
        this.successOrderCount = successOrderCount;
    }
}
