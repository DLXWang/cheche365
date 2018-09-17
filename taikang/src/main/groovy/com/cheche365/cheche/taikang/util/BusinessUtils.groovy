package com.cheche365.cheche.taikang.util

import groovyx.net.http.Method
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.core.model.LogType.Enum.TaiKang_57
import static com.cheche365.cheche.parser.Constants._INSURANCE_PACKAGE_FIELD_NAME_DESCRIPTION_MAPPINGS
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_COMMERCIAL_CAPTCHA_IMAGE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_COMPULSORY_CAPTCHA_IMAGE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.cheche.parser.util.BusinessUtils.saveAppLog
import static com.cheche365.cheche.taikang.flow.Constants._COMPULSORY_CHECK_FLAG
import static com.cheche365.cheche.taikang.flow.Constants._COMMERCIAL_CHECK_FLAG
import static com.cheche365.cheche.taikang.flow.Constants._TAIKANG_DRIVER_AMOUNT_LIST
import static com.cheche365.cheche.taikang.flow.Constants._TAIKANG_PASSENGER_AMOUNT_LIST
import static com.cheche365.cheche.taikang.flow.Constants._TAIKANG_SCRATCH_AMOUNT_LIST
import static com.cheche365.cheche.taikang.flow.Constants._TAIKANG_THIRD_PARTY_AMOUNT_LIST
import static java.math.BigDecimal.ROUND_HALF_UP
import static java.util.UUID.randomUUID
import static net.sf.json.JSONObject.fromObject as toJson
import static com.cheche365.cheche.common.util.ContactUtils.getGenderByIdentity
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._DRIVER_IOP
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._ENGINE_IOP
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._PASSENGER_IOP
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS_IOP
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THEFT_IOP
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_IOP
import static com.cheche365.cheche.parser.Constants._UNABLE_FIND_THIRDPARTY
import static com.cheche365.cheche.parser.Constants._DESIGNATED_REPAIR_SHOP
import static com.cheche365.cheche.parser.Constants._COMPULSORY
import static org.apache.commons.io.IOUtils.toByteArray
import static org.apache.commons.io.IOUtils.toInputStream
import static org.springframework.util.DigestUtils.md5DigestAsHex
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5



/**
 * 泰康公共方法
 */
class BusinessUtils {

