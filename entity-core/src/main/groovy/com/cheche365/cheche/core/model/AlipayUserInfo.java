package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by zhaozhong on 2015/10/16.
 */
@Entity
public class AlipayUserInfo {
    private long id;
    private User user;
    private String openid;
    private Date createTime;

    private String userTypeValue;
    private String userStatus;
    private String firmName;
    private String realName;
    private String avatar;
    private String certNo;
    private String gender;
    private String phone;
    private String mobile;
    private String isCertified;
    private String isStudentCertified;
    private String isBankAuth;
    private String isIdAuth;
    private String isMobileAuth;
    private String isLicenceAuth;
    private String certTypeValue;
    private String province;
    private String city;
    private String area;
    private String address;
    private String zip;
    private String addressCode;

    /* 记录关注标记，时间信息 */
    private Boolean follow;
    private Date followTime;
    private Date unFollowTime;

    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Column(name="open_id",columnDefinition = "varchar(100)")
    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Column
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column
    public String getUserTypeValue() {
        return userTypeValue;
    }

    public void setUserTypeValue(String userTypeValue) {
        this.userTypeValue = userTypeValue;
    }

    @Column
    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    @Column
    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    @Column
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Column
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Column
    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    @Column
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Column
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column
    public String getIsCertified() {
        return isCertified;
    }

    public void setIsCertified(String isCertified) {
        this.isCertified = isCertified;
    }

    @Column
    public String getIsStudentCertified() {
        return isStudentCertified;
    }

    public void setIsStudentCertified(String isStudentCertified) {
        this.isStudentCertified = isStudentCertified;
    }

    @Column
    public String getIsBankAuth() {
        return isBankAuth;
    }

    public void setIsBankAuth(String isBankAuth) {
        this.isBankAuth = isBankAuth;
    }

    @Column
    public String getIsIdAuth() {
        return isIdAuth;
    }

    public void setIsIdAuth(String isIdAuth) {
        this.isIdAuth = isIdAuth;
    }

    @Column
    public String getIsMobileAuth() {
        return isMobileAuth;
    }

    public void setIsMobileAuth(String isMobileAuth) {
        this.isMobileAuth = isMobileAuth;
    }

    @Column
    public String getIsLicenceAuth() {
        return isLicenceAuth;
    }

    public void setIsLicenceAuth(String isLicenceAuth) {
        this.isLicenceAuth = isLicenceAuth;
    }

    @Column
    public String getCertTypeValue() {
        return certTypeValue;
    }

    public void setCertTypeValue(String certTypeValue) {
        this.certTypeValue = certTypeValue;
    }

    @Column
    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    @Column
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Column
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Column
    public Boolean getFollow() {
        return follow;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
    }

    @Column
    public Date getFollowTime() {
        return followTime;
    }

    public void setFollowTime(Date followTime) {
        this.followTime = followTime;
    }

    @Column
    public Date getUnFollowTime() {
        return unFollowTime;
    }

    public void setUnFollowTime(Date unFollowTime) {
        this.unFollowTime = unFollowTime;
    }
}


