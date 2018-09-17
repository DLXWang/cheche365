package com.cheche365.cheche.zhongan.util

import com.zhongan.scorpoin.signature.SignatureUtils
import groovy.json.JsonBuilder
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.json.JsonSlurperClassic
import groovy.util.logging.Slf4j

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

import static com.cheche365.cheche.common.util.AreaUtils.getProvincialCapitalCode
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.core.util.MockUrlUtil.findPrivateKey
import static com.cheche365.cheche.core.util.MockUrlUtil.findPublicKey
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.Constants._QUOTE_RECORD_ANNOTATION_META_CATEGORY_SHOW
import static com.cheche365.cheche.parser.Constants._QUOTE_RECORD_ANNOTATION_META_OPERATION_TYPE_STATIC_PAGE_COMPULSORY_MANUALLY_CONFIRM
import static com.cheche365.cheche.parser.Constants._COMPULSORY
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS_IOP
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THEFT_IOP
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.cheche.parser.util.BusinessUtils.resolveNewQuoteRecordInContext
import static com.cheche365.cheche.parser.util.BusinessUtils.uniqueSequenceNo
import static com.cheche365.cheche.parser.util.BusinessUtils.updateAutoSeats
import static com.cheche365.cheche.parser.util.InsuranceUtils._COMPOSITE_ALLOWED_POLICY_BASE
import static com.cheche365.cheche.parser.util.InsuranceUtils._JUDGE_SINGLE_ADVICE_BASE
import static com.cheche365.cheche.parser.util.InsuranceUtils._SINGLE_ALLOWED_POLICY_BASE
import static com.cheche365.cheche.parser.util.InsuranceUtils._CHECK_ADVICE_WITH_TRUE
import static com.cheche365.cheche.parser.util.InsuranceUtils._RENEW_QUOTE_POLICY
import static com.cheche365.cheche.zhongan.flow.Constants._DATE_FORMAT
import static com.cheche365.cheche.zhongan.flow.Constants._RESULT_CODE_GROUP1
import static com.cheche365.flow.business.flow.util.BusinessUtils.getVehicleOption
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static java.time.LocalDate.now as today
import static java.time.LocalDateTime.now as now

/**
 * 业务相关工具类
 */
@Slf4j
class BusinessUtils {

    //保险条款
    static final _GET_INSURANCECLAUSE = { context ->
        def url = getEnvProperty context, 'zhongan.insurance_clause_mobile_url'

        def reqURL = url + "?i=e#/exInsureConfirm?quoteId=${context.insureFlowCode}&isApi=1"

        def payload = [
            method     : 'GET',
            url        : reqURL,
            description: "保险条款"
        ]

        [
            payload : [payload],
            metaInfo: [
                operationType: _QUOTE_RECORD_ANNOTATION_META_OPERATION_TYPE_STATIC_PAGE_COMPULSORY_MANUALLY_CONFIRM,
                category     : _QUOTE_RECORD_ANNOTATION_META_CATEGORY_SHOW
            ]
        ]
    }


    private static final _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE = { propName, kindCode, propAmountName, context, insurancePackage, allKindItems, needCheckExpectedAmount = true ->
        def expectedAmount = insurancePackage[propName]
        def amountList = allKindItems.find { it.coverageCode == kindCode }?.sumInsureds?.reverse()

        def actualAmount = needCheckExpectedAmount ? (expectedAmount ?
            (adjustInsureAmount(expectedAmount, amountList, { it as double }, { it as double }) ?: 0)
            : 0)
            : (adjustInsureAmount(expectedAmount, amountList, { it as double }, { it as double }) ?: 0)

        actualAmount = actualAmount ?: 0L
        insurancePackage[propName] = actualAmount
        def newQuoteRecord = resolveNewQuoteRecordInContext context
        newQuoteRecord[propAmountName] = actualAmount as double
        actualAmount as int
    }

    private static final _AMOUNT_CONVERTER_FROM_JSON = { propName, kindCode, propAmountName, context, insurancePackage, allKindItems ->
        def practicalValue = getVehicleActualPrice(context)
        def newQuoteRecord = resolveNewQuoteRecordInContext context
        newQuoteRecord[propAmountName] = practicalValue as double
        insurancePackage[propName] ? practicalValue : ''
    }

