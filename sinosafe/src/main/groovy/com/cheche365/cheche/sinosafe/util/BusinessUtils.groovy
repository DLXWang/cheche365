package com.cheche365.cheche.sinosafe.util

import groovy.xml.StreamingMarkupBuilder

import java.text.SimpleDateFormat

import static com.cheche365.cheche.common.util.AreaUtils.getProvinceCode
import static com.cheche365.cheche.common.util.AreaUtils.getProvincialCapitalCode
import static com.cheche365.cheche.common.util.CollectionUtils.checkAndConvertList
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.common.util.XmlUtils.mapToXml
import static com.cheche365.cheche.common.util.XmlUtils.xmlToMap
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.parser.Constants._AUTO_TAX
import static com.cheche365.cheche.parser.Constants._COMPULSORY
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._ENGINE_IOP
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._GLASS_TYPE
import static com.cheche365.cheche.parser.Constants._INSURANCE_COMMERCIAL_MAPPINGS
import static com.cheche365.cheche.parser.Constants._IOP_PREMIUM_NOTHING
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._PASSENGER_IOP
import static com.cheche365.cheche.parser.Constants._QUOTE_RECORD_COMMERCIAL_MAPPINGS
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS_IOP
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THEFT_IOP
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_IOP
import static com.cheche365.cheche.parser.Constants._UNABLE_FIND_THIRDPARTY
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._QUOTE_RECORD_ANNOTATION_META_CATEGORY_FLOW_CONTROL
import static com.cheche365.cheche.parser.Constants._QUOTE_RECORD_ANNOTATION_META_OPERATION_TYPE_CUSTOM
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsurancePackageItem
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.parser.util.BusinessUtils.resolveNewQuoteRecordInContext
import static com.cheche365.cheche.parser.util.InsuranceUtils._CHECK_ADVICE_WITH_TRUE
import static com.cheche365.cheche.parser.util.InsuranceUtils._COMPOSITE_ALLOWED_POLICY_BASE
import static com.cheche365.cheche.parser.util.InsuranceUtils._JUDGE_SINGLE_ADVICE_BASE
import static com.cheche365.cheche.parser.util.InsuranceUtils._SINGLE_ALLOWED_POLICY_BASE
import static com.cheche365.cheche.sinosafe.flow.Constants._CITY_PAYTAX_VOU_MAPPINGS
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovy.lang.Closure.IDENTITY
import static groovyx.net.http.ContentType.XML



/**
 * 业务相关工具类
 */
class BusinessUtils {
    private static final _RENRENCHEPARAMETER = 'renrenche'

    private static final _QUOTE_PRICE_VEHICLE_DEFAULT = [:]

    private static final _QUOTE_PRICE_VEHICLE_TIANJIN = [
        TAX_AUTHOR: '10100',
        VHL_ADDR  : '11000',
        STREET_CDE: '120116021'
    ]

    static final _QUOTE_PRICE_MAPPINGS = [
        120000L : _QUOTE_PRICE_VEHICLE_TIANJIN,
        default : _QUOTE_PRICE_VEHICLE_DEFAULT
    ]


    private static KIND_AMOUNT_MAPPINGS = [
        //三者险
        (_THIRD_PARTY_AMOUNT):
            [50000  : '306006004',
             100000 : '306006005',
             150000 : '306006018',
             200000 : '306006006',
             300000 : '306006007',
             500000 : '306006009',
             1000000: '306006014',
             1500000: '306006019',
             2000000: '306006020',
             2500000: '306006021',
             3000000: '306006022',
             3500000: '306006023',
             4000000: '306006024',
             4500000: '306006025',
             5000000: '306006026'],
        //划痕险
        (_SCRATCH_AMOUNT)    :
            [2000 : '365001',
             5000 : '365002',
             10000: '365003',
             20000: '365004'],
    ]

    //只针对商业险
    static final _KIND_CODE_TO_LIST_CVRG_TYPE = { seqNo, kindCode, insurancePackagePremium, isIop ->
        [
            SEQ_NO        : seqNo, //险种序号
            INSRNC_CDE    : kindCode, //险种代码
            AMT           : insurancePackagePremium, //保险金额/赔偿限额(元）
            FRANCHISE_FLAG: isIop, //不计免赔
        ]
    }

    //商业险保额To编码
    static final _KIND_AMOUNT_TO_SINOSAFE_CODE = { propName, context ->
        KIND_AMOUNT_MAPPINGS[propName] ? KIND_AMOUNT_MAPPINGS[propName].find { key, value ->
            key == context.accurateInsurancePackage[propName]
        }?.value : ''
    }

