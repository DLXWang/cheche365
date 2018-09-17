package com.cheche365.cheche.pingan.flow.step.m

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.ContactUtils.getGenderByIdentity
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
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
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts



/**
 * Created by wangxin on 2015/11/9.
 */
@Slf4j
class Handlers {

    private static final _GENDERS = ['F', 'M']

    //insuranceCode到中文名和propName的映射，适合于险种都是分开的情況
    static final _CODE_INSURANCE_CHINESE_NAME_MAPPINGS_DEFAULT = [
        '01': ['propNameCNA': '车辆损失险', 'propNameENG': _DAMAGE],
        '02': ['propNameCNA': '商业第三者责任险', 'propNameENG': _THIRD_PARTY_AMOUNT],
        '03': ['propNameCNA': '全车盗抢险', 'propNameENG': _THEFT],
        '04': ['propNameCNA': '司机座位责任险', 'propNameENG': _DRIVER_AMOUNT],
        '05': ['propNameCNA': '乘客座位责任险', 'propNameENG': _PASSENGER_AMOUNT],
        '08': ['propNameCNA': '玻璃单独破碎险', 'propNameENG': _GLASS],
        '17': ['propNameCNA': '车身划痕损失险', 'propNameENG': _SCRATCH_AMOUNT],
        '18': ['propNameCNA': '自燃损失险', 'propNameENG': _SPONTANEOUS_LOSS],
        '41': ['propNameCNA': '发动机涉水损失险', 'propNameENG': _ENGINE],
        '63': ['propNameCNA': '无法找到第三方特约险', 'propNameENG': _UNABLE_FIND_THIRDPARTY],
        '27': ['propNameCNA': '不计免赔险(车损)', 'propNameENG': _DAMAGE_IOP],
        '28': ['propNameCNA': '不计免赔险(三者)', 'propNameENG': _THIRD_PARTY_IOP],
        '48': ['propNameCNA': '不计免赔险(盗抢)', 'propNameENG': _THEFT_IOP],
        '49': ['propNameCNA': '不计免赔险(司机)', 'propNameENG': _DRIVER_IOP],
        '75': ['propNameCNA': '不计免赔(车身划痕损失险)', 'propNameENG': _SCRATCH_IOP],
        '77': ['propNameCNA': '不计免赔(自燃损失险)', 'propNameENG': _SPONTANEOUS_LOSS_IOP],
        '79': ['propNameCNA': '不计免赔(发动机涉水损失险)', 'propNameENG': _ENGINE_IOP],
        '80': ['propNameCNA': '不计免赔(乘客)', 'propNameENG': _PASSENGER_IOP],
    ]

    //RPG块
    static final _BOOLEAN_TO_BOOLEAN = { insuranceCode, propName, context ->
        def options = context.insuranceItemOptions
        //判断平安M站页面是否可以投保此险种
        if (insuranceCode in options.keySet()) {
            context.accurateInsurancePackage[propName] ? 1 : 0
        } else {
            //平安M站没有显示可以投的险种，直接改成false，请求参数修改为0
            context.accurateInsurancePackage[propName] = false
            0
        }
    }


    static final _GLASS_TYPE_TO_INT = { insuranceCode, propName, context ->
        def options = context.insuranceItemOptions[insuranceCode]
        def insurancePackage = context.accurateInsurancePackage
        def value = insurancePackage[propName] ? (insurancePackage.glassType == DOMESTIC_1 ? 1 : 2) : 0
        if (!((value as String) in options)) {
            value = options.collect { it as int }.sum()
            context.accurateInsurancePackage.glassType == DOMESTIC_1 ? IMPORT_2 : DOMESTIC_1
        }
        value
    }

