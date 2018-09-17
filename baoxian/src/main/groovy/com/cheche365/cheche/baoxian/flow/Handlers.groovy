package com.cheche365.cheche.baoxian.flow

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._UNABLE_FIND_THIRDPARTY
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getQuoteKindItemParams
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static java.time.ZonedDateTime.now as today
import static com.cheche365.cheche.common.util.DateUtils.getLocalDateTime

@Slf4j
class Handlers {

    static
    final _O2I_PREMIUM_CONVERTER = { context, innerKindCode, quoteItem, propName, premiumName, isIop, iopPremiumName, extConfig ->
        [
            quoteItem ? quoteItem.amount as double : 0,
            quoteItem ? quoteItem.premium as double : 0,
            quoteItem?.ncfPremium ? quoteItem.ncfPremium as double : 0,
            {
                if (_GLASS == innerKindCode) {
                    quoteItem ? '1.00' == quoteItem.amount ? DOMESTIC_1 : IMPORT_2 : null
                } else if (_PASSENGER_AMOUNT == innerKindCode) {
                    (context.selectedCarModel?.seat ?: 0) as int //续保车拿不到，只有走查询车型接口的才能够查到车的座位数
                } else {
                    0
                }
            }()
        ]
    }


    static final _CONVERTER_FROM_AMOUNT = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        if (insurancePackage[propName]) {
            [
                amount       : 1,
                notDeductible: insurancePackage[iopPropName] ? 'Y' : 'N'
            ]
        }
    }
    static final _AMOUNT_CONVERTER_BOOLEAN = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        if (insurancePackage[propName]) {
            if ('unableFindThirdParty' != propName) {
                [
                    amount       : 1,
                    notDeductible: insurancePackage[iopPropName] ? 'Y' : 'N'
                ]
            } else {
                [
                    amount       : 1,
                    notDeductible: 'N'
                ]
            }
        }
    }

    static
    final _GLASS_CONVERTER_FROM_AMOUNT = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        if (insurancePackage[propName]) {
            def glassAmount = (DOMESTIC_1 == insurancePackage.glassType ? 1 : 2)
            [
                amount       : glassAmount,
                notDeductible: 'N'
            ]
        }
    }

    static
    final _AMOUNT_CONVERTER_FROM_AMOUNT_LIST = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        def expectedAmount = insurancePackage[propName]
        def amountList = kindItem?.amountList?.reverse()

        def actualAmount = expectedAmount ?
            (adjustInsureAmount(expectedAmount, amountList, { it as double }, { it as double }) ?: 0)
            : 0

        if (actualAmount) {
            [
                amount       : actualAmount as int,
                notDeductible: insurancePackage[iopPropName] ? 'Y' : 'N'
            ]
        }
    }

    static final _I2O_PREMIUM_CONVERTER = { context, outerKindCode, outKindItem, result ->
        if (result) {
            [
                riskCode     : outerKindCode,
                riskName     : outKindItem.riskName,
                amount       : result.amount,
            ] + ('GlassIns' != outerKindCode ? [notDeductible: result.notDeductible] : [:])
        }
    }

    static final _KIND_CODE_CONVERTERS_CONFIG = [
        ['VehicleDemageIns', _DAMAGE, _CONVERTER_FROM_AMOUNT, [name: '车辆损失险'], _O2I_PREMIUM_CONVERTER, null],
        ['ThirdPartyIns', _THIRD_PARTY_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [name: '第三者责任险', amountList: _THIRD_PARTY_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null],
        ['DriverIns', _DRIVER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [name: '司机责任险', amountList: _DRIVER_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null],
        ['PassengerIns', _PASSENGER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [name: '乘客责任险', amountList: _PASSENGER_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null],
        ['TheftIns', _THEFT, _CONVERTER_FROM_AMOUNT, [name: '全车盗抢险'], _O2I_PREMIUM_CONVERTER, null],
        ['GlassIns', _GLASS, _GLASS_CONVERTER_FROM_AMOUNT, [name: '玻璃单独破碎险'], _O2I_PREMIUM_CONVERTER, null],
        ['CombustionIns', _SPONTANEOUS_LOSS, _CONVERTER_FROM_AMOUNT, [name: '自燃损失险'], _O2I_PREMIUM_CONVERTER, null],
        ['ScratchIns', _SCRATCH_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, [name: '车身划痕险', amountList: _SCRATCH_AMOUNT_LIST], _O2I_PREMIUM_CONVERTER, null],
        ['WadingIns', _ENGINE, _AMOUNT_CONVERTER_BOOLEAN, [name: '涉水损失险'], _O2I_PREMIUM_CONVERTER, null],
        ['VehicleDemageMissedThirdPartyCla', _UNABLE_FIND_THIRDPARTY, _AMOUNT_CONVERTER_BOOLEAN, [name: '机动车损失保险无法找到第三方特约险'], _O2I_PREMIUM_CONVERTER, null],
    ]

    static final getAllKindItems(kindCodeConvertersConfig) {
        kindCodeConvertersConfig.collectEntries { outerKindCode, _1, _2, kindInfo, _4, _5 ->
            [
                (outerKindCode): [
                    riskName  : kindInfo.name,
                    amountList: kindInfo.amountList
                ]
            ]
        }
    }

    static final _QUOTE_PRICE_BASE = { kindCodeConvertersConfig, context ->
        context.kindCodeConvertersConfig = kindCodeConvertersConfig

        def area = context.area
        def auto = context.auto
        def selectedCarModel = context.selectedCarModel
        def isNew = !(auto.licensePlateNo as boolean)
        def transferFlag = context.additionalParameters.supplementInfo?.transferFlag
        def (efcStartDateText, efcEndDateText) = getCompulsoryInsurancePeriodTexts(context)
        def (bizStartDateText, bizEndDateText) = getCommercialInsurancePeriodTexts(context)
        def ip = context.accurateInsurancePackage
        def lessThan270Days = today().minusDays(270) <= getLocalDateTime(auto.enrollDate)

        def kindList = getQuoteKindItemParams(context, getAllKindItems(kindCodeConvertersConfig), kindCodeConvertersConfig, _I2O_PREMIUM_CONVERTER)

        [
            insureAreaCode: area.id,
            channelUserId : context.channelID ?: context.channelId,
            carInfo       : [
                isNew        : isNew ? 'Y' : 'N',
                //新车未上牌或者初登日期小于9个月，此参数必传
                price        : isNew || lessThan270Days ? selectedCarModel.price : null,
                carLicenseNo : isNew ? null : auto.licensePlateNo, //当isNew为Y时不传
                vinCode      : auto.vinNo,
                engineNo     : auto.engineNo,
                registDate   : _DATE_FORMAT3.format(auto.enrollDate),
                isTransferCar: transferFlag ? 'Y' : 'N',
                transferDate : transferFlag ? _DATE_FORMAT3.format(context.additionalParameters.supplementInfo.transferDate) : null, //当isTransferCar=Y时，此参数必传
                vehicleName  : selectedCarModel.vehicleName,
                vehicleId    : selectedCarModel.vehicleId,
                seat         : selectedCarModel.seat,
            ],
            carOwner      : [
                name      : auto.owner,
                idcardType: '0', //默认身份证
                idcardNo  : auto.identity,
                phone     : auto.mobile
            ],
            insureInfo    : [
                efcInsureInfo: isCompulsoryOrAutoTaxQuoted(ip) ? [
                    startDate: efcStartDateText,
                    endDate  : efcEndDateText,
                ] : null,
                taxInsureInfo: [
                    isPaymentTax: isCompulsoryOrAutoTaxQuoted(ip) ? 'Y' : 'N'
                ],
                bizInsureInfo: isCommercialQuoted(ip) ? [
                    startDate: bizStartDateText,
                    endDate  : bizEndDateText,
                    riskKinds: kindList
                ] : null
            ],
            providers: context.providers
        ]

    }


    static final _QUOTE_PRICE_RPG = _QUOTE_PRICE_BASE.curry _KIND_CODE_CONVERTERS_CONFIG

    static final _QUOTE_PRICE_2M_RPG = _QUOTE_PRICE_BASE.curry _KIND_CODE_CONVERTERS_CONFIG

}