    private static final _CONVERTER_FROM_PROPERTY = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        //iop附加险默认险别标识传0，保额为空
        [null, 0]
    }

    private static final _AMOUNT_CONVERTER_FROM_AMOUNT_LIST = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        def expectedAmount = insurancePackage[propName]
        def listAmount = kindItem?.amountList?.reverse()
        if (_GLASS == propName && expectedAmount) {
            def glassType = insurancePackage.glassType
            expectedAmount = (DOMESTIC_1 == glassType ? 1 : IMPORT_2 == glassType ? 2 : 0)
        }
        def actualAmount = expectedAmount ?
            (adjustInsureAmount(expectedAmount as int, listAmount, { item -> item as double },
                { item -> item as double }, 1) ?: 0) : 0
        def actual = actualAmount as int
        [actual, 0]
    }

    private static final _O2I_PREMIUM_CONVERTER = { context, innerKindCode, kindItem, amountName, premiumName, isIop,
                                                    iopPremiumName, extConfig ->
        def other = null
        if (_GLASS == innerKindCode) {
            other = ('1' == kindItem?.glassType) ? DOMESTIC_1 : ('2' == kindItem?.glassType) ? IMPORT_2 : null
        }
        if (_PASSENGER_AMOUNT == innerKindCode) {
            other = kindItem?.quantity ?: 0  // 座位数在上一步已-1
        }
        [
            isIop ? null : kindItem?.amount,
            isIop ? null : kindItem?.premium,
            isIop ? kindItem?.iopPremium : null,
            other
        ]
    }

    public static final _KIND_CODE_CONVERTERS_CONFIG = [
        ['BZ', _COMPULSORY, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null],
        ['01', _DAMAGE, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 机动车损失保险
        ['02', _THIRD_PARTY_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: _THIRD_PARTY_AMOUNT_LIST, outAmountList: _TAIKANG_THIRD_PARTY_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null], // 三责
        ['03', _THEFT, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 盗抢
        ['13', _SPONTANEOUS_LOSS, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 自燃险
        ['041', _DRIVER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: _DRIVER_AMOUNT_LIST, outAmountList: _TAIKANG_DRIVER_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null], // 司机
        ['044', _PASSENGER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: _PASSENGER_AMOUNT_LIST, outAmountList: _TAIKANG_PASSENGER_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null], // 乘客
        ['21', _SCRATCH_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: _SCRATCH_AMOUNT_LIST, outAmountList: _TAIKANG_SCRATCH_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null], // 划痕
        ['28', _UNABLE_FIND_THIRDPARTY, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 无法找到第三方
        ['31', _DESIGNATED_REPAIR_SHOP, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 无法找到第三方
        ['SW', _ENGINE, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 涉水险
        ['11', _GLASS, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: [0, 1, 2], outAmountList: [0, 1, 2]], _O2I_PREMIUM_CONVERTER, null], // 玻璃
        ['601', _DAMAGE_IOP, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 自燃
        ['602', _THIRD_PARTY_IOP, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 三责不及免赔
        ['603', _THEFT_IOP, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 盗抢不及免赔
        ['605', _DRIVER_IOP, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 司机不及免赔
        ['606', _PASSENGER_IOP, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 乘客不及免赔
        ['611', _SPONTANEOUS_LOSS_IOP, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 自燃不及免赔
        ['612', _SCRATCH_IOP, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 划痕不及免赔
        ['613', _ENGINE_IOP, _CONVERTER_FROM_PROPERTY, null, _O2I_PREMIUM_CONVERTER, null], // 涉水不及免赔
    ]

    /**
     * 内部的保额转换成外部的请求(内转外)
     */
    public static final _I2O_PREMIUM_CONVERTER = { context, outerKindCode, kindItem, result ->
        if (result) {
            def params = [
                kindFlag: result[1],   //不记免赔标志
                kindCode: outerKindCode
            ]
            if (outerKindCode in ['02', '21', '041']) { //车损险不送保额 免赔险 交强险不送保额
                params << [
                    amount: result[0]
                ]
            }
            if (outerKindCode == 'BZ') {
                params << [
                    amount: 122000
                ]
            }
            //==================需要确认怎么取值
            //车上人员责任险(司机)
            if (outerKindCode == '041') {
                params << [
                    quantity  : 1,    //投保人数/约定最高补偿天数
                    unitAmount: result[0]   //  单位保额
                ]
            }
            def seats = str2double(context.selectedCarModel?.carModel?.seatCount) as int
            def passengerSeats = context.auto.seats ? context.auto.seats - 1 : seats - 1
            //车上人员责任险(乘客)
            if (outerKindCode == '044') {
                params << [
                    amount    : result[0],
                    quantity  : passengerSeats,    //投保人数/约定最高补偿天数
                    unitAmount: result[0]   //  单位保额
                ]
            }
            if ('11' == outerKindCode) {  //玻璃险
                params << [
                    modeCode: result[0],
                ]
            }
            //extendInfos 是否进口 扩展字段
            params
        }
    }

    /**
     * md5加签 applyContent
     */
    private static md5Encode(origin) {
        md5DigestAsHex(toByteArray(toInputStream(origin, 'UTF-8')))
    }

    private static getBody(context, func, applyContent) {
        def bodyContent = [apply_content: applyContent]
        def head = [
            head: [
                version  : context.version,
                function : func,
                transTime: _DATE_FORMAT5.format(new Date()),
                channelId: context.channelId,
//                reqMsgId : randomAlphanumeric(24)
                reqMsgId : getRandomId()
            ]
        ]
        if (applyContent) {
            context.proposalFormId = context.proposalFormId ?: getRandomId()
            head.head << [
                proposalFormToken: context.token,
                proposalFormId   : context.proposalFormId,
                sign_type        : context.signType,
                sign             : md5Encode(context.channelKey + toJson(applyContent).toString())
            ]
        }
        head << bodyContent
    }

    /**
     * 泰康发送报文公共方法
     * @param context 上下文
     * @param func 请求参数中对应的function
     * @param applyContent 请求体中的内容
     * @return
     */
    static sendParamsAndReceive(context, func, applyContent, log) {
        def params = getBody context, func, applyContent
        log.debug '{} 泰康请求参数为：{}', func, toJson(params).toString()
        saveAppLog(context.logRepo, TaiKang_57, context.taskId, context.insuranceCompany?.name, toJson(params).toString(), func, "$context.auto.licensePlateNo:request")
        def result = context.client.request(Method.POST, 'application/json;charset=UTF-8') {
            body = params
            response.success = { resp, json ->
                json
            }
            response.failure = { resp, json ->
                json
            }
        }
        log.debug '{} 泰康返回结果为：{}', func, result
        saveAppLog context.logRepo, TaiKang_57, context.taskId, context.insuranceCompany?.name, result.toString(), func, "$context.auto.licensePlateNo:response"
        result
    }

    /**
     * 通过身份证号码获取出生日期 1990-01-01
     */
    private static getBirthdayFromId(id) {
        "${id[6..9]}-${id[10..11]}-${id[12..13]}" as String
    }

    /**
     * 起始时间加1年减一天拼接24:00:00
     * @param date
     * @return
     */
    static getEndTime(date) {
        if (date) {
            def cal = new GregorianCalendar()
            cal.time = date
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1)
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1)
            _DATE_FORMAT3.format(cal.getTime()) + ' 24:00:00'
        }
    }

    /**
     * 返回套餐中选中的、对方不支持的所有险别描述
     */
    static getUnsupportedInsurancePackagePropDescriptions(kindCodes, insurancePackage) {
        if (kindCodes) {
            def validKindCodes = kindCodes.kind.kindCode
            _KIND_CODE_CONVERTERS_CONFIG.findResults { configItem ->
                def (kindCode, propName) = configItem
                // 套餐中选中的、对方不支持的险别
                if (insurancePackage[propName] && !(kindCode in validKindCodes)) {
                    _INSURANCE_PACKAGE_FIELD_NAME_DESCRIPTION_MAPPINGS[propName]
                }
            }
        }
    }

    static getBizConfig(kindConvertConfig, riskInfo) {
        kindConvertConfig.findAll { configItem ->
            configItem[0] in riskInfo.risk.kindList.collect {
                kindInfo -> kindInfo.kind.kindCode
            }
        }
    }

    static String getInnerCode(outKindConfig) {
        _KIND_CODE_CONVERTERS_CONFIG.find { convertConfig -> convertConfig[0] == outKindConfig }[1]
    }

    static str2double(value) {
        ((value ?: 0) as BigDecimal).setScale(2, ROUND_HALF_UP).doubleValue()
    }

    //获取转保信息第一个check信息
    static getFirstCheck(result) {
        result.apply_content.data.checkList?.get(0)
    }

    //自动识别验证码赋值
    static updateDecaptchaText(context, checkFlag, text) {
        if (_COMMERCIAL_CHECK_FLAG == checkFlag) {
            context.additionalParameters.supplementInfo.commercialCaptchaImage = text
        }
        if (_COMPULSORY_CHECK_FLAG == checkFlag) {
            context.additionalParameters.supplementInfo.compulsoryCaptchaImage = text
        }
    }

    //填充验证码推送信息
    static getCaptchaImageSupplementInfo(quoteCheckList, currentBase64) {
        def supplementInfo = _COMMERCIAL_CHECK_FLAG == quoteCheckList.last().check.checkFlag ?
            _SUPPLEMENT_INFO_COMMERCIAL_CAPTCHA_IMAGE_TEMPLATE_QUOTING
            : _SUPPLEMENT_INFO_COMPULSORY_CAPTCHA_IMAGE_TEMPLATE_QUOTING
        mergeMaps supplementInfo, [meta: [imageData: currentBase64]]
    }

    //验证码替换
    static replaceCheckCode(checkList, checkFlag, replaceCode) {
        checkList.collect { checkInfo ->
            checkInfo.check.checkFlag == checkFlag ? mergeMaps(checkInfo, [check: [checkCode: replaceCode]]) : checkInfo
        }
    }

    private static getRandomId() {
        randomUUID().toString().replace('-', '')
    }

    static getAllKindItems(kindCodeConvertersConfig) {
        //collectEntries  ：list 转 map
        kindCodeConvertersConfig.collectEntries { outerKindCode, innerKindCoder, _2, itemFeatures, _4, _5 ->
            [
                (outerKindCode): [
                    amountList: itemFeatures?.inAmountList?.intersect(itemFeatures?.outAmountList)
                ]
            ]
        }
    }

    static createPrivy(flag, name, idNo, userMobile, identifyType, email) {
        [
            privy: [
                insuredFlag       : flag,                                              //关系人标志，投保人/被保险人/车主
                insuredName       : name,                                              //关系人名称
                identifyType      : (identifyType as String).padLeft(2, '0'),               //证件类型
                identifyNumber    : idNo,                                              //证件号码
                mobile            : userMobile,    //移动电话
                carinsureDrelation: '01',                                              //被保险人与车辆关系
                email             : email,                       //邮箱
                sex               : getGenderByIdentity(idNo),        //性别
                birthDate         : getBirthdayFromId(idNo),                           //出生日期
            ]
        ]
    }

    /**
     * 预处理图片验证码
     * @param context 上下文
     * @param result 返回报文
     */
    static preProcessCaptcha(context, result) {

        def checkInfo = getFirstCheck(result).check
        context.currentBase64 = checkInfo.checkCode
        context.quoteCheckList = replaceCheckCode(result.apply_content.data.checkList, checkInfo.checkFlag,
            checkInfo.checkFlag == _COMMERCIAL_CHECK_FLAG ?
            context.additionalParameters.supplementInfo?.commercialCaptchaImage
            : context.additionalParameters.supplementInfo?.compulsoryCaptchaImage)
    }
}
