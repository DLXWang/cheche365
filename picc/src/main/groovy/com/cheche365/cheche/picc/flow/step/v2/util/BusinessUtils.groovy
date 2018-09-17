package com.cheche365.cheche.picc.flow.step.v2.util

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._ENGINE_IOP
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._GLASS_TYPE
import static com.cheche365.cheche.parser.Constants._INSURANCE_COMMERCIAL_MAPPINGS
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THEFT_IOP
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_IOP
import static com.cheche365.cheche.parser.Constants.get_DRIVER_IOP
import static com.cheche365.cheche.parser.Constants.get_PASSENGER_IOP
import static com.cheche365.cheche.parser.Constants.get_SPONTANEOUS_LOSS_IOP
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsurancePackageItem
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.cheche.parser.util.InsuranceUtils._CHECK_ADVICE_WITH_TRUE
import static com.cheche365.cheche.parser.util.InsuranceUtils._COMPOSITE_ALLOWED_POLICY_BASE
import static com.cheche365.cheche.parser.util.InsuranceUtils._ERROR_MESSAGE_POLICY
import static com.cheche365.cheche.parser.util.InsuranceUtils._JUDGE_SINGLE_ADVICE_BASE
import static com.cheche365.cheche.parser.util.InsuranceUtils._KNOWN_REASON_QUOTE_POLICY
import static com.cheche365.cheche.parser.util.InsuranceUtils._SINGLE_ALLOWED_POLICY_BASE
import static com.cheche365.cheche.parser.util.InsuranceUtils.get_RENEW_QUOTE_POLICY
import static com.cheche365.cheche.picc.flow.step.v2.Handlers._KIND_ITEM_CONVERTERS_CONFIG



/**
 * V2流程
 */
@Slf4j
class BusinessUtils {

    //<editor-fold defaultstate="collapsed" desc="处理套餐建议">

    private static final _POLICY_ERROR_ADVICE = 0L
    private static final _POLICY_IGNORABLE_ADVICE = _POLICY_ERROR_ADVICE + 1
    private static final _POLICY_CODE_ADJUST_SCRATCH = _POLICY_IGNORABLE_ADVICE + 1
    private static final _POLICY_CODE_ADJUST_GLASS_TO_DOMESTIC = _POLICY_CODE_ADJUST_SCRATCH + 1
    private static final _POLICY_CODE_ADJUST_DAMAGE = _POLICY_CODE_ADJUST_GLASS_TO_DOMESTIC + 1
    private static final _POLICY_CODE_ADJUST_THIRD_PARTY = _POLICY_CODE_ADJUST_DAMAGE + 1
    private static final _POLICY_CODE_ADJUST_ENGINE = _POLICY_CODE_ADJUST_THIRD_PARTY + 1
    private static final _POLICY_CODE_ADJUST_GLASS_TO_IMPORT = _POLICY_CODE_ADJUST_ENGINE + 1
    private static final _POLICY_CODE_ADJUST_THEFT = _POLICY_CODE_ADJUST_GLASS_TO_IMPORT + 1
    private static final _POLICY_CODE_ADJUST_SCRATCH_IOP = _POLICY_CODE_ADJUST_THEFT + 1
    private static final _POLICY_KNOWN_REASON_ADVICE = _POLICY_CODE_ADJUST_SCRATCH_IOP + 1
    private static final _POLICY_CODE_FORBID_THEFT_AND_DAMAGE = _POLICY_KNOWN_REASON_ADVICE + 1
    private static final _POLICY_CODE_ADD_THIRD_PARTY = _POLICY_CODE_FORBID_THEFT_AND_DAMAGE + 1
    private static final _POLICY_CODE_FORBID_IOP = _POLICY_CODE_ADD_THIRD_PARTY + 1

    private static final _ALL_KIND_IOP_LIST = [_DAMAGE_IOP, _THIRD_PARTY_IOP, _THEFT_IOP, _DRIVER_IOP, _PASSENGER_IOP, _SCRATCH_IOP, _ENGINE_IOP, _SPONTANEOUS_LOSS_IOP]


    static final _AMOUNT_ALLOWED_POLICY_BASE = { propName, iopPropName, policyCode, advice, context, others ->
        def propValue = adjustInsureAmount(others[policyCode].amount, _INSURANCE_COMMERCIAL_MAPPINGS[propName].amountList,
            { it as double }, ({ it as double }) ?: 0)
        adjustInsurancePackageItem context, propName, iopPropName, propValue, null
        def insurancePackage = context.accurateInsurancePackage

        context.newQuoteRecord = null
        getLoopContinueFSRV insurancePackage, advice
    }

