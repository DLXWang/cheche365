package com.cheche365.cheche.manage.common.model

/**
 * Created by chenxiangyin on 2018/4/9.
 */
class BaiduInsurSenderViewModel implements  Cloneable {
    def applyNo //1 ApplyNo 百度方申请单号
    def policyNo //2 PolicyNo 保单号
    def applicationNo //3 ApplicationNo 投保单号
    def itemCode
    def holderName //5 HolderName 投保人真实姓名
    def holderCertType //6 HolderCertType 投保人证件类型
    def holderCertNo //7 HolderCertNo 投保人证件号码
    def holderIdExpiry //8 HolderIdExpiry 证件有效期
    def holderSex //9 HolderSex 投保人性别
    def holderBirth //10 HolderBirth 投保人生日
    def holderEmail //11 HolderEmail 投保人电子邮箱Syr
    def holderZip //12 HolderZip 投保人邮编
    def holderAddr //13 HolderAddr 投保人联系地址positive()
    def holderMobile //14 HolderMobile 投保人手机号
    def insuredName //15 InsuredName 被保险人姓名
    def insuredCertType //16 InsuredCertType 被保险人证件类型
    def insuredCertNo //17 InsuredCertNo 被保险人证件号码
    def insuredIdExpiry //18 InsuredIdExpiry 被保险人证件有效期
    def insuredNameSp //19 InsuredNameSp 被保险人姓名全拼
    def insuredRela //30 InsuredRela 被保人与投保人关系
    def insuredSex //21 InsuredSex 被保险人性别
    def insuredBirth //22 InsuredBirth 被保险人生日
    def placeholder // 22+ 占位符
    def status //23 Status 订单状态
    def policyAmount //24 PolicyAmount 保额(精确到分)
    def premium //25 Premium 保费(精确到分)
    def unit //26 Unit 分数
    def payAmount // 27 PayAmount 支付金额(精确到分)
    def payTime // 28 PayTime 订单支付时间
    def acceptTime // 29 AcceptTime 承保时间
    def validateDate //30 ValidateDate 保单生效日
    def expireDate //31 ExpireDate 保单失效日
    def coverageClassCode //32 CoverageClassCode 险种性质代码
    def coverageCode //33 CoverageCode 险种代码
    def licensePlateNo //34 LicensePlateNo 号牌号码
    def licensePlateType //35 LicensePlateType 号牌种类代码
    def motorTypeCode // 36 MotorTypeCode 车辆种类代码
    def motorUsageTypeCode //37 MotorUsageTypeCode 使用性质代码
    def firstRegisterDate //38 FirstRegisterDate 车辆初始登记日期。格式：精确到天

    def vin //39 Vin 车辆识别代号（车架号/VIN码）
    def engineNo //40 EngineNo 发动机号
    def wholeWeight //41 WholeWeight 整备质量(千克)
    def fatedPassengerCapacity //42 RatedPassengerCapacity 核定载客人数
    def tonnage //43 Tonnage 核定载质量(千克)
    def model //44 Model 车辆型号
    def displacement //45 Displacement 排量(毫升)
    def brandCN //46 BrandCN 中文品牌
    def brandEN// 47 BrandEN 英文品牌
    def noLicenseFlag// 48 NoLicenseFlag 未上牌车辆标志
    def newVehicleFlag//49 NewVehicleFlag 新车标志
    def chgOwnerFlag//50 ChgOwnerFlag 过户车辆标志
    def ownerName//51 OwnerName 车主姓名
    def ownerCertNo//52 OwnerCertNo 车主证件号码
    def ownerCertType//53 OwnerCertType 被保险人证件类型
    def reciverName//54 ReciverName 收货人
    def reciverMobile//55 ReciverMobile 收货人手机号
    def reciverAddress//56 ReciverAddress 收货人地址
    def setPremium(policyAmount, premium, payAmount, coverageClassCode, coverageCode){
        BaiduInsurSenderViewModel cloneModel = this.clone()
        cloneModel.setPolicyAmount(policyAmount)
        cloneModel.setPremium(premium)
        cloneModel.setPayAmount(payAmount)
        cloneModel.setCoverageClassCode(coverageClassCode)
        cloneModel.setCoverageCode(coverageCode)
        cloneModel
    }

//    BaiduInsurSenderViewModel clone() {
//        def mapProperties = new BaiduInsurSenderViewModel().getProperties()
//        BaiduInsurSenderViewModel model = new BaiduInsurSenderViewModel()
//        mapProperties.each {property->
//            model."$property.key" = this."$property.key"
//        }
//    }

    /*
　　* 0 指前面补充零
　　* formatLength 字符总长度为 formatLength
　　* d 代表为正数。1
　　*/
    def frontFill(int source,int formatLength){
        return String.format("%0"+formatLength+"d", source)
    }


    /*
　　* 后面补充空格
    * str 原字符串
    * length 总长度
　　*/
    static def backFill(str, length){
        if (str == null){
            str=""
        }
        int strLen = str.length()
        if (strLen == length){
            return str
        } else if (strLen < length){
            int temp = length - strLen
            String tem = ""
            for (int i = 0; i < temp; i++){
                tem = tem + " "
            }
            return str + tem
        } else{
            return str.substring(0, length)
        }
    }
    static fillModel(model){
        def config = [
            ['applyNo',32],
            ['policyNo',32],
            ['applicationNo',32],
            ['itemCode',64],
            ['holderName',16],
            ['holderCertType',1],
            ['holderCertNo',32],
            ['holderIdExpiry',8],
            ['holderSex',1],
            ['holderBirth',8],
            ['holderEmail',64],
            ['holderZip',10],
            ['holderAddr',256],
            ['holderMobile',20],
            ['insuredName',16],
            ['insuredCertType',1],
            ['insuredCertNo',20],
            ['insuredIdExpiry',8],
            ['insuredNameSp',32],
            ['insuredRela',1],
            ['insuredSex',1],
            ['insuredBirth',8],
            ['placeholder',1740/20*19],
            ['status',1],
            ['policyAmount',16],
            ['premium',16],
            ['unit',6],
            ['payAmount',16],
            ['payTime',14],
            ['acceptTime',14],
            ['validateDate',14],
            ['expireDate',14],
            ['coverageClassCode',1],
            ['coverageCode',3],
            ['licensePlateNo',15],
            ['licensePlateType',15],
            ['motorTypeCode',20],
            ['motorUsageTypeCode',8],
            ['firstRegisterDate',8],
            ['vin',20],
            ['engineNo',50],
            ['wholeWeight',64],
            ['fatedPassengerCapacity',3],
            ['tonnage',10],
            ['model',64],
            ['displacement',8],
            ['brandCN',64],
            ['brandEN',64],
            ['noLicenseFlag',1],
            ['newVehicleFlag',1],
            ['chgOwnerFlag',1],
            ['ownerName',16],
            ['ownerCertNo',20],
            ['ownerCertType',1],
            ['reciverName',16],
            ['reciverMobile',20],
            ['reciverAddress',100]]
        StringBuffer content = new StringBuffer()
        config.inject(model) { i, c ->
            def (key, maxLength) = c
            content.append(backFill(model."${key}",maxLength ))
//            content.append(  )
//            i[key] = toString(i[key] as String, maxLength)
//            i
        }
        return content.toString()

    }
}