    //商业险保额To 新车购置价
    static final _KIND_AMOUNT_TO_SINOSAFE_CAR_PRICE = { propName, context ->
        context.selectedCarModel?.CAR_PRICE
    }

    //默认 商业险保额To 保额
    static final _KIND_AMOUNT_TO_SINOSAFE_AMOUNT = { propName, context ->
        //如果是玻璃的话，保额为空就可以
        if (propName in [_GLASS, _ENGINE, _UNABLE_FIND_THIRDPARTY]) {
            ''
        } else {
            context.insurancePackage[propName]
        }
    }

    //只针对商业险
    private static final _KIND_CODE_MAPPING_DOUBLE_TYPE = [
        ['030101', _DAMAGE, _KIND_AMOUNT_TO_SINOSAFE_CAR_PRICE, _KIND_CODE_TO_LIST_CVRG_TYPE],                      // 机动车辆损失险
        ['030102', _THIRD_PARTY_AMOUNT, _KIND_AMOUNT_TO_SINOSAFE_CODE, _KIND_CODE_TO_LIST_CVRG_TYPE],              // 第三者责任险
        ['030103', _THEFT, _KIND_AMOUNT_TO_SINOSAFE_CAR_PRICE, _KIND_CODE_TO_LIST_CVRG_TYPE],                        // 盗抢险
        ['030104', _DRIVER_AMOUNT, _KIND_AMOUNT_TO_SINOSAFE_AMOUNT, _KIND_CODE_TO_LIST_CVRG_TYPE],                      // 车上人员责任险-司机
        ['030105', _PASSENGER_AMOUNT, _KIND_AMOUNT_TO_SINOSAFE_AMOUNT, _KIND_CODE_TO_LIST_CVRG_TYPE],                  // 车上人员责任险-乘客
        ['030107', _GLASS, _KIND_AMOUNT_TO_SINOSAFE_AMOUNT, _KIND_CODE_TO_LIST_CVRG_TYPE],                    // 玻璃
        ['030108', _SPONTANEOUS_LOSS, _KIND_AMOUNT_TO_SINOSAFE_CAR_PRICE, _KIND_CODE_TO_LIST_CVRG_TYPE],    // 自燃损失险
        ['030110', _SCRATCH_AMOUNT, _KIND_AMOUNT_TO_SINOSAFE_CODE, _KIND_CODE_TO_LIST_CVRG_TYPE],                    // 车身划痕损失险
        ['030111', _ENGINE, _KIND_AMOUNT_TO_SINOSAFE_AMOUNT, _KIND_CODE_TO_LIST_CVRG_TYPE],                    // 涉水险
        ['030115', _UNABLE_FIND_THIRDPARTY, _KIND_AMOUNT_TO_SINOSAFE_AMOUNT, _KIND_CODE_TO_LIST_CVRG_TYPE],                    // 无法找到第三方
    ]

    /**
     * 根据我们的套餐建议生产他们所要对应的值
     * @param args
     */
    static Object getCoverageInfoList(context) {
        def insurancePackage = context.accurateInsurancePackage
        List list = []
        //第一步首先判断是不是要投保交强险
        list << (isCompulsoryOrAutoTaxQuoted(insurancePackage) ? _KIND_CODE_TO_LIST_CVRG_TYPE(1, '0357', context.carPrice?:context.selectedCarModel?.CAR_PRICE, 0) : null)

        //第二步 根据套餐建议的里的值封装华安套餐
        def seqNo = 0
        _KIND_CODE_MAPPING_DOUBLE_TYPE.collect { kindCode, propName, amount2Code, action ->
            if (insurancePackage[propName]) {
                seqNo += 1
                //先判断是否投保iop
                def iopName = _QUOTE_RECORD_COMMERCIAL_MAPPINGS[propName].iopPremiumName
                list << action(seqNo, kindCode, amount2Code(propName, context), iopName ? insurancePackage[iopName] ? 1 : 0 : 0)
            } else {
                [:]
            }
        }
        list - null
    }

    private static final _O2I_PREMIUM_CONVERTER = { context, innerKindCode, kindItem, _3, _4, _5, _6, extConfig ->
        def other = null
        def amount = null
        def premium = null
        if (_GLASS == innerKindCode) {
            other = kindItem?.glassType
        }
        if (_PASSENGER_AMOUNT == innerKindCode) {
            other = kindItem?.quantity ? kindItem?.quantity as int : (context.selectedCarModel?.SET_NUM as int) - 1
        }
        //三者险 划痕险需要特殊处理
        if (innerKindCode in [_SCRATCH_AMOUNT, _THIRD_PARTY_AMOUNT]) {
            amount = KIND_AMOUNT_MAPPINGS[innerKindCode].findResult { key, value ->
                value == kindItem?.amount ? key : null
            }
        } else {
            amount = (kindItem?.amount ?: 0) as double
        }
        premium = (kindItem?.premium ?: 0) as double

        def iopPremium = kindItem?.iopPremium ? kindItem?.iopPremium as double : _IOP_PREMIUM_NOTHING

        [amount, premium, iopPremium, other]
    }

