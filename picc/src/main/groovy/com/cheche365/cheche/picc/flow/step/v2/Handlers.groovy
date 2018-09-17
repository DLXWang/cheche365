package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.ContactUtils.getBirthdayByIdentity
import static com.cheche365.cheche.common.util.ContactUtils.getRandomEmail
import static com.cheche365.cheche.common.util.ContactUtils.getRandomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT1
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT1
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
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.cheche.parser.util.BusinessUtils.getQuoteKindItemParams
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.picc.flow.step.v2.util.BusinessUtils.disableUnsupportedKindItems
import static com.cheche365.cheche.picc.flow.step.v2.util.BusinessUtils.getAllBaseKindItems
import static com.cheche365.cheche.picc.util.BusinessUtils.getDefaultStartDateTextBI
import static com.cheche365.cheche.picc.util.BusinessUtils.getNextDays4Commercial
import static java.time.LocalDate.now as today

/**
 * 请求生成器（RPG）---郑州V2流程
 */
@Slf4j
class Handlers {

    //<editor-fold defaultstate="collapsed" desc="RequestParametersGenerators(RPGs)">

    //商业险报价前校验V2

    static final _PRE_FOR_CAL_BI_RPG_BASE = { getPartialParameters, context ->
        def (startDateText, endDateText) = getDefaultStartDateTextBI(context)
        def insuredInfo = context.insuredInfo

        [
            licenseNo                             : context.auto.licensePlateNo,
            serverDateTime                        : _DATETIME_FORMAT1.format(today()),
            uniqueID                              : context.uniqueID,
            entryId                               : context.entryId, //登录账户必须
            'mainReqDto.isRenewal'                : context.isRenewal,
            'mainReqDto.reuseFlag'                : context.reuseFlag,
            'mainReqDto.cityCode'                 : context.cityCode,
            'mainReqDto.startDateBI'              : startDateText,
            'mainReqDto.startHourBI'              : getNextDays4Commercial(context) ? 0 : 23,
            'mainReqDto.endDateBI'                : endDateText,
            'mainReqDto.endHourBI'                : 24,
            'carReqDto.frameNoFlag'               : context.renewable ? 0 : null, //续保要用，转保无entry，0表示用户没有修改，1表示用户修改过，可以发送带*号数据
            'carReqDto.vinNoFlag'                 : context.renewable ? 0 : null, //续保要用，转保无entry，0表示用户没有修改，1表示用户修改过，可以发送带*号数据
            'carReqDto.engineNoFlag'              : context.renewable ? 0 : null, //续保要用，转保无entry，0表示用户没有修改，1表示用户修改过，可以发送带*号数据
            'insuredReqDtos[1].changeIdentifyFlag': 0, //续保要用，转保发空，0表示用户没有修改，1表示用户修改过
            'insuredReqDtos[1].changeMobileFlag'  : 0, //续保要用，转保发空，0表示用户没有修改，1表示用户修改过
            'insuredReqDtos[1].changeEmailFlag'   : 0, //续保要用，转保发空，0表示用户没有修改，1表示用户修改过
            'insuredReqDtos[1].idAddr'            : insuredInfo?.insuredIdentifyAddr ?: '天堂地狱街18层',
            'insuredReqDtos[1].mobile'            : insuredInfo?.insuredMobile ?: insuredInfo?.mobile ?: randomMobile,
            'insuredReqDtos[1].email'             : insuredInfo?.insuredEmail ?: randomEmail,
            'insuredReqDtos[1].identifyno'        : context.auto.identity ?: insuredInfo?.insuredIDNumber ?: insuredInfo?.identifynumber,
            'insuredReqDtos[0].insuredFlag'       : '1000000',
            'insuredReqDtos[0].serialno'          : '1',
            'insuredReqDtos[1].insuredFlag'       : '0100000',
            'insuredReqDtos[1].serialno'          : '2',
            'insuredReqDtos[2].insuredFlag'       : '0010000',
            'insuredReqDtos[2].serialno'          : '3',
            'insuredReqDtos[3].insuredFlag'       : '00000001',
            'insuredReqDtos[3].serialno'          : '4',
            'insuredReqDtos[1].identifytype'      : '01',
            'insuredReqDtos[1].birthday'          : _DATE_FORMAT1.format(getBirthdayByIdentity(context.auto.identity))?: insuredInfo?.insuredBirthday
        ] + getPartialParameters(context)
    }

