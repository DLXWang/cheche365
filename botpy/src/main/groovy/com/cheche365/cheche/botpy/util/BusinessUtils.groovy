package com.cheche365.cheche.botpy.util

import com.cheche365.cheche.botpy.flow.Constants
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.InsurancePackage
import net.sf.json.JSONObject

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.time.LocalDate

import static com.cheche365.cheche.botpy.flow.CityCodeMappings._CITY_CODE_MAPPINGS
import static com.cheche365.cheche.common.util.ContactUtils.getRandomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._DRIVER_IOP
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._ENGINE_IOP
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._PASSENGER_IOP
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS_IOP
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THEFT_IOP
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_IOP
import static com.cheche365.cheche.parser.Constants._UNABLE_FIND_THIRDPARTY
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.GET
import static java.lang.System.currentTimeMillis
import static java.math.BigDecimal.ROUND_HALF_UP



/**
 * 工具集
 * Created by Huabin on 2016/6/24.
 */
class BusinessUtils {

    static final _KIND_CODE_INSURANCE_PACKAGE_MAPPINGS = [
        ['damage', _DAMAGE, 'exempt_damage', _DAMAGE_IOP, true], // 机动车辆损失险
        ['third', _THIRD_PARTY_AMOUNT, 'exempt_third', _THIRD_PARTY_IOP, false], // 第三者责任险
        ['driver', _DRIVER_AMOUNT, 'exempt_driver', _DRIVER_IOP, false], // 车上人员责任险-司机
        ['passenger', _PASSENGER_AMOUNT, 'exempt_passenger', _PASSENGER_IOP, false], // 车上人员责任险-乘客
        ['pilfer', _THEFT, 'exempt_pilfer', _THEFT_IOP, true], // 盗抢险
        ['scratch', _SCRATCH_AMOUNT, 'exempt_scratch', _SCRATCH_IOP, false], // 车身划痕损失险
        ['combust', _SPONTANEOUS_LOSS, 'exempt_combust', _SPONTANEOUS_LOSS_IOP, true], // 自燃损失险
        ['water', _ENGINE, 'exempt_water', _ENGINE_IOP, true], // 发动机特别损失险
        ['third_party', _UNABLE_FIND_THIRDPARTY, null, null, true], // 无法找到第三方特约险
        ['glass', _GLASS, null, null, true, true], // 玻璃险
    ]

    /**
     * 获取报哪些保险公司的参数
     */
    static getQuoteGroup(context) {
        def samCode = context.samCode ? [sam_code  : context.samCode]: [:]
        [
            [
                code      : context.outerCode,
                account_id: context.accountId,
            ] + samCode
        ]
    }

    /**
     * 获取保险公司对应账户信息
     */
    static final getInsuranceCompanyAccount(context) {
        def samCode = context.samCode ? [sam_code  : context.samCode]: [:]
        [
            code      : context.outerCode,
            account_id: context.accountId,
        ] + samCode
    }

    /**
     * 获取续保套餐
     */
    static generateRenewalPackage(allItemKinds) {
        def renewalPackage = new InsurancePackage()
        renewalPackage.compulsory = true
        renewalPackage.autoTax = true

        _KIND_CODE_INSURANCE_PACKAGE_MAPPINGS.each { outKindCode, propName, outIopCode, iopName,
                                                     isBoolean, isGlass = false ->
            def amount = allItemKinds[outKindCode] ? (allItemKinds[outKindCode] as double) : 0
            renewalPackage[propName] = isBoolean ? (1 == amount) : amount
            if (iopName) {
                renewalPackage[iopName] = (1 == allItemKinds[outIopCode])
            }

            if (isGlass) {
                renewalPackage.glassType = (1 == amount ? DOMESTIC_1 : IMPORT_2)
            }
        }

        renewalPackage
    }

