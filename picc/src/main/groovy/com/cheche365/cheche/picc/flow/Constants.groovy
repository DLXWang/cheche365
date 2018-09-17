package com.cheche365.cheche.picc.flow

import com.cheche365.cheche.core.model.InsuranceBasicInfo

import static com.cheche365.cheche.parser.Constants._DATE_FORMAT1
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMMERCIAL_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMPULSORY_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.isDefaultStartDate
import static com.cheche365.cheche.picc.util.BusinessUtils.getNextDays4Commercial
import static com.cheche365.cheche.picc.util.BusinessUtils.getRealStartDateTextBI
import static com.cheche365.flow.business.flow.util.BusinessUtils.getVehicleOption


/**
 * PICC流程步骤所需的常量
 */
class Constants {

    static final _STATUS_CODE_PICC_GET_CAPTCHA_FAILURE           = 10001L
    static final _STATUS_CODE_PICC_VERIFY_CAPTCHA_FAILURE        = _STATUS_CODE_PICC_GET_CAPTCHA_FAILURE + 1
    static final _STATUS_CODE_PICC_CAR_MODEL_NOT_FOUND           = _STATUS_CODE_PICC_VERIFY_CAPTCHA_FAILURE + 1
    static final _STATUS_CODE_PICC_CAR_IN_BLACK_LIST             = _STATUS_CODE_PICC_CAR_MODEL_NOT_FOUND + 1
    static final _STATUS_CODE_PICC_INSURED_IN_BLACK_LIST         = _STATUS_CODE_PICC_CAR_IN_BLACK_LIST + 1
    static final _STATUS_CODE_PICC_CHECK_COMPULSORY_FAILURE      = _STATUS_CODE_PICC_INSURED_IN_BLACK_LIST + 1
    static final _STATUS_CODE_PICC_CHECK_PERIOD_FAILURE          = _STATUS_CODE_PICC_CHECK_COMPULSORY_FAILURE + 1
    static final _STATUS_CODE_PICC_CHECK_POLICY_FAILURE          = _STATUS_CODE_PICC_CHECK_PERIOD_FAILURE + 1
    static final _STATUS_CODE_PICC_CANNOT_SEND_NEW_CAR_FAILURE   = _STATUS_CODE_PICC_CHECK_POLICY_FAILURE + 1
    static final _STATUS_CODE_PICC_CHECK_SUPPORT_NEW_CAR_FAILURE = _STATUS_CODE_PICC_CANNOT_SEND_NEW_CAR_FAILURE + 1

    private static final _CITY_SUPPLEMENT_INFO_LIST_TYPE2 = [
        _SUPPLEMENT_INFO_COMMERCIAL_START_DATE_TEMPLATE_QUOTING,
        _SUPPLEMENT_INFO_COMPULSORY_START_DATE_TEMPLATE_QUOTING
    ]
    static final _CITY_SUPPLEMENT_INFO_MAPPINGS = [
        default : _CITY_SUPPLEMENT_INFO_LIST_TYPE2
    ]
    //即时投保的城市列表 苏州 成都 重庆 哈尔滨 南京 合肥 济南 昆明 银川 (杭州 宁波 温州 嘉兴 湖州 绍兴 金华 义乌 衢州 台州 丽水)
    static final _REAL_TIME_CITIES = [
        320500L, 510100L, 500000L, 230100L, 320100L, 340100L, 370100L, 530100L, 640100L,
        330100L, 330200L, 330300L, 330400L, 330500L, 330600L, 330700L, 330782L, 330800L, 331000L, 331100L
    ]

    static final _AUTOTYPE_EXTRACTOR = { context ->
        def brand = context.selectedCarModel?.brand ?: context.selectedCarModel?.brandName ?: context.vehicleInfo?.brandName
        brand ? [brand] : []
    }

