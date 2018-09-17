package com.cheche365.cheche.sinosafe.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.Constants._DATE_FORMAT5
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.parser.Constants._AUTO_TAX
import static com.cheche365.cheche.parser.Constants._GLASS_TYPE
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMMERCIAL_CAPTCHA_IMAGE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMPULSORY_CAPTCHA_IMAGE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.addQFSMessage
import static com.cheche365.cheche.parser.util.BusinessUtils.checkVehicleSupplementInfo
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.getNotQuotedPolicyCauseFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.sinosafe.flow.Constants._USE_CHARACTER_MAPPINGS
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.CreateNewQR
import static com.cheche365.cheche.sinosafe.util.BusinessUtils._QUOTE_PRICE_MAPPINGS
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.createRequestParams
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.getCoverageInfoList
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.getPayTaxVou
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.parameterHandle
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.premiumToDouble
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.sendAndReceive2Map
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getNeedSupplementInfoFSRV



/**
 * 报价
 * create by sufc
 *
 */
@Slf4j
class RRCQuotePrice implements IStep {

    private static final _TRAN_CODE = 100004
    private static final _C00000000 = 'C00000000'
    private static final _RENRENCHEPARAMETER = 'renrenche'

    private static final _DECAPTCHA_HANDLER = { context, head, body ->

        if (_C00000000 == head.RESPONSECODE) {

            context.CAL_APP_NO = body.SY_BASE?.CAL_APP_NO ?: body.JQ_BASE?.CAL_APP_NO ?: null
            context.needSupplementInfos = []
            //如果有question 说明华安没有识别成功
            if (body.SY_BASE?.QUESTON || body.JQ_BASE?.QUESTON) {

                if (body.JQ_BASE?.QUESTON) {

                    context.JQ_DEMAND_NO = body.JQ_BASE?.QUERY_SEQUENCE_NO

                    if (isCompulsoryOrAutoTaxQuoted(context.insurancePackage)) {
                        context.needSupplementInfos << mergeMaps(_SUPPLEMENT_INFO_COMPULSORY_CAPTCHA_IMAGE_TEMPLATE_QUOTING, [meta: [imageData: body.JQ_BASE.QUESTON]])
                    }
                }

                if (body.SY_BASE?.QUESTON) {
                    context.SY_DEMAND_NO = body.SY_BASE?.QUERY_SEQUENCE_NO
                    if (isCommercialQuoted(context.insurancePackage)) {
                        context.needSupplementInfos << mergeMaps(_SUPPLEMENT_INFO_COMMERCIAL_CAPTCHA_IMAGE_TEMPLATE_QUOTING, [meta: [imageData: body.SY_BASE.QUESTON]])
                    }
                }
                //如果用户输入了，我们就拿着用户去报价
                if (context.additionalParameters.supplementInfo?.commercialCaptchaImage || context.additionalParameters.supplementInfo?.compulsoryCaptchaImage) {
                    log.info "用户已经补充验证码"
                    getLoopContinueFSRV(null, head.RESPONSECODE)
                } else if (body.SY_BASE?.QUESTON || body.JQ_BASE?.QUESTON) { //用户没有补充就把验证码推送到前端
                    log.info "推送验证码给前端"
                    getNeedSupplementInfoFSRV { context.needSupplementInfos }
                }
            } else { //只有在校验通过以后，才会走到此分支
                context.quoteResult = body
                //非人人车渠道下，报价两次，第二次报价避免了在交强险被禁掉的情况下，直接核保，会出现承保保费大于0的问题
                //用来判定第一次交强险是否返回0,如果是0,则需要重新报价,解决交强险为0时,系统禁掉交强,但还拿着返回的商业险和交强险报价单号去报价
                if (body.JQ_BASE != null && body.JQ_BASE.PREMIUM == '0.0') {
                    context.quotePriceCount = 0
                }
                getCompulsoryPremiumAndAutoTax(context, body)
                CreateNewQR(context, head.ERRORMESSAGE, body)
                getContinueFSRV body
            }
        }
    }

