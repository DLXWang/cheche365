package com.cheche365.cheche.core.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by houjinxin on 15/8/27.
 * 车型列表
 * 商业险起保日期
 * 交强险起保日期
 */
public class SupplementInfo {

    public static final PropertyDescriptor[] PROPERTY_DESCRIPTORS = BeanUtils.getPropertyDescriptors(SupplementInfo.class);

    private String autoModel;

    private Date commercialStartDate;

    private Date compulsoryStartDate;

    private String commercialCaptchaImage;

    private String compulsoryCaptchaImage;

    private Date transferDate;

    private Boolean transferFlag;

    private String verifyCode; // 手机验证码

    private String code;

    private Integer seats; //座位数

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }

    private Date enrollDate;



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAutoModel() {
        return autoModel;
    }

    public void setAutoModel(String autoModel) {
        this.autoModel = autoModel;
    }

    public Date getCommercialStartDate() {
        return commercialStartDate;
    }

    public void setCommercialStartDate(Date commercialStartDate) {
        this.commercialStartDate = commercialStartDate;
    }

    public Date getCompulsoryStartDate() {
        return compulsoryStartDate;
    }

    public void setCompulsoryStartDate(Date compulsoryStartDate) {
        this.compulsoryStartDate = compulsoryStartDate;
    }

    public String getCommercialCaptchaImage() {
        return commercialCaptchaImage;
    }

    public void setCommercialCaptchaImage(String commercialCaptchaImage) {
        this.commercialCaptchaImage = commercialCaptchaImage;
    }

    public String getCompulsoryCaptchaImage() {
        return compulsoryCaptchaImage;
    }

    public void setCompulsoryCaptchaImage(String compulsoryCaptchaImage) {
        this.compulsoryCaptchaImage = compulsoryCaptchaImage;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }

    public Boolean getTransferFlag() {
        if (this.transferFlag == null && this.getTransferDate() != null) {
            return Boolean.TRUE;
        }
        return transferFlag;
    }

    public void setTransferFlag(Boolean transferFlag){
        this.transferFlag = transferFlag;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