    static final _DOUBLE_TO_DOUBLE = { insuranceCode, propName, context ->
        def options = context.insuranceItemOptions
        def insurancePackage = context.accurateInsurancePackage
        //有些险种页面不能投，直接就不显示，并且参数当中也不会有相应的insuranceCode
        if (insuranceCode in options.keySet()) {
            def value = insurancePackage[propName] ?: 0.0

            if (value) {
                def valueOptions = options[insuranceCode].collect { it as double } - 0.0
                insurancePackage[propName] = valueOptions.sort { Math.abs(value - it) }.first()
                value = insurancePackage[propName]
                value = valueOptions.sort { Math.abs(value - it) }.first()
                context.accurateInsurancePackage = insurancePackage
            }
            value
        } else {
            //若此险种不能投，就直接返回0
            0.0
        }
    }

    static final _INPUT_AMOUNT_ = { insuranceCode, propName, context ->
        context.necessaryInfo[propName] ?: 0
    }


    static final _BOOLEAN_TO_DOUBLE = { insuranceCode, propName, bizConfigName, context ->
        def options = context.insuranceItemOptions
        if (insuranceCode in options.keySet()) {
            context.accurateInsurancePackage[propName] ? context.necessaryInfo[bizConfigName] : 0
        } else {
            0
        }
    }

    static final _CHECHE_UNSUPPORT = { context ->
        0
    }

    static final _M_INSURANCE_ITEMS_CONVERTERS_DEFAULT = [
        'bizConfig.amount01'   : _BOOLEAN_TO_BOOLEAN.curry('bizConfig.amount01', _DAMAGE),
        'bizConfig.amount02'   : _DOUBLE_TO_DOUBLE.curry('bizConfig.amount02', _THIRD_PARTY_AMOUNT),
        'bizConfig.amount03'   : _BOOLEAN_TO_DOUBLE.curry('bizConfig.amount03', _THEFT, 'theftAmount'),
        'bizConfig.amount04'   : _DOUBLE_TO_DOUBLE.curry('bizConfig.amount04', _DRIVER_AMOUNT),
        'bizConfig.amount05'   : _DOUBLE_TO_DOUBLE.curry('bizConfig.amount05', _PASSENGER_AMOUNT),
        'bizConfig.amount08'   : _GLASS_TYPE_TO_INT.curry('bizConfig.amount08', _GLASS),
        'bizConfig.amount17'   : _DOUBLE_TO_DOUBLE.curry('bizConfig.amount17', _SCRATCH_AMOUNT),
        'bizConfig.amount18'   : _BOOLEAN_TO_DOUBLE.curry('bizConfig.amount18', _SPONTANEOUS_LOSS, 'spontaneousLossAmount'),
        'bizConfig.amount27'   : _BOOLEAN_TO_BOOLEAN.curry('bizConfig.amount27', _DAMAGE_IOP),
        'bizConfig.amount28'   : _BOOLEAN_TO_BOOLEAN.curry('bizConfig.amount28', _THIRD_PARTY_IOP),
        'bizConfig.amount41'   : _BOOLEAN_TO_BOOLEAN.curry('bizConfig.amount41', _ENGINE),
        'bizConfig.amount48'   : _BOOLEAN_TO_BOOLEAN.curry('bizConfig.amount48', _THEFT_IOP),
        'bizConfig.amount49'   : _BOOLEAN_TO_BOOLEAN.curry('bizConfig.amount49', _DRIVER_IOP),//司机不计免赔（重庆的城市amount49是司机的不计免赔）
        'bizConfig.amount57'   : _CHECHE_UNSUPPORT,  //暂不支持"指定专修厂特约险"
        'bizConfig.amount63'   : _BOOLEAN_TO_BOOLEAN.curry('bizConfig.amount63', _UNABLE_FIND_THIRDPARTY),  //暂不支持“无法找到第三方特约险”
        'bizConfig.amount75'   : _BOOLEAN_TO_BOOLEAN.curry('bizConfig.amount75', _SCRATCH_IOP),//车身划痕损失险不计免赔
        'bizConfig.amount77'   : _BOOLEAN_TO_BOOLEAN.curry('bizConfig.amount77', _SPONTANEOUS_LOSS_IOP),//自燃险的不计免赔
        'bizConfig.amount79'   : _BOOLEAN_TO_BOOLEAN.curry('bizConfig.amount79', _ENGINE_IOP),//engineIop发动机涉水险不计免赔
        'bizConfig.amount80'   : _BOOLEAN_TO_BOOLEAN.curry('bizConfig.amount80', _PASSENGER_IOP),//乘客不计免赔
        'bizConfig.inputAmount': _INPUT_AMOUNT_.curry('bizConfig.inputAmount', 'damageAmount')
    ]