    private static final _AMOUNT_CONVERTER_BOOLEAN = { propName, kindCode, context, insurancePackage, allKindItems ->
        if ('glass' == propName) {
            if (insurancePackage.glass) {
                def kindItem = allKindItems.find { it.coverageCode == kindCode }.glassTypes.tokenize(',').collect {
                    (it as int) + 1
                }
                if (kindItem && ((insurancePackage.glassType.id as int) in kindItem)) {
                    DOMESTIC_1 == insurancePackage.glassType ? 0 : 1
                } else if (kindItem) { //只有一种选择,改套餐
                    def actualGlassType = kindItem[0]
                    insurancePackage.glassType = actualGlassType == 1 ? DOMESTIC_1 : IMPORT_2
                    actualGlassType - 1
                }
            }
        } else {
            insurancePackage[propName] ? 1 : null
        }
    }

    private static final _AMOUNT_CONVERTER_BOOLEAN_IOP = { propName, kindCode, context, insurancePackage, allKindItems ->
        insurancePackage[propName]
    }

    private static final _KIND_CODE_CONVERTERS = [
        ['911', 'damage', 'damagePremium', false, _AMOUNT_CONVERTER_FROM_JSON.curry('damage', '911', 'damageAmount')], //机动车辆损失险

        ['912', 'thirdPartyAmount', 'thirdPartyPremium', false, _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE.curry('thirdPartyAmount', '912', 'thirdPartyAmount')], //第三者责任险

        ['913', 'driverAmount', 'driverPremium', false, _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE.curry('driverAmount', '913', 'driverAmount')], //车上人员责任险-司机

        ['914', 'passengerAmount', 'passengerPremium', false, _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE.curry('passengerAmount', '914', 'passengerAmount')], //车上人员责任险-乘客

        ['915', 'theft', 'theftPremium', false, _AMOUNT_CONVERTER_FROM_JSON.curry('theft', '915', 'theftAmount')], //盗抢险

        ['91A', 'glass', 'glassPremium', false, _AMOUNT_CONVERTER_BOOLEAN.curry('glass', '91A')], //玻璃单独破碎险

        ['91B', 'spontaneousLoss', 'spontaneousLossPremium', false, _AMOUNT_CONVERTER_FROM_JSON.curry('spontaneousLoss', '91B', 'spontaneousLossAmount')], //自燃损失险

        ['91C', 'scratchAmount', 'scratchPremium', false, _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE.curry('scratchAmount', '91C', 'scratchAmount')], //车身划痕损失险

        ['91D', 'designatedRepairShop', 'designatedRepairShopPremium', false, _AMOUNT_CONVERTER_BOOLEAN.curry('designatedRepairShop', '91D')],//指定修理厂险

        ['91E', 'engine', 'enginePremium', false, _AMOUNT_CONVERTER_BOOLEAN.curry('engine', '91E')], //发动机涉水险

        ['91F', 'unableFindThirdParty', 'unableFindThirdPartyPremium', false, _AMOUNT_CONVERTER_BOOLEAN.curry('unableFindThirdParty', '91F')],//无法找到第三方特约险

        ['91G', 'damageIop', 'damageIop', true, _AMOUNT_CONVERTER_BOOLEAN_IOP.curry('damageIop', '91G')], //不计免赔率险-车损

        ['91H', 'theftIop', 'theftIop', true, _AMOUNT_CONVERTER_BOOLEAN_IOP.curry('theftIop', '91H')], //不计免赔率险-盗抢

        ['91J', 'scratchIop', 'scratchIop', true, _AMOUNT_CONVERTER_BOOLEAN_IOP.curry('scratchIop', '91J')], //不计免赔率险-划痕

        ['91K', 'engineIop', 'engineIop', true, _AMOUNT_CONVERTER_BOOLEAN_IOP.curry('engineIop', '91K')],//不计免赔率险-发动机涉水

        ['91Q', 'thirdPartyIop', 'thirdPartyIop', true, _AMOUNT_CONVERTER_BOOLEAN_IOP.curry('thirdPartyIop', '91Q')], //不计免赔率险-三责

        ['91R', 'driverIop', 'driverIop', true, _AMOUNT_CONVERTER_BOOLEAN_IOP.curry('driverIop', '91R')], //不计免赔率险-司机

        ['91S', 'passengerIop', 'passengerIop', true, _AMOUNT_CONVERTER_BOOLEAN_IOP.curry('passengerIop', '91S')], //不计免赔率险-乘客

        ['91I', 'spontaneousLossIop', 'spontaneousLossIop', true, _AMOUNT_CONVERTER_BOOLEAN_IOP.curry('spontaneousLossIop', '91I')],//不计免赔率险-自燃

        // 未添加：精神损害抚慰金责任险、不计免赔率险-精神损害抚慰金
    ]