    private static final _GET_VEHICLE_HANDLER = { context, head, body ->
        if (head.ERRORMESSAGE.contains('精友车型编码为') && !context.carQuotePriceCount) {
            if (context.additionalParameters?.supplementInfo?.autoModel != null) {
                getKnownReasonErrorFSRV '选择的车辆不能报价,请选择其他车辆'
            } else {
                def errorMsg = head.ERRORMESSAGE
                log.info "报价失败，原因： {}", errorMsg
                def m = errorMsg =~ /.*精友车型编码为：([a-zA-Z\d]*).*/
                if (m.find()) {
                    context.accurateSelectedCarModel = m[0][1]
                    if (body.VHL_LIST && body.VHL_LIST.VHL_DATA) {
                        if (body.VHL_LIST.VHL_DATA instanceof Collection) {
                            context.carPrice = body.VHL_LIST.VHL_DATA.find { it ->
                                if (it.MODEL_CODE == m[0][1]) {
                                    it
                                }
                            }.with { it ->
                                it.CAR_PRICE
                            }
                        } else {
                            context.carPrice = body.VHL_LIST.VHL_DATA.CAR_PRICE
                        }
                        context.quotePriceCount = 0
                    }
                    context.carQuotePriceCount++
                    getLoopContinueFSRV null, errorMsg
                }
            }
        } else if (context.carQuotePriceCount) {
            //用平台返回的车报价,还是出现车型码错误,则返回车型列表,退出报价
            return checkVehicleSupplementInfo(context, ['byCode': context.resultByCode])
        }
    }
    private static final _PUSH_CAPTCHA_HANDLER = { context, head, body ->
        if (head.ERRORMESSAGE.contains('录入的校验码有误') || head.ERRORMESSAGE.contains('K0M519H6GU9OPVVU')) {
            log.info "验证码识别失败,把验证码推到前段"
            getNeedSupplementInfoFSRV { context.needSupplementInfos }
        }
    }

    private static final _DECAPTCHA_SUCCESS_HANDLER = { context, head, body ->
        if (head.ERRORMESSAGE.contains('平台校验：成功') && head.ERRORMESSAGE.contains('重复投保')) {
            log.info "验证码识别成功，但该车重复投保"
            getKnownReasonErrorFSRV "重复投保"
        } else if (head.ERRORMESSAGE.contains('平台校验：成功') || head.ERRORMESSAGE.contains('K0M51ITK9ELIZZR8')) {
            log.info "验证码识别成功，重新报价"
            getLoopContinueFSRV null, head.ERRORMESSAGE
        }
    }