    // 操作（外转内）
    static _KIND_CODE_CONVERTERS_CONFIG = [
        //外界渠道的险种编码 内部编码
        ['030101', _DAMAGE, null, null, _O2I_PREMIUM_CONVERTER, null], // 机动车三者保险
        ['030102', _THIRD_PARTY_AMOUNT, null, null, _O2I_PREMIUM_CONVERTER, null], // 机动车三者保险
        ['030103', _THEFT, null, null, _O2I_PREMIUM_CONVERTER, null], // 盗抢险
        ['030104', _DRIVER_AMOUNT, null, null, _O2I_PREMIUM_CONVERTER, null], // 车上人员责任险-司机
        ['030105', _PASSENGER_AMOUNT, null, null, _O2I_PREMIUM_CONVERTER, null], // 车上人员责任险-乘客
        ['030107', _GLASS, null, null, _O2I_PREMIUM_CONVERTER, null], // 玻璃
        ['030108', _SPONTANEOUS_LOSS, null, null, _O2I_PREMIUM_CONVERTER, null], // 自燃损失险
        ['030110', _SCRATCH_AMOUNT, null, null, _O2I_PREMIUM_CONVERTER, null], // 车身划痕损失险
        ['030111', _ENGINE, null, null, _O2I_PREMIUM_CONVERTER, null], // 涉水险
        ['030115', _UNABLE_FIND_THIRDPARTY, null, null, _O2I_PREMIUM_CONVERTER, null], // 无法找到第三方
    ]

    /**
     * 输入map类型的param业务参数，返回xml转换成的json或map
     * @param context
     * @param stepNote
     * @param serviceName
     * @param param
     */
    static sendAndReceive2Map(context, param, log) {

        def message = generateXML mapToXml(param)

        log.debug '发送报文---- {}', message

        def args = [
            requestContentType: XML,
            contentType       : 'application/XML; charset=UTF-8',
            body              : message
        ]

        context.client.post args, { resp, text ->
            def resultText = (new StringWriter() << text).toString()
            log.info '接收报文---- {}', resultText
            xmlToMap(resultText)
        }
    }


    private static generateXML(xml) {
        new StreamingMarkupBuilder().with {
            encoding = 'UTF-8'
            def soap = {
                mkp.xmlDeclaration()
                mkp.yieldUnescaped xml
            }
            bind(soap) as String
        }
    }

    static createRequestParams(context, transCode, body) {

        def (date, time) = _DATE_FORMAT5.format(new Date()).split(' ')
        [
            PACKET: [
                HEAD : [
                    TRANSTYPE  : 'SNY', //同步异步请求标识
                    TRANSCODE  : transCode,  //请求类型
                    CONTENTTYPE: 'XML', //请求内容类型
                    VERIFYTYPE : '1',
                    USER       : context.user,
                    PASSWORD   : context.password,
                ],
                THIRD: [
                    EXTENTERPCODE: context.extenterpcode,
                    PRODNO       : '0000',
                    PLANNO       : '0000',
                    TRANSCODE    : transCode,
                    TRANSDATE    : date,
                    TRANSTIME    : time,
                ],
                BODY : body
            ]
        ]
    }

    /**
     * 调整currentAmount的保额为距amountList最近的保额
     * @param currentAmount
     * @param amountList
     * @param returnExtractor
     * @param amountExtractor
     * @param selectLowerWhenAbsEquals 表示调整套餐的方向，1表示上限，0表示下限
     * @includesExpectedAmount 选择的值是否包含当前期望值
     */
    static getCustomPremiumForAmountList(expectedAmount, amountList, returnExtractor = IDENTITY, amountExtractor = IDENTITY, selectLowerWhenAbsEquals = 1, includesExpectedAmount = true) {

        amountList?.collect { item ->
            [returnExtractor(item), expectedAmount - amountExtractor(item)]
        }?.findAll { item ->
            (selectLowerWhenAbsEquals ? (item[-1] > 0) : (item[-1] < 0)) || (includesExpectedAmount && item[-1] == 0)
        }?.sort { item ->
            Math.abs item[-1]
        }?.with { sortedAmounts ->
            if (sortedAmounts) {
                sortedAmounts[0][0]
            } else {
                0
            }

        }
    }

//<editor-fold defaultstate="collapsed" desc="处理套餐建议">