    //M站报价请求的参数
    static final _M_BIZ_QUOTE_COMMON_ARGS = [
        'flowId'                 : { context -> context.flowId },
        '__xrc'                  : { context -> context.__xrc },
        'bizConfig.pkgName'      : { context -> 'optional' },
        'responseProtocol'       : { context -> 'json' },
        'bizInfo.beginDate'      : { context -> getCommercialInsurancePeriodTexts(context).first as String },
        'forceInfo.beginDate'    : { context -> getCompulsoryInsurancePeriodTexts(context).first as String},
        'bizInfo.isNeedRuleCheck': { context -> 'false' }
    ]

    static final _M_QUOTE_BIZ_CALCULATE_RPG_BASE = { insuranceItemConverters, context ->
        context.bizQuoteArgs = insuranceItemConverters.collectEntries { insuranceItem, converter ->
            [(insuranceItem): converter(context)]
        }
    }

    static final _M_QUOTE_BIZCULATE_RPG_DEFAULT = _M_QUOTE_BIZ_CALCULATE_RPG_BASE.curry(_M_INSURANCE_ITEMS_CONVERTERS_DEFAULT + _M_BIZ_QUOTE_COMMON_ARGS)

    static final _M_SAVE_QUOTE_INFO_RPG_DEFAULT = { context ->
        context.passengerCount = context.carInfo.seat ?: context.auto.autoType.seats
        def transferDate = context.extendedAttributes?.transferDate
        def auto = context.auto
        def carInfo = context.carInfo

        [
            'flowId'                : context.flowId,
            'bizConfig.pkgName'     : 'optional',
            'vehicle.frameNo'       : auto.vinNo,
            'vehicle.engineNo'      : auto.engineNo,
            'vehicle.vehicleId'     : carInfo.vehicle_id,
            'vehicle.modelName'     : carInfo.standard_name,
            'vehicle.registerDate'  : context.registerDate,
            'vehicle.inputSeatFlag' : carInfo.seat_flag, //判断查出的车型的作为是否为人工输入
            'bizInfo.specialCarFlag': transferDate ? 1 : 0, //过户标志
            'bizInfo.specialCarDate': transferDate ? _DATE_FORMAT3.format(transferDate) : '',//过户日期
            '__xrc'                 : context.__xrc,
            'register.name'         : auto.owner,
            'vehicle.seat'          : carInfo.seat ?: auto.autoType.seats,//若查出的车型并没有车座的信息，默认为5
            'register.gender'       : getGenderByIdentity(auto.identity, _GENDERS),//车主性别对报价有影响
            'vehicle.tonNumber'     : carInfo.ton_number,   //绍兴地区针对两吨以下的小型货车必须提供tonNumber，vehicleWeight
            'vehicle.vehicleWeight' : carInfo.whole_weight,
            'register.idNo': auto.identity,
            'register.idType':'01',
        ]
    }

    static final _PREMIUM_CONVERTER_DOUBLE_TYPE = { kindCode, propName, quoteRecord, context ->
        quoteRecord[propName] = (context.bizPremium[kindCode] ?: 0.0) as double
    }

    static final _AMOUNT_CONVERTER_DOUBLE_TYPE_FROM_KIND_ITEM = { kindCode, propName, quoteRecord, context ->
        quoteRecord[propName] = context.necessaryInfo[kindCode] as double
    }