    static final _PRE_FOR_CAL_BI_RPG_PARTIAL_DEFAULT = { context ->
        Auto auto = context.auto
        def kind = context.extendedAttributes?.transferFlag ? 'normal' : context.renewable ? 'renewal' : context.historical ? 'reuse' : 'normal'

        def renewalVehicleInfo = context.renewalVehicleInfo
        def vehicleInfo = context.vehicleInfo
        def insuredInfo = context.insuredInfo
        def carInfo = context.carInfo

        def _PRE_FOR_CAL_BI_PARTIAL_REQUEST_PARAMS_GENERATOR_MAPPINGS = [
            // 转保参数生成
            normal : {
                [
                    vehicleDetail : vehicleInfo.modelDesc ? "${vehicleInfo.modelName}${vehicleInfo.modelDesc}" : "$carInfo.familyName$carInfo.engineDesc$carInfo.gearboxType$carInfo.vehicleName $carInfo.parentVehName ${carInfo.seat}座${(carInfo.price as double) / 10000}万",
                    vheiclaName   : vehicleInfo.standardName,
                    carSeat       : vehicleInfo.seat ?: auto.autoType.seats,
                    // 河南历史客户返回的初登日期格式为enrolldate
                    carEnrollDate : auto.enrollDate ? _DATE_FORMAT1.format(auto.enrollDate) : context.additionalParameters.supplementInfo?.enrollDate ? _DATE_FORMAT1.format(context.additionalParameters.supplementInfo?.enrollDate) : vehicleInfo?.enrolldate ?: vehicleInfo?.enrollDate,
                    frameNo       : auto.vinNo ?: vehicleInfo.frameNo,
                    engineNo      : auto.engineNo ?: vehicleInfo.engineNo,
                    insuredName   : auto.owner
                ]
            },
            // 续保参数
            renewal: {
                [
                    vehicleDetail : vehicleInfo.carModelDetail ?: "${vehicleInfo.modelName}${vehicleInfo.modelDesc}",
                    vheiclaName   : vehicleInfo.VEHICLE_MODELSH ?: vehicleInfo.modelName,
                    carSeat       : vehicleInfo.SeatCount ?: vehicleInfo.seat,
                    carEnrollDate : auto.enrollDate ? _DATE_FORMAT1.format(auto.enrollDate) : vehicleInfo.enrollDate,
                    frameNo       : renewalVehicleInfo.frameNo ?: auto.vinNo ?: vehicleInfo.frameNo,
                    engineNo      : renewalVehicleInfo.engineNo ?: auto.engineNo ?: vehicleInfo.engineNo,
                    insuredName   : auto.owner ?: insuredInfo?.insuredName
                ]
            },
            // 历史用户
            reuse  : {
                [
                    vehicleDetail : vehicleInfo.carModelDetail ?: "${vehicleInfo.modelName}${vehicleInfo.modelDesc}",
                    vheiclaName   : vehicleInfo.modelName ?: vehicleInfo.modelname + '轿车',
                    carSeat       : auto.autoType.seats ?: vehicleInfo.seatcount ?: vehicleInfo.seat,
                    carEnrollDate : auto.enrollDate ? _DATE_FORMAT1.format(auto.enrollDate) : vehicleInfo.enrolldate,
                    frameNo       : auto.vinNo ?: vehicleInfo.frameno,
                    engineNo      : auto.engineNo ?: vehicleInfo.engineno,
                    insuredName   : auto.owner ?: insuredInfo?.insuredName
                ]
            }
        ]

        def partialRequestParams = _PRE_FOR_CAL_BI_PARTIAL_REQUEST_PARAMS_GENERATOR_MAPPINGS[kind](context)

        [
            'carReqDto.frameno'                   : partialRequestParams.frameNo,
            'carReqDto.engineno'                  : partialRequestParams.engineNo,
            'carReqDto.enrolldate'                : partialRequestParams.carEnrollDate,
            'carReqDto.vehicle_modelsh'           : partialRequestParams.vheiclaName,
            'carReqDto.carModelDetail'            : partialRequestParams.vehicleDetail,
            'carReqDto.seatcount'                 : partialRequestParams.carSeat,
            'insuredReqDtos[1].insuredname'       : partialRequestParams.insuredName,
            // 过户车
            'carReqDto.haveOwnerChange'           : context.extendedAttributes?.transferFlag ? 1 : 0,
            'carReqDto.ownerChangeDate'           : context.extendedAttributes?.transferDate ? _DATE_FORMAT1.format(context.extendedAttributes.transferDate) : null
        ]
    }