    static final _VEHICLE_INFO_EXTRACTOR = { context ->
        getVehicleInfo context.renewalVehicleInfo ?: context.vehicleInfo ?: context.historicalVehicleInfo
    }

    static final _INSURANCE_BASIC_INFO_EXTRACTOR = { context ->
        def nextDay = getNextDays4Commercial(context)

        def commercialStartDate
        def commercialStartDateText = getRealStartDateTextBI(context)
        if (commercialStartDateText && !isDefaultStartDate(commercialStartDateText, _DATE_FORMAT1, nextDay)) {
            commercialStartDate = _DATE_FORMAT1.parse(commercialStartDateText)
        }
        def compulsoryStartDate
        def compulsoryStartDateText = context.bzStartDateText
        if (compulsoryStartDateText && !isDefaultStartDate(compulsoryStartDateText, _DATE_FORMAT1, nextDay)) {
            compulsoryStartDate = _DATE_FORMAT1.parse(compulsoryStartDateText)
        }
        if(commercialStartDate || compulsoryStartDate || context.insurancePackage) {
            new InsuranceBasicInfo(commercialStartDate: commercialStartDate, compulsoryStartDate: compulsoryStartDate, insurancePackage: context.insurancePackage)
        }
    }

    static final get_VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS() {
        [
            default: [
                _SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING,
                _SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
            ]
        ]
    }

    private static getVehicleInfo(payload) {
        [
            vinNo      : payload?.frameNo?.trim() ?: payload?.frameno?.trim(),
            engineNo   : payload?.engineNo?.trim() ?: payload?.engineno?.trim(),
            enrollDate : payload?.enrollDate ? _DATE_FORMAT1.parse(payload.enrollDate) : payload?.enrolldate ? _DATE_FORMAT5.parse(payload.enrolldate) : null,
            brandCode  : payload?.modelName?.trim() ?: payload?.brandname?.trim() ?: payload?.carModelDetail?.trim()?: payload?.modelname?.trim()  // TODO 改成城市结果映射
        ].with {
            it.vinNo && it.engineNo ? it : null
        }
    }

    static final _PICC_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand          : vehicle.brandName,
            family         : vehicle.familyName,
            gearbox        : vehicle.gearboxType ?: vehicle.gearboxName,
            exhaustScale   : vehicle.engineDesc ?: vehicle.displacement,
            model          : vehicle.vehicleName ?: vehicle.modelName,
            productionDate : vehicle.parentVehName,
            seats          : vehicle.seat,
            newPrice       : vehicle.price,
        ]
        getVehicleOption vehicle.parentId ?: vehicle.modelCode, vehicleOptionInfo
    }

    // 获取车型价格失败,系统提示车辆信息有误并返回正确的车型列表,对返回的车型信息进行处理
    static final _PICC_GET_VEHICLE_OPTION2 = { context, vehicle ->

        def first = context.autoJYModel ?: ''
        def second = vehicle.vehicleID
        def vehicleCode = [first, second].join('_')

        def vehicleOptionInfo = [
            brand         : vehicle.brandName,
            family        : vehicle.familyName,
            gearbox       : vehicle.transmissionType,
            exhaustScale  : vehicle.vehicleExhaust as String,
            model         : vehicle.vehicleName,
            productionDate: vehicle.parentVehName ?: '',
            seats         : vehicle.seat ?: '',
            newPrice      : vehicle.priceP,
        ]
        getVehicleOption vehicleCode, vehicleOptionInfo
    }

    // 续保SeatCount，历史客户seatcount，转保seat
    static final _AUTO_INFO_EXTRACTOR = { context ->
        if (context.vehicleInfo?.seat || context.vehicleInfo?.SeatCount || context.vehicleInfo?.seatcount) {
            [
                autoType: [
                    seats: (context.vehicleInfo?.SeatCount ?: context.vehicleInfo?.seatcount ?: context.vehicleInfo?.seat) as int
                ]
            ]
        }
    }

}
