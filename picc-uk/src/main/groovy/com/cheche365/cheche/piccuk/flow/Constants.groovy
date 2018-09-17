package com.cheche365.cheche.piccuk.flow

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.core.model.PaymentChannel.Enum.AGENT_PARSER_ALIPAY_62
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.AGENT_PARSER_WECHAT_63
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMMERCIAL_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMPULSORY_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.flow.business.flow.util.BusinessUtils.getVehicleOption



/**
 * 流程步骤所需的常量
 */
@Slf4j
class Constants {

    static final _CITY_SUPPLEMENT_INFO_MAPPINGS = [
        default: [
            _SUPPLEMENT_INFO_COMMERCIAL_START_DATE_TEMPLATE_QUOTING,
            _SUPPLEMENT_INFO_COMPULSORY_START_DATE_TEMPLATE_QUOTING
        ]
    ]

    static final _AUTO_TYPE_EXTRACTOR = { context ->
        def brand = context.vehicleInfo?.brand
        brand ? [brand] : []
    }

    static final get_VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS() {
        [
            default: [
                _SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING,
                _SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
            ]
        ]
    }

    static final _VEHICLE_INFO_EXTRACTOR = { context ->
        if (context.carInfo) {
            [
                vinNo     : context.carInfo?.rackNo?.trim(),
                engineNo  : context.carInfo?.engineNo?.trim(),
                enrollDate: context.carInfo?.enrollDate ? new Date(context.carInfo.enrollDate.time as long) : null,
                brandCode : context.carModelInfo?.modelName
            ].with {
                it.vinNo && it.engineNo ? it : null
            }
        }
    }

    static final _PICCUK_GET_VEHICLE_OPTION_TYPE = { vehicle ->
        [
            value: vehicle.key,
            text : vehicle.value
        ]
    }

    static final _PICCUK_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand         : vehicle.brandName ?: vehicle.brandNameNew,
            family        : vehicle.familyNameNew,
            gearbox       : null,
            exhaustScale  : vehicle.vehicleExhaust,
            model         : vehicle.vehicleName,
            productionDate: vehicle.vehicleYear,
            seats         : vehicle.vehicleSeat,
            newPrice      : vehicle.priceT,
        ]
        getVehicleOption vehicle.vehicleId, vehicleOptionInfo
    }

    static final _PICCUK_GET_VEHICLE_AGAIN_OPTION = { context, vehicle ->
        def first = context.autoModelByVIN ?: ''
        def second = vehicle.vehicleId
        def vehicleCode = [first, second].join('_')

        def vehicleOptionInfo = [
            brand         : vehicle.brandName ?: vehicle.brandNameNew,
            family        : vehicle.familyNameNew,
            gearbox       : null,
            exhaustScale  : vehicle.vehicleExhaust,
            model         : vehicle.vehicleName,
            productionDate: vehicle.vehicleYear,
            seats         : vehicle.vehicleSeat,
            newPrice      : vehicle.priceT,
        ]
        getVehicleOption vehicleCode, vehicleOptionInfo
    }

    static final _PICC_UK_SAVE_PERSISTENT_STATE = { context ->
        [
            proposalNos   : context.proposalNos,
            proposalStatus: context.proposalStatus,
            insertArgs    : context.insertArgs
        ]
    }


    static final _PICC_UK_LOAD_PERSISTENT_STATE = { context, persistentState ->
        [
            proposalNos   : persistentState?.proposalNos,
            proposalStatus: persistentState?.proposalStatus,
            insertArgs    : persistentState?.insertArgs
        ]
    }

    //人保支付方式
    static final _PAYMENT_CHANNELS_MAPPINGS = [
        110000L: [AGENT_PARSER_ALIPAY_62, AGENT_PARSER_WECHAT_63],
        510100L: [AGENT_PARSER_ALIPAY_62, AGENT_PARSER_WECHAT_63],
        default: [AGENT_PARSER_WECHAT_63]
    ]
}
