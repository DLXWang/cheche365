package com.cheche365.cheche.bihu.flow

import static com.cheche365.cheche.bihu.flow.CityCodeMappings.getCityCode
import static com.cheche365.cheche.bihu.util.BusinessUtils.getQuoteGroup
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
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
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialSupplementInfoPeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsorySupplementInfoPeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getQuoteKindItemParams
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted

/**
 * Bihu RPG&RH
 * Created by suyaqiang on 2017/11/16.
 */
class Handlers {

    private static final _AMOUNT_CONVERTER_BOOLEAN = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        insurancePackage[propName] ? 1 : 0
    }

    private static final _CONVERTER_FROM_AMOUNT = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
//        insurancePackage[propName] ? kindItem?.amount : false
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
            (adjustInsureAmount(expectedAmount, amountList, { it as double }, { it as double }) ?: 0)
            : 0

        actualAmount as int
    }

    private static final _I2O_PREMIUM_CONVERTER = { context, innerKindCode, kindItem, _3, _4, _5, _6, extConfig ->
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
        ['CheSun',   _DAMAGE,             _CONVERTER_FROM_AMOUNT,    null, _I2O_PREMIUM_CONVERTER,     null], // 机动车损失保险
        ['SanZhe',   _THIRD_PARTY_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [amountList: _THIRD_PARTY_AMOUNT_LIST], _I2O_PREMIUM_CONVERTER,     null], // 三责
        ['DaoQiang', _THEFT,              _CONVERTER_FROM_AMOUNT,    null, _I2O_PREMIUM_CONVERTER,     null], // 盗抢
        ['SiJi',     _DRIVER_AMOUNT,      _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [amountList: _DRIVER_AMOUNT_LIST],    _I2O_PREMIUM_CONVERTER,     null], // 司机
        ['ChengKe',  _PASSENGER_AMOUNT,   _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [amountList: _PASSENGER_AMOUNT_LIST], _I2O_PREMIUM_CONVERTER,     null], // 乘客
        ['HuaHen',   _SCRATCH_AMOUNT,     _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [amountList: _SCRATCH_AMOUNT_LIST],   _I2O_PREMIUM_CONVERTER,     null], // 划痕
        ['SheShui',  _ENGINE,             _AMOUNT_CONVERTER_BOOLEAN, null, _I2O_PREMIUM_CONVERTER,     null], // 涉水险
        ['BoLi',     _GLASS,              _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [amountList : [0, 1, 2]], _I2O_PREMIUM_CONVERTER,     null], // 玻璃
        ['ZiRan',    _SPONTANEOUS_LOSS,   _CONVERTER_FROM_AMOUNT,    null, _I2O_PREMIUM_CONVERTER,     null], // 自燃
        ['BuJiMianCheSun',   _DAMAGE_IOP,         _AMOUNT_CONVERTER_BOOLEAN, null, _I2O_PREMIUM_CONVERTER, null], // 车损不及免赔
        ['BuJiMianSanZhe',   _THIRD_PARTY_IOP,    _AMOUNT_CONVERTER_BOOLEAN, null, _I2O_PREMIUM_CONVERTER, null], // 三责不及免赔
        ['BuJiMianDaoQiang', _THEFT_IOP,          _AMOUNT_CONVERTER_BOOLEAN, null, _I2O_PREMIUM_CONVERTER, null], // 盗抢不及免赔
        ['BuJiMianSiJi',     _DRIVER_IOP,         _AMOUNT_CONVERTER_BOOLEAN, null, _I2O_PREMIUM_CONVERTER, null], // 司机不及免赔
        ['BuJiMianChengKe',  _PASSENGER_IOP,      _AMOUNT_CONVERTER_BOOLEAN, null, _I2O_PREMIUM_CONVERTER, null], // 乘客不及免赔
        ['BuJiMianZiRan',    _SPONTANEOUS_LOSS_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _I2O_PREMIUM_CONVERTER, null], // 自燃不及免赔
        ['BuJiMianHuaHen',   _SCRATCH_IOP,        _AMOUNT_CONVERTER_BOOLEAN, null, _I2O_PREMIUM_CONVERTER, null], // 划痕不及免赔
        ['BuJiMianSheShui',  _ENGINE_IOP,         _AMOUNT_CONVERTER_BOOLEAN, null, _I2O_PREMIUM_CONVERTER, null], // 涉水不及免赔
        ['HcSanFangTeYue',   _UNABLE_FIND_THIRDPARTY, _AMOUNT_CONVERTER_BOOLEAN, null, _I2O_PREMIUM_CONVERTER, null], // 无法找到第三方
    ]
    // @formatter:on

    static final _PARAMS_CONVERTER = { context, outerKindCode, kindItem, result ->
        [(outerKindCode): result]
    }

    private static getAllKindItems(context) {
        context.kindCodeConvertersConfig.collectEntries { outerKindCode, innerKindCoder, _2, itemFeatures, _4, _5 ->
            [
                (outerKindCode): [
                    amountList: itemFeatures?.amountList
                ]
            ]
        }
    }

    static final _QUOTE_PRICE_RPG_BASE = { kindCodeConvertersConfig, context ->
        context.kindCodeConvertersConfig = kindCodeConvertersConfig
        def auto = context.auto

        def commercialQuoted = isCommercialQuoted(context.accurateInsurancePackage)
        def compulsoryOrAutoTaxQuoted = isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage)
        def transferDate = context.extendedAttributes?.transferDate

        def queryBody = [
            LicenseNo     : auto.licensePlateNo,
            CityCode      : getCityCode(context.area.id),
            CarOwnersName : auto.owner,
            QuoteGroup    : getQuoteGroup(context),
            SubmitGroup   : getQuoteGroup(context),
            EngineNo      : auto.engineNo,
            CarVin        : auto.vinNo,
            RegisterDate  : auto.enrollDate ? _DATE_FORMAT3.format(auto.enrollDate) : context.vehicleInfo?.RegisterDate ?: _DATE_FORMAT3.format(new Date()),
            MoldName      : auto.autoType?.code ?: context.vehicleInfo.ModleName,
            ForceTax      : commercialQuoted && compulsoryOrAutoTaxQuoted ? 1 : commercialQuoted ? 0 : 2,

            BizStartDate  : getCommercialSupplementInfoPeriodTexts(context).first,
            ForceStartDate: getCompulsorySupplementInfoPeriodTexts(context).first,
            TransferDate  : transferDate ? _DATE_FORMAT3.format(transferDate) : ''
        ]

        def quoteParams = getQuoteKindItemParams(context, getAllKindItems(context), kindCodeConvertersConfig, _PARAMS_CONVERTER) - null

        queryBody + quoteParams.collectEntries { it }
    }

    static final _QUOTE_PRICE_RPG = _QUOTE_PRICE_RPG_BASE.curry _KIND_CODE_CONVERTERS_CONFIG

}
