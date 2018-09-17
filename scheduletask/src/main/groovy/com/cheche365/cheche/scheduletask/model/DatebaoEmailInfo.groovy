package com.cheche365.cheche.scheduletask.model
/**
 * Created by yinJianBin on 2017/5/18.
 */
class DatebaoEmailInfo extends AttachmentData {
    def parterUserId = ""                        //大特保用户ID
    //订单信息
    def orderNo = ""                             //订单号：
    def orderCreateTime = ""                     //下单时间：
    def orderStatus = ""                       //订单状态：
    def cityName = ""                            //城市
    def insuranceCompanyName = ""                //保险公司
    //用户信息
    def mobile = ""                              //手机号：
    def nickName = ""                           //微信昵称：
    def ownerMobile = ""                        //车主手机号：
    def source = ""                          //来源：
    def registerChannel = ""                    //平台
    //车辆信息
    def licensePlateNo = ""                     //车牌号：
    def vinNo = ""                              //车架号：
    def engineNo = ""                            //发动机号：
    def enrollDate = ""                          //车辆初登日期：
    def seats = ""                               //车座数：
    def brandAndModel = ""                       //品牌型号：
    //车辆补充信息
    def transferDate = ""                       //过户日期：
    //车险信息
    def insranceEffectiveDate = ""               //商业险生效日期：
    def insranceExpireDate = ""                  //商业险失效日期：
    def insrancePolicyNo = ""                    //商业险保单号：
    def compulsoryEffectiveDate = ""
    //交强险生效日期：                                                                                                                                                                                              ate
    def compulsoryExpireDate = ""               //交强险失效日期：
    def compulsoryPolicyNo = ""                 //交强险保单号：
    def payableAmount = ""                       //应付金额：
    def paidAmount = ""                          //实付金额：
    //险种详情：
    def compulsoryPremium = ""                   //交强险：
    def autoTax = ""                            //车船税：
    def commecialPremium = ""                    //商业险：
    def damagePremium = ""                       //机动车辆损失险 保费：7085.83元
    def damageAmount = ""                        //机动车辆损失险 保额：327215.20元
    def thirdPartyPremium = ""                   //第三者责任险 保费：7085.83元
    def thirdPartyAmount = ""                    //第三者责任险 保额：
    def driverPremium = ""                       //车上人员责任险(司机) 保费：7085.83元
    def driverAmount = ""                       //车上人员责任险(司机) 保额：
    def passengerPremium = ""                    //车上人员责任险(乘客) 保费：7085.83元
    def passengerAmount = ""                     //车上人员责任险(乘客) 保额：
    def theftPremium = ""                        //盗抢险 保费：7085.83元
    def theftAmount = ""                         //盗抢险 保额：
    def scratchPremium = ""                      //车身划痕损失险 保费：7085.83元
    def scratchAmount = ""                       //车身划痕损失险 保额：
    def spontaneousLossPremium = ""             //自燃损失险 保费：7085.83元
    def spontaneousLossAmount = ""               //自燃损失险 保额：
    def enginePremium = ""                       //发动机特别损失险 保费：7085.83元
    def engineAmount = ""                       //发动机特别损失险 保额：
    //车主信息
    def ownerName = ""                          //姓名：
    def ownerIdentityType = ""                   //证件类型：
    def ownerIdentity = ""                       //身份证：
    //投保人信息
    def applicantName = ""                       //姓名：
    def applicantIdentityType = ""               //证件类型：
    def applicantIdentity = ""                   //身份证：
    //被保险人信息
    def insuredName = ""                        //姓名：
    def insuredIdentityType = ""                 //证件类型：
    def insuredIdentity = ""                    //身份证：
    //支付信息
    def paymentId = ""                         //序号 ：
    def paymentType = ""                       //类型：
    def amount = ""                              //金额：
    def status = ""                              //状态：
    def updateTime = ""                          //支付时间：
    def paymentChannel = ""                      //交付通道 ：
    def outTradeNo = ""                         //车车流水号：
    def thirdpartyPaymentNo = ""                 //支付平台流水号：
    //配送信息
    def receiver = ""                           //收件人：
    def receiverIdentity = ""                    //身份证：
    def receiverMobile = ""                      //电话：
    def sendTime = ""                            //派送日期：
    def sendPeriod = ""                         //派送时段：
    def address = ""                             //快递地址：


    @Override
    public MetaClass getMetaClass() {
        this.metaClass
    }

    def getOrderNo() {
        return orderNo
    }

    void setOrderNo(orderNo) {
        this.orderNo = orderNo
    }

    def getParterUserId() {
        return parterUserId
    }

    void setParterUserId(parterUserId) {
        this.parterUserId = parterUserId
    }

    def getOrderCreateTime() {
        return orderCreateTime
    }

    void setOrderCreateTime(orderCreateTime) {
        this.orderCreateTime = orderCreateTime
    }

    def getOrderStatus() {
        return orderStatus
    }

