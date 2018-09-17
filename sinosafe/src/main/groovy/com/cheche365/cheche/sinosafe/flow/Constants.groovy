package com.cheche365.cheche.sinosafe.flow

import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMMERCIAL_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMPULSORY_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.flow.business.flow.util.BusinessUtils.getVehicleOption


class Constants {

    static final _IDENTITY_TYPE_MAPPINGS = [
        1L : '120001',//身份证
        2L : '120005',//护照
        3L : '120012',//军官证
        4L : '120014',//回乡证
        5L : '120014',//临时身份证
        6L : '120014',//户口本
        7L : '120014',//警官证
        8L : '120004',//台胞证
        9L : '120014',//营业执照
        10L: '120014',//其它证件
        11L: '120011',//组织机构代码
        12L: '120013',//工商注册号码
        13L: '120014',//统一社会信用代码
        14L: '120014',//港澳通行证
        15L: '120006',//台湾通行证

    ]

    static final _USE_CHARACTER_MAPPINGS = [
        21L: '343002',//私家车
        22L: '343003',//企业用车
        23L: '343004',//组织用车
    ]

    static final _STATUS_CODE_SINOSAFE_CONFIRM_INSURE_FAILURE = -205001L

    static final _CITY_SUPPLEMENT_INFO_MAPPINGS = [
        default: [
            _SUPPLEMENT_INFO_COMMERCIAL_START_DATE_TEMPLATE_QUOTING,
            _SUPPLEMENT_INFO_COMPULSORY_START_DATE_TEMPLATE_QUOTING
        ]
    ]

    static final get_VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS() {
        [
            default: [
                _SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING,
                _SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
            ]
        ]
    }

    static final _CITY_PAYTAX_VOU_MAPPINGS = [
        110000L: 'isNull',
        310000L: 'isNull',
        330000L: 'insuredIdNo',
        410000L: 'insuredIdNo',
        350000L: '235888',
        370200L: '0012061001',
        default: '12061001'
    ]
    static final _SPECIAL_PROMISE_CODE_MAPPINGS = [custom_special_agreement_01: 'HB9999']

    static final _SINOSAFE_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand         : vehicle.BRAND_NAME ?: vehicle.MODEL_NAME,
            family        : vehicle.FAMILY_NAME,
            gearbox       : vehicle.TRANSMISSIONT_TYPE,
            exhaustScale  : vehicle.DISPLACEMENT,
            model         : vehicle.MODEL_NAME,
            productionDate: vehicle.MARKET_YEAR,
            seats         : vehicle.SET_NUM,
            newPrice      : vehicle.CAR_PRICE,
        ]
        getVehicleOption vehicle.MODEL_CODE, vehicleOptionInfo
    }

    static final _SINOSAFE_LOAD_PERSISTENT_STATE = { context, persistentState ->
        [
            CAL_APP_NO              : persistentState?.CAL_APP_NO, // 报价单号
            SY_DEMAND_NO            : persistentState?.SY_DEMAND_NO, // 商业转保查询码
            JQ_DEMAND_NO            : persistentState?.JQ_DEMAND_NO, // 交强转保查询码
            isUpdateImages          : persistentState?.isUpdateImages, // 是否上传影像
            updateImagesErrorMessage: persistentState?.updateImagesErrorMessage, // 上传图片的原始错误提示信息
            verificationCode        : persistentState?.verificationCode, // 保监会验证码
        ]
    }

    static final _SINOSAFE_SAVE_PERSISTENT_STATE = { context ->
        [
            CAL_APP_NO              : context.CAL_APP_NO,
            SY_DEMAND_NO            : context.SY_DEMAND_NO,
            JQ_DEMAND_NO            : context.JQ_DEMAND_NO,
            isUpdateImages          : context.isUpdateImages,
            updateImagesErrorMessage: context.updateImagesErrorMessage,
            verificationCode        : context.additionalParameters.supplementInfo.verificationCode
        ]
    }
}
