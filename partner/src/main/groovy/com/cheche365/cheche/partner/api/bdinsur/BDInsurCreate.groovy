package com.cheche365.cheche.partner.api.bdinsur

import com.baidu.callback.DTO.request.insurance.Apply
import com.baidu.callback.DTO.request.insurance.Body
import com.baidu.callback.DTO.request.insurance.Header
import com.baidu.callback.DTO.request.insurance.Holder
import com.baidu.callback.DTO.request.insurance.Insured
import com.baidu.callback.DTO.request.insurance.Message
import com.baidu.callback.DTO.request.insurance.PolicyDetail
import com.baidu.callback.DTO.request.insurance.Vehicle
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.service.QuoteSupplementInfoService
import com.cheche365.cheche.partner.serializer.CommonBills
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.baidu.tool.JAXBTool.marshal
import static com.baidu.tool.RsaUtil.encryptByPublicKey
import static com.baidu.tool.RsaUtil.getSortMap
import static com.baidu.tool.RsaUtil.map2SortedStr
import static com.baidu.tool.RsaUtil.sign
import static com.cheche365.cheche.common.Constants._DATE_FORMAT2
import static com.cheche365.cheche.common.Constants._DATE_FORMAT7
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey
import static com.cheche365.cheche.core.model.IdentityType.Enum.HONGKONG_MACAO_LAISSEZ_PASSER
import static com.cheche365.cheche.core.model.IdentityType.Enum.IDENTITYCARD
import static com.cheche365.cheche.core.model.IdentityType.Enum.MTP
import static com.cheche365.cheche.core.model.IdentityType.Enum.OFFICERARD
import static com.cheche365.cheche.core.model.IdentityType.Enum.OTHER_IDENTIFICATION
import static com.cheche365.cheche.core.model.IdentityType.Enum.PASSPORT
import static com.cheche365.cheche.core.model.IdentityType.Enum.RESIDENCE_BOOKLET
import static com.cheche365.cheche.core.model.IdentityType.Enum.TAIWAN_LAISSEZ_PASSER
import static com.cheche365.cheche.core.model.OrderStatus.Enum.FINISHED_5
import static com.cheche365.cheche.core.model.OrderStatus.Enum.HANDLING_2
import static com.cheche365.cheche.core.model.OrderStatus.Enum.INSURE_FAILURE_7
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PAID_3
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PENDING_PAYMENT_1
import static com.cheche365.cheche.core.model.OrderStatus.Enum.REFUNDED_9
import static com.cheche365.cheche.partner.service.index.BaiduInsurService.CHECHE_PRIVATE_KEY
import static com.cheche365.cheche.partner.service.index.BaiduInsurService.PARTNER_PUBLIC_KEY
import static com.cheche365.cheche.partner.service.index.BaiduInsurService.PARTNER_SP_NO

/**
 * Created by liheng on 2018/3/15 015.
 */
@Slf4j
@Service
class BDInsurCreate extends BDInsurApi {

    private static final DEFAULT_EMAIL = 'baodan@cheche365.com'
    // 有的保险公司没有分项IOP
    static final POLICY_CODE_MAPPING = [
        compulsoryPremium   : '100', // 机动车强制责任保险
        autoTax             : '962', // 车船税
        damage              : '200', // 车损险
        scratch             : '210', // 车身划痕损失险条款
        glass               : '231', // 玻璃单独破碎险条款
        engine              : '291', // 发动机特别损失险条款
        spontaneousLoss     : '310', // 自燃损失险条款
        theft               : '500', // 盗抢险
        thirdParty          : '600', // 三者险
        driver              : '701', // 车上人员责任险（司机）
        passenger           : '702', // 车上人员责任险（乘客）
        iopTotal            : '965', // 不计免赔率特约条款
        damageIop           : '911', // 不计免赔率（车损险）
        thirdPartyIop       : '912', // 不计免赔率（三者险）
        theftIop            : '921', // 不计免赔率（机动车盗抢险）
        scratchIop          : '922', // 不计免赔率（车身划痕损失险）
        engineIop           : '924', // 不计免赔率（发动机特别损失险）
        driverIop           : '928', // 不计免赔率（车上人员责任险（司机））
        passengerIop        : '929', // 不计免赔率（车上人员责任险（乘客））
        spontaneousLossIop  : '963', // 不计免赔率（自燃）
        unableFindThirdParty: '964', // 无法找到第三方特约险
        designatedRepairShop: '252', // 指定专修厂险
    ]

    @Autowired
    private PurchaseOrderService purchaseOrderService
    @Autowired
    private QuoteSupplementInfoService supplementInfoService

    @Override
    List supportOrderStatus() {
        [FINISHED_5]
    }

    @Override
    def prepareData(rawData) {
        def partnerOrder = getPartnerOrder rawData
        def message = convertMessage partnerOrder
        def sortMap = getSortMap "Message", message, new HashMap()
        def signString = map2SortedStr sortMap
        message.body.sign = sign signString, findByPartnerAndKey(apiPartner(), CHECHE_PRIVATE_KEY).value
        try {
            def xml = marshal message
            log.debug '生成百度同步订单信息报文,订单号为 {},报文内容为:\n{}', partnerOrder.purchaseOrder.orderNo, xml
            xml
        } catch (e) {
            throw new IllegalStateException("百度同步xml转换异常：$e.stackTrace")
        }
    }

