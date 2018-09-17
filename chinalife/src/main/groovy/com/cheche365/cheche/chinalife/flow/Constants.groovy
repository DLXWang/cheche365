package com.cheche365.cheche.chinalife.flow

import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.isDefaultStartDate
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts



/**
 * 流程步骤所需的常量
 */
class Constants {

    static final _STATUS_CODE_CHINALIFE_CAR_MODEL_NOT_FOUND = 40001L
    static final _STATUS_CODE_CHINALIFE_RENEW_FAILURE       = _STATUS_CODE_CHINALIFE_CAR_MODEL_NOT_FOUND + 1


    //险种保额在unitAmount上险种
    static final  def _UNIT_AMOUNT_KIND_ITEMS = [
        'D11', //司机座位责任险
        'D12'  //乘客座位责任险
    ]

    static final _CITY_SUPPLEMENT_INFO_MAPPINGS = [

        default : []
    ]

    static final _AUTOTYPE_EXTRACTOR = { context ->
        context.vehicleInfo?.brandName ? [context.vehicleInfo.brandName] : []
    }

    static final _KINDCODE_KINDNAME_MAPPING = [
        A   : '机动车辆损失险',
        B   : '第三者责任险',
        G   : '盗抢险',
        D11 : '车上人员责任险-司机',
        D12 : '车上人员责任险-乘客',
        L   : '车身划痕损失险',
        Z   : '自燃损失险',
        X1  : '发动机特别损失险',
        F   : '玻璃单独破碎险'
    ]

    static final _BOOLEAN_KINDCODES = ['A','G','Z']

    static final get_VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS() {
        [
            default: [
                _SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING,
                _SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
            ]
        ]
    }

    static final _VEHICLE_INFO_EXTRACTOR = { context ->
        def vehicle = context.carRenewalInfo
        if (vehicle && vehicle.brandName) {
            [
                vinNo          : vehicle.frameNo?.trim(),
                engineNo       : vehicle.engineNo?.trim(),
                enrollDate     : vehicle.enrollDate ? _DATE_FORMAT3.parse(vehicle.enrollDate) : null,
                brandCode      : vehicle.brandName
            ].with {
                it.vinNo && it.engineNo ? it : null
            }
        }
    }

    static final _UPDATE_CONTEXT_FIND_CAR_MODEL_INFO = { log, context, result, fsrv ->
        context.vehicleInfo = fsrv[2]
        context.carInfo = result.temporary.quoteMain.geQuoteCars[0]
        log.info '车型信息：{}', context.vehicleInfo
        log.info '车辆信息{}', context.carInfo

        def bsStartDateText = result.temporary.bsMinDate
        def bzStartDateText = result.temporary.bzMinDate
        log.info '商业险起保日期：{}，交强险起保日期：{}', bsStartDateText , bzStartDateText
        if (bsStartDateText && !isDefaultStartDate(bsStartDateText)) {
            setCommercialInsurancePeriodTexts context, bsStartDateText
        }
        if (bzStartDateText && !isDefaultStartDate(bzStartDateText)) {
            setCompulsoryInsurancePeriodTexts context, bzStartDateText
        }
    }

    static final _AUTO_INFO_EXTRACTOR = { context ->
        def vehicleInfo = context.carRenewalInfo ?: context.vehicleInfo ?: context.carBrandInfo ?: context.selectedCarModel
        if (vehicleInfo && vehicleInfo?.seatCount) {
            [
                autoType: [
                    seats: vehicleInfo.seatCount as int
                ]
            ]
        }
    }
}