    private static final _POLICY_IGNORABLE_ADVICE = 0L
    //禁掉盗强险
    private static final _POLICY_CODE_FORBID_THEFT = _POLICY_IGNORABLE_ADVICE + 1 //盗抢险
    //禁掉车损险
    private static final _POLICY_CODE_FORBID_DAMAGE = _POLICY_CODE_FORBID_THEFT + 1 //车损
    //禁掉划痕险
    private static final _POLICY_CODE_FORBID_SCRATCH = _POLICY_CODE_FORBID_DAMAGE + 1 //划痕
    //禁掉自燃险
    private static final _POLICY_CODE_FORBID_SPONTANEOUS_LOSS = _POLICY_CODE_FORBID_SCRATCH + 1 //自燃
    //禁掉涉水险
    private static final _POLICY_CODE_FORBID_ENGINE_LOSS = _POLICY_CODE_FORBID_SPONTANEOUS_LOSS + 1 //发动机
    //禁掉玻璃险种
    private static final _POLICY_CODE_FORBID_GLASS_LOSS = _POLICY_CODE_FORBID_ENGINE_LOSS + 1 //玻璃

    //禁掉交强险
    private static final _POLICY_CODE_FORBID_COMPULSORY_LOSS = _POLICY_CODE_FORBID_GLASS_LOSS + 1 //交强险
    //禁掉三者险
    private static final _POLICY_CODE_FORBID_THIRD_PARTY_LOSS = _POLICY_CODE_FORBID_COMPULSORY_LOSS + 1 //三者险

    //调整划痕险保额
    private static final _POLICY_CODE_ADJUST_SCRATCH = _POLICY_CODE_FORBID_THIRD_PARTY_LOSS + 1 //划痕保额调整

    //调整三者险保额
    private static final _POLICY_CODE_ADJUST_THIRD_PARTY = _POLICY_CODE_ADJUST_SCRATCH + 1 //划痕保额调整

    //调整划痕险保额
    private static final _POLICY_CODE_ADJUST_PASSENGER = _POLICY_CODE_ADJUST_THIRD_PARTY + 1 //划痕保额调整

    //异地车，抛已知原因错误
    private static final _POLICY_CODE_KNOWN_REASON = _POLICY_CODE_ADJUST_PASSENGER + 1 //已知原因错误
    //同时禁掉交强险和车船税
    private static final _POLICY_FORBID_DAMAGE_AND_COMPULSORY_LOSS = _POLICY_CODE_KNOWN_REASON + 1 //禁掉交强险和车船税
    private static final _POLICY_CODE_FORBID_UNABLE_FIND_THIRD_PARTY = _POLICY_FORBID_DAMAGE_AND_COMPULSORY_LOSS + 1
    private static final _POLICY_CODE_VALUABLE_HINTS = _POLICY_CODE_FORBID_UNABLE_FIND_THIRD_PARTY + 1

    //开启三者险，并给保额值为10万 单投车损
    //private static final _POLICY_INSURE_THIRD_PARTY = _POLICY_FORBID_DAMAGE_AND_COMPULSORY_LOSS + 1 //开启三者

    //已知原因错误的policy
//    private static final _KNOWN_REASON_POLICY_BASE = { advice, context, others ->
//        //已知原因错误
//        getFatalErrorFSRV advice.entrySet().first().value
//    }

    //调整保额
    private static final _AMOUNT_ALLOWED_POLICY_BASE = { propName, iopPropName, policyCode, advice, context, others ->
        //这是最后的policy
        def propValue = others[policyCode].amount
        def iopPropValue = 0 == propValue ? false : null
        adjustInsurancePackageItem context, propName, iopPropName, propValue, iopPropValue
        def insurancePackage = context.accurateInsurancePackage

        context.newQuoteRecord = null
        getLoopContinueFSRV insurancePackage, advice
    }