    // 商业险报价前校验-北京
    static final _PRE_FOR_CAL_BI_RPG_PARTIAL_110000 = { context ->
        def renewalVehicleInfo = context.renewalVehicleInfo
        def vehicleInfo = context.vehicleInfo
        def insuredInfo = context.insuredInfo
        Auto auto = context.auto
        [
            'carReqDto.frameno'            : renewalVehicleInfo?.frameNo ?: auto.vinNo ?: vehicleInfo?.frameNo,
            'carReqDto.engineno'           : renewalVehicleInfo?.engineNo ?: auto.engineNo ?: vehicleInfo?.engineNo,
            'carReqDto.enrolldate'         : auto.enrollDate ? _DATE_FORMAT1.format(auto.enrollDate) : context.additionalParameters.supplementInfo?.enrollDate ? _DATE_FORMAT1.format(context.additionalParameters.supplementInfo?.enrollDate) : renewalVehicleInfo?.enrollDate ?: vehicleInfo?.enrollDate,
            'carReqDto.vehicle_modelsh'    : vehicleInfo?.modelName,
            'carReqDto.carModelDetail'     : "${vehicleInfo?.modelName}${vehicleInfo?.modelDesc}",
            'carReqDto.seatcount'          : auto.autoType.seats ?: vehicleInfo?.seat,
            'insuredReqDtos[1].insuredname': auto.owner ?: insuredInfo?.insuredName
        ]
    }

