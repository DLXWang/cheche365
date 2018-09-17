package com.cheche365.cheche.admin.web.model.auto;

import com.cheche365.cheche.admin.web.model.user.UserViewModel;

import java.util.List;

/**
 * Created by guoweifu on 2015/9/7.
 */
public class AutoViewModel {
    private Long id;

    private String owner;//车主姓名
    private String identityType;//证件类型,1.身份证,2.护照,3.军官证
    private String identity;//证件id

    private String licensePlateNo; //车牌号
    private String vinNo;//车架号
    private String engineNo; //发动机号
    private String enrollDate;//初登日期
    private String model;     //车型
    private String brandCode;//品牌型号
    private String expireDate;//保险到期日

    private List<UserViewModel> userViewModels;//用户信息

    public List<UserViewModel> getUserViewModels() {
        return userViewModels;
    }

    public void setUserViewModels(List<UserViewModel> userViewModels) {
        this.userViewModels = userViewModels;
    }

    private boolean disable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
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

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }
}