    private static final _CHECK_ADVICE_PREMIUM_VALUE_BASE = { policyCode, propName, codes, advice, context, others ->
        //能够获取到预期保额向下取值
        def m1 = advice =~ /.*(?:${propName})(?:险?)保额(?:超|超过)?([a-zA-Z\d]+)元.*/ //m1[0][1] --2000
        def m2 = advice =~ /.*(?:${propName})(?:险?)保额(?:超|超过)?([a-zA-Z\d]+)万.*/ //m1[0][1] --2000
        //获取不到期望保额  向下取值
        def m3 = advice =~ /.*(?:${propName})(?:险?)保额(?:过高|过大).*/
        def m4 = advice =~ /.*降低(?:${propName})(?:险?)限额.*/
        def m5 = advice =~ /.*(?:${propName})(?:险?)限额超权限.*/

        //能够获取到预期保额 向上取值
        def m6 = advice =~ /.*(?:${propName})(?:险?)保额(?:小于)([a-zA-Z\d]+)万.*/
        def m7 = advice =~ /.*(?:${propName})(?:险?)保额(?:小于)([a-zA-Z\d]+)元.*/
        //需降低三者限额
        def lowerAmount = m1.find() ? m1[0][1] as long : m2.find() ? (m2[0][1] as long) * 10000 : m3.find() || m4.find() || m5.find() ? context.accurateInsurancePackage[policyCode] : 0

        def higherAmount = m6.find() ? (m6[0][1] as long) * 10000 : m7.find() ? m7[0][1] as long : 0

        if (lowerAmount || higherAmount) {
            def direction = lowerAmount ? 1 : 0
            def amount = getCustomPremiumForAmountList(lowerAmount ?: higherAmount, _INSURANCE_COMMERCIAL_MAPPINGS[policyCode].amountList,
                { it as double }, { it as double }, direction, !(direction as boolean))
            if (!others[policyCode]?.amount) {
                others << [(policyCode): [amount: amount]]
            }
            if (lowerAmount && amount < others[policyCode].amount || higherAmount && amount > others[policyCode].amount) {
                others << [(policyCode): [amount: amount]]
            }
            true
        } else {
            false
        }

    }

    /**
     * 针对无效套餐建议，使用终结来标识payload
     */
    static final _TERMINAL_QUOTE_POLICY = { advice, context, others ->
        def errorMsg = advice.entrySet().first().value as String
        if (context.quoteResult?.CVRG_LIST?.CVRG_DATA) {
            context.terminalFsrvPayload = context.quoteResult.CVRG_LIST.CVRG_DATA
            getLoopBreakFSRV context.terminalFsrvPayload
        } else {
            getKnownReasonErrorFSRV errorMsg
        }
    }

    /**
     * 针对我们报价过程中“请核实车架号是否录入有误”的套餐建议，推有价值提示信息
     */
    static final _VALUABLE_HINTS_POLICY = { advice, context, others ->
        getValuableHintsFSRV(context, [
            _VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING.with {
                it.originalValue = context.auto?.autoType?.code
                it
            },
            _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING.with {
                it.originalValue = context.auto.engineNo
                it
            },
            _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING.with {
                it.originalValue = context.auto.vinNo
                it
            }
        ])
    }

    //套餐建议修改保额问题
    static final _ADVICE_POLICY_MAPPINGS = [
        //禁掉三者险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_THIRD_PARTY_LOSS))       : _SINGLE_ALLOWED_POLICY_BASE.curry(_THIRD_PARTY_AMOUNT, _THIRD_PARTY_IOP, 0, false),
        //禁掉交强险
//        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_COMPULSORY_LOSS)) : _SINGLE_ALLOWED_POLICY_BASE.curry(_COMPULSORY, null, 0, false),
        //禁掉划痕险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_SCRATCH))                : _SINGLE_ALLOWED_POLICY_BASE.curry(_SCRATCH_AMOUNT, _SCRATCH_IOP, 0, false),
        //禁掉盗强险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_THEFT))                  : _SINGLE_ALLOWED_POLICY_BASE.curry(_THEFT, _THEFT_IOP, 0, false),
        //禁掉车损险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_DAMAGE))                 : _SINGLE_ALLOWED_POLICY_BASE.curry(_DAMAGE, _DAMAGE_IOP, 0, false),
        //禁掉自燃险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_SPONTANEOUS_LOSS))       : _SINGLE_ALLOWED_POLICY_BASE.curry(_SPONTANEOUS_LOSS, _SPONTANEOUS_LOSS_IOP, 0, false),
        //禁掉涉水险
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_ENGINE_LOSS))            : _SINGLE_ALLOWED_POLICY_BASE.curry(_ENGINE, _ENGINE_IOP, 0, false),

        //同时禁掉交强险和车船税
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_COMPULSORY_LOSS))            : _COMPOSITE_ALLOWED_POLICY_BASE.curry([_COMPULSORY, _AUTO_TAX], [null, null], false, false),

        //调整玻璃险种 国产车投保进口玻璃
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_GLASS_LOSS))             : _SINGLE_ALLOWED_POLICY_BASE.curry(_GLASS, _GLASS_TYPE, true, DOMESTIC_1),

        //开启三者险 并给100000保额
        //(_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_INSURE_THIRD_PARTY))          : _SINGLE_ALLOWED_POLICY_BASE.curry(_THIRD_PARTY_AMOUNT, _THIRD_PARTY_IOP, 100000.0, true),
        //调整划痕险保额
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_SCRATCH))                : _AMOUNT_ALLOWED_POLICY_BASE.curry(_SCRATCH_AMOUNT, _SCRATCH_IOP, _SCRATCH_AMOUNT),
        //_AMOUNT_ALLOWED_POLICY_BASE curry的第三个参数先保留 以后可能会修改
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_THIRD_PARTY))            : _AMOUNT_ALLOWED_POLICY_BASE.curry(_THIRD_PARTY_AMOUNT, _THIRD_PARTY_IOP, _THIRD_PARTY_AMOUNT),
        //人员责任险 司机乘客
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_PASSENGER))              : _AMOUNT_ALLOWED_POLICY_BASE.curry(_PASSENGER_AMOUNT, _PASSENGER_IOP, _PASSENGER_AMOUNT),
        //已知原因错误 异地车等
