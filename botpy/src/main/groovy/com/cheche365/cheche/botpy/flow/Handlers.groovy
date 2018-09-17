package com.cheche365.cheche.botpy.flow

import static com.cheche365.cheche.botpy.flow.Constants._BOTPY_DRIVER_AMOUNT_LIST
import static com.cheche365.cheche.botpy.flow.Constants._BOTPY_PASSENGER_AMOUNT_LIST
import static com.cheche365.cheche.botpy.flow.Constants._BOTPY_SCRATCH_AMOUNT_LIST
import static com.cheche365.cheche.botpy.flow.Constants._BOTPY_THIRD_PARTY_AMOUNT_LIST
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._DRIVER_IOP
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._ENGINE_IOP
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._IOP_PREMIUM_NOTHING
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
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount

/**
 * 金斗云 RPG&RH
 */
class Handlers {


    private static final _AMOUNT_CONVERTER_BOOLEAN = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        insurancePackage[propName] ? 1 : 0
    }

    private static final _CONVERTER_FROM_AMOUNT = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        insurancePackage[propName] ? 1 : 0
    }

    private static final _AMOUNT_CONVERTER_FROM_AMOUNT_LIST = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        def expectedAmount = insurancePackage[propName]
        def amountList = kindItem?.amountList?.reverse()

        if (_GLASS == propName && expectedAmount) {
            def glassType = insurancePackage.glassType
            expectedAmount = (DOMESTIC_1 == glassType ? 1 : IMPORT_2 == glassType ? 2 : 0)
        }

        def actualAmount = expectedAmount ?
            (adjustInsureAmount(expectedAmount as int, amountList, { it as double }, { it as double }) ?: 0)
            : 0

        actualAmount as int
    }

    private static final _O2I_PREMIUM_CONVERTER = { context, innerKindCode, kindItem, _3, _4, _5, _6, extConfig ->
        def other = null
        if (_GLASS == innerKindCode) {
            def glassType = ((kindItem?.amount ?: 0) as double) as int // 0表示不投玻璃
            other = (1 == glassType) ? DOMESTIC_1 : (2 == glassType) ? IMPORT_2 : null
        }

        def amount = (kindItem?.amount ?: 0) as double
        def premium = (kindItem?.premium ?: 0) as double

        [amount, premium, _IOP_PREMIUM_NOTHING, other]
    }

    // @formatter:off
    static final _KIND_CODE_CONVERTERS_CONFIG = [
        ['damage', _DAMAGE, _CONVERTER_FROM_AMOUNT, null, _O2I_PREMIUM_CONVERTER, null], // 机动车损失保险
        ['third', _THIRD_PARTY_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: _THIRD_PARTY_AMOUNT_LIST, outAmountList: _BOTPY_THIRD_PARTY_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null], // 三责
        ['pilfer', _THEFT, _CONVERTER_FROM_AMOUNT, null, _O2I_PREMIUM_CONVERTER, null], // 盗抢
        ['driver', _DRIVER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: _DRIVER_AMOUNT_LIST, outAmountList: _BOTPY_DRIVER_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null], // 司机
        ['passenger', _PASSENGER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: _PASSENGER_AMOUNT_LIST, outAmountList: _BOTPY_PASSENGER_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null], // 乘客
        ['scratch', _SCRATCH_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [  inAmountList: _SCRATCH_AMOUNT_LIST, outAmountList: _BOTPY_SCRATCH_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null], // 划痕
        ['water', _ENGINE, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], // 涉水险
        ['glass', _GLASS, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [inAmountList: [0, 1, 2], outAmountList: [0, 1, 2]], _O2I_PREMIUM_CONVERTER, null], // 玻璃
        ['combust', _SPONTANEOUS_LOSS, _CONVERTER_FROM_AMOUNT, null, _O2I_PREMIUM_CONVERTER, null], // 自燃
        ['exempt_damage', _DAMAGE_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], // 车损不及免赔
        ['exempt_third', _THIRD_PARTY_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], // 三责不及免赔
        ['exempt_pilfer', _THEFT_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], // 盗抢不及免赔
        ['exempt_driver', _DRIVER_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], // 司机不及免赔
        ['exempt_passenger', _PASSENGER_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], // 乘客不及免赔
        ['exempt_combust', _SPONTANEOUS_LOSS_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], // 自燃不及免赔
        ['exempt_scratch', _SCRATCH_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], // 划痕不及免赔
        ['exempt_water', _ENGINE_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], // 涉水不及免赔
        ['third_party', _UNABLE_FIND_THIRDPARTY, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], // 无法找到第三方
    ]
    // @formatter:on

    static final _PARAMS_CONVERTER = { context, outerKindCode, kindItem, result ->
        [(outerKindCode): result]
    }

    static getAllKindItems(context) {
        context.kindCodeConvertersConfig.collectEntries { outerKindCode, innerKindCoder, _2, itemFeatures, _4, _5 ->

            [
                (outerKindCode): [
                    amountList: itemFeatures?.inAmountList?.intersect(itemFeatures?.outAmountList)
                ]
            ]
        }
    }

}