    static final _AMOUNT_CONVERTER_DOUBLE_TYPE = { qrPropName, ipPropName, quoteRecord, context ->
        quoteRecord[qrPropName] = context.accurateInsurancePackage[ipPropName]
    }

    static final _AMOUNT_CONVERTER_FROM_CONTEXT = { qrPropName, quoteRecord, context ->
        quoteRecord[qrPropName] = context[qrPropName] as int
    }

    static final _M_MAPPINGS_QUOTERECORD_PROPS_TO_AMOUNT_DEFAULT = [
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium01', 'damagePremium'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium02', 'thirdPartyPremium'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium03', 'theftPremium'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium04', 'driverPremium'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium05', 'passengerPremium'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium08', 'glassPremium'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium17', 'scratchPremium'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium18', 'spontaneousLossPremium'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium27', 'damageIop'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium28', 'thirdPartyIop'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium41', 'enginePremium'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium48', 'theftIop'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium49', 'driverIop'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium63', 'unableFindThirdPartyPremium'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium75', 'scratchIop'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium77', 'spontaneousLossIop'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium79', 'engineIop'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('premium80', 'passengerIop'),
        _AMOUNT_CONVERTER_DOUBLE_TYPE_FROM_KIND_ITEM.curry('damageAmount', 'damageAmount'),
        _AMOUNT_CONVERTER_DOUBLE_TYPE_FROM_KIND_ITEM.curry('theftAmount', 'theftAmount'),
        _AMOUNT_CONVERTER_DOUBLE_TYPE_FROM_KIND_ITEM.curry('spontaneousLossAmount', 'spontaneousLossAmount'),
        _AMOUNT_CONVERTER_DOUBLE_TYPE.curry('thirdPartyAmount', 'thirdPartyAmount'),
        _AMOUNT_CONVERTER_DOUBLE_TYPE.curry('driverAmount', 'driverAmount'),
        _AMOUNT_CONVERTER_DOUBLE_TYPE.curry('passengerAmount', 'passengerAmount'),
        _AMOUNT_CONVERTER_DOUBLE_TYPE.curry('scratchAmount', 'scratchAmount'),
        _AMOUNT_CONVERTER_DOUBLE_TYPE.curry('driverAmount', 'driverAmount'),
        _AMOUNT_CONVERTER_DOUBLE_TYPE.curry('passengerAmount', 'passengerAmount'),
        _AMOUNT_CONVERTER_DOUBLE_TYPE.curry('scratchAmount', 'scratchAmount'),
        _PREMIUM_CONVERTER_DOUBLE_TYPE.curry('totalPremium', 'premium'),
        _AMOUNT_CONVERTER_FROM_CONTEXT.curry('passengerCount')
    ]

    //根据获取的套餐计算QuoteRecord
    private static final _POPULATE_QUOTERECORD_BASE = { mappingsQuoteRecordPropsToAmount, quoteRecord, context ->
        mappingsQuoteRecordPropsToAmount.each { converter ->
            converter(quoteRecord, context)
        }
        context.newQuoteRecord = quoteRecord
        quoteRecord.iopTotal = quoteRecord.sumIopItems()
        getContinueFSRV quoteRecord
    }

    static final _POPULATE_QUOTERECORD_DEFAULT = _POPULATE_QUOTERECORD_BASE.curry(_M_MAPPINGS_QUOTERECORD_PROPS_TO_AMOUNT_DEFAULT)

    private static final _GET_INSURANCE_CONVERT_BASE = { convert, context ->
        convert
    }

    static final _GET_INSURANCE_CONVERT_DEFAULT = _GET_INSURANCE_CONVERT_BASE.curry(_CODE_INSURANCE_CHINESE_NAME_MAPPINGS_DEFAULT)

}
