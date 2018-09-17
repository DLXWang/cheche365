package com.cheche365.cheche.operationcenter.web.model.red;

/**
 * Created by xu.yelong on 2015/10/20.
 */

public class QuotePhotoViewModel {
    private Long id;
    private String licensePlateNo;//车牌号
    private String owner;//车主
    private String identity;//车主身份证
    private String insuredName;//被保险人
    private String insuredIdNo;//被保险人身份证
    private String vinNo;//车架号
    private String engineNo;//发动机号
    private String enrollDate;//初登日期
    private String model;//车型
    private String code;//品牌型号
    private String expireDate;//失效日期
    private Boolean disable;//是否失效 true失效 false有效
    private Boolean visited;//是否需回访 true已回访 false需回访
    private String comment;//备注
    private Long userId;//用户ID
    private String mobile;//用户手机号
    private Integer userImg;//用户图片
    private String createTime;//创建时间
    private String updateTime;//更新时间
    private String drivingLicensePath;//行驶证图片
    private String ownerIdentityPath;//驾驶证图片

    private String startTime; //开始时间
    private String endTime;   //结束时间
    private Integer totalNum;  //参与总数
    private Integer sendNum;   //发放数（已去重）
    private Integer status;//审核状态，1-待审核，2-审核通过，3-审核失败，4-已发放

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

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public Boolean getVisited() {
        return visited;
    }

    public void setVisited(Boolean visited) {
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

    public Integer getUserImg() {
        return userImg;
    }

    public void setUserImg(Integer userImg) {
        this.userImg = userImg;
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

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDrivingLicensePath() {
        return drivingLicensePath;
    }

    public void setDrivingLicensePath(String drivingLicensePath) {
        this.drivingLicensePath = drivingLicensePath;
    }

    public String getOwnerIdentityPath() {
        return ownerIdentityPath;
    }

    public void setOwnerIdentityPath(String ownerIdentityPath) {
        this.ownerIdentityPath = ownerIdentityPath;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
