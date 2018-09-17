package com.cheche365.cheche.scheduletask.model;

/**
 * Created by chenxiangyin on 2017/6/2.
 */
public class HaodaiOrderReportModel  extends AttachmentData{
    private String uid;
    private String updateDate;
    private String city;
    private String orderNo;
    private String orderStatus;
    private String mobile;
    private String insuranceCompany;
    private String applicantName;
    private String plateNo;
    private String compulsory;
    private String commercial;
    private String autoTax;
    private String compulsoryNo;
    private String commercialNo;
    private String recipientMobile;
    public String getRecipientMobile() {return recipientMobile;}

    public void setRecipientMobile(String recipientMobile) {this.recipientMobile = recipientMobile;}
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getCompulsory() {
        return compulsory;
    }

    public void setCompulsory(String compulsory) {
        this.compulsory = compulsory;
    }

    public String getCommercial() {
        return commercial;
    }

    public void setCommercial(String commercial) {
        this.commercial = commercial;
    }

    public String getAutoTax() {
        return autoTax;
    }

    public void setAutoTax(String autoTax) {
        this.autoTax = autoTax;
    }

    public String getCompulsoryNo() {
        return compulsoryNo;
    }

    public void setCompulsoryNo(String compulsoryNo) {
        this.compulsoryNo = compulsoryNo;
    }

    public String getCommercialNo() {
        return commercialNo;
    }

    public void setCommercialNo(String commercialNo) {
        this.commercialNo = commercialNo;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }
}
