package com.cheche365.cheche.aibao.util

import com.cheche365.cheche.aibao.flow.Constants
import groovy.util.logging.Slf4j
import groovyx.net.http.Method
import org.apache.commons.codec.binary.Base64

import static com.cheche365.cheche.aibao.flow.Constants.COMMERCIALCHECKFLAG
import static com.cheche365.cheche.aibao.flow.Constants.get_AIBAO_DRIVER_AMOUNT_LIST
import static com.cheche365.cheche.aibao.flow.Constants.get_AIBAO_PASSENGER_AMOUNT_LIST
import static com.cheche365.cheche.aibao.flow.Constants.get_AIBAO_SCRATCH_AMOUNT_LIST
import static com.cheche365.cheche.aibao.flow.Constants.get_AIBAO_THIRD_PARTY_AMOUNT_LIST
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.ContactUtils.getBirthdayByIdentity
import static com.cheche365.cheche.common.util.ContactUtils.getGenderByIdentity
import static com.cheche365.cheche.common.util.ContactUtils.getRandomMobile
import static com.cheche365.cheche.common.util.DateUtils.DATE_LONGTIME24_PATTERN
import static com.cheche365.cheche.common.util.DateUtils.getDateString
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.core.model.LogType.Enum.AIBAO_61
import static com.cheche365.cheche.parser.Constants.get_DAMAGE
import static com.cheche365.cheche.parser.Constants.get_DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants.get_DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants.get_DESIGNATED_REPAIR_SHOP
import static com.cheche365.cheche.parser.Constants.get_DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants.get_DRIVER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants.get_DRIVER_IOP
import static com.cheche365.cheche.parser.Constants.get_ENGINE
import static com.cheche365.cheche.parser.Constants.get_ENGINE_IOP
import static com.cheche365.cheche.parser.Constants.get_GLASS
import static com.cheche365.cheche.parser.Constants.get_IOP_PREMIUM_NOTHING
import static com.cheche365.cheche.parser.Constants.get_PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants.get_PASSENGER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants.get_PASSENGER_IOP
import static com.cheche365.cheche.parser.Constants.get_SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants.get_SCRATCH_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants.get_SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants.get_SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants.get_SPONTANEOUS_LOSS_IOP
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_COMMERCIAL_CAPTCHA_IMAGE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_COMPULSORY_CAPTCHA_IMAGE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants.get_THEFT
import static com.cheche365.cheche.parser.Constants.get_THEFT_IOP
import static com.cheche365.cheche.parser.Constants.get_THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants.get_THIRD_PARTY_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants.get_THIRD_PARTY_IOP
import static com.cheche365.cheche.parser.Constants.get_UNABLE_FIND_THIRDPARTY
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.cheche.parser.util.BusinessUtils.saveAppLog
import static groovyx.net.http.ContentType.TEXT
import static java.math.BigDecimal.ROUND_HALF_UP
import static net.sf.json.JSONObject.fromObject as toJson

@Slf4j
class BusinessUtils {

    private static final _CONVERTER_FROM_PROPERTIES = {
        context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
            0 // iop附加险默认险别标识传0，保额为空
    }

    private static final _AMOUNT_CONVERTER_FROM_AMOUNT_LIST = {
        context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
            def expectedAmount = insurancePackage[propName as String]
            if (!expectedAmount) return 0
            def listAmount = kindItem?.amountList?.reverse()

            if (_GLASS == propName && expectedAmount) {
                def glassType = insurancePackage.glassType
                expectedAmount = (DOMESTIC_1 == glassType ? 1 : IMPORT_2 == glassType ? 2 : 0)
            }
            def actualAmount = expectedAmount ?
                (adjustInsureAmount(expectedAmount as int, listAmount, { item -> item as double },
                    { item -> item as double }, 1) ?: 0) : 0
            actualAmount as int
    }