    void setOrderStatus(orderStatus) {
        this.orderStatus = orderStatus
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

    def getMobile() {
        return mobile
    }

    void setMobile(mobile) {
        this.mobile = mobile
    }

    def getNickName() {
        return nickName
    }

    void setNickName(nickName) {
        this.nickName = nickName
    }

    def getOwnerMobile() {
        return ownerMobile
    }

    void setOwnerMobile(ownerMobile) {
        this.ownerMobile = ownerMobile
    }

    def getRegisterChannel() {
        return registerChannel
    }

    void setRegisterChannel(registerChannel) {
        this.registerChannel = registerChannel
    }

    def getSource() {
        return source
    }

    void setSource(source) {
        this.source = source
    }

    def getLicensePlateNo() {
        return licensePlateNo
    }

    void setLicensePlateNo(licensePlateNo) {
        this.licensePlateNo = licensePlateNo
    }

    def getVinNo() {
        return vinNo
    }

    void setVinNo(vinNo) {
        this.vinNo = vinNo
    }

    def getEngineNo() {
        return engineNo
    }

    void setEngineNo(engineNo) {
        this.engineNo = engineNo
    }

    def getEnrollDate() {
        return enrollDate
    }

    void setEnrollDate(enrollDate) {
        this.enrollDate = enrollDate
    }

    def getSeats() {
        return seats
    }

    void setSeats(seats) {
        this.seats = seats
    }

    def getBrandAndModel() {
        return brandAndModel
    }

    void setBrandAndModel(brandAndModel) {
        this.brandAndModel = brandAndModel
    }

    def getTransferDate() {
        return transferDate
    }

    void setTransferDate(transferDate) {
        this.transferDate = transferDate
    }

    def getInsranceEffectiveDate() {
        return insranceEffectiveDate
    }

    void setInsranceEffectiveDate(insranceEffectiveDate) {
        this.insranceEffectiveDate = insranceEffectiveDate
    }

    def getInsranceExpireDate() {
        return insranceExpireDate
    }

    void setInsranceExpireDate(insranceExpireDate) {
        this.insranceExpireDate = insranceExpireDate
    }

    def getInsrancePolicyNo() {
        return insrancePolicyNo
    }

    void setInsrancePolicyNo(insrancePolicyNo) {
        this.insrancePolicyNo = insrancePolicyNo
    }

    def getCompulsoryEffectiveDate() {
        return compulsoryEffectiveDate
    }

    void setCompulsoryEffectiveDate(compulsoryEffectiveDate) {
        this.compulsoryEffectiveDate = compulsoryEffectiveDate
    }

    def getCompulsoryExpireDate() {
        return compulsoryExpireDate
    }

    void setCompulsoryExpireDate(compulsoryExpireDate) {
        this.compulsoryExpireDate = compulsoryExpireDate
    }

    def getCompulsoryPolicyNo() {
        return compulsoryPolicyNo
    }

    void setCompulsoryPolicyNo(compulsoryPolicyNo) {
        this.compulsoryPolicyNo = compulsoryPolicyNo
    }

    def getPayableAmount() {
        return payableAmount
    }

    void setPayableAmount(payableAmount) {
        this.payableAmount = payableAmount
    }

    def getPaidAmount() {
        return paidAmount
    }

    void setPaidAmount(paidAmount) {
        this.paidAmount = paidAmount
    }

    def getCompulsoryPremium() {
        return compulsoryPremium
    }

    void setCompulsoryPremium(compulsoryPremium) {
        this.compulsoryPremium = compulsoryPremium
    }

    def getAutoTax() {
        return autoTax
    }

    void setAutoTax(autoTax) {
        this.autoTax = autoTax
    }

    def getCommecialPremium() {
        return commecialPremium
    }

    void setCommecialPremium(commecialPremium) {
        this.commecialPremium = commecialPremium
    }

    def getDamagePremium() {
        return damagePremium
    }

    void setDamagePremium(damagePremium) {
        this.damagePremium = damagePremium
    }

    def getDamageAmount() {
        return damageAmount
    }

    void setDamageAmount(damageAmount) {
        this.damageAmount = damageAmount
    }

    def getThirdPartyPremium() {
        return thirdPartyPremium
    }

    void setThirdPartyPremium(thirdPartyPremium) {
        this.thirdPartyPremium = thirdPartyPremium
    }

    def getThirdPartyAmount() {
        return thirdPartyAmount
    }

    void setThirdPartyAmount(thirdPartyAmount) {
        this.thirdPartyAmount = thirdPartyAmount
    }

    def getDriverPremium() {
        return driverPremium
    }

    void setDriverPremium(driverPremium) {
        this.driverPremium = driverPremium
    }

    def getDriverAmount() {
        return driverAmount
    }

    void setDriverAmount(driverAmount) {
        this.driverAmount = driverAmount
    }

    def getPassengerPremium() {
        return passengerPremium
    }

    void setPassengerPremium(passengerPremium) {
        this.passengerPremium = passengerPremium
    }

    def getPassengerAmount() {
        return passengerAmount
    }

    void setPassengerAmount(passengerAmount) {
        this.passengerAmount = passengerAmount
    }

    def getTheftPremium() {
        return theftPremium
    }

    void setTheftPremium(theftPremium) {
        this.theftPremium = theftPremium
    }

    def getTheftAmount() {
        return theftAmount
    }

    void setTheftAmount(theftAmount) {
        this.theftAmount = theftAmount
    }

    def getScratchPremium() {
        return scratchPremium
    }

    void setScratchPremium(scratchPremium) {
        this.scratchPremium = scratchPremium
    }

    def getScratchAmount() {
        return scratchAmount
    }

    void setScratchAmount(scratchAmount) {
        this.scratchAmount = scratchAmount
    }

    def getSpontaneousLossPremium() {
        return spontaneousLossPremium
    }

    void setSpontaneousLossPremium(spontaneousLossPremium) {
        this.spontaneousLossPremium = spontaneousLossPremium
    }

    def getSpontaneousLossAmount() {
        return spontaneousLossAmount
    }

    void setSpontaneousLossAmount(spontaneousLossAmount) {
        this.spontaneousLossAmount = spontaneousLossAmount
    }

    def getEnginePremium() {
        return enginePremium
    }

    void setEnginePremium(enginePremium) {
        this.enginePremium = enginePremium
    }

    def getEngineAmount() {
        return engineAmount
    }

    void setEngineAmount(engineAmount) {
        this.engineAmount = engineAmount
    }

    def getOwnerName() {
        return ownerName
    }

    void setOwnerName(ownerName) {
        this.ownerName = ownerName
    }

    def getOwnerIdentityType() {
        return ownerIdentityType
    }

    void setOwnerIdentityType(ownerIdentityType) {
        this.ownerIdentityType = ownerIdentityType
    }

    def getOwnerIdentity() {
        return ownerIdentity
    }

    void setOwnerIdentity(ownerIdentity) {
        this.ownerIdentity = ownerIdentity
    }

    def getApplicantName() {
        return applicantName
    }

    void setApplicantName(applicantName) {
        this.applicantName = applicantName
    }

    def getApplicantIdentityType() {
        return applicantIdentityType
    }

    void setApplicantIdentityType(applicantIdentityType) {
        this.applicantIdentityType = applicantIdentityType
    }

    def getApplicantIdentity() {
        return applicantIdentity
    }

    void setApplicantIdentity(applicantIdentity) {
        this.applicantIdentity = applicantIdentity
    }

    def getInsuredName() {
        return insuredName
    }

    void setInsuredName(insuredName) {
        this.insuredName = insuredName
    }

    def getInsuredIdentityType() {
        return insuredIdentityType
    }

    void setInsuredIdentityType(insuredIdentityType) {
        this.insuredIdentityType = insuredIdentityType
    }

    def getInsuredIdentity() {
        return insuredIdentity
    }

    void setInsuredIdentity(insuredIdentity) {
        this.insuredIdentity = insuredIdentity
    }

    def getPaymentId() {
        return paymentId
    }

    void setPaymentId(paymentId) {
        this.paymentId = paymentId
    }

    def getPaymentType() {
        return paymentType
    }

    void setPaymentType(paymentType) {
        this.paymentType = paymentType
    }

    def getAmount() {
        return amount
    }

    void setAmount(amount) {
        this.amount = amount
    }

    def getStatus() {
        return status
    }

    void setStatus(status) {
        this.status = status
    }

    def getUpdateTime() {
        return updateTime
    }

    void setUpdateTime(updateTime) {
        this.updateTime = updateTime
    }

    def getPaymentChannel() {
        return paymentChannel
    }

    void setPaymentChannel(paymentChannel) {
        this.paymentChannel = paymentChannel
    }

    def getOutTradeNo() {
        return outTradeNo
    }

    void setOutTradeNo(outTradeNo) {
        this.outTradeNo = outTradeNo
    }

    def getThirdpartyPaymentNo() {
        return thirdpartyPaymentNo
    }

    void setThirdpartyPaymentNo(thirdpartyPaymentNo) {
        this.thirdpartyPaymentNo = thirdpartyPaymentNo
    }

    def getReceiver() {
        return receiver
    }

    void setReceiver(receiver) {
        this.receiver = receiver
    }

    def getReceiverIdentity() {
        return receiverIdentity
    }

    void setReceiverIdentity(receiverIdentity) {
        this.receiverIdentity = receiverIdentity
    }

    def getReceiverMobile() {
        return receiverMobile
    }

    void setReceiverMobile(receiverMobile) {
        this.receiverMobile = receiverMobile
    }

    def getSendTime() {
        return sendTime
    }

    void setSendTime(sendTime) {
        this.sendTime = sendTime
    }

    def getSendPeriod() {
        return sendPeriod
    }

    void setSendPeriod(sendPeriod) {
        this.sendPeriod = sendPeriod
    }

    def getAddress() {
        return address
    }

    void setAddress(address) {
        this.address = address
    }

}
