package com.cheche365.cheche.scheduletask.model
/**
 * 线下数据导入邮件信息
 * Created by yinJianBin on 2017/12/11.
 */
class OfflineDataMatchResult extends AttachmentData {

    String policyNo            //	保险单号码/批单号码
    String insuredName         //	被保险人
    String paidAmount          //	实收保费
    String rebate          //	费用比例%
    String rebateAmount            //	已结收付费
    String balanceTime         //	结算时间
    String licensePlateNo          //	车牌号码
    String brandModel          //	车型名称
    String engineNo            //	发动机号
    String vinNo           //	车架号
    String issueTime           //	出单日期
    String rebateAddTimes      //补点次数
    String isCancle             //是否退保

    String errorMessage        //校验错误原因
    Integer order        //行号


    String getPolicyNo() {
        return policyNo
    }

    void setPolicyNo(String policyNo) {
        this.policyNo = policyNo
    }

    String getInsuredName() {
        return insuredName
    }

    void setInsuredName(String insuredName) {
        this.insuredName = insuredName
    }

    def getPaidAmount() {
        return paidAmount
    }

    void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount
    }

    def getRebate() {
        return rebate
    }

    void setRebate(Double rebate) {
        this.rebate = rebate
    }

    def getRebateAmount() {
        return rebateAmount
    }

    void setRebateAmount(Double rebateAmount) {
        this.rebateAmount = rebateAmount
    }

    def getBalanceTime() {
        return balanceTime
    }

    void setBalanceTime(balanceTime) {
        this.balanceTime = balanceTime
    }

    String getLicensePlateNo() {
        return licensePlateNo
    }

    void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo
    }

    String getBrandModel() {
        return brandModel
    }

    void setBrandModel(String brandModel) {
        this.brandModel = brandModel
    }

    String getEngineNo() {
        return engineNo
    }

    void setEngineNo(String engineNo) {
        this.engineNo = engineNo
    }

    String getVinNo() {
        return vinNo
    }

    void setVinNo(String vinNo) {
        this.vinNo = vinNo
    }

    def getIssueTime() {
        return issueTime
    }

    void setIssueTime(String issueTime) {
        this.issueTime = issueTime
    }

    def getRebateAddTimes() {
        return rebateAddTimes
    }

    void setRebateAddTimes(Integer rebateAddTimes) {
        this.rebateAddTimes = rebateAddTimes
    }

    String getIsCancle() {
        return isCancle
    }

    void setIsCancle(String isCancle) {
        this.isCancle = isCancle
    }

    String getErrorMessage() {
        return errorMessage
    }

    void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage
    }

    Integer getOrder() {
        return order
    }

    void setOrder(Integer order) {
        this.order = order
    }
}
