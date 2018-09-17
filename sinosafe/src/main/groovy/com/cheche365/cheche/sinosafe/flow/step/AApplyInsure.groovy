package com.cheche365.cheche.sinosafe.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.sinosafe.flow.Constants._IDENTITY_TYPE_MAPPINGS
import static com.cheche365.cheche.sinosafe.flow.Constants._SPECIAL_PROMISE_CODE_MAPPINGS
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.createRequestParams
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.sendAndReceive2Map



/**
 * 核保
 */
@Slf4j
abstract class AApplyInsure implements IStep {

    private static final _TRAN_CODE = 100014
    private static final _DIDIPARAMATER = 'didicda'

    @Override
    run(context) {

        def verificationCode = context.additionalParameters.supplementInfo?.verificationCode
        if (verificationCode) {
            log.info "短信验证码录入： {}", verificationCode
            context.verificationCode = verificationCode
            return getContinueFSRV(verificationCode)
        }

        def result = sendAndReceive2Map(context, getRequestParams(context), log)
        def head = result.PACKET.HEAD
        if (head.RESPONSECODE in ['C00000000', 'C99999999']) {
            doDealInsureSuccess(context, result, head.ERRORMESSAGE)
        } else {
            getFatalErrorFSRV head.ERRORMESSAGE
        }
    }

    private static getRequestParams(context) {
        def auto = context.auto
        def order = context.order
        def applicant = order?.applicant
        def area = context.area
        def additionalParameters = context.additionalParameters

        def autoId = auto.identity

        def applicantIdentityType = context.insurance?.applicantIdentityType ?: context.compulsoryInsurance?.applicantIdentityType//投保人证件类型
        def applicantName = context.insurance?.applicantName ?: context.compulsoryInsurance?.applicantName ?: order?.applicantName ?: auto.owner//投保人姓名
        def applicantIdNo = context.insurance?.applicantIdNo ?: context.compulsoryInsurance?.applicantIdNo ?: order?.applicantIdNo ?: autoId//投保人证件号
        def applicantEmail = context.insurance?.applicantEmail ?: context.compulsoryInsurance?.applicantEmail//投保人邮箱
        def insuredIdentityType = context.insurance?.insuredIdentityType ?: context.compulsoryInsurance?.insuredIdentityType//被保人证件类型
        def insuredName = context.insurance?.insuredName ?: context.compulsoryInsurance?.insuredName ?: order?.insuredName ?: auto.owner//被保人姓名
        def insuredIdNo = context.insurance?.insuredIdNo ?: context.compulsoryInsurance?.insuredIdNo ?: order?.insuredIdNo ?: autoId//被保人证件号
        def insuredEmail = context.insurance?.insuredEmail ?: context.compulsoryInsurance?.insuredEmail //被保人邮箱

        def deliveryAddress = order?.deliveryAddress?.address ?: '北京东城区大取灯胡同2号乐家轩'
        log.info '当前为{}阶段，交货地址为：{}', context.quoting ? '报价' : '核保', deliveryAddress
        def userMobile = null//电话号
        def applicantMobile = null//投保人电话
        def insuredMobile = null//被保人电话
        if (context.channel == _DIDIPARAMATER) {
            userMobile = context.quoting ? randomMobile : context.additionalParameters.agent?.customer?.mobile
            log.info 'didicda渠道下,电话号码:{}', userMobile
        } else {
            //投保人电话
            applicantMobile = context.insurance?.applicantMobile ?: context.compulsoryInsurance?.applicantMobile
            //被保人电话
            insuredMobile = context.insurance?.insuredMobile ?: context.compulsoryInsurance?.insuredMobile
            log.info '非didicda渠道下,电话号码:{} additionalParameters={} applicant={}', userMobile, additionalParameters?.agent?.customer?.mobile, applicant?.mobile
        }


        def specialAgreement = additionalParameters?.supplementInfo?.specialAgreements
        def appntData = specialAgreement.collect {
            [
                PROD_NO      : '0302',// 产品编码,目前只有商业险特约
                APPNT_CDE    : _SPECIAL_PROMISE_CODE_MAPPINGS[it.code], //内部特约编码转换成外部特约编码
                APPNT_NME    : it.name,//特约名称
                APPNT_DTL    : it.content,//特约明细
                IS_MODIFY    : '0',// 是否可修改
                IS_NEEDED    : '1',//是否必选
                REMARK       : '',//备注
                MODIFIED_FLAG: '0' //是否已修改
            ]
        }
        def body = [
            BASE      : [
                PAY_PRSN_NME   : order?.applicantName ?: auto.owner, //付款人姓名
                CAL_APP_NO     : context.CAL_APP_NO,//context.CAL_APP_NO, //报价单号
                ORIG_FLG       : context.renewalInfo ? 1 : 0, //续保标志
                IS_ELEC        : 1, //电子投保标识
                APP_NME        : applicantName ?: auto.owner, //投保人姓名
                APP_CERT_TYPE  : _IDENTITY_TYPE_MAPPINGS[applicantIdentityType.id], //投保人证件类型
                APP_CERT_NO    : applicantIdNo, //投保人证件号码
                APP_TEL        : userMobile ?: applicantMobile, //投保人联系人电话
                EMAIL_APP      : applicantEmail, //投保人邮箱 北京必传
                APP_ADDR       : deliveryAddress, //投保人地址 北京、上海必传
                APP_ZIP        : area.postalCode, //投保人邮编 北京、上海必传
                INSRNT_CNM     : insuredName ?: auto.owner, //被保险人姓名
                INSRNT_ZIP     : area.postalCode, //被保险人邮编 北京、上海必传
                INSRNT_ADDR    : deliveryAddress, //被保险人地址 北京、上海必传
                EMAIL          : insuredEmail, //被保人邮箱 北京必传
                BIZ_CERT_TYPE  : _IDENTITY_TYPE_MAPPINGS[insuredIdentityType.id], //被保人证件类型
                BIZ_CERT_NO    : insuredIdNo, //被保人证件号码
                INSRNT_TEL     : userMobile ?: insuredMobile, //被保险人电话
                TAX_PERSON_TYPE: '03', // 纳税人类型 (个人)
                SLS_PER        : context.slsCode, //业务员编码
            ],
            APPNT_LIST: [APPNT_DATA: appntData]
        ]
        createRequestParams(context, _TRAN_CODE, body)
    }

    protected static checkResponseStatus(context, errorMsg, statusMsg, otherStatusMsg) {
        context.accurateInsurancePackage.with { insurancePackage ->
            def isCompulsoryOrAutoTaxQuoted = isCompulsoryOrAutoTaxQuoted(insurancePackage)
            def isCommercialQuoted = isCommercialQuoted(insurancePackage)
            def count = errorMsg.count(statusMsg) + errorMsg.count(otherStatusMsg)

            isCommercialQuoted && isCompulsoryOrAutoTaxQuoted ? 2 == count :
                isCommercialQuoted || isCompulsoryOrAutoTaxQuoted ? 1 == count : false
        }
    }

    abstract protected doDealInsureSuccess(context, result, errorMsg)
}
