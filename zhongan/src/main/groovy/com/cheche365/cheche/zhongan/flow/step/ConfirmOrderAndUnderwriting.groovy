package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Area
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.ContactUtils.getAgeByIdentity
import static com.cheche365.cheche.common.util.ContactUtils.getGenderByIdentity
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.parser.util.BusinessUtils.populateNewQuoteRecordAndInsurances
import static com.cheche365.cheche.zhongan.util.BusinessUtils.getBirthdayFromId
import static com.cheche365.cheche.zhongan.util.BusinessUtils.getCityCode
import static com.cheche365.cheche.zhongan.util.BusinessUtils.getStandardHintsFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive
import static com.cheche365.cheche.zhongan.util.CityCodeMappings2._CITY_CODE_MAPPINGS


/**
 * 核保及订单确认
 */
@Component
@Slf4j
class ConfirmOrderAndUnderwriting implements IStep {

    private static final _SERVICE_NAME = 'zhongan.castle.policy.confirmOrderAndUnderwriting'


    @Override
    def run(Object context) {

        def verificationCode = context.extendedAttributes?.verificationCode // 信息采集验证码
        //核保之前判断一次是不是有这个验证码，如果有的话就说明之前已经调过一次核保
        context.verificationCode = verificationCode
        if (verificationCode) {
            return getContinueFSRV(verificationCode)
        }
        //purchaseOrder
        def order = context.order
        def insurance = context.insurance ?: context.compulsoryInsurance
        def outTradeNo = order.orderNo
        def auto = context.auto
        def applicant = order.applicant

        def autoId = auto.identity                      // 车主身份证
        def applicantId = order.applicantIdNo ?: autoId // 投保人身份证
        def insureId = order.insuredIdNo ?: autoId      // 被保险人身份证
        //收件人地址中的area为空，被分散到了city district 字段中 city是String类型
        def deliveryAddress = order.deliveryAddress
        def userMobile = applicant?.mobile // 用户手机
        def applicantEmail = getEnvProperty context, 'zhongan.applicant_email'

        /**
         * 收件人的地址信息可能和投保人不在一个城市但是在一个省, 故收件人城市编码优先级：收件人城市 > 投保人地区
         */
        def distributionCityCode = getCityCode(new Area(id: deliveryAddress.city as long)) ?: context.cityCode
        log.info '核保流程：distributionCityCode：{}', distributionCityCode
        def cityCodeMapping = getObjectByCityCode(new Area(id: distributionCityCode as long), _CITY_CODE_MAPPINGS)
        def distributionDistrictCode = cityCodeMapping?.districtCode //收件人县区编码
        log.info '核保流程：distributionDistrictCode：{}', distributionDistrictCode

        def params = [
            insureFlowCode             : context.insureFlowCode,                              //投保流程编码
            outTradeNo                 : outTradeNo,                                          //商户唯一订单号,由车车生成，后续需传递给支付接口和承保接口
            vehicleOwnerName           : auto.owner,                                          //车主姓名
            vehicleOwnerCertificateNo  : autoId,                                              //车主证件号码
            vehicleOwnerCertificateType: 'I',                                                 //车主证件类型 只支持身份证，传I
            vehicleOwnerSex            : getGenderByIdentity(autoId) == 1 ? 'M' : 'F',       //车主性别
            vehicleOwnerBirthday       : getBirthdayFromId(autoId),                           //车主生日
            applicantName              : order.applicantName ?: auto.owner,                   //投保人姓名
            applicantCertificateNo     : applicantId,                                         //投保人证件号码
            applicantCertificateType   : 'I',                                                 //投保人证件类型 只支持身份证，传I
            applicantSex               : getGenderByIdentity(applicantId) == 1 ? 'M' : 'F',    //投保人性别
            applicantBirthday          : getBirthdayFromId(applicantId),                      //投保人生日
            applicantEmail             : insurance?.applicantEmail ?: applicantEmail,         //投保人邮件
            applicantAge               : getAgeByIdentity(applicantId) as String,             //投保人年龄
            applicantNationality       : '中国',                                               //投保人国籍
            applicantProvinceCode      : context.provinceCode,                                    //投保人省份编码
            applicantCityCode          : context.cityCode,                                      //投保人城市编码
            applicantDistrictCode      : context.districtCode,                                      //投保人区县编码
            applicantAddress           : deliveryAddress.street,                              //投保人地址
            applicantPostcode          : order.area.postalCode,                               //投保人邮编
            applicantPhoneNo           : insurance?.applicantMobile ?: userMobile,            //投保人手机
            distributionProvinceCode   : deliveryAddress?.province ?: context.provinceCode,       //收件人省份编码
            distributionCityCode       : distributionCityCode,             //收件人城市编码
            distributionDistrictCode   : distributionDistrictCode ?: deliveryAddress?.district ?: context.districtCode,//收件人区县编码
            distributionName           : deliveryAddress?.name ?: auto.owner,                 //收件人姓名
            distributionAddress        : deliveryAddress.street,                              //收件详细地址
            distributionPhoneNo        : deliveryAddress?.mobile ?: userMobile,               //收件人电话
            insurantName               : order.insuredName ?: auto.owner,                     //被保人姓名
            insurantCertificateType    : 'I',                                                 //被保人证件类型 只支持身份证，传I
            insurantCertificateNo      : insureId,                                            //被保人证件号码
            insurantPhoneNo            : insurance?.insuredMobile ?: userMobile,              //被保人手机
            insurantSex                : getGenderByIdentity(insureId) == 1 ? 'M' : 'F',       //被保人性别
            insurantBirthday           : getBirthdayFromId(insureId),                         //被保人生日
            insurantAge                : getAgeByIdentity(insureId) as String,                //被保人年龄
            insurantNationality        : '中国',                                               //被保人国籍
            insurantProvinceCode       : context.provinceCode,                                    //被保人省份编码
            insurantCityCode           : context.cityCode,                                      //被保人城市编码
            insurantDistrictCode       : context.districtCode,                                      //被保人区县编码
            insurantAddress            : deliveryAddress.street,                              //被保人地址
            insurantPostcode           : order.area.postalCode,                               //被保人邮编
            insurantEmail              : insurance?.insuredEmail ?: applicantEmail,           //被保人邮箱
            isOffLinePay               : '0'                                                  //线上支付
        ]

        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)
        log.info 'result = {}', result

        if ('0' == result.result) { //10393是针对深圳地区,核保成功用户还未签名
            context.newQuoteRecordAndInsurances = populateNewQuoteRecordAndInsurances context, result.businessApplyNo, result.businessPolicyNo, result.compelApplyNo, result.compelPolicyNo
            log.info '核保众安返回businessApplyNo = {}，businessPolicyNo = {} compelApplyNo = {}, compelPolicyNo ={} ', result.businessApplyNo, result.businessPolicyNo, result.compelApplyNo, result.compelPolicyNo

            log.info "众安支持的支付方式 : {}", result.payChannelList
            //针对所有地区将payChannelList放入additionalParameters.persistentState中，web会做处理
            context.payChannelList = result.payChannelList
            context.applySignStageFlag = result.applySignStageFlag
            context.materialsStageFlag = result.materialsStageFlag
            getLoopBreakFSRV result
        } else if (!result.result) {
            getLoopContinueFSRV result, '众安服务异常'
        } else {
            getStandardHintsFSRV result
        }
    }

}