    private Message convertMessage(PartnerOrder partnerOrder) {
        def orderStatusMapping = [
            (HANDLING_2)       : '0', // 无记录
            (PENDING_PAYMENT_1): '1', // 核保通过（待支付）
            (INSURE_FAILURE_7) : '2', // 核保失败
//            (PENDING_PAYMENT_1): '3', // 支付处理中
            (FINISHED_5)       : '4', // 承保成功
            (PAID_3)           : '5', // 承保失败
            (REFUNDED_9)       : '6', // 退保成功
        ]
        def commonBills = new CommonBills(purchaseOrderService, supplementInfoService)
        def converter = commonBills.convert partnerOrder

        def state = new JsonSlurper().parseText partnerOrder.state
        def po = partnerOrder.purchaseOrder
        def auto = po.auto
        def insurance = purchaseOrderService.getInsuranceBillsByOrder po
        def compulsoryInsurance = purchaseOrderService.getCIBillByOrder po
        def payment = purchaseOrderService.getPaymentByPurchaseOrder po
        def header = new Header(
            spNo: findByPartnerAndKey(apiPartner(), PARTNER_SP_NO).value,
            channelId: state.channelId,
            requestTime: _DATE_FORMAT7.format(new Date())
        )
        def (holder, insured) = convertPersonMessage(po, insurance ?: compulsoryInsurance)
        def deliveryAddress = po.deliveryAddress
        def vehicle = new Vehicle(
            licensePlateNo: auto.licensePlateNo,
            licensePlateType: '02',
            motorTypeCode: '11',
            motorUsageTypeCode: '000',
            firstRegisterDate: _DATE_FORMAT2.format(auto.enrollDate),
            vin: auto.vinNo,
            engineNo: auto.engineNo,
            model: auto.autoType.code,
            noLicenseFlag: '0',
            newVehicleFlag: '0',
            chgOwnerFlag: '0', // TODO 是否过户
            ownerName: auto.owner,
            ownerCertNo: auto.identity,
            ownerCertType: getCertType(auto.identityType),
            reciverName: deliveryAddress.name,
            reciverMobile: deliveryAddress.mobile,
            reciverAddress: deliveryAddress.address
        )

        def baseApply = [
            applyNo   : state.applyNo,
            status    : orderStatusMapping[po.status],
            payTime   : payment ? _DATE_FORMAT7.format(payment.createTime) : null,
            acceptTime: payment ? _DATE_FORMAT7.format(payment.createTime) : null, // 承保时间
        ]

        new Message(head: header, body: new Body(
            policyList: converter.fields.inject([]) { policyList, field ->
                def policy = field.name in ['compulsoryPremium', 'autoTax'] ? compulsoryInsurance : insurance
                def fieldApply = baseApply + [
                    policyNo     : policy.policyNo,
                    applicationNo: policy.proposalNo ?: '000000',
                    validateDate : policy.effectiveDate ? _DATE_FORMAT2.format(policy.effectiveDate) : null,
                    expireDate   : policy.effectiveDate ? _DATE_FORMAT2.format(policy.expireDate) : null,
                    policyAmount : field.amount.value,
                ]
                policyList << new PolicyDetail(holder: holder, insuredList: [insured], vehicle: vehicle, apply: new Apply(fieldApply + [
                    premium          : field.premium,// 保费
                    payAmount        : field.premium,// 支付金额
                    coverageClassCode: '1',// 险种性质代码
                    coverageCode     : POLICY_CODE_MAPPING[field.name],// 险种代码
                ]))
                if (insurance.iopTotal && POLICY_CODE_MAPPING[field.name + 'Iop'] && field.iop) {
                    policyList << new PolicyDetail(holder: holder, insuredList: [insured], vehicle: vehicle, apply: new Apply(fieldApply + [
                        premium          : field.iop,// 保费
                        payAmount        : field.iop,// 支付金额
                        coverageClassCode: '2',// 险种性质代码
                        coverageCode     : POLICY_CODE_MAPPING[field.name + 'Iop'],// 险种代码
                    ]))
                }
                policyList
            } as ArrayList
        ))
    }

    private convertPersonMessage(purchaseOrder, insurance) {
        [encryptPersonMessage(new Holder(
            name: insurance.applicantName,
            certType: getCertType(insurance.applicantIdentityType),
            certNo: insurance.applicantIdNo,
            mobile: insurance.applicantMobile ?: purchaseOrder.applicant.mobile,
            email: insurance.applicantEmail ?: DEFAULT_EMAIL
        )),
         encryptPersonMessage(new Insured(
             name: insurance.insuredName,
             certType: getCertType(insurance.insuredIdentityType),
             certNo: insurance.insuredIdNo,
             mobile: insurance.insuredMobile ?: purchaseOrder.applicant.mobile,
             email: insurance.insuredEmail ?: DEFAULT_EMAIL
         ))]
    }

    private encryptPersonMessage(person) {
        try {
            ['Address', 'Email', 'CertNo', 'Name', 'Mobile'].each {
                person[it] = person[it] ? encryptByPublicKey(person[it], findByPartnerAndKey(apiPartner(), PARTNER_PUBLIC_KEY).value) : person[it]
            }
            person
        } catch (e) {
            throw new IllegalStateException("百度同步投（被）保人信息加密异常：$e.stackTrace")
        }
    }

    private static getCertType(insuredIdentityType) {
        def identityTypeMapping = [
            (IDENTITYCARD)                 : 'b', // 身份证
            (PASSPORT)                     : 'c', // 护照
            (OTHER_IDENTIFICATION)         : 'i', // 其他
            (TAIWAN_LAISSEZ_PASSER)        : 'j', // 港澳台护照
            (HONGKONG_MACAO_LAISSEZ_PASSER): 'k', // 港澳通行证
            (MTP)                          : 'l', // 台胞证
            (RESIDENCE_BOOKLET)            : 'm', // 户口本
            (OFFICERARD)                   : 'o', // 军官证
        ]
        identityTypeMapping[insuredIdentityType] ?: 'i'
    }
}