    /**
     * 将byte转为16进制
     */
    private static final byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer()
        bytes.each {
            def temp = Integer.toHexString(it & 0xFF)
            if (temp.length() == 1) {
                stringBuffer.append('0')
            }
            stringBuffer.append(temp)
        }
        stringBuffer.toString()
    }

    private static final HMACHA256(secretKey, data) {
        Mac mac = Mac.getInstance('HmacSHA256')
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), 'HmacSHA256')
        mac.init(secretKeySpec)
        byte2Hex(mac.doFinal(data.getBytes('utf-8'))).toLowerCase()
    }

    private static final MD5Encode(origin) {
        MessageDigest md = MessageDigest.getInstance('MD5')
        byte2Hex(md.digest(origin.getBytes('utf-8'))).toLowerCase()
    }

    private static final getHeaders(context, path, params, httpMethod, log) {
        def accept = "application/vnd.botpy.${context.appVersion}+json"
        def timeStamp = (currentTimeMillis() / 1000L).toLong()
        def stitchStr = GET == httpMethod ? params.sort { it.key }.inject('', { prev, key, value ->
            if(value instanceof Collection) {
                def values = value.sort { it }.inject('', {prevValue, it ->
                    prevValue + key + it
                })
                prev + values
            } else {
                prev + key + value
            }
        }) : MD5Encode(JSONObject.fromObject(params).toString()).toLowerCase()
        def url = context.client.defaultURI.base.string[0..-2] + path
        def stringToSign = timeStamp + context.appKey + accept + url + stitchStr + context.appId
        log.debug("金斗云stringToSign:{}",stringToSign)
        [
            Accept             : accept,
            Authorization      : "appid ${context.appId}",
            'X-Yobee-Timestamp': timeStamp,
            'X-Yobee-Signature': HMACHA256(context.appKey, stringToSign)
        ]

    }

    static sendParamsAndReceive(context, path, params, method, log) {
        log.debug '金斗云请求参数为：{}， {}', path, params
        def result = context.client.request(method, JSON) { req ->
            uri.path = path
            headers = getHeaders(context, path, params, method, log)
            if (GET == method) {
                uri.query = params
            } else {
                body = params
            }

            response.success = { resp, json ->
                json
            }
            response.failure = { resp, json ->
                json
            }
        }

        log.debug '金斗云返回结果为：{}', result
        result
    }

    /**
     * 创建车主信息 PrivyDTO
     */
    static getPrivyDTO(context, auto) {
        def email = getEnvProperty context, 'email'
        def userMobile = context.extendedAttributes?.verificationMobile ?: context.order?.applicant?.mobile ?: randomMobile // 用户手机
        [
            name         : auto.owner,
            holder_type  : getMappingsValue(context, 'identityType', 'HOLDER_TYPE') ?: '01', //持有人类型代码 01个人
            document_type: getMappingsValue(context, 'identityType') ?: '01', //关系人证件类型代码 01身份证
            document_no  : auto.identity,
            phone        : userMobile,
            email        : email,
        ]
    }

    static getVehicleDTO(context, vehicleInfo) {
        def auto = context.auto
        vehicleInfo?.vehicle_id ? [vehicle_id: vehicleInfo.vehicle_id] : [
            license_no  : auto.licensePlateNo, // 车牌号
            owner       : auto.owner, // 车主姓名
            frame_no    : auto.vinNo,//车架号
            engine_no   : auto.engineNo, //发动机号
            license_type: auto.licensePlateNo?.length() == 8 ? '52' : '02', //号牌种类，新能源号牌多一位，暂时只支持小型车
            enroll_date : _DATE_FORMAT3.format(auto.enrollDate)
        ]
    }

    static getIdCardDTO(context) {
        def order = context.order
        def auto = context.auto
        def insurance = context.insurance
        def compulsoryInsurance = context.compulsoryInsurance

        def applicantId = (insurance ?: compulsoryInsurance ?: order).applicantIdNo ?: auto.identity // 投保人身份证
        def applicantName = (insurance ?: compulsoryInsurance ?: order).applicantName ?: auto.owner // 投保人姓名

        def now = LocalDate.now()
        [
            id_no     : applicantId,
            name      : applicantName,
            nation    : '汉',
            issue_date: _DATETIME_FORMAT3.format(now), // 发证日期
            valid_date: _DATETIME_FORMAT3.format(now.plusYears(10)), // 有效日期
            address   : context.area.name,
            issuer    : '签发机构' // 发证机关
        ]
    }

    static getExtendDTO(context) {
        def transferFlag = context?.additionalParameters?.supplementInfo?.transferDate
        [
            is_transfer  : transferFlag ? true : false,
            transfer_date: transferFlag ? _DATE_FORMAT3.format(context.additionalParameters.supplementInfo?.transferDate) : null,
        ]
    }

    static setRequestIdForPath(context, result, path) {
        context[path] = [
            request_id: result.request_id
        ]
    }

    static getRequestIdForPath(context, path) {
        context[path]?.request_id
    }

    static setNotificationIdForPath(context, result, path) {
        context[path] = [
            notification_id: result.notification_id
        ]
    }

    static getNotificationIdForPath(context, path) {
        context[path]?.notification_id
    }

    static getCityCode(Area area) {
        _CITY_CODE_MAPPINGS[area.id]
    }

    static notNeedUploadImage(data) {
        !(data.audit_records.comment =~ /.*(?:验车).*/).matches()
    }

    static getQuotedItems(items, glassType, model) {
        items.collectEntries {
            [
                (it.code): [
                    amount    : str2double(it.amount),
                    premium   : str2double(it.premium),
                    iopPremium: null,
                    quantity  : (model.seat_count as int) - 1,//乘客的数量
                    glassType : glassType  //玻璃类型  (默认就是1 国产)
                ]
            ]
        }
    }

    static str2double(value) {
        ((value ?: 0) as BigDecimal).setScale(2, ROUND_HALF_UP).doubleValue()
    }

    static getVehicleName(context) {
        context?.renewalsInfo?.vehicleName ?: (context.outerCode == 'cpicn' ? context.auto.autoType.code - '牌' : context.auto.autoType.code)
    }

    static getMappingsValue(context, valueName, mappingsName = null) {
        def name = mappingsName ?: valueName.toUpperCase()
        def mappings = Constants.getAt('_' + name + '_MAPPINGS')
        mappings ? mappings[context.auto[valueName]?.id] : null
    }

    static isAccountError(data) {
        def m1 = data =~ /[\s\S]*(锁定)[\s\S]*/
        m1.matches()
    }

    static getAddressDetail(deliveryAddress) {
        [
            prov_code: deliveryAddress?.province ?: '',
            city_code: deliveryAddress?.city ?: '',
            area_code: deliveryAddress?.district ?: '',
            street   : deliveryAddress?.street ?: '',
        ]
    }

    static getAddress(deliveryAddress) {
        if(deliveryAddress?.street) {
            ([
                deliveryAddress?.provinceName,
                deliveryAddress?.cityName,
                deliveryAddress?.districtName,
                deliveryAddress.street,
            ] - null).inject { p, c -> p + c }
        } else {
            ''
        }
    }

    static getVehicleNameByPiccModel(context) {
        def selectedAutoModel = context.additionalParameters.supplementInfo?.selectedAutoModel
        selectedAutoModel?.meta?.vehicleOptionInfo?.model ?: getVehicleName(context)
    }

    static getAutoModelByResult(context, result) {
        def autoModelMappings = context.autoModelMappings
        def autoModel = null
        def autoModelSelector = autoModelMappings[context.outerCode + '_' + context.area.id] ?: autoModelMappings[context.outerCode] ?: autoModelMappings.default
        if (!autoModelSelector) {
            return autoModel
        }
        if (context.optionsByVinNo) {
            autoModel = autoModelSelector(context, context.optionsByVinNo, result)
            if(autoModel) {
                autoModel.optionsSource = 'byVinNo'
            }
        }
        if (!autoModel && context.optionsByCode) {
            autoModel = autoModelSelector(context, context.optionsByCode, result)
            if(autoModel) {
                autoModel.optionsSource = 'byCode'
            }
        }
        autoModel
    }

    static isVehicleModelError(reason) {
        def m1 = reason =~ /[\s\S]*(车型)[\s\S]*/
        m1.matches()
    }
}