    // 商业险报价前校验-上海
    static final _PRE_FOR_CAL_BI_RPG_PARTIAL_310000 = { context ->
        Auto auto = context.auto
        def kind = context.extendedAttributes?.transferFlag ? 'normal' : context.renewable ? 'renewal' : context.historical ? 'reuse' : 'normal'

        def renewalVehicleInfo = context.renewalVehicleInfo
        def vehicleInfo = context.vehicleInfo
        def insuredInfo = context.insuredInfo
        def carInfo = context.carInfo

        def _PRE_FOR_CAL_BI_PARTIAL_REQUEST_PARAMS_GENERATOR_MAPPINGS = [
            // 转保参数生成
            normal : {
                [
                    vehicleDetail : vehicleInfo?.modelDesc ? "${vehicleInfo?.modelName}${vehicleInfo?.modelDesc}" : "${carInfo.modelName}${carInfo.modelDesc}",
                    vheiclaName   : vehicleInfo?.standardName ?: carInfo.modelName,
                    carSeat       : auto.autoType.seats ?: vehicleInfo?.seat ?: carInfo.seat,
                    carEnrollDate : auto.enrollDate ? _DATE_FORMAT1.format(auto.enrollDate) : context.additionalParameters.supplementInfo?.enrollDate  ? _DATE_FORMAT1.format(context.additionalParameters.supplementInfo?.enrollDate) : carInfo?.enrollDate,
                    frameNo       : auto.vinNo ?: vehicleInfo.frameNo,
                    engineNo      : auto.engineNo ?: vehicleInfo.engineNo,
                    insuredName   : auto.owner
                ]
            },
            // 续保参数
            renewal: {
                [
                    vehicleDetail : vehicleInfo.carModelDetail ?: "${vehicleInfo.modelName}${vehicleInfo.modelDesc}",
                    vheiclaName   : vehicleInfo.VEHICLE_MODELSH ?: vehicleInfo.modelName,
                    carSeat       : vehicleInfo.SeatCount ?: vehicleInfo.seat,
                    carEnrollDate : auto.enrollDate ? _DATE_FORMAT1.format(auto.enrollDate) : vehicleInfo.enrollDate,
                    frameNo       : renewalVehicleInfo.frameNo ?: auto.vinNo ?: vehicleInfo.frameNo,
                    engineNo      : renewalVehicleInfo.engineNo ?: auto.engineNo ?: vehicleInfo.engineNo,
                    insuredName   : auto.owner ?: insuredInfo?.insuredName
                ]
            },
            // 历史用户
            reuse  : {
                [
                    vehicleDetail : vehicleInfo?.carModelDetail ?: "${vehicleInfo?.modelName}${vehicleInfo?.modelDesc}",
                    vheiclaName   : vehicleInfo?.modelName ?: vehicleInfo?.modelname + '轿车',
                    carSeat       : auto.autoType.seats ?: vehicleInfo?.seatcount ?: vehicleInfo?.seat,
                    carEnrollDate : auto.enrollDate ? _DATE_FORMAT1.format(auto.enrollDate) : vehicleInfo.enrolldate,
                    frameNo       : auto.vinNo ?: vehicleInfo?.frameno,
                    engineNo      : auto.engineNo ?: vehicleInfo?.engineno,
                    insuredName   : auto.owner ?: insuredInfo?.insuredName
                ]
            }
        ]

        def partialRequestParams = _PRE_FOR_CAL_BI_PARTIAL_REQUEST_PARAMS_GENERATOR_MAPPINGS[kind](context)

        [
            'carReqDto.frameno'                   : partialRequestParams.frameNo,
            'carReqDto.engineno'                  : partialRequestParams.engineNo,
            'carReqDto.enrolldate'                : partialRequestParams.carEnrollDate,
            'carReqDto.vehicle_modelsh'           : partialRequestParams.vheiclaName,
            'carReqDto.carModelDetail'            : partialRequestParams.vehicleDetail,
            'carReqDto.seatcount'                 : partialRequestParams.carSeat,
            'insuredReqDtos[1].insuredname'       : partialRequestParams.insuredName,
            // 经测试，购车发票日期可以按初登日期处理
            'carReqDto.certificatedateSH'         : partialRequestParams.carEnrollDate,
            // 过户车
            'carReqDto.haveOwnerChange'           : context.extendedAttributes?.transferFlag ? 1 : 0,
            'carReqDto.ownerChangeDate'           : context.extendedAttributes?.transferDate ? _DATE_FORMAT1.format(context.extendedAttributes.transferDate) : null
        ]
    }

    static final _PRE_FOR_CAL_BI_RPG_DEFAULT =_PRE_FOR_CAL_BI_RPG_BASE.curry(_PRE_FOR_CAL_BI_RPG_PARTIAL_DEFAULT)

    static final _PRE_FOR_CAL_BI_RPG_110000 =_PRE_FOR_CAL_BI_RPG_BASE.curry(_PRE_FOR_CAL_BI_RPG_PARTIAL_110000)

