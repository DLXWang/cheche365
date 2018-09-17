package com.cheche365.cheche.sinosig.flow.util

import com.cheche365.cheche.core.model.GlassType
import com.cheche365.cheche.core.model.InsurancePackage

import static ComboUtils._KIND_ITEM_CONFIG
import static ComboUtils.getKindItemFromList
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._INSURANCE_MAPPINGS
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._QUOTE_RECORD_COMMERCIAL_MAPPINGS
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._UNABLE_FIND_THIRDPARTY
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.resolveNewQuoteRecordInContext
import static com.cheche365.flow.business.flow.util.BusinessUtils.getVehicleOption


/**
 * Created by wangxin on 2016/3/3.
 */
class BusinessUtils {

    static final _SINOSIG_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand          : vehicle.brandName,
            family         : vehicle.familyName,
            gearbox        : vehicle.gearboxName,
            exhaustScale   : vehicle.engineDesc,
            model          : vehicle.standardName,
            productionDate : vehicle.parentVehName,
            seats          : vehicle.seat,
            newPrice       : vehicle.price,
        ]
        getVehicleOption vehicle.rbCode, vehicleOptionInfo
    }

    static final _KIND_CODE_CONVERTERS = [
        (_DAMAGE)                : { kindItem, insurancePackageAmount, context ->
            ['paraMap.amount': insurancePackageAmount ? getAmount(kindItem.mapValue) : 0.00]
        },
        (_THIRD_PARTY_AMOUNT)    : { kindItem, insurancePackageAmount, context ->
            ['paraMap.amount': insurancePackageAmount]
        },

        (_THEFT)                 : { kindItem, insurancePackageAmount, context ->
            ['paraMap.amount': insurancePackageAmount ? getAmount(kindItem.mapValue) : 0.00]
        },

        (_DRIVER_AMOUNT)         : { kindItem, insurancePackageAmount, context ->
            ['paraMap.unitAmount': insurancePackageAmount]
        },

        (_PASSENGER_AMOUNT)      : { kindItem, insurancePackageAmount, context ->
            [
                'paraMap.unitAmount': insurancePackageAmount,
                'paraMap.quantity'  : getSeats(context) - 1,
            ]
        },

        (_SPONTANEOUS_LOSS)      : { kindItem, insurancePackageAmount, context ->
            ['paraMap.amount': insurancePackageAmount ? getAmount(kindItem.mapValue) : 0.00]
        },

        (_GLASS)                 : { kindItem, insurancePackageAmount, context ->
            ['paraMap.modeCode': insurancePackageAmount ? (context.accurateInsurancePackage.glassType == GlassType.Enum.DOMESTIC_1 ? 1 : 2) : 0]
        },

        (_SCRATCH_AMOUNT)        : { kindItem, insurancePackageAmount, context ->
            ['paraMap.amount': insurancePackageAmount]
        },

        (_ENGINE)                : { kindItem, insurancePackageAmount, context ->
            ['paraMap.remark': insurancePackageAmount ? 1 : 0]
        },

        (_UNABLE_FIND_THIRDPARTY): { kindItem, insurancePackageAmount, context ->
            ['paraMap.remark': insurancePackageAmount ? 1 : 0]
        }
    ]

    static populateQuoteRecord(context, kindItemList, paraMap) {
        resolveNewQuoteRecordInContext(context).with { newQuoteRecord ->
            _QUOTE_RECORD_COMMERCIAL_MAPPINGS.each { key, configs ->
                def kindItem = getKindItemFromList kindItemList, key
                if (kindItem) {
                    if (configs.amountName) {
                        newQuoteRecord[configs.amountName] = 'D3' == kindItem.kindCode || 'D4' == kindItem.kindCode ? kindItem.unitAmount as double : kindItem.amount as double
                    }
                    if (configs.premiumName) {
                        newQuoteRecord[configs.premiumName] = kindItem.premium as double
                    }
                    if (configs.iopPremiumName && context.iopEnabled) {
                        newQuoteRecord[configs.iopPremiumName] = '0.00' == kindItem.premium ? 0.00 : kindItem.excludingPremium as double
                    }
                }
            }
            premium = paraMap.sumPremium_com as double //商业险总计，不包含iop
            newQuoteRecord
        }
    }

    private static getAmount(map) {
        map.find {
            it.key as double
        }?.key?.toDouble()
    }

    static final generateRenewalPackage(kindItemList, paraMap) {
        new InsurancePackage().with { insurancePackage ->

            _KIND_ITEM_CONFIG.each { itemName, itemValue ->
                def kindItem = kindItemList.find { it.kindCode == itemValue.kindCode }
                def kindItemIopName = _INSURANCE_MAPPINGS[itemName]?.iopPropName
                insurancePackage[itemName] = kindItem.premium as double ? ('D4' != kindItem.kindCode ? kindItem.amount : kindItem.unitAmount) as double : 0.0
                if (itemName == _GLASS && insurancePackage[_GLASS]) {
                    insurancePackage.glassType = '1' == kindItem.modeCode ? GlassType.Enum.DOMESTIC_1 : GlassType.Enum.IMPORT_2
                }
                if (kindItemIopName) {
                    insurancePackage[kindItemIopName] = kindItem.excludingPremium as double
                }
            }

            def compulsoryEnabled = paraMap.sumPremium_tra as double || paraMap.sumPremium_tax as double
            compulsory = compulsoryEnabled
            autoTax = compulsoryEnabled

            insurancePackage
        }
    }

    static getSeats(context) {
        def param = context.selectedCarModel.seat
        def seats = (param instanceof List ? param.first() : param) as int
        seats
    }

    static getExhaust(context) {
        def param = context.selectedCarModel.displacement
        def exhaust = (param instanceof List ? param.first() : param) as double
        exhaust
    }


    static getEnrollDate(context) {
        context.selectedCarModel?.enroll ? context.selectedCarModel.enroll :
            context.auto.enrollDate ? _DATE_FORMAT3.format(context.auto.enrollDate) : null
    }

    /**
     * 获取商业险报价的请求参数：
     * 下面为页面js的注释
     * calcFlag 1代表 是页面初始化过来的 需要重新获取日期算价 2是用户收到更改 提示用户重复投保.
     * @param context
     * @param calcFlag 日期校验标志位
     * @param planType 套餐种类
     * @return
     */
    static getCommercialRequestParameters(context, calcFlag, planType, premiumRepeatedly = '1') {
        def (startDateText) = getCommercialInsurancePeriodTexts(context)
        [
            'paraMap.id'                  : context.token,
            'paraMap.insuApp'             : startDateText,
            'paraMap.calcFlag'            : calcFlag,
            'paraMap.ifTra'               : context.accurateInsurancePackage?.compulsory ? '1' : '0',
            'paraMap.planType'            : context.renewable ? '2' : planType, // 1:大众热门;2:经济实惠;-1:自由选择
            'paraMap.isCalcObjectiveStore': '0',
            'paraMap.renewalFlag'         : context.renewable ? '1' : '',
            'blackBgcount'                : '1',
            'paraMap.premiumRepeatedly'   : context.renewable ? '0' : premiumRepeatedly,
            'paraMap.initRenePrice'       : context.renewable ? '1' : ''
        ]
    }
}