    //去掉交强险
    private static final _CHANGE_IS_COMPULSORY_INSURANCE = [
        '12422': 'isInsureCompelInsurance' //建议您的爱车交强险于（XX）日之后再来投保哦！
    ]
    //去掉商业险
    private static final _CHANGE_IS_COMMERCIAL_INSURANCE = [
        '10322': ['911', '912', '913', '914', '915', '91A', '91B', '91C', '91E', '91G', '91H', '91J', '91Q', '91R', '91S'], //建议您的爱车商业险于（XX）日之后再来投保哦！
    ]

    private static final _MINUS_PARAM_INSURE_TYPE_MAPPING = [
        //险种异常码  报价参数param中的险种码  param中的不计免赔码
        'B10309': ['915', '91H'], //不能投保盗抢险
        'B10310': ['915', '91H'], //不能投保盗抢险
        'B10311': ['915', '91H', '911', '91G'], //不能投保盗抢险/车损险
        'B10312': ['911', '91G'], //不能投保车损险
        'B10313': ['915', '91H'], //不能投保盗抢险
        'B10335': ['91C', '91J'], //非新车不投保划痕
        'B10353': ['91C', '91J'], //不投保划痕险
        'B10354': ['91B', '91I'], //不投自燃险
    ]

    private static final _UPDATE_AMOUNT_INSURE_TYPE_MAPPING = [
        'B10336': ['91C'], //划痕险保额不在可投保范围内
    ]

    private static final _INSURE_TYPE_DEFAULT_AMOUNT_MAPPING = [
        '91C': 2000, //划痕险保额不在可投保范围内
    ]


    static getStandardHintsFSRV(result) {
        log.debug "getStandardHintsFSRV —— result : {}", result
        if (result.result in _RESULT_CODE_GROUP1) {
            return getKnownReasonErrorFSRV(result.resultMessage)
        } else {
            return getFatalErrorFSRV("您的订单需要人工处理，稍后客服会与您联系")
        }
    }

    /**
     * 组装报价结果、禁用不能投保的险种
     */
    static populateQuoteRecord(context, accurateQuote) {
        def newQuoteRecord = resolveNewQuoteRecordInContext context

        newQuoteRecord.with { quoteRecord ->

            def seats = context.selectedCarModel?.vehiclePassengerCap as int
            // 更新座位数
            updateAutoSeats(context, seats)
            // 乘客数
            quoteRecord.passengerCount = seats - 1
            // 不计免赔总数
            def iopTotal = 0
            _KIND_CODE_CONVERTERS.each { kindCode, propName, premiumPropName, isIop, converter ->
                def kindItem = accurateQuote.coverageList.find {
                    it.coverageCode == kindCode
                }
                def insured = (null != kindItem)
                quoteRecord[premiumPropName] = insured ? (kindItem.coveragePreimum as double) : 0
                def iopPremium = isIop && insured ? (kindItem.coveragePreimum as double) : 0

                iopTotal += iopPremium
            }

            // 商业险总保费
            quoteRecord.premium = accurateQuote.businessSumPreimum ? accurateQuote.businessSumPreimum as double : 0
            // 商业险不计免赔总保费
            quoteRecord.iopTotal = new BigDecimal(iopTotal).setScale(2, BigDecimal.ROUND_HALF_UP)

            // 交强险和车船税
            quoteRecord.compulsoryPremium = accurateQuote.compelSumPreimum ? accurateQuote.compelSumPreimum as double : 0
            quoteRecord.autoTax = accurateQuote.taxPreimum ? accurateQuote.taxPreimum as double : 0

            quoteRecord
        }
    }

