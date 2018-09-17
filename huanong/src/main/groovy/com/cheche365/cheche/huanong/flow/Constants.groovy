package com.cheche365.cheche.huanong.flow

import static com.cheche365.cheche.parser.Constants.*
import static com.cheche365.flow.business.flow.util.BusinessUtils.getVehicleOption



/**
 * 华农流程步骤所需的常量
 */
class Constants {

    static final _SUCCESS = '#0000'
    static final _STATUS_CODE_HUANONG_CONFIRM_INSURE_FAILURE = -205001L

    static final _HUANONG_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand         : vehicle.brand,
            family        : vehicle.series,
            gearbox       : '',
            exhaustScale  : vehicle.displacement as String,
            model         : vehicle.modelCode,
            productionDate: vehicle.lfDate,
            seats         : vehicle.approvedPassengersCapacity,
            newPrice      : vehicle.purchasePriceTax,//用的是含税的新车价
        ]
        getVehicleOption vehicle.localModelCode, vehicleOptionInfo
    }

    static final get_VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS() {
        [
            default: [
                _SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING,
                _SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
            ]
        ]
    }

    static final _CITY_SUPPLEMENT_INFO_MAPPINGS = [
        default: [
            _SUPPLEMENT_INFO_COMMERCIAL_START_DATE_TEMPLATE_QUOTING,
            _SUPPLEMENT_INFO_COMPULSORY_START_DATE_TEMPLATE_QUOTING
        ]
    ]

    static final _HUANONG_LOAD_PERSISTENT_STATE = { context, persistentState ->
        [
            orderNo                : persistentState?.orderNo,//报价订单号
            token                  : persistentState?.token,            //token令牌
            firstQuotePriceReqJSON : persistentState?.firstQuotePriceReqJSON,//第一次报价的json
            firstQuotePriceRespJOSN: persistentState?.firstQuotePriceRespJOSN,//第一次报价返回结果
            verificationCode       : persistentState?.verificationCode,
            phoneNo                : persistentState?.phoneNo,
            waitIdentityCaptcha    : persistentState?.waitIdentityCaptcha,
            compulsoryExpireDate   : persistentState?.compulsoryExpireDate,
            compulsoryBeginDate    : persistentState?.compulsoryBeginDate,
            commercialExpireDate   : persistentState?.commercialExpireDate,
            commercialBeginDate    : persistentState?.commercialBeginDate,
            formId                 : persistentState?.formId,
            isUpdateImages         : persistentState?.isUpdateImages, // 是否上传影像
            imageUploadId          : persistentState?.imageUploadId,      //图片上传业务id


        ]
    }

    static final _HUANONG_SAVE_PERSISTENT_STATE = { context ->
        [
            orderNo                : context.orderNo, //报价订单号
            token                  : context.token,            //token令牌
            firstQuotePriceReqJSON : context.firstQuotePriceReqJSON,//第一次报价的json
            firstQuotePriceRespJOSN: context.firstQuotePriceRespJOSN,//第一次报价返回结果
            phoneNo                : context.phoneNo,
            waitIdentityCaptcha    : context.waitIdentityCaptcha,
            compulsoryExpireDate   : context.compulsoryExpireDate,
            compulsoryBeginDate    : context.compulsoryBeginDate,
            commercialExpireDate   : context.commercialExpireDate,
            commercialBeginDate    : context.commercialBeginDate,
            formId                 : context.formId,
            isUpdateImages         : context.isUpdateImages, // 是否上传影像
            imageUploadId          : context.imageUploadId,      //图片上传业务id
        ]
    }

    static final _USE_CHARACTER = [
        '21': '8A', //家庭自用
        '22': '8B',//企业非营业
        '23': '8C',//'机关非营业'
        '1' : '8D' //非营业货运
    ]

    /**
     * 证件对照   车保易  -> 华农
     */
    static _CERTI_TYPE_MAPPING = [
        1L : '01',//身份证
        2L : '03',//护照
        11L: '10',//组织机构代码
        13L: 'B'//税务登记证,统一社会信用代码
    ]

    /**
     * 华农提供
     */
    static
    final _HUANONG_THIRD_PARTY_AMOUNT_LIST = [50000, 100000, 150000, 200000, 300000, 500000, 1000000, 1500000, 2000000, 2500000, 3000000, 3500000, 4000000, 4500000, 5000000, 5500000, 6000000, 6500000, 7000000, 7500000, 8000000, 8500000, 9000000, 9500000, 10000000]

    static final _HUANONG_SCRATCH_AMOUNT_LIST = [2000, 5000, 10000, 20000]


}
