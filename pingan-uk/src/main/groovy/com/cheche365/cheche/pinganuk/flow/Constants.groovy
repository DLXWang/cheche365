package com.cheche365.cheche.pinganuk.flow

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5
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

    static final _VEHICLE_INFO_EXTRACTOR = { context ->
        def vehicle = context.voucher?.vehicleTarget
        if (vehicle) {
            [
                vinNo     : vehicle.vehicleFrameNo?.trim(),
                engineNo  : vehicle.engineNo?.trim(),
                enrollDate: vehicle.firstRegisterDate ? _DATE_FORMAT5.parse(vehicle.firstRegisterDate) : null,
                brandCode : context.selectedCarModel?.autoModelName
            ].with {
                it.vinNo && it.engineNo ? it : null
            }
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

    /**
     * 车型列表option构造闭包
     */
    static final _PINGANUK_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand         : vehicle.brandName,
            family        : vehicle.autoModelName,
            gearbox       : '',
            exhaustScale  : vehicle.exhaustMeasure.toString(),
            model         : vehicle.pa18AliasName + ' ' + vehicle.remark,
            productionDate: vehicle.firstSaleDate,
            seats         : vehicle.seats,
            newPrice      : vehicle.purchasePrice,
        ]
        getVehicleOption vehicle.key, vehicleOptionInfo
    }

    static final _PINGANUK_SAVE_PERSISTENT_STATE = { context ->
        [
            mainQuotationNo    : context.mainQuotationNo,// 询价单号
            circInfoDTO        : context.circInfoDTO,// 报价返回的信息
            autoTypeQueryCode  : context.circVehicleTypeInfo,//车辆查询返回的信息
            voucher            : context.voucher,// 报价返回的车辆信息
            c51CircInfoDTO     : context.c51CircInfoDTO,
            applyPolicyNo      : context.applyPolicyList?.applyPolicyNo ?: context.applyPolicyNo, // 投保单号
            waitIdentityCaptcha: context.waitIdentityCaptcha // 是否等待身份验证码
        ]
    }

    static final _PINGANUK_LOAD_PERSISTENT_STATE = { context, persistentState ->
        [
            mainQuotationNo    : persistentState?.mainQuotationNo,// 询价单号
            circInfoDTO        : persistentState?.circInfoDTO,// 报价返回的信息
            autoTypeQueryCode  : persistentState?.circVehicleTypeInfo,////车辆查询返回的信息
            voucher            : persistentState?.voucher,// 报价返回的车辆信息
            c51CircInfoDTO     : persistentState?.c51CircInfoDTO,
            applyPolicyNo      : persistentState?.applyPolicyNo,// 投保单号
            waitIdentityCaptcha: persistentState?.waitIdentityCaptcha // 是否等待身份验证码
        ]
    }

}