    /**
     * 禁用的套餐加入到quoteFieldStatus中
     */
    private static void disableUnavailableKindItems(context, minusInsure) {
        def newQuoteRecord = resolveNewQuoteRecordInContext context
        def insurancePackage = newQuoteRecord.insurancePackage
        _KIND_CODE_CONVERTERS.each { entry ->
            if (entry[0] == minusInsure) {
                insurancePackage[entry[1]] = (insurancePackage[entry[1]] instanceof Double ? 0 : false)
            }
        }
    }

    // TODO: 添加险种列表
    /*
    10308	亲，您不能只投保车损和车上人员责任险
    10314	亲，您的爱车必须同时投保车损险和商业三责险

     */

    static sendAndReceive(context, stepNote, serviceName, params) {
        try {
            def appKey = getEnvProperty context, 'zhongan.auth_app_key'
            def publicKey = findPublicKey(context.additionalParameters) ?: getEnvProperty(context, 'zhongan.auth_public_key')
            def privateKey = findPrivateKey(context.additionalParameters) ?: getEnvProperty(context, 'zhongan.auth_private_key')
            def thirdPartyCode = getEnvProperty context, 'zhongan.auth_third_party_code'

            def commonParams = [
                requestNo: uniqueSequenceNo, // 请求序列号
                thirdCode: thirdPartyCode    // 合作伙伴编码
            ]

            def reqJson = new JsonBuilder(commonParams + params).toString()

            log.debug 'cheche_sendAndReceive方法请求数据格式如下： {}', reqJson
/*
//新版SDK,另一种实现方式
          def client = new ZhongAnApiClient("uat", appKey, privateKey, "1.0.0")
            def request = new CommonRequest(serviceName)

            def param = new JSONObject()

            param.put("requestParam", reqJson)

            request.params = param

            CommonResponse response = client.call(request)

           def  resultJson =  response.bizContent*/

            def toSignMap = [
                appKey      : appKey,
                charset     : 'UTF-8',
                serviceName : serviceName,
                signType    : 'RSA',
                format      : 'json',
                version     : '1.0.0',
                timestamp   : _DATE_FORMAT.format(new Date()),
                requestParam: reqJson
            ]

            def signedMap = SignatureUtils.encryptAndSign(toSignMap, publicKey, privateKey, 'UTF-8', true, true)
            def args = [
                requestContentType: URLENC,
                contentType       : JSON,
                body              : [
                    sign       : signedMap.sign,
                    timestamp  : signedMap.timestamp,
                    bizContent : signedMap.bizContent,
                    signType   : signedMap.signType,
                    charset    : signedMap.charset,
                    appKey     : signedMap.appKey,
                    serviceName: signedMap.serviceName,
                    format     : signedMap.format,
                    version    : signedMap.version
                ]
            ]
            def restClient = context.client
            def resultJson = restClient.post args, { resp, json ->
                json
            }
            log.info '{}，返回报文---- {}', serviceName, resultJson
//            saveMutualMessage context, reqJson, stepNote, _MUTUAL_MESSAGE_TYPE_SEND
//            saveAppLog()

            def bizContent = SignatureUtils.checkSignAndDecrypt(resultJson, publicKey, privateKey, true, resultJson.bizContent as boolean) ?: '{}'

            log.debug 'Zhongan_sendAndReceive方法响应数据格式如下： {}', bizContent

            def bizContentJson = new JsonSlurper().with {
                type = JsonParserType.LAX
                parseText(bizContent)
            }

            resultJson.bizContent = bizContentJson

            if (bizContentJson?.responseResult) { //responseResult
                new JsonSlurperClassic().parseText(bizContentJson.responseResult)
            } else if (bizContentJson?.param) {
                new JsonSlurperClassic().parseText(bizContentJson.param)
            } else {
                resultJson
            }
        } catch (Exception ex) {
            log.warn '调用众安接口失败!!', ex
            new JsonSlurper().parseText('{"result" : "调用众安接口失败!!"}')
        }

    }