//        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_KNOWN_REASON))           : _RENEW_QUOTE_POLICY,
        //单投车损
        //(_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_INSURE_THIRD_PARTY))          : _KNOWN_REASON_POLICY_BASE,
        //无法找到第三方
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_UNABLE_FIND_THIRD_PARTY)): _SINGLE_ALLOWED_POLICY_BASE.curry(_UNABLE_FIND_THIRDPARTY, null, false, false),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_VALUABLE_HINTS))            : _VALUABLE_HINTS_POLICY,
        //缺省策略
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_IGNORABLE_ADVICE))                   : _TERMINAL_QUOTE_POLICY,
    ]

    //利用华安返回的状态吗进行判断  找到对应的套餐建议码
    private static final _CHECK_ADVICE_BASE = { sinosafeCodes, advice, context, others ->
        sinosafeCodes.any { treit ->
            advice.contains(treit)
        }
    }

    private static final _COMMON_REGULATOR_BASE = { keyCode, advice, context, others ->
        [(keyCode): advice]
    }

    static final _GET_EFFECTIVE_ADVICES = { advices, context, others ->
        advices.tokenize('\r\n').findAll { line ->
            !line.contains('核保返回信息：')
        }.findResults { line ->
            line.contains('<br/>') ? line[0..<line.indexOf('<br/>')] : line
        }
    }

    //套餐建议
    static final _ADVICE_REGULATOR_MAPPINGS = [

        //开启三者，并将三者的保额赋值为10万
        //(_CHECK_ADVICE_BASE.curry(['保单投保车损险转人工核保', 'X25T05201700800']))                                        : _COMMON_REGULATOR_BASE.curry(_POLICY_INSURE_THIRD_PARTY),

        //禁掉车船税和交强险都禁用
//        (_CHECK_ADVICE_BASE.curry(['减税、完税、免税、拒缴转人工核保', 'X25T06201700100']))                                    : _COMMON_REGULATOR_BASE.curry(_POLICY_FORBID_DAMAGE_AND_COMPULSORY_LOSS),
        //将商业险和交强险都禁用
//        (_CHECK_ADVICE_BASE.curry(['异地车请核实', 'X00T05201700400']))                                              : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_KNOWN_REASON),
        //禁止投保交强险
//        (_CHECK_ADVICE_BASE.curry(['交强险转人工核保']))                                                               : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_COMPULSORY_LOSS),
        //禁止投保三者险
        (_CHECK_ADVICE_BASE.curry(['X00T05201602000']))                                                        : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_THIRD_PARTY_LOSS),
        //禁止投保盗强险
        (_CHECK_ADVICE_BASE.curry(['X25T05201701100', 'X25S01201700200', 'X25T05201701200', 'X00T05201603900', 'X00S01201600500'])): _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_THEFT),
        /**
         * 禁止投保车损险
         * 暂不处理的情况：1,'X00T05201603400'：标的车为D、E类高风险车型承保车损险，转人工核保，
         *              2,'X25T05201700800'：保单投保车损险转人工核保
         */
        (_CHECK_ADVICE_BASE.curry(['X25T05201700700', 'X00T05201602800', 'X00S01201600200', 'X12S01201700200']))                   : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_DAMAGE),
        //禁止投保划痕险
        (_CHECK_ADVICE_BASE.curry(['X00S01201600700', 'X00T05201605000']))                                     : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_SCRATCH),
        //禁止投保自燃险
        (_CHECK_ADVICE_BASE.curry(['X00T05201605300', 'X00S01201600600']))                                     : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_SPONTANEOUS_LOSS),
        //禁止投保涉水险
        (_CHECK_ADVICE_BASE.curry(['X25T05201702200']))                                                        : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_ENGINE_LOSS),

        //国产车投保进口玻璃
        (_CHECK_ADVICE_BASE.curry(['X00T02201601500']))                                                        : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_GLASS_LOSS),
        //调整划痕险保额
        (_CHECK_ADVICE_PREMIUM_VALUE_BASE.curry(_SCRATCH_AMOUNT, "划痕", ['X00S01201600800', 'X00T05201605200'])): _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_SCRATCH),
        (_CHECK_ADVICE_PREMIUM_VALUE_BASE.curry(_THIRD_PARTY_AMOUNT, "三者", ['X00T05201600900']))               : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_THIRD_PARTY),
        (_CHECK_ADVICE_PREMIUM_VALUE_BASE.curry(_PASSENGER_AMOUNT, "人员责任", ['X00T05201604400']))               : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_PASSENGER),
        //禁止投保无法找到第三方特约险
        (_CHECK_ADVICE_BASE.curry(['X25T05201702100']))                                                        : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_UNABLE_FIND_THIRD_PARTY),
        (_CHECK_ADVICE_BASE.curry(['X00T02201700500']))                                                        : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_VALUABLE_HINTS),
        (_CHECK_ADVICE_WITH_TRUE)                                                                              : _COMMON_REGULATOR_BASE.curry(_POLICY_IGNORABLE_ADVICE)
    ]

    //</editor-fold>


    static getQuotedItems(items, glassType) {
        items.collectEntries {
            [
                (it.INSRNC_CDE): [
                    amount    : it.AMT,
                    premium   : it.PREMIUM,
                    iopPremium: it.N_YL12,
                    quantity  : it.NUMBER_PER,//乘客的数量
                    glassType : glassType  //玻璃类型  (默认就是1 国产)
                ]
            ]
        }
    }

    static premiumToDouble(premium) {
        premium ? premium as double : 0
    }

    static getPayTaxVou(context) {
        def cityCode = context.cityCode as long
        def payTaxVou = _CITY_PAYTAX_VOU_MAPPINGS[cityCode] ?: _CITY_PAYTAX_VOU_MAPPINGS[getProvinceCode(cityCode)] ?: _CITY_PAYTAX_VOU_MAPPINGS[getProvincialCapitalCode(cityCode)] ?: _CITY_PAYTAX_VOU_MAPPINGS.default
        'isNull' == payTaxVou ?
            null : 'insuredIdNo' == payTaxVou ?
            (context?.order?.insuredIdNo ?: context.auto.identity) : payTaxVou
    }

    static populateQR(context) {
        def body = context.quoteResult
        def insuranceCompany = context.accurateInsurancePackage
        def cvrgData = checkAndConvertList body.CVRG_LIST.CVRG_DATA
        def allKindItems = getQuotedItems(cvrgData, insuranceCompany[_GLASS_TYPE])

        if (isCompulsoryOrAutoTaxQuoted(insuranceCompany)) {
            populateQuoteRecordBZ(context, premiumToDouble(allKindItems['0357'].premium), premiumToDouble(body.VHLTAX.SUM_UP_TAX))
        }

        if (isCommercialQuoted(insuranceCompany)) {
            populateQuoteRecord(context, allKindItems, _KIND_CODE_CONVERTERS_CONFIG, premiumToDouble(body.SY_BASE.PREMIUM), null)
        }
    }

    static parameterHandle(body, insurancePackage) {
        def cvrgData = checkAndConvertList body.CVRG_LIST.CVRG_DATA
        return getQuotedItems(cvrgData, insurancePackage[_GLASS_TYPE])
    }

    static CreateNewQR(context, ERRORMESSAGE, body) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
        def sy = '商业险'
        def jq = '交强险'
        def parameter_1 = null
        def parameter_2 = null
        def sy_insure = false//商业险
        def jq_insure = false//交强险
        def sy_startDate = null //商业险开始时间
        def sy_endDate = null //商业险结束时间
        def jq_startDate = null//交强险结束时间
        def jq_endDate = null//交强险结束时间
        if (context.channel == _RENRENCHEPARAMETER) {
            def newQuoteRecord = resolveNewQuoteRecordInContext context
            newQuoteRecord.with { qr ->
                if (ERRORMESSAGE.size() != 0 && ERRORMESSAGE.contains('重复投保')) {
                    if (ERRORMESSAGE.contains(sy) && ERRORMESSAGE.contains(jq) && ERRORMESSAGE.indexOf(sy) < ERRORMESSAGE.indexOf(jq)) {
                        parameter_1 = ERRORMESSAGE.substring(ERRORMESSAGE.indexOf(sy), ERRORMESSAGE.indexOf(jq))
                        def parameterCode_1 = parameter_1 =~ /[0-9]+-[0-9]+-[0-9]+/
                        if (parameterCode_1.size() != 0) {
                            //有商业险时间,并且商业险在前
                            sy_startDate = sdf.parse(parameterCode_1[0])
                            sy_endDate = sdf.parse(parameterCode_1[1])
                        }
                        if (parameter_1.contains('重复投保')) {
                            sy_insure = true
                        }
                        parameter_2 = ERRORMESSAGE.substring(ERRORMESSAGE.indexOf(jq), ERRORMESSAGE.length())
                        def parameterCode_2 = parameter_2 =~ /[0-9]+-[0-9]+-[0-9]+/
                        if (parameterCode_2.size() != 0) {
                            //有交强险时间,并且交强险时间在后
                            jq_startDate = sdf.parse(parameterCode_2[0])
                            jq_endDate = sdf.parse(parameterCode_2[1])
                        }
                        if (parameter_2.contains('重复投保')) {
                            jq_insure = true
                        }
                        qr.annotations = [duplicateInsurance: getPayload(sy_insure, jq_insure,sy_startDate,sy_endDate,jq_startDate,jq_endDate)]
                    } else if (ERRORMESSAGE.contains(sy) && ERRORMESSAGE.contains(jq) && ERRORMESSAGE.indexOf(sy) > ERRORMESSAGE.indexOf(jq)) {
                        parameter_1 = ERRORMESSAGE.substring(ERRORMESSAGE.indexOf(sy), ERRORMESSAGE.length())
                        def parameterCode_1 = parameter_1 =~ /[0-9]+-[0-9]+-[0-9]+/
                        if (parameterCode_1.size() != 0) {
                            //有商业险时间,并且商业险在后
                            sy_startDate = sdf.parse(parameterCode_1[0])
                            sy_endDate = sdf.parse(parameterCode_1[1])
                        }
                        if (parameter_1.contains('重复投保')) {
                            sy_insure = true
                        }
                        parameter_2 = ERRORMESSAGE.substring(ERRORMESSAGE.indexOf(jq), ERRORMESSAGE.indexOf(sy))
                        def parameterCode_2 = parameter_2 =~ /[0-9]+-[0-9]+-[0-9]+/
                        if (parameterCode_2.size() != 0) {
                            //有交强险时间，并且交强险在前
                            jq_startDate = sdf.parse(parameterCode_2[0])
                            jq_endDate = sdf.parse(parameterCode_2[1])
                        }
                        if (parameter_2.contains('重复投保')) {
                            jq_insure = true
                        }
                        qr.annotations = [duplicateInsurance: getPayload(sy_insure, jq_insure,sy_startDate,sy_endDate,jq_startDate,jq_endDate)]
                    } else if (ERRORMESSAGE.contains(sy) && !ERRORMESSAGE.contains(jq)) {
                        def parameterCode_1 = ERRORMESSAGE =~ /[0-9]+-[0-9]+-[0-9]+/
                        if (parameterCode_1.size() != 0) {
                            //有商业险时间,没有交强险
                            sy_startDate = sdf.parse(parameterCode_1[0])
                            sy_endDate = sdf.parse(parameterCode_1[1])
                        }
                        qr.annotations = [duplicateInsurance: getPayload(true, jq_insure,sy_startDate,sy_endDate,jq_startDate,jq_endDate)]
                    } else if (!ERRORMESSAGE.contains(sy) && ERRORMESSAGE.contains(jq)) {
                        def parameterCode_1 = ERRORMESSAGE =~ /[0-9]+-[0-9]+-[0-9]+/
                        if (parameterCode_1.size() != 0) {
                            //有交强险时间,没有商业险时间
                            jq_startDate = sdf.parse(parameterCode_1[0])
                            jq_endDate = sdf.parse(parameterCode_1[1])
                        }
                        qr.annotations = [duplicateInsurance: getPayload(sy_insure, true,sy_startDate,sy_endDate,jq_startDate,jq_endDate)]
                    }
                }
                qr.discount = Double.parseDouble(body.ADJUST.APPLY_TOTAL_ADJ)//申请总折扣
                qr
            }
        }
    }

    static final getPayload(base,compulsory,effectiveDate,expireDate,compulsoryEffectiveDate,compulsoryExpireDate) {
        [
            payload: [
                base      : [
                    insurance: base,//商业险
                    startDate: effectiveDate,//商业险开始时间
                    endDate  : expireDate,//商业险结束时间
                ],
                compulsory: [
                    insurance: compulsory,//交强险
                    startDate: compulsoryEffectiveDate,//交强险开始时间
                    endDate  : compulsoryExpireDate,//交强结束始时间
                ]
            ],
            metaInfo: [
                operationType: _QUOTE_RECORD_ANNOTATION_META_OPERATION_TYPE_CUSTOM,
                category     : _QUOTE_RECORD_ANNOTATION_META_CATEGORY_FLOW_CONTROL
            ]
        ]
    }
}