    private static final _NOT_FOUND_VEHICLE_HANDLER = { context, head, body ->
        if (head.ERRORMESSAGE.contains('没有找到车型相关信息') || head.ERRORMESSAGE.contains('D0M4CTHJTXHA7AM4')) {
            // 商业险平台返回：没有找到车型相关信息，请核实车辆信息是否准确或提交电子联系单处理
            getValuableHintsFSRV(context, [
                _VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto?.autoType?.code
                    it
                }])
        }
    }

    private static final _DEFAULT_HANDLER = { context, head, body ->
        //兜底处理
        if (body?.CVRG_LIST?.CVRG_DATA) {
            context.quoteResult = body
            context.CAL_APP_NO = body.SY_BASE?.CAL_APP_NO ?: body.JQ_BASE?.CAL_APP_NO ?: null
            getCompulsoryPremiumAndAutoTax(context, body)
            //非人人车渠道下，报价两次，第二次报价避免了在交强险被禁掉的情况下，直接核保，会出现承保保费大于0的问题
            //用来判定第一次交强险是否返回0,如果是0,则需要重新报价,解决交强险为0时,系统禁掉交强,但还拿着返回的商业险和交强险报价单号去报价
            if (body.JQ_BASE != null && body.JQ_BASE.PREMIUM == '0.0') {
                context.quotePriceCount = 0
            }
            CreateNewQR(context, head.ERRORMESSAGE, body)
            getContinueFSRV body
        } else {
            getFatalErrorFSRV head.ERRORMESSAGE
        }
    }

    private static getCompulsoryPremiumAndAutoTax(context, body) {
        def insurancePackage = context.accurateInsurancePackage

        def allKindItems = parameterHandle(body, insurancePackage)
        def compulsoryPremium = 0
        def autoTax = 0
        if (allKindItems['0357']) {
            compulsoryPremium = premiumToDouble allKindItems['0357'].premium
        }
        if (body.VHLTAX) {
            autoTax = premiumToDouble body.VHLTAX.SUM_UP_TAX
        }
        if (!compulsoryPremium) {
            disableCompulsoryAndAutoTax context
        }
        if (compulsoryPremium && !autoTax) {
            context.accurateInsurancePackage.autoTax = false
            addQFSMessage context, _AUTO_TAX, '不支持投保车船税'
        }
    }

    private static final _BRANCHES = [
        _DECAPTCHA_HANDLER,
        _GET_VEHICLE_HANDLER,
        _PUSH_CAPTCHA_HANDLER,
        _DECAPTCHA_SUCCESS_HANDLER,
        _NOT_FOUND_VEHICLE_HANDLER,
        _DEFAULT_HANDLER
    ]

    @Override
    Object run(Object context) {

        //检查交强险、商业险都能投保
        def fsrv = getNotQuotedPolicyCauseFSRV context
        if (fsrv) {
            fsrv
        } else {
            if (context.quotePriceCount == 1) {
                getLoopBreakFSRV '可以正常报价,不进行第二次报价'
            } else {
                def result = sendAndReceive2Map(context, getRequestParams(context), log)
                def head = result.PACKET.HEAD
                def body = result.PACKET.BODY
                _BRANCHES.findResult { handler ->
                    handler(context, head, body)
                }
            }
        }
    }

    private static getRequestParams(context) {
        //车型代码
        def vehicleModelCode = context.accurateSelectedCarModel ?: context.selectedCarModel?.MODEL_CODE
        //新车购置价
        def newPrice = context.carPrice ?: context.selectedCarModel?.CAR_PRICE
        def coverageInfoList = getCoverageInfoList context
        Auto auto = context.auto
        def userMobile = randomMobile // 用户手机
        def transferFlag = context?.additionalParameters?.supplementInfo?.transferDate
        def glassType = context.accurateInsurancePackage[_GLASS_TYPE] ?: null

        //起保日期
        def bsStartDate = context.additionalParameters.supplementInfo?.commercialStartDate ? _DATE_FORMAT5.format(context.additionalParameters.supplementInfo.commercialStartDate) : null
        def bzStartDate = context.additionalParameters.supplementInfo?.compulsoryStartDate ? _DATE_FORMAT5.format(context.additionalParameters.supplementInfo.compulsoryStartDate) : null

        //是否是过户车
        def params = [
            BASE     : [
                DPT_CDE           : context.cityCode, //县级编码
                CMPNY_AGT_CDE     : context.proxyCode, //代理人编码
                APP_NME           : auto.owner, //车主姓名
                CMPNY_AGT_NME     : '泛华时代',
                APP_CERT_TYPE     : '120001',
                BIZ_CERT_TYPE     : '120001',
                INSRNT_TEL        : userMobile, //被保险人电话
                APP_TEL           : userMobile, //投保人联系人电话
                PAYTAX_VOU        : getPayTaxVou(context), // 完税凭证号（减免税证明号） 减税或完税时必传
                PAYTAX_REVENUECODE: '', // 开具税务机关代码 完税时必传
                PAYTAX_REVENUE    : '地税局', // 开具税务机关 减税或完税时必传
                APP_CERT_NO       : '', //投保人证件号码
                BIZ_CERT_NO       : '', //被保人证件号码
                INSRNC_BGN_TM     : bsStartDate,//商业险起保日期
                INSRNC_BGN_TM_JQ  : bzStartDate,//加强险起止日期
                INSTITUTE_CDE     : context.instituteCode, // 机构代码
                TAX_PERSON_TYPE   : '03', // 纳税人类型 (个人)
                SY_ANSWER         : context.additionalParameters.supplementInfo?.commercialCaptchaImage ?: '', // 商业验证码答案
                JQ_ANSWER         : context.additionalParameters.supplementInfo?.compulsoryCaptchaImage ?: '', // 交强验证码答案
                SY_DEMAND_NO      : context.SY_DEMAND_NO, // 商业转保查询码
                JQ_DEMAND_NO      : context.JQ_DEMAND_NO, // 交强转保查询码
                CALC_APP_NO       : context.CAL_APP_NO, // 商业交强报价单号(合并单号)
                SLS_CDE           : context.slsCode, //业务员编码
            ],
            VHL      : [
                LCN_NO       : context.additionalParameters.supplementInfo.newCarFlag ? '*-*' : auto.licensePlateNo, //车牌号码
                ENG_NO       : auto.engineNo, //发动机号
                VHL_FRM      : auto.vinNo, //车架号
                BRND_CDE     : vehicleModelCode, //厂牌车型代码
                REGISTER_DATE: _DATE_FORMAT3.format(auto.enrollDate), //初等日期
                GLASS_TYPE   : glassType ? glassType.id == 1 ? '303011001' : '303011002' : '', //玻璃类型 303011001国内
                DRV_OWNER    : auto.owner, //车主姓名
                CERTI_TYPE   : (1L == auto.identityType.id ? '120001' : '120011'), //证件类型
                CERTI_CODE   : auto.identity, //证件号码
                CERTI_TEL    : randomMobile, //车主电话
                VHL_VAL      : newPrice,//新车购置价,
                CHGOWNERFLAG : transferFlag ? 1 : '',
                TRANSFERDATE : transferFlag ? _DATE_FORMAT3.format(context.additionalParameters.supplementInfo.transferDate) : '',
                BELONG_TYPE  : auto.useCharacter ? _USE_CHARACTER_MAPPINGS[auto.useCharacter.id] : '343002'
            ],
            CVRG_LIST: [
                CVRG_DATA: [
                    coverageInfoList
                ]
            ]

        ]
        params.VHL = params.VHL + getObjectByCityCode(context.area, _QUOTE_PRICE_MAPPINGS)
        //如果前台传进来折扣系数,那么,就用当前传的折扣系数,如果传进来为空,则不传折扣
        if (context.channel == _RENRENCHEPARAMETER && context.additionalParameters?.discount) {
            params.BASE << [APPLY_TOTAL_ADJUST: context.additionalParameters?.discount]
        }
        context.quotePriceCount++
        createRequestParams(context, _TRAN_CODE, params)
    }

}