    static final _PRE_FOR_CAL_BI_RPG_310000 =_PRE_FOR_CAL_BI_RPG_BASE.curry(_PRE_FOR_CAL_BI_RPG_PARTIAL_310000)

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ResponseHandler(RHs)">

    static final _CALCULATE_CI_RH_120000 = { result, context ->
        log.info '交强险报价成功，结果为：{}', result
        def carShipTaxMessage = result.civiewmodel
        populateQuoteRecordBZ(context, carShipTaxMessage.ci_premium as double, 0)
        return getContinueFSRV(result)
    }


    static final _CALCULATE_CI_RH_DEFAULT = { result, context ->
        log.info '交强险报价成功，结果为：{}', result
        def carShipTaxMessage = result.civiewmodel
        def autoTax = ['thisPayTax', 'prePayTax', 'delayPayTax'].inject 0.0, { sum, propName ->
            sum + (carShipTaxMessage[propName] as double ?: 0)
        }
        populateQuoteRecordBZ(context, carShipTaxMessage.ci_premium as double, autoTax)
        return getContinueFSRV(result)
    }

    //</editor-fold>


    static final _AMOUNT_CONVERTER_BOOLEAN = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        insurancePackage[propName] ? 1 : -1
    }

    static final _AMOUNT_CONVERTER_UNSUPPORTED = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        -1
    }

    static final _AMOUNT_CONVERTER_FROM_JSON = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        insurancePackage[propName] ? kindItem.amount : -1
    }

    static final _GLASS_CONVERTER_FROM_AMOUNT = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        //获取玻璃险类型列表，用于判断哪种类型的玻璃可投
        def amountList = context.enableInsurancePackageList.find { item ->
            kindItem.outerKindCode == item.kindCode
        }?.amountList

        insurancePackage[propName] ? ('0|10|20' == amountList ? (DOMESTIC_1 == insurancePackage.glassType ? 10 : 20): amountList.tokenize('|')[1]) : -1
    }

    static final _AMOUNT_CONVERTER_FROM_AMOUNT_LIST = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        def expectedAmount = insurancePackage[propName]
        if (expectedAmount) {
            def outerKindCode = kindItem.outerKindCode

            def outerKindAmountList = context.initKindInfo.items.find { kind ->
                kind.kindCode == outerKindCode
            }.amountList.tokenize('|').collect { it as int }

            def kindAmountList = kindItem.amountList

            kindItem.amountList = outerKindAmountList.intersect kindAmountList
            def amountList = kindItem?.amountList?.reverse()
            adjustInsureAmount(expectedAmount, amountList, { it as double }, { it as double }) ?: -1
        } else {
            -1
        }
    }

    /**
     * 内部的保额转换成外部的请求(内转外)
     */
    static final _I2O_PREMIUM_CONVERTER = { context, outerKindCode, kindItem, result ->
        [
            kindCode: outerKindCode,
            amount  : result
        ]
    }

    /**
     * 获取报价后，将返回的报价转换成内部的结果（外转内）
     * 返回值有四项：保额，报价，iop的报价，其他的信息(比如玻璃险的类型，获取乘客险的座位数)
     */
    static final _O2I_PREMIUM_CONVERTER = { context, innerKindCode, kindItem, amountName, premiumName, isIop,
                                            iopPremiumName, extConfig ->
        def other
        if (_GLASS == innerKindCode) {
            other = (10 == kindItem?.amount) ? DOMESTIC_1 : (20 == kindItem?.amount) ? IMPORT_2 : null
        }

        /**
         * context.vehicleInfo?.seat 转保
         * context.vehicleInfo?.SeatCount 续保
         * context.vehicleInfo?.seatcount 历史客户
         * context.initKindInfo?.seatCount 快速续保，这里直接从初始化险种步骤取
         */
        if (_PASSENGER_AMOUNT == innerKindCode) {
            other = (context.auto.autoType.seats ?: context.vehicleInfo?.seat ?: context.vehicleInfo?.SeatCount ?: context.vehicleInfo?.seatcount ?: context.initKindInfo?.seatCount) as int
            if (kindItem?.amount) {
                kindItem.amount = kindItem.amount / (other - 1)
            }
        }

        [
            isIop ? null : kindItem?.amount,
            isIop ? null : kindItem?.premium,
            isIop ? kindItem?.premium : null,
            other
        ]
    }

    static final _KIND_ITEM_CONVERTERS_CONFIG = [
        ['050210', _SCRATCH_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _SCRATCH_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null], //车身划痕损失险
        ['050231', _GLASS, _GLASS_CONVERTER_FROM_AMOUNT, null, _O2I_PREMIUM_CONVERTER, null], //玻璃单独破碎险
        ['050200', _DAMAGE, _AMOUNT_CONVERTER_FROM_JSON, null, _O2I_PREMIUM_CONVERTER, null], //机动车损失保险
        ['050310', _SPONTANEOUS_LOSS, _AMOUNT_CONVERTER_FROM_JSON, null, _O2I_PREMIUM_CONVERTER, null], //自燃损失险
        ['050600', _THIRD_PARTY_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _THIRD_PARTY_AMOUNT_LIST,
         _O2I_PREMIUM_CONVERTER, null], //机动车第三者责任保险
        ['050500', _THEFT, _AMOUNT_CONVERTER_FROM_JSON, null, _O2I_PREMIUM_CONVERTER, null], //机动车盗抢保险
        ['050291', _ENGINE, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //涉水发动机损坏险
        ['050701', _DRIVER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _DRIVER_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null], //机动车车上人员责任保险（司机）
        ['050702', _PASSENGER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _PASSENGER_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null], //机动车车上人员责任保险（乘客）
        ['050451', _UNABLE_FIND_THIRDPARTY, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //机动车损失保险无法找到第三方特约险
        ['050921', _THEFT_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（盗抢险）
        ['050924', _ENGINE_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（涉水发动机损坏险）
        ['050911', _DAMAGE_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车损险）
        ['050912', _THIRD_PARTY_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（三者险）
        ['050922', _SCRATCH_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车身划痕险）
        ['050935', _SPONTANEOUS_LOSS_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（自燃险）
        ['050928', _DRIVER_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车上人员司机）
        ['050929', _PASSENGER_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车上人员乘客）
        ['050252', null, _AMOUNT_CONVERTER_UNSUPPORTED, null, _O2I_PREMIUM_CONVERTER, null], //指定修理厂
        ['050643', null, _AMOUNT_CONVERTER_UNSUPPORTED, null, _O2I_PREMIUM_CONVERTER, null], //精神损害抚慰金责任险
    ]

    // 计算非续保自定义商业险---郑州
    static final _CALCULATE_BI_FOR_CHANGE_ITEM_KIND_RPG_BASE = { config, context ->
        disableUnsupportedKindItems context
        context.kindItemConvertersConfig = config
        def allBaseKindItems = getAllBaseKindItems context, context.kindItemConvertersConfig
        def quoteParams = getQuoteKindItemParams context, allBaseKindItems, config, _I2O_PREMIUM_CONVERTER
        def (startDateText, endDateText) = getDefaultStartDateTextBI(context)

        def changedKindItems = quoteParams.collect { it ->
            "$it.kindCode:${it.amount}"
        }.join ','

        [
            uniqueID      : context.uniqueID,
            packageName   : 'OptionalPackage',
            changeItemKind: changedKindItems,
            startDateCI   : startDateText,
            endDateCI     : endDateText,
            ciselect      : isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) ? 1 : 0
        ]
    }

    static final _CALCULATE_BI_FOR_CHANGE_ITEM_KIND_RPG = _CALCULATE_BI_FOR_CHANGE_ITEM_KIND_RPG_BASE.curry _KIND_ITEM_CONVERTERS_CONFIG

}