    /**
     * 禁用所有的IOP
     */
    static final _PORBID_IOP_POLICY_BASE = { advice, context, others ->
        def insurancePackage = context.accurateInsurancePackage
        _ALL_KIND_IOP_LIST.each { iopPropName ->
            insurancePackage[iopPropName] = false
        }
        context.newQuoteRecord = null
        getLoopContinueFSRV insurancePackage, advice
    }


    private static final _ADVICE_POLICY_MAPPINGS = [
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_SCRATCH))          : _AMOUNT_ALLOWED_POLICY_BASE.curry(_SCRATCH_AMOUNT, _SCRATCH_IOP, _POLICY_CODE_ADJUST_SCRATCH),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_THIRD_PARTY))      : _AMOUNT_ALLOWED_POLICY_BASE.curry(_THIRD_PARTY_AMOUNT, _THIRD_PARTY_IOP, _POLICY_CODE_ADJUST_THIRD_PARTY),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_GLASS_TO_DOMESTIC)): _SINGLE_ALLOWED_POLICY_BASE.curry(_GLASS, _GLASS_TYPE, true, DOMESTIC_1),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_GLASS_TO_IMPORT))  : _SINGLE_ALLOWED_POLICY_BASE.curry(_GLASS, _GLASS_TYPE, true, IMPORT_2),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_DAMAGE))     : _SINGLE_ALLOWED_POLICY_BASE.curry(_DAMAGE, _DAMAGE_IOP, false, false),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_THEFT))      : _SINGLE_ALLOWED_POLICY_BASE.curry(_THEFT, _THEFT_IOP, false, false),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_ENGINE))     : _SINGLE_ALLOWED_POLICY_BASE.curry(_ENGINE, _ENGINE_IOP, false, false),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_SCRATCH_IOP)): _SINGLE_ALLOWED_POLICY_BASE.curry(_SCRATCH_AMOUNT, _SCRATCH_IOP, null, false),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADD_THIRD_PARTY)): _SINGLE_ALLOWED_POLICY_BASE.curry(_THIRD_PARTY_AMOUNT, _THIRD_PARTY_IOP, 100000, true),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_KNOWN_REASON_ADVICE))    : _KNOWN_REASON_QUOTE_POLICY,
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_THEFT_AND_DAMAGE)): _COMPOSITE_ALLOWED_POLICY_BASE.curry([_DAMAGE, _THEFT], [_DAMAGE_IOP, _THEFT_IOP], false, false),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_IOP)): _PORBID_IOP_POLICY_BASE,
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_ERROR_ADVICE))           : _ERROR_MESSAGE_POLICY,
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_IGNORABLE_ADVICE))       : _RENEW_QUOTE_POLICY,
    ]
    // 保额类险种提示信息
    private static final _CHECK_ADVICE_BASE = { propName, policyCode, advice, context, others ->
        def m = advice =~ /.*$propName.*保额(?:不高于|等于)(\d*).*/
        def m1 = advice =~ /.*$propName.*(?:最低保额|保额不低于)(\d*)万.*/
        if (m.matches()) {
            others << [(policyCode): [amount: m[0][1] as int]]
        } else if (m1.matches()) {
            others << [(policyCode): [amount: (m1[0][1] as int) * 10000]]
        }
        m.matches() || m1.matches()
    }

    private static final _CHECK_ADVICE_TEXT_BASE = { text, advice, context, others ->
        (advice =~ /.*(?:$text).*/).with { m ->
            m.find()
        }
    }

    private static final _COMMON_REGULATOR_BASE = { keyCode, advice, context, others ->
        [(keyCode): advice]
    }

    private static final _KNOWN_REASON_REGULATOR = { keyCode, advice, context, others ->
        if (advice.contains('需同时投保交强险、商业三者险、盗抢险其中之一')){
            advice = '须投交强、三者、盗抢之一'
        } else if (advice.contains('投保了车损险，必须投保三责险或盗抢险')){
            advice = '须投三者或盗抢'
        }
        [(keyCode): advice]
    }

    static final _ADVICE_REGULATOR_MAPPINGS = [
        (_CHECK_ADVICE_BASE.curry('划痕险', _POLICY_CODE_ADJUST_SCRATCH))    : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_SCRATCH),
        (_CHECK_ADVICE_BASE.curry('三责险|第三者责任险', _POLICY_CODE_ADJUST_THIRD_PARTY)): _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_THIRD_PARTY),
        (_CHECK_ADVICE_TEXT_BASE.curry('玻璃破碎险按国产玻璃投保|需按国产玻璃投保|不能按进口玻璃承保')): _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_GLASS_TO_DOMESTIC),
        (_CHECK_ADVICE_TEXT_BASE.curry('需按进口玻璃(投|承)保'))                  : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_GLASS_TO_IMPORT),
        (_CHECK_ADVICE_TEXT_BASE.curry('不能投保盗抢险和车损险|取消投保车损险和盗抢险')): _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_THEFT_AND_DAMAGE),
        (_CHECK_ADVICE_TEXT_BASE.curry('取消投保车损险'))                        : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_DAMAGE),
        (_CHECK_ADVICE_TEXT_BASE.curry('发动机特别损失险|取消投保发动机涉水损失险')) : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_ENGINE),
        (_CHECK_ADVICE_TEXT_BASE.curry('投保盗抢险时必须同时投保车损险'))           : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_THEFT),
        (_CHECK_ADVICE_TEXT_BASE.curry('划痕险不能附加不计免赔率特约险|不能投保划痕险不计免赔')) : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_SCRATCH_IOP),
        (_CHECK_ADVICE_TEXT_BASE.curry('同时投保三责险|至少再投保.*中的一个')) : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADD_THIRD_PARTY),
        (_CHECK_ADVICE_TEXT_BASE.curry('投保了车损险，需同时投保交强险、商业三者险、盗抢险其中之一|投保了车损险，必须投保三责险或盗抢险')) : _KNOWN_REASON_REGULATOR.curry(_POLICY_KNOWN_REASON_ADVICE),
        (_CHECK_ADVICE_TEXT_BASE.curry('已有一张订单，是否按照本次填写的信息进行订单变更')) : _COMMON_REGULATOR_BASE.curry(_POLICY_ERROR_ADVICE),
        (_CHECK_ADVICE_TEXT_BASE.curry('不能在网上投保不计免赔')): _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_IOP),
        (_CHECK_ADVICE_WITH_TRUE)                                               : _COMMON_REGULATOR_BASE.curry(_POLICY_IGNORABLE_ADVICE)
    ]


    static final _GET_EFFECTIVE_ADVICES = { advices, context, others ->
        def m = advices =~ /.*获取新核保返回信息[：|:](.*)/
        if (m.find()) {
            m.collect { advice ->
                advice[1].split(';|；')
            }.flatten()
        } else {
            [advices]
        }
    }

    static final _CITY_ADVICE_POLICY_MAPPINGS = [
        default: _ADVICE_POLICY_MAPPINGS
    ]

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="商业险报价">

    static getAllBaseKindItems(context, convertersConfig) {
        convertersConfig.collectEntries { outerKindCode, innerKindCode, _2, extConfig, _4, _5 ->
            [
                (outerKindCode): [
                    outerKindCode: outerKindCode,
                    amountList   : extConfig,
                    amount       : context.initKindInfo.amount050200
                ]
            ]
        }
    }

    static getAllKindItems (kindItemsList) {
        kindItemsList.findAll { key,_ ->
            key.contains('_')
        }.groupBy {
            it.key.split ('_') [1]
        }.collectEntries {
            def key = it.key
            [
                (key): [
                    amount : it.value.('a_' + key) as double,
                    premium: it.value.('p_' + key) as double
                ]
            ]
        }
    }

    /**
     * 套餐初始化险种就确定了可投险种，以下代码处理accurateInsurancePackage中不可投险种的处理
     */
    static disableUnsupportedKindItems(context) {

        // 这里初始化险种就确定了可投险种，以下代码处理accurateInsurancePackage中不可投险种的处理
        def supportedKindCodes = context.enableInsurancePackageList.kindCode
        def allKindCodePropNameMappings = _KIND_ITEM_CONVERTERS_CONFIG.collectEntries { item ->
            def (kindCode, propName) = [item[0], item[1]]
            [(kindCode): propName]
        }
        def supportedPropNames = allKindCodePropNameMappings.subMap(supportedKindCodes).values()
        def unsupportedPropNames = allKindCodePropNameMappings.values() - supportedPropNames - null

        def ip = context.accurateInsurancePackage
        unsupportedPropNames.each { propName ->
            ip[propName] = 0 // 给boolean型的成员赋值0，会自动转为对应的真值，即false
        }
    }

    //</editor-fold>

}