    static sendAndReceiveGet(context, stepNote, serviceName, params) {
        try {
            def appKey = getEnvProperty context, 'zhongan.auth_app_key'
            def publicKey = getEnvProperty context, 'zhongan.auth_public_key'
            def privateKey = getEnvProperty context, 'zhongan.auth_private_key'
            def thirdPartyCode = getEnvProperty context, 'zhongan.auth_third_party_code'

            def commonParams = [
                requestNo: uniqueSequenceNo, // 请求序列号
                thirdCode: thirdPartyCode    // 合作伙伴编码
            ]
            def reqJson = new JsonBuilder(commonParams + params).toString()

            log.info '{}，发送报文---- {}', serviceName, reqJson
//            saveMutualMessage context, reqJson, stepNote, _MUTUAL_MESSAGE_TYPE_SEND
//            saveAppLog()

            def toSignMap = [
                appKey      : appKey,
                charset     : 'UTF-8',
                serviceName : serviceName,
                signType    : 'RSA',
                format      : 'json',
                version     : '1.0.0',
                timestamp   : _DATE_FORMAT.format(new Date()),
                requestParam: reqJson
            ]
            def signedMap = encryptAndSign(toSignMap, publicKey, privateKey, 'UTF-8', true, true)

            def args = [
                requestContentType: URLENC,
                contentType       : JSON,
                body              : [
                    sign       : signedMap.sign,
                    timestamp  : signedMap.timestamp,
                    bizContent : signedMap.bizContent,
                    signType   : signedMap.signType,
                    charset    : signedMap.charset,
                    appKey     : signedMap.appKey,
                    serviceName: signedMap.serviceName,
                    format     : signedMap.format,
                    version    : signedMap.version
                ]
            ]
            def restClient = context.client
            def resultJson = restClient.get args, { resp, json ->
                json
            }

            def bizContent = checkSignAndDecrypt(resultJson, publicKey, privateKey, true, resultJson.bizContent as boolean)
            def bizContentJson = new JsonSlurper().with {
                type = JsonParserType.LAX
                parseText(bizContent)
            }

            resultJson.bizContent = bizContentJson

            log.info '{}，返回报文---- {}', serviceName, resultJson

            if (bizContentJson?.responseResult) { //responseResult
                new JsonSlurper().parseText(bizContentJson.responseResult)
            } else if (bizContentJson?.param) {
                new JsonSlurper().parseText(bizContentJson.param)
            } else {
                resultJson
            }
        } catch (Exception ex) {
            log.warn '调用众安接口失败!!', ex
            new JsonSlurper().parseText('{"result" : "调用众安接口失败!!"}')
        }

    }

    /**
     * 获取计算套餐险种参数
     */
    static getCustomPremiumParams(context) {
        def insurancePackage = context.accurateInsurancePackage
        def allKindItems = context.allKindItems

        _KIND_CODE_CONVERTERS.collect { kindCode, propName, propPremium, isIop, converter ->
            def converterResult = converter(context, insurancePackage, allKindItems)

            def kindItem = allKindItems.find {
                it.coverageCode == kindCode
            }

            if (kindItem && insurancePackage[propName]) {
                [
                    baseRiderType     : kindItem.baseRiderType,
                    coverageCode      : kindItem.coverageCode,
                    coverageName      : kindItem.coverageName,
                    isNonDeductible   : kindItem.isNonDeductible,
                    parentCoverageCode: kindItem.parentCoverageCode,
                    glassType         : '91A' == kindCode ? converterResult : '',
                    sumInsured        : kindCode in ['91A', '91E'] || '1' == kindItem.isNonDeductible ? '' : converterResult
                ]
            }

        } - null
    }

    /**
     * 返回车辆实际价格
     * 车辆实际价值的算法：price - (price * 0.006) *months
     * price是车型查询返回的新车购置价，months是车辆查询返回的车辆初登日期与当前日期相差的月数
     * 有两个限制，1，是一个月内的新车不折旧，2，是最高折旧80%
     * @param context
     * @return
     */
    private static getVehicleActualPrice(context) {
        if (context.vehicleActualPrice) {
            context.vehicleActualPrice as double
        } else { // TODO: 据众安的说法是一定能得到车辆的价格，但是为了以防万一，还是留着
            def price = context.selectedCarModel?.vehicleAcquisitionPrice as double
            def enrollDate = getCarEnrollDate context
            def intervalDay = 10
            def minDiscountPrice = price * 0.2
            def actualDiscountPrice = intervalDay <= 30 ? price : price - (price * 0.006) * (intervalDay / 30)
            minDiscountPrice > actualDiscountPrice ? minDiscountPrice : actualDiscountPrice
        }
    }

    /**
     * 返回车辆初登日期
     * @param context
     * @return
     */
    static getCarEnrollDate(context) {
        context.auto.enrollDate ?: new Date()
    }

