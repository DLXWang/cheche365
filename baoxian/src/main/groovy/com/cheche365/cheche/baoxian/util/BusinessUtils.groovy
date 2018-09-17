package com.cheche365.cheche.baoxian.util

import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import sun.misc.BASE64Decoder
import sun.misc.BASE64Encoder

import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec

import static com.cheche365.cheche.baoxian.flow.Constants._TASKID_TTL
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.HashUtils.MD5
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.core.model.LogType.Enum.BAOXIAN_35
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._DRIVER_IOP
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._ENGINE_IOP
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._GLASS_TYPE
import static com.cheche365.cheche.parser.Constants._INSURANCE_COMMERCIAL_MAPPINGS
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT_LIST
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
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsurancePackageItem
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.cheche.parser.util.BusinessUtils.saveAppLog
import static com.cheche365.cheche.parser.util.FlowUtils.getShowInsuranceChangeAdviceFSRV
import static com.cheche365.cheche.parser.util.InsuranceUtils._CHECK_ADVICE_WITH_TRUE
import static com.cheche365.cheche.parser.util.InsuranceUtils._JUDGE_SINGLE_ADVICE_BASE
import static groovyx.net.http.ContentType.JSON
import static java.util.concurrent.TimeUnit.HOURS
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric



/**
 * Created by wangxin on 2017/2/10.
 */
@Slf4j
class   BusinessUtils {

    static final _CITY_PROPERTIES_MAPPINGS = [
//            default : 'baoxian.base_url',
            default : 'baoxian.v2.base_url',
    ]
    static final _CITY_PROPERTIES_MAPPINGS2 = [
            url : [
                default : 'baoxian.v2.base_url',
//                default : 'baoxian.base_url',
            ],
            channelID : [
                default : 'baoxian.v2.channel_id',
//                default : 'baoxian.channel_id',
            ],
            channelSecret : [
                default : 'baoxian.v2.channel_secret',
//                default : 'baoxian.channel_secret',
            ]
    ]

