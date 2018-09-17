package com.cheche365.cheche.scheduletask.model

/**
 * 车保易订单导出信息
 * Created by zhangpengcheng on 2018/6/25.
 */
public class ChebaoyiOrderExportInfo extends AttachmentData{

    private String platform;//平台
    private String drivedCity;//行驶城市
    private String plateNum;// 车牌号
    private String vin;//车架号
    private String engineNo;//发动机号
    private String brandNo;//品牌型号

    private String company;//保险公司
    private String forceNum;//交强险金额
    private String taxNum;//车船税金额
    private String insuranceNum;//商业险金额

    private String owner;//车主
    private String insPerson;//被保险人
    private String getMailed;//收件人
    private String getAddress;//收货地址
    private String customerPhone;//客户电话


    private String orderAccount;//出单账号


    private String agentAccount;//代理人登录账号
    private String insuranceNo;//商业险保单号
    private String jiaoshangNo;//交强险保单号
    private String identityNo;//车主身份证号
    private String mailAddress;//电子邮箱
    private String orderNo;//订单号



    String getPlatform() {
        return platform
    }

    void setPlatform(String platform) {
        this.platform = platform
    }

    String getDrivedCity() {
        return drivedCity
    }

    void setDrivedCity(String drivedCity) {
        this.drivedCity = drivedCity
    }

    String getPlateNum() {
        return plateNum
    }

    void setPlateNum(String plateNum) {
        this.plateNum = plateNum
    }

    String getVin() {
        return vin
    }

    void setVin(String vin) {
        this.vin = vin
    }

    String getEngineNo() {
        return engineNo
    }

    void setEngineNo(String engineNo) {
        this.engineNo = engineNo
    }

    String getBrandNo() {
        return brandNo
    }

    void setBrandNo(String brandNo) {
        this.brandNo = brandNo
    }

    String getCompany() {
        return company
    }

    void setCompany(String company) {
        this.company = company
    }

    String getForceNum() {
        return forceNum
    }

    void setForceNum(String forceNum) {
        this.forceNum = forceNum
    }

    String getTaxNum() {
        return taxNum
    }

    void setTaxNum(String taxNum) {
        this.taxNum = taxNum
    }

    String getInsuranceNum() {
        return insuranceNum
    }

    void setInsuranceNum(String insuranceNum) {
        this.insuranceNum = insuranceNum
    }

    String getOwner() {
        return owner
    }

    void setOwner(String owner) {
        this.owner = owner
    }

    String getInsPerson() {
        return insPerson
    }

    void setInsPerson(String insPerson) {
        this.insPerson = insPerson
    }

    String getGetMailed() {
        return getMailed
    }

    void setGetMailed(String getMailed) {
        this.getMailed = getMailed
    }

    String getGetAddress() {
        return getAddress
    }

    void setGetAddress(String getAddress) {
        this.getAddress = getAddress
    }

    String getCustomerPhone() {
        return customerPhone
    }

    void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone
    }

    String getOrderAccount() {
        return orderAccount
    }

    void setOrderAccount(String orderAccount) {
        this.orderAccount = orderAccount
    }

    String getAgentAccount() {
        return agentAccount
    }

    void setAgentAccount(String agentAccount) {
        this.agentAccount = agentAccount
    }

    String getInsuranceNo() {
        return insuranceNo
    }

    void setInsuranceNo(String insuranceNo) {
        this.insuranceNo = insuranceNo
    }

    String getJiaoshangNo() {
        return jiaoshangNo
    }

    void setJiaoshangNo(String jiaoshangNo) {
        this.jiaoshangNo = jiaoshangNo
    }

    String getIdentityNo() {
        return identityNo
    }

    void setIdentityNo(String identityNo) {
        this.identityNo = identityNo
    }

    String getMailAddress() {
        return mailAddress
    }

    void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress
    }

    String getOrderNo() {
        return orderNo
    }

    void setOrderNo(String orderNo) {
        this.orderNo = orderNo
    }

}
