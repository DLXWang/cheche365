package com.cheche365.cheche.scheduletask.model

/**
 * Created by zhangtc on 2018/1/23.
 */
class TuhuEmailInfo extends AttachmentData {
    def orderNo = ""                             //订单号：1
    def mobile = ""                              //手机号：2
    def licensePlateNo = ""                     //车牌号：3
    def applicantName = ""                       //姓名：4
    def compulsoryPremium = ""                   //交强险：5
    def commecialPremium = ""                    //商业险：6
    def orderCreateTime = ""                     //日期：7
    def cityName = ""                            //城市：8
    def insuranceCompanyName = ""                //保险公司：9
    def giftDetail  = ""                            //奖品：10

    def getOrderNo() {
        return orderNo
    }

    void setOrderNo(orderNo) {
        this.orderNo = orderNo
    }

    def getMobile() {
        return mobile
    }

    void setMobile(mobile) {
        this.mobile = mobile
    }

    def getLicensePlateNo() {
        return licensePlateNo
    }

    void setLicensePlateNo(licensePlateNo) {
        this.licensePlateNo = licensePlateNo
    }

    def getApplicantName() {
        return applicantName
    }

    void setApplicantName(applicantName) {
        this.applicantName = applicantName
    }

    def getCompulsoryPremium() {
        return compulsoryPremium
    }

    void setCompulsoryPremium(compulsoryPremium) {
        this.compulsoryPremium = compulsoryPremium
    }

    def getCommecialPremium() {
        return commecialPremium
    }

    void setCommecialPremium(commecialPremium) {
        this.commecialPremium = commecialPremium
    }

    def getOrderCreateTime() {
        return orderCreateTime
    }

    void setOrderCreateTime(orderCreateTime) {
        this.orderCreateTime = orderCreateTime
    }

    def getCityName() {
        return cityName
    }

    void setCityName(cityName) {
        this.cityName = cityName
    }

    def getInsuranceCompanyName() {
        return insuranceCompanyName
    }

    void setInsuranceCompanyName(insuranceCompanyName) {
        this.insuranceCompanyName = insuranceCompanyName
    }

    def getGiftDetail() {
        return giftDetail
    }

    void setGiftDetail(giftDetail) {
        this.giftDetail = giftDetail
    }
}