    public static String sign(String data, context) {
        byte[] keyBytes = new BASE64Decoder().decodeBuffer(context.privateKey)
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes)
        KeyFactory keyFactory = KeyFactory.getInstance('RSA')
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec)
        Signature signature = Signature.getInstance('MD5withRSA')
        signature.initSign(priKey)
        signature.update(data.getBytes())
         new BASE64Encoder().encodeBuffer(signature.sign())
    }

    static final getCityProperty(area,propName){
        _CITY_PROPERTIES_MAPPINGS2[propName][area.id] ?: _CITY_PROPERTIES_MAPPINGS2[propName]['default']
    }

    final static sendAndReceiveV2 = { context, stepNote, api, params ->
        def licensePlateNo = context.auto.licensePlateNo
        log.info '泛华先核保再支付流程{}接口开始请求。',api
        log.info '{} 请求数据：{}', api, params
        def seed = randomAlphanumeric(40)
        log.info '随机数：{}', seed
        String signStr = sign(context.token + seed, context).replaceAll(System.lineSeparator(),'')
        log.info '签名字串：{}', signStr

        RESTClient client = context.client.with {
            headers.put('nonceStr', seed)
            headers.put('signStr', signStr)
            it
        }

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : api,
            body              : params
        ]

        def argsJson = new JsonBuilder(args).toString()
        log.debug '{} 发送请求报文（未加密）---- {}', stepNote, argsJson
        saveAppLog(context.logRepo, BAOXIAN_35, context.taskId, context.insuranceCompany?.name, argsJson, stepNote, "$licensePlateNo:request")

        def result = client.post args, { resp, json ->
            json
        }
        def resultJson = new JsonBuilder(result).toString()
        log.debug '{} 接收响应报文 ---- {}', stepNote, result
        saveAppLog(context.logRepo, BAOXIAN_35, context.taskId, context.insuranceCompany?.name, resultJson, stepNote, "$licensePlateNo:response")

        log.info '{} 响应内容：{}', api, result
        log.info '泛华先核保再支付流程{}接口结束请求。',api
        result
    }


    final static sendAndReceive = { context, stepNote, api, params ->
        def jsonText = new JsonBuilder(params).toString()
        def licensePlateNo = context.auto?.licensePlateNo
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : api,
            body              : jsonText
        ]
        def argsJson = new JsonBuilder(args).toString()
        log.debug '{} 发送请求报文（未加密）---- {}', stepNote, argsJson
        saveAppLog(context.logRepo, BAOXIAN_35, context.taskId, context.insuranceCompany?.name, argsJson, stepNote, "$licensePlateNo:request")

        def client = context.client
        // 获取待签名消息，泛华要求：json尾部追加密钥
        def message = "$jsonText${context.channelSecret}"
        // 计算消息签名：md5
        def signature = MD5 message, false
        // 放入请求头
        def result = client.with {
            headers.sign = signature
            it
        }.post args, { resp, json ->
            json
        }

        def resultJson = new JsonBuilder(result).toString()
        log.debug '{} 接收响应报文 ---- {}', stepNote, result
        saveAppLog(context.logRepo, BAOXIAN_35, context.taskId, context.insuranceCompany?.name, resultJson, stepNote, "$licensePlateNo:response")
        result
    }



    //<editor-fold defaultstate="collapsed" desc="处理套餐建议">

    private static final _POLICY_IGNORABLE_ADVICE = 0L
    private static final _POLICY_SHOW_INSURANCE_ADVICE = _POLICY_IGNORABLE_ADVICE + 1
    private static final _POLICY_CODE_FORBID_SCRATCH = _POLICY_SHOW_INSURANCE_ADVICE + 1
    private static final _POLICY_CODE_FORBID_ENGINE = _POLICY_CODE_FORBID_SCRATCH + 1
    private static final _POLICY_CODE_FORBID_DAMAGE_AND_THEFT = _POLICY_CODE_FORBID_ENGINE + 1
    private static final _POLICY_CODE_FORBID_SPONTANEOUS_LOSS = _POLICY_CODE_FORBID_DAMAGE_AND_THEFT + 1
    private static final _POLICY_CODE_ADJUST_GLASS_IMPORT_2 = _POLICY_CODE_FORBID_SPONTANEOUS_LOSS + 1
    private static final _POLICY_CODE_FORBID_KIND_TYPE = _POLICY_CODE_ADJUST_GLASS_IMPORT_2 + 1
    private static final _POLICY_CODE_ADJUST_START_DATE = _POLICY_CODE_FORBID_KIND_TYPE + 1
    private static final _POLICY_CODE_ADJUST_SCRATCH = _POLICY_CODE_ADJUST_START_DATE + 1
    private static final _POLICY_CODE_FORBID_DAMAGE = _POLICY_CODE_ADJUST_SCRATCH + 1
    private static final _POLICY_CODE_ADJUST_THIRD_PARTY = _POLICY_CODE_FORBID_DAMAGE + 1
    private static final _POLICY_CODE_ADJUST_STAFF_DUTY = _POLICY_CODE_ADJUST_THIRD_PARTY + 1
    private static final _POLICY_CODE_FORBID_THEFT_IOP = _POLICY_CODE_ADJUST_STAFF_DUTY + 1
    private static final _POLICY_CODE_FORBID_THEFT = _POLICY_CODE_FORBID_THEFT_IOP + 1
    private static final _POLICY_CODE_ADJUST_DAMAGE_ADDITION = _POLICY_CODE_FORBID_THEFT + 1
    private static final _POLICY_CODE_FORBID_SCRATCH_IOP = _POLICY_CODE_ADJUST_DAMAGE_ADDITION + 1
    private static final _POLICY_CODE_FORBID_GLASS = _POLICY_CODE_FORBID_SCRATCH_IOP + 1
    private static final _POLICY_CODE_FORBID_SPONTANEOUS_IOP = _POLICY_CODE_FORBID_GLASS + 1
    private static final _POLICY_CODE_FORBID_DAMAGE_IOP = _POLICY_CODE_FORBID_SPONTANEOUS_IOP + 1
    private static final _POLICY_CODE_ADJUST_DRIVERS = _POLICY_CODE_FORBID_DAMAGE_IOP + 1
    private static final _POLICY_CODE_ADJUST_PASSENGER = _POLICY_CODE_ADJUST_DRIVERS + 1
    private static final _POLICY_CODE_FORBID_DRIVERS = _POLICY_CODE_ADJUST_PASSENGER + 1
    private static final _POLICY_CODE_FORBID_PASSENGER = _POLICY_CODE_FORBID_DRIVERS + 1
    private static final _POLICY_CODE_FORBID_ENGINE_IOP = _POLICY_CODE_FORBID_PASSENGER + 1
    private static final _POLICY_CODE_ADJUST_GLASS_DOMESTIC_1 = _POLICY_CODE_FORBID_ENGINE_IOP + 1
    private static final _POLICY_CODE_ADJUST_MULTI_PROP_BASE = _POLICY_CODE_ADJUST_GLASS_DOMESTIC_1 + 1
    private static final _POLICY_CODE_ADJUST_SPONTANEOUS_LOSS_IOP = _POLICY_CODE_ADJUST_MULTI_PROP_BASE + 1

    private static final AdjustInsurancePackage(context, propName, iopPropName, policyCode, others) {
        def propValue = adjustInsureAmount(others[policyCode].amount, _INSURANCE_COMMERCIAL_MAPPINGS[propName].amountList,
            { it as double }, ({ it as double }) ?: 0, others[policyCode].direction)
        def iopPropValue = 0 == propValue ? false : null
        adjustInsurancePackageItem context, propName, iopPropName, propValue, iopPropValue
    }


    private static final _STAFF_DUTY_ALLOWED_POLICY_BASE = { propNames, iopPropNames, policyCode, advice, context, others ->
        [propNames, iopPropNames].transpose().each { propName, iopPropName ->
            AdjustInsurancePackage(context, propName, iopPropName, policyCode, others)
        }
        getContinueFSRV false
    }

    private static final _SINGLE_ALLOWED_POLICY_BASE = { propName, iopPropName, propValue, iopPropValue, advice, context, others ->
        adjustInsurancePackageItem context, propName, iopPropName, propValue, iopPropValue
        getContinueFSRV false
    }

    private static final _MULTI_PROP_ALLOWED_POLICY_BASE = { advice, context, others ->
        def insurancePackage = context.accurateInsurancePackage
        others.kindType?.each { propName, propValue->
            insurancePackage[propName] = propValue
        }
        getContinueFSRV false
    }

    private static final _AMOUNT_ALLOWED_POLICY_BASE = { propName, iopPropName, policyCode, advice, context, others ->
        AdjustInsurancePackage(context, propName, iopPropName, policyCode, others)
        getContinueFSRV false
    }

    private static final _SHOW_INSURANCE_CHANGE_ADVICE_POLICY = { advice, context, others ->
        getShowInsuranceChangeAdviceFSRV advice.entrySet().first().value as String
    }

    final static _ADVICE_POLICY_MAPPINGS = [
        //调整多个险种，人员责任
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_MULTI_PROP_BASE)): _MULTI_PROP_ALLOWED_POLICY_BASE,
        //禁用单个险种，划痕
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_SCRATCH))        : _SINGLE_ALLOWED_POLICY_BASE.curry(_SCRATCH_AMOUNT, _SCRATCH_IOP, 0, false),
        //禁用单个险种，涉水
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_ENGINE))         : _SINGLE_ALLOWED_POLICY_BASE.curry(_ENGINE, _ENGINE_IOP, 0, false),
        //禁用单个险种，涉水不计免赔
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_ENGINE_IOP))     : _SINGLE_ALLOWED_POLICY_BASE.curry(_ENGINE, _ENGINE_IOP, null, false),
        //禁用单个险种，玻璃
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_GLASS))           : _SINGLE_ALLOWED_POLICY_BASE.curry(_GLASS, _GLASS_TYPE, false, DOMESTIC_1),
        //禁用单个险种，自燃
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_SPONTANEOUS_LOSS)): _SINGLE_ALLOWED_POLICY_BASE.curry(_SPONTANEOUS_LOSS, _SPONTANEOUS_LOSS_IOP, 0, false),
        //禁用单个险种，盗抢险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_THEFT))           : _SINGLE_ALLOWED_POLICY_BASE.curry(_THEFT, _THEFT_IOP, 0, false),
        //禁用单个险种，划痕险不计免赔
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_SCRATCH_IOP))     : _SINGLE_ALLOWED_POLICY_BASE.curry(_SCRATCH_AMOUNT, _SCRATCH_IOP, null, false),
        //禁用单个险种，盗抢不计免赔
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_THEFT_IOP))       : _SINGLE_ALLOWED_POLICY_BASE.curry(_THEFT, _THEFT_IOP, null, false),
        //禁用单个险种，自燃险不计免赔
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_SPONTANEOUS_IOP))       : _SINGLE_ALLOWED_POLICY_BASE.curry(_SPONTANEOUS_LOSS, _SPONTANEOUS_LOSS_IOP, null, false),
        //禁用单个险种，车损
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_DAMAGE))          : _SINGLE_ALLOWED_POLICY_BASE.curry(_DAMAGE, _DAMAGE_IOP, 0, false),
        //禁用单个险种，车损不计免赔
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_DAMAGE_IOP))     : _SINGLE_ALLOWED_POLICY_BASE.curry (_DAMAGE, _DAMAGE_IOP, null, false),
        //禁用单个险种，车损及其副险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_DAMAGE_ADDITION)) : _SINGLE_ALLOWED_POLICY_BASE.curry(_DAMAGE, _DAMAGE_IOP, true, null),
        //调整单个险种，玻璃
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_GLASS_DOMESTIC_1)) : _SINGLE_ALLOWED_POLICY_BASE.curry(_GLASS, _GLASS_TYPE, null, DOMESTIC_1),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_GLASS_IMPORT_2)) : _SINGLE_ALLOWED_POLICY_BASE.curry(_GLASS, _GLASS_TYPE, null, IMPORT_2),
        //调整单个险种，划痕
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_SCRATCH))         : _AMOUNT_ALLOWED_POLICY_BASE.curry(_SCRATCH_AMOUNT, _SCRATCH_IOP, _POLICY_CODE_ADJUST_SCRATCH),
        //调整单个险种，自燃不计
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_SPONTANEOUS_LOSS_IOP)): _SINGLE_ALLOWED_POLICY_BASE.curry(_SPONTANEOUS_LOSS, _SPONTANEOUS_LOSS_IOP, true, true),
        //调整单个险种，三者
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_THIRD_PARTY))     : _AMOUNT_ALLOWED_POLICY_BASE.curry(_THIRD_PARTY_AMOUNT, _THIRD_PARTY_IOP, _POLICY_CODE_ADJUST_THIRD_PARTY),
        //调整多个险种，人员责任
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_STAFF_DUTY))      : _STAFF_DUTY_ALLOWED_POLICY_BASE.curry([_DRIVER_AMOUNT, _PASSENGER_AMOUNT], [_DRIVER_IOP, _PASSENGER_IOP], _POLICY_CODE_ADJUST_STAFF_DUTY),
        //调整司机险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_DRIVERS))      : _AMOUNT_ALLOWED_POLICY_BASE.curry(_DRIVER_AMOUNT, _DRIVER_IOP, _POLICY_CODE_ADJUST_DRIVERS),
        //调整乘客险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_PASSENGER)): _AMOUNT_ALLOWED_POLICY_BASE.curry(_PASSENGER_AMOUNT, _PASSENGER_IOP, _POLICY_CODE_ADJUST_PASSENGER),
        //禁用司机险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_DRIVERS))  : _SINGLE_ALLOWED_POLICY_BASE.curry(_DRIVER_AMOUNT, _DRIVER_IOP, 0, false),
        //禁用乘客险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_PASSENGER)): _SINGLE_ALLOWED_POLICY_BASE.curry(_PASSENGER_AMOUNT, _PASSENGER_IOP, 0, false),
        //未做处理的前置规则，抛出
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_SHOW_INSURANCE_ADVICE)): _SHOW_INSURANCE_CHANGE_ADVICE_POLICY
    ]



    private static final _CHECK_ADVICE_BASE = { propName, advice, context, others ->
        def m1 = advice =~ /.*(?:禁止承保|限制承保|不可承保|不承保|不保|不能投保|不能承保|不予承保|不可单独承保|不允许投保|不允许承保|不能单保|不可单保).*$propName.*/
        def m2 = advice =~ /.*$propName(?:不予承保|不允许单独承保).*/

        m1.find() || m2.find()
    }

    private static final _CHECK_SCRATCH_BASE = { advice, context, others ->
        (advice =~ /.*(仅限于9座及以下，且车龄小于5年的家用和非营业用客车承保|禁止承保划痕险).*/).with { m ->
            m.find()
        }
    }

    private static final _CHECK_DAMAGE_BASE = { propName, advice, context, others ->
        def m1 = advice =~ /.*(?:划痕|自燃|涉水|玻璃|盗抢).*(?:必须承保|必须投保|必须同时投保|必须先投保|需投保|须保|必须同时保).*$propName.*/
        def m2 = advice =~ /.*${propName}才能购买(?:划痕|自燃|涉水|玻璃).*/

        m1.find() || m2.find()
    }

    private static final _CHECK_GLASS_BASE = { propName, advice, context, others ->
        (advice =~ /.*(?:需投保|只能保|必须按|只能承保|需按)${propName}.*/).with { m ->
            m.find()
        }
    }

    private static final _CHECK_MULTI_PROP_BASE = { advice, context, others ->
        def m1 = advice =~ /.*(?:承保乘客险时必保司机险|投保乘客险必须同时投保司机险).*/
        def m2 = advice =~ /.*承保司机险必须承保乘客险.*/
        def m3 = advice =~ /.*不能承保划痕和第三方.*/
        def m4 = advice =~ /.*投保车损险需要同时投保无法找到第三方责任险.*/
        if (m1.matches()) {
            others << [
                kindType: [
                    (_DRIVER_AMOUNT): _DRIVER_AMOUNT_LIST.first(),
                    (_DRIVER_IOP)   : true
                ]
            ]
        }

        if (m2.matches()) {
            others << [
                kindType: [
                    (_PASSENGER_AMOUNT): _PASSENGER_AMOUNT_LIST.first(),
                    (_PASSENGER_IOP)   : true
                ]
            ]
        }

        if (m3.matches()) {
            others << [
                kindType: [
                    (_SCRATCH_AMOUNT)    : 0,
                    (_SCRATCH_IOP)       : false,

                    (_THIRD_PARTY_AMOUNT): 0,
                    (_THIRD_PARTY_IOP)   : false
                ]
            ]
        }

        if (m4.matches()) {
            others << [
                kindType: [
                    (_DAMAGE)           : true,
                    (_DAMAGE_IOP)       : true,

                    (_UNABLE_FIND_THIRDPARTY): true,
                ]
            ]
        }

        m1.matches() || m2.matches() || m3.matches() || m4.matches()

    }

    private static final _CHECK_ADVICE_FOR_VALUE_BASE = { propName, policyCode, advice, context, others ->

        def m1 = advice =~ /.*${propName}.*(?:最高|保额|限|限额)(\d+)(?:元)?/
        def m2 = advice =~ /.*${propName}.*(?:不得低于|最低限额|不低于|不能低于)(\d+)万.*/
        def m3 = advice =~ /.*${propName}.*(?:赔偿限额不可大于|限|限额|最高保额|最高承保|最高)(\d+)万.*/

        def amount = m1.matches() ?
            (m1[0][1] as long)
            : m2.matches() ?
            (m2[0][1] as long) * 10000
            : m3.matches() ?
            (m3[0][1] as long) * 10000
            : 0

        def direction = m2.matches() ? 0 : 1

        if (amount) {
            others << [(policyCode): [amount: amount, direction : direction]]
        }
        amount
    }

    private static final _COMMON_REGULATOR_BASE = { keyCode, advice, context, others ->
        [(keyCode): advice]
    }



    static final _CITY_ADVICE_POLICY_MAPPINGS = [
        default: _ADVICE_POLICY_MAPPINGS
    ]

    final static _ADVICE_REGULATOR_MAPPINGS = [
        // 车上人员乘客险不同时启用和禁用、划痕及三方同时禁用
        (_CHECK_MULTI_PROP_BASE)                                                             : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_MULTI_PROP_BASE),
        //禁用划痕
        (_CHECK_ADVICE_BASE.curry('划痕'))                                           : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_SCRATCH),
        (_CHECK_SCRATCH_BASE)                                                       : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_SCRATCH),
        //禁用自燃
        (_CHECK_ADVICE_BASE.curry('自燃'))                                           : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_SPONTANEOUS_LOSS),
        //禁用涉水
        (_CHECK_ADVICE_BASE.curry('涉水'))                                           : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_ENGINE),
        (_CHECK_ADVICE_BASE.curry('发动机涉水损坏险不计免赔险'))                        : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_ENGINE_IOP),
        //禁用玻璃
        (_CHECK_ADVICE_BASE.curry('玻璃'))                                           : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_GLASS),
        //禁用车损
        (_CHECK_ADVICE_BASE.curry('(?:车损|车损险)'))                                 : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_DAMAGE),
        //禁用车损险的不计免赔
        (_CHECK_ADVICE_BASE.curry('车损不计'))                                        : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_DAMAGE_IOP),
        //启用车损副险
        (_CHECK_DAMAGE_BASE.curry('(?:车损|车辆损失险)'))                              : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_DAMAGE_ADDITION),
        //启用自燃不计
        (_CHECK_DAMAGE_BASE.curry('自燃不计'))                                        : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_DAMAGE_ADDITION),
        //盗抢险
        (_CHECK_ADVICE_BASE.curry('盗抢险'))                                           : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_GLASS),
        //禁用盗抢险的不计免赔
        (_CHECK_ADVICE_BASE.curry('盗抢不计'))                                        : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_THEFT_IOP),
        //禁用划痕险的不计免赔
        (_CHECK_ADVICE_BASE.curry('(?:划痕险不计免赔|划痕险的不计免赔)'))                                   : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_SCRATCH_IOP),
        //禁用自燃险的不计免赔
        (_CHECK_ADVICE_BASE.curry('(?:自燃险不计|自燃险的不计免赔)'))                                       : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_SPONTANEOUS_IOP),
        //调整玻璃
        (_CHECK_GLASS_BASE.curry('国产玻璃'))                                         : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_GLASS_DOMESTIC_1),
        (_CHECK_GLASS_BASE.curry('进口玻璃'))                                         : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_GLASS_IMPORT_2),
        //调整划痕险
        (_CHECK_ADVICE_FOR_VALUE_BASE.curry('划痕', _POLICY_CODE_ADJUST_SCRATCH))     : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_SCRATCH),
        //调整三者
        (_CHECK_ADVICE_FOR_VALUE_BASE.curry('(?:三者|三责)', _POLICY_CODE_ADJUST_THIRD_PARTY)) : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_THIRD_PARTY),
        //调整车上人员
        (_CHECK_ADVICE_FOR_VALUE_BASE.curry('车上人员', _POLICY_CODE_ADJUST_STAFF_DUTY)): _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_STAFF_DUTY),
        //调整司机险
        (_CHECK_ADVICE_FOR_VALUE_BASE.curry('司机险', _POLICY_CODE_ADJUST_DRIVERS))    : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_DRIVERS),
        //调整座位险
        (_CHECK_ADVICE_FOR_VALUE_BASE.curry('(?:座位险|乘客险)', _POLICY_CODE_ADJUST_PASSENGER))  : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_PASSENGER),
        //禁用司机险
        (_CHECK_ADVICE_BASE.curry('司机险'))                                           : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_DRIVERS),
        //禁用乘客险
        (_CHECK_ADVICE_BASE.curry('乘客险'))                                           : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_PASSENGER),
        //不做处理，抛出错误
        (_CHECK_ADVICE_WITH_TRUE)                                                     : _COMMON_REGULATOR_BASE.curry(_POLICY_SHOW_INSURANCE_ADVICE)
    ]



    final static _GET_EFFECTIVE_ADVICES = { advices, context, others ->
//        advices.first().tokenize(',')
        if(advices instanceof List){
            advices
        }else{
            advices.tokenize(',')
        }
    }

    final static _GET_EFFECTIVE_ADVICES_SUBMIT_QUOTE = { advices, context, others ->
        advices.tokenize('，')
    }
    //</editor-fold>

    static void saveInfo(context, log) {
        def globalContext = context.globalContext
        def insuranceCompanyCode = context.insuranceCompany.id
        def carOwner = context.auto.owner
        def licenseNo = context.auto.licensePlateNo

        //缓存taskId
        context.providers.each { provider ->
            globalContext.bindWithTTL("${licenseNo}_${carOwner}_${provider.prvId}".toString(), context.taskId, _TASKID_TTL, HOURS)
        }
        log.info '缓存taskId：{}成功', context.taskId
        //缓存选择的车型
        globalContext.bindWithTTL("${licenseNo}_${carOwner}_vehicleId".toString(), new JsonBuilder(context.selectedCarModel).toString(), _TASKID_TTL, HOURS)
        log.info '缓存taskId：{}对应选择的车型成功：{}', context.taskId, context.selectedCarModel
        //缓存供应商
        globalContext.bindWithTTL("${context.area.id}_providers".toString(), new JsonBuilder(context.providers).toString(), _TASKID_TTL, HOURS)
    }
}