    /*
     * 外转内
     */
    static final _O2I_PREMIUM_CONVERTER = {
        context, innerKindCode, kindItem, amountName, premiumName, isIop, iopPremiumName, extConfig ->
            def other = 1
            if (_GLASS == innerKindCode) {
                other = ('1' == kindItem?.glassType) ? DOMESTIC_1 : ('2' == kindItem?.glassType) ? IMPORT_2 : null
            }
            if (_PASSENGER_AMOUNT == innerKindCode) {
                other = kindItem?.quantity ?: 1  // 座位数在上一步已-1
            }
            def amount = (kindItem?.amount ? kindItem?.amount / other : 0) as double
            def premium = (kindItem?.premium ?: 0) as double

            [amount, premium, _IOP_PREMIUM_NOTHING, other]
    }

    /**
     * 207 机动车停驶损失险
     * 208 车损险可选免赔额特约条款
     * 209 精神损害抚慰金责任险
     * 210 法定节假日责任限额翻倍险
     * 309 不计免赔（精神损害）
     */
    static final _KIND_CODE_CONVERTERS_CONFIG = [
        ['001', _DAMAGE, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 机动车损失保险
        ['002', _THIRD_PARTY_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: _THIRD_PARTY_AMOUNT_LIST, outAmountList: _AIBAO_THIRD_PARTY_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null], // 三者
        ['003', _THEFT, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 盗抢
        ['202', _SPONTANEOUS_LOSS, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 自燃险
        ['004', _DRIVER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: _DRIVER_AMOUNT_LIST, outAmountList: _AIBAO_DRIVER_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null], // 司机
        ['005', _PASSENGER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: _PASSENGER_AMOUNT_LIST, outAmountList: _AIBAO_PASSENGER_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null], // 乘客
        ['203', _SCRATCH_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: _SCRATCH_AMOUNT_LIST, outAmountList: _AIBAO_SCRATCH_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null], // 划痕
        ['205', _UNABLE_FIND_THIRDPARTY, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 无法找到第三方
        ['206', _DESIGNATED_REPAIR_SHOP, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 指定专修厂险
        ['204', _ENGINE, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 涉水险
        ['201', _GLASS, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: [0, 1, 2], outAmountList: [0, 1, 2]], _O2I_PREMIUM_CONVERTER, null], // 玻璃
        ['301', _DAMAGE_IOP, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 车损不计免赔
        ['302', _THIRD_PARTY_IOP, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 三责不计免赔
        ['303', _THEFT_IOP, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 盗抢不计免赔
        ['304', _DRIVER_IOP, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 司机不计免赔
        ['305', _PASSENGER_IOP, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 乘客不计免赔
        ['306', _SPONTANEOUS_LOSS_IOP, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 自燃不计免赔
        ['307', _SCRATCH_IOP, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 划痕不计免赔
        ['308', _ENGINE_IOP, _CONVERTER_FROM_PROPERTIES, null, _O2I_PREMIUM_CONVERTER, null], // 涉水不计免赔
    ]

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

    /**
     * 内部的保额转换成外部的请求(内转外)
     */
    static final _I2O_PREMIUM_CONVERTER = { context, outerKindCode, kindItem, result ->
        def params = [
            kindCode        : outerKindCode,
            noDeductKindCode: '',                        // 不记免赔标志
            insureFlag      : '1'                         // 承保标志1是0否
//                "kindName":"车辆损失险"，                // 险别名称（全称）N
//                "premium":"6474.08",                   // 实交保费 N
        ]
        // 车损险不送保额 免赔险 交强险不送保额
        if (outerKindCode in ['002', '203', '201']) params << [amount: result]
        // 车上人员责任险(司机)，车上人员责任险(乘客)
        if (outerKindCode in ['004', '005']) {
            def seats = str2double(context.auto.seats ?: context.selectedCarModel?.carModel?.seatCount) as int
            def passengerSeats = outerKindCode == '004' ? 1 : seats - 1
            params << [
                amount    : passengerSeats * result,
                quantity  : passengerSeats,       // 投保人数/约定最高补偿天数
                unitAmount: result                //  单位保额
            ]
        }
        params
    }

    static getBody = { context, applyContext, interfaceID ->
        def apply_content = [body: applyContext]
        // 添加url上的base64参数
        def param = toJson([system: context.systemId, interface: interfaceID, mode: '']).toString() + ' '
        log.debug '爱保科技加密前路径param参数为：{}', param
        def urlParam = Base64.encodeBase64String(param.getBytes('utf-8'))

        def head = [
            head: [
                transactionNo     : TransCodeUtils.nextId(),
                operator          : context.systemId,
                timeStamp         : getDateString(new Date(), DATE_LONGTIME24_PATTERN),
                aiBaoTransactionNo: context.aiBaoTransactionNo ?: getRandomId(),
            ]
        ]
        def params = head << apply_content
        [params, urlParam]
    }

    /**
     * 爱保发送报文公共方法
     * @param context 上下文
     * @param applyContent 请求体中的内容
     * @return
     */
    static sendParamsAndReceive(context, applyContent, log, interfaceID) {
        def requestParams = getBody context, applyContent, interfaceID
        String encrykey = context.encrykey
//        log.debug '爱保科技加密后路径param参数为:{}', requestParams[1]
        log.debug '{} 爱保科技请求body参数为：{}', interfaceID, toJson(requestParams[0]).toString()
        def encryptStr = AESUtil.encrypt(toJson(requestParams[0]).toString(), encrykey)

//        log.debug '爱保科技请求body加密后为:{}', encryptStr
        saveAppLog(context.logRepo, AIBAO_61, context.taskId, context.insuranceCompany?.name, toJson(requestParams[0]).toString(), interfaceID, '$context.auto.licensePlateNo:request')
        def result = context.client.request(Method.POST, TEXT) {
            uri.query = [param: requestParams[1]]
            body = encryptStr
            response.success = { resp, text ->
                AESUtil.decrypt(text.readLine(), encrykey)
            }
            response.failure = { resp, text ->
                json
            }
        }

        log.debug '{} 爱保科技返回结果为：{}', interfaceID, result
        saveAppLog context.logRepo, AIBAO_61, context.taskId, context.insuranceCompany?.name, result.toString(), interfaceID, '$context.auto.licensePlateNo:response'
        toJson(result)
    }

    /**
     * 获取车的信息
     */
    static getCarInfo(context) {
        def auto = context.auto
        def carInfo = [
            vehicleCode: context.selectedCarModel?.vehicleCode,
            frameNo    : auto.vinNo,                                     // 车架号
            engineNo   : auto.engineNo,                                 // 发动机号
            enrollDate : _DATE_FORMAT3.format(auto.enrollDate),          // 初登日期
//            isLoanVehicleFlag: '0',                                   // 是否车贷投保多年1：是，0：否 默认否
        ]
        def chgOwnerFlag = context.additionalParameters?.supplementInfo?.transferDate
        // 否过户车
        if (chgOwnerFlag) carInfo << [
            chgOwnerFlag: '1',
            transferDate: _DATE_FORMAT3.format(chgOwnerFlag)
        ]
        // TODO 上海、天津、北京，新车需要补充信息
        def extendInfo = [
            buyCarDate      : '',            // 购车发票日期，上海地区 新车未上牌必传
            vehicleType     : '',            // 车辆类型，天津地区必返回，参见天津地区车辆类型代码
            traveltaxAddress: '',            // 行驶证;登记地区，天津地区必返回，参见天津登记地区代码
            carproofdate    : '',            // 开具车辆来历凭证所在日期，北京地区 新车未上牌必返回
            fueltype        : '',            // 燃料种类，北京地区 新车未上牌必返回，参见能源种类代码
            carprooftype    : '',            // 车辆来历凭证种类，北京地区 新车未上牌必返回，参见车辆来历凭证种类代码
            carproofno      : '',            // 车辆来历凭证编号，北京地区 新车未上牌必返回
        ]
        [carInfo: carInfo, extendInfo: extendInfo]
    }

    /**
     * 创建车主信息
     */
    static getCarOwnerInfo(context) {
        def auto = context.auto
        def userMobile = context.additionalParameters?.supplementInfo?.verificationMobile ?: context.order?.applicant?.mobile ?: randomMobile
        _AIBAO_IDENTITY_MAPPINGS context, auto.identity, auto.owner, userMobile, context.defaultEmail
    }

    /**
     * 创建投保被保人信息
     *
     * @param context
     * @param isApplicant true：投保人，false：被保人
     * @return 人员信息map
     */
    static getUserInfo(context, isApplicant = false) {
        def auto = context.auto
        def order = context.order
        def insurances = context.insurance ?: context.compulsoryInsurance
        def verificationMobile = context.additionalParameters?.supplementInfo?.verificationMobile
        def defaultMobile = verificationMobile ?: order?.applicant?.mobile ?: randomMobile

        def userIdNo, userName, userMobile, email
        if (isApplicant) {
            userIdNo = insurances?.applicantIdNo ?: auto.identity       // 投保人身份证
            userName = insurances?.applicantName ?: auto.owner          // 投保人姓名
            userMobile = insurances?.applicantMobile ?: defaultMobile
            email = insurances?.applicantEmail ?: context.defaultEmail
        } else {
            userIdNo = insurances?.insuredIdNo ?: auto.identity       // 被保保人身份证
            userName = insurances?.insuredName ?: auto.owner          // 投保人姓名
            userMobile = insurances?.insuredMobile ?: defaultMobile
            email = insurances?.insuredEmail ?: context.defaultEmail
        }

        _AIBAO_IDENTITY_MAPPINGS context, userIdNo, userName, userMobile, email
    }

    // 人员信息map
    static final _AIBAO_IDENTITY_MAPPINGS = { context, identityId, idName, mobile, email ->
        def address = context.order?.deliveryAddress?.address ?: ''
        [
            birthday: _DATE_FORMAT3.format(getBirthdayByIdentity(identityId)),                    // 生日
            idNo    : identityId,                                                                 // 身份证号
            idType  : getMappingsValue(context.auto.identityType?.id, 'IDENTITYTYPE'), // 持有人类型代码 01个人
            idName  : idName,                                                                     // 姓名
            sex     : getMappingsValue(getGenderByIdentity(identityId), 'SEX'),         // 性别
            mobile  : mobile,
            email   : email,
            address : address
        ]
    }

    /**
     * 返回证件类型性别等的内转外值
     *
     * @param key 内部映射关系的 key
     * @param valueName 拼接 constants 中的映射集合
     * @return 外部的值
     */
    static getMappingsValue(key, valueName) {
        def mappings = Constants.getAt('_' + valueName + '_MAPPINGS')
        mappings ? mappings[key] ?: mappings['default'] : ''
    }

    /**
     * 获取验证码
     */
    static getVeriteCodes(context) {
        def veriteCodes = context.veriteCodes
        def commercialCaptchaImage = context.additionalParameters.supplementInfo?.commercialCaptchaImage,
            compulsoryCaptchaImage = context.additionalParameters.supplementInfo?.compulsoryCaptchaImage
        [
            bzVerifyCode  : veriteCodes?.bzVerifyCode ?: compulsoryCaptchaImage ?: '',
            busiVerifyCode: veriteCodes?.busiVerifyCode ?: commercialCaptchaImage ?: ''
        ]
    }

    /**
     * 将数字类型字符串转为，double 类型数字
     */
    static str2double(value) {
        ((value ?: 0) as BigDecimal).setScale(2, ROUND_HALF_UP).doubleValue()
    }

    /**
     * 填充验证码推送信息
     */
    static supplyInfo(Map<String, String> imagesBase64, needSupplementInfo = []) {
        // 验证码的 base64 编码集合，包括交强险、商业险
        imagesBase64.collectEntries { key, val ->
            def tag = COMMERCIALCHECKFLAG == key ? _SUPPLEMENT_INFO_COMMERCIAL_CAPTCHA_IMAGE_TEMPLATE_QUOTING :
                _SUPPLEMENT_INFO_COMPULSORY_CAPTCHA_IMAGE_TEMPLATE_QUOTING
            needSupplementInfo << mergeMaps(tag, [meta: [imageData: imagesBase64[key]]])
        }
        needSupplementInfo
    }

    static getRandomId() {
        UUID.randomUUID().toString().replace('-', '')
    }

}