    /**
     * 获取从明天到明年今天的日期
     */
    private static getDefaultInsurancePeriodText() {
        def today = today()
        def startDateText = _DATETIME_FORMAT3.format(today.plusDays(1))
        def endDateText = _DATETIME_FORMAT3.format(today.plusYears(1))

        new Tuple2(startDateText, endDateText)
    }

    /**
     *  获取从指定日期和下年的日期
     */
    private static getInsurancePeriod(startDate) {
        if (startDate) {
            def startDay = getLocalDate(startDate)
            new Tuple2(_DATETIME_FORMAT3.format(startDay), _DATETIME_FORMAT3.format(startDay.plusYears(1).minusDays(1)))
        } else {
            getDefaultInsurancePeriodText()
        }
    }

    /**
     * 通过身份证号码获取出生日期 1990-01-01
     */
    static getBirthdayFromId(id) {
        "${id[6..9]}-${id[10..11]}-${id[12..13]}" as String
    }

    /**
     * 拼装车型列表选择的map
     */
    static final _ZHONGAN_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand         : vehicle.vehicleBrand,
            family        : vehicle.vehicleFamily,
            gearbox       : vehicle.vehicleGear,
            exhaustScale  : vehicle.vehicleDisplacement as String,
            model         : vehicle.vehicleModel,
            productionDate: vehicle.vehicleConfigurationMode ?: '',
            seats         : vehicle.vehiclePassengerCap ?: '',
            newPrice      : vehicle.vehicleAcquisitionPrice,
        ]
        getVehicleOption vehicle.vehicleModelCode, vehicleOptionInfo
    }

    static getCityCode(area) {
        area.id in [110000L, 500000L, 120000L, 310000L] ? getProvincialCapitalCode(area.id) : area.id
    }

    //<editor-fold defaultstate="collapsed" desc="处理套餐建议">

    static final _QUOTING_PARAM_INSURE_TYPE_MAPPING = [
        //险种异常码  报价参数param中的险种码  param中的不计免赔码
        'B10309', //不能投保盗抢险
        'B10310', //不能投保盗抢险
        'B10311', //不能投保盗抢险/车损险
        'B10312', //不能投保车损险
        'B10313', //不能投保盗抢险
        'B10335', //非新车不投保划痕
        'B10353', //不投保划痕险
        'B10354', //不投自燃险
        '12422', //加强险
        'B10336',//划痕险保额
    ]

    private static final _POLICY_IGNORABLE_ADVICE = 0L
    //禁掉盗强险
    private static final _POLICY_CODE_FORBID_THEFT = _POLICY_IGNORABLE_ADVICE + 1 //盗抢险
    private static final _POLICY_CODE_FORBID_THEFT_IOP = _POLICY_CODE_FORBID_THEFT + 1
    //禁掉车损险
    private static final _POLICY_CODE_FORBID_DAMAGE = _POLICY_CODE_FORBID_THEFT_IOP + 1 //车损
    private static final _POLICY_CODE_FORBID_DAMAGE_IOP = _POLICY_CODE_FORBID_DAMAGE + 1
    //禁掉划痕险
    private static final _POLICY_CODE_FORBID_SCRATCH = _POLICY_CODE_FORBID_DAMAGE_IOP + 1 //划痕
    private static final _POLICY_CODE_FORBID_SCRATCH_IOP = _POLICY_CODE_FORBID_SCRATCH + 1
    //禁掉自燃险
    private static final _POLICY_CODE_FORBID_SPONTANEOUS_LOSS = _POLICY_CODE_FORBID_SCRATCH_IOP + 1 //自燃
    private static final _POLICY_CODE_FORBID_SPONTANEOUS_LOSS_IOP = _POLICY_CODE_FORBID_SPONTANEOUS_LOSS + 1
    //禁掉交强险
    private static final _POLICY_CODE_FORBID_INSURE_COMPEL_INSURANCE = _POLICY_CODE_FORBID_SPONTANEOUS_LOSS_IOP + 1//交强险
    //同时禁掉盗强险和车损险
    private static final _POLICY_CODE_FORBID_THEFT_AND_DAMAGE = _POLICY_CODE_FORBID_INSURE_COMPEL_INSURANCE + 1 //盗强和交强
    //调整划痕保额
    private static final _POLICY_CODE_ADJUST_THEFT_AND_DAMAGE = _POLICY_CODE_FORBID_THEFT_AND_DAMAGE + 1 //划痕保额


    static final _ADVICE_POLICY_MAPPINGS = [
        //禁掉划痕险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_SCRATCH))                : _SINGLE_ALLOWED_POLICY_BASE.curry(_SCRATCH_AMOUNT, _SCRATCH_IOP, 0, false),

        //禁掉盗强险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_THEFT))                  : _SINGLE_ALLOWED_POLICY_BASE.curry(_THEFT, _THEFT_IOP, 0, false),

        //禁掉车损险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_DAMAGE))                 : _SINGLE_ALLOWED_POLICY_BASE.curry(_DAMAGE, _DAMAGE_IOP, 0, false),

        //禁掉自燃险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_SPONTANEOUS_LOSS))       : _SINGLE_ALLOWED_POLICY_BASE.curry(_SPONTANEOUS_LOSS, _SPONTANEOUS_LOSS_IOP, 0, false),

        //禁掉交强险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_INSURE_COMPEL_INSURANCE)): _SINGLE_ALLOWED_POLICY_BASE.curry(_COMPULSORY, null, 0, false),

        //同时进到盗抢险和车损险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_THEFT_AND_DAMAGE))       : _COMPOSITE_ALLOWED_POLICY_BASE.curry([_THEFT, _DAMAGE], [_THEFT_IOP, _DAMAGE_IOP], false, false),

        //调整划痕保额
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_THEFT_AND_DAMAGE))       : _SINGLE_ALLOWED_POLICY_BASE.curry(_SCRATCH_AMOUNT, _SCRATCH_IOP, 2000, true),
        //兜底策略，防止NullPointException
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_IGNORABLE_ADVICE))                   : _RENEW_QUOTE_POLICY
    ]

    //利用众安返回的状态吗进行判断  找到对应的套餐建议码
    private static final _CHECK_ADVICE_BASE = { ZhongAnCodeMap, advice, context, others ->
        advice in ZhongAnCodeMap
    }

    private static final _COMMON_REGULATOR_BASE = { keyCode, advice, context, others ->
        [(keyCode): advice]
    }

    static final _ADVICE_REGULATOR_MAPPINGS = [
        //禁止投保盗强险
        (_CHECK_ADVICE_BASE.curry(['B10309', 'B10310', 'B10311'])): _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_THEFT),
        //禁止投保车损险
        (_CHECK_ADVICE_BASE.curry(['B10312']))                    : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_DAMAGE),
        //禁止投保划痕险
        (_CHECK_ADVICE_BASE.curry(['B10335', 'B10353']))          : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_SCRATCH),
        //禁止投保自燃险
        (_CHECK_ADVICE_BASE.curry(['B10354']))                    : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_SPONTANEOUS_LOSS),
        //禁止投保交强险
        (_CHECK_ADVICE_BASE.curry(['Z12422', '12422']))           : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_INSURE_COMPEL_INSURANCE),
        //同时禁止盗强险和车损险
        (_CHECK_ADVICE_BASE.curry(['B10311']))                    : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_THEFT_AND_DAMAGE),

        //调整划痕的保额
        (_CHECK_ADVICE_BASE.curry(['B10336']))                    : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_THEFT_AND_DAMAGE),
        //缺省策略
        (_CHECK_ADVICE_WITH_TRUE)                                 : _COMMON_REGULATOR_BASE.curry(_POLICY_IGNORABLE_ADVICE)

    ]

    static final _GET_EFFECTIVE_ADVICES = { advices, context, others ->
        [advices]
    }

    //</editor-fold>

    static final DateTimeFormatter _INSTANT_FORMAT = new DateTimeFormatterBuilder().appendPattern('yyyy-MM-dd HH:00:00').toFormatter()

    static getInstantInsurancePeriodTexts() {
        def newStartDate = now()
        def plusHour = newStartDate.getMinute() >= 15 ? 2 : 1
        newStartDate = newStartDate.plusHours(plusHour)
        def newEndDate = newStartDate.plusYears(1)//.minusDays(1)
        new Tuple(_INSTANT_FORMAT.format(newStartDate), _DATETIME_FORMAT3.format(newEndDate))
    }
}
