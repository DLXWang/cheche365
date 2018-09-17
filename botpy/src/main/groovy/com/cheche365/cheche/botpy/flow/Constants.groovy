package com.cheche365.cheche.botpy.flow

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMMERCIAL_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMPULSORY_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_IOP
import static com.cheche365.cheche.parser.Constants.updateBusinessObjects
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsurancePackageItem
import static com.cheche365.flow.business.flow.util.BusinessUtils.getVehicleOption



/**
 * 金斗云流程步骤所需的常量
 */
class Constants {

    static final _STATUS_CODE_INSURE_SUCCESS = 60001L

    static final _BOTPY_THIRD_PARTY_AMOUNT_LIST = [50000, 100000, 150000, 200000, 300000, 500000, 1000000, 1500000, 2000000]
    static final _BOTPY_DRIVER_AMOUNT_LIST = [10000, 20000, 30000, 40000, 50000, 100000, 200000]
    static final _BOTPY_PASSENGER_AMOUNT_LIST = [10000, 20000, 30000, 40000, 50000, 100000, 200000]
    static final _BOTPY_SCRATCH_AMOUNT_LIST = [2000, 5000, 10000, 20000]

    static final _API_PATH_CREATE_FIND_IC_MODELS = '/requests/ic-models'
    static final _API_PATH_CREATE_QUOTE = '/requests/quotations'
    static final _API_PATH_CREATE_RENEWAL_INFO = '/requests/renewals'

    static final _ENGAGES_TTL = 1L

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

    private static final _TOGGLE_INSURANCE_PACKAGE = { context ->
        def insurancePackage = context.accurateInsurancePackage


        if (!insurancePackage[_THIRD_PARTY_AMOUNT] && (insurancePackage[_DRIVER_AMOUNT] || insurancePackage[_PASSENGER_AMOUNT])) {
            adjustInsurancePackageItem context, _THIRD_PARTY_AMOUNT, _THIRD_PARTY_IOP, 500_000.0, true
        }
        getContinueFSRV insurancePackage
    }

    static final _CITY_RULES_MAPPINGS = [
        default: _TOGGLE_INSURANCE_PACKAGE
    ]

    static final _BOTPY_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand         : '',
            family        : vehicle.vehicle_name,
            gearbox       : '',
            exhaustScale  : vehicle.exhaust,
            model         : vehicle.vehicle_name + ' ' + vehicle.remark,
            productionDate: vehicle.market_date, // 制造年月, 201010
            seats         : vehicle.seat_count,
            newPrice      : vehicle.purchase_price,
        ]
        getVehicleOption vehicle.vehicle_data_id, vehicleOptionInfo
    }


    static final _BOTPY_SAVE_PERSISTENT_STATE = { context ->
        [
            quotation_id     : context.quotation_id, // 报价单号
            proposal_id      : context.proposal_id, // 投保单号
            isNeedUpdateImage: context.isNeedUpdateImage,//是否上传图片
            waitIdentityCaptcha: context.waitIdentityCaptcha, // 是否等待身份验证码
            proposal_status  : context.proposal_status // 核保状态
        ]
    }


    static final _BOTPY_LOAD_PERSISTENT_STATE = { context, persistentState ->
        [
            quotation_id     : persistentState?.quotation_id, // 报价单号
            proposal_id      : persistentState?.proposal_id,// 投保单号
            isNeedUpdateImage: persistentState?.isNeedUpdateImage, //是否上传图片
            waitIdentityCaptcha: persistentState?.waitIdentityCaptcha, // 是否等待身份验证码
            proposal_status  : persistentState?.proposal_status // 核保状态
        ]
    }

    static final _IDENTITYTYPE_MAPPINGS = [
        1L  : '01',//身份证
        2L  : '02',//护照
        3L  : '',//军官证
        4L  : '',//回乡证
        5L  : '',//临时身份证
        6L  : '',//户口本
        7L  : '',//警官证
        8L  : '',//台胞证
        9L  : '11',//营业执照
        10L : '',//其它证件
        11L : '07',//组织机构代码
        12L : '',//工商注册号码
        13L : '09',//统一社会信用代码
        14L : '',//港澳通行证
        15L : '',//台湾通行证
    ]

    static final _HOLDER_TYPE_MAPPINGS = [
        1L  : '01',//身份证
        2L  : '01',//护照
        3L  : '',//军官证
        4L  : '',//回乡证
        5L  : '',//临时身份证
        6L  : '',//户口本
        7L  : '',//警官证
        8L  : '',//台胞证
        9L  : '02',//营业执照
        10L : '',//其它证件
        11L : '02',//组织机构代码
        12L : '',//工商注册号码
        13L : '02',//统一社会信用代码
        14L : '',//港澳通行证
        15L : '',//台湾通行证
    ]

    static final _FUELTYPE_MAPPINGS = [
        1L : '0',//汽油
        2L : '1',//电
        3L : '2',//柴油
        4L : '4',//天然气
    ]

    static final _USECHARACTER_MAPPINGS = [
        20L : '290',//其它非营业车辆
        21L : '211',//家庭自用汽车
        22L : '212',//非营业企业客车
        23L : '213',//非营业机关、事业团体客车
    ]

    static final _BOTPY_AUTO_MODEL_SELECTION_OPTIONS = { context ->
        def autoModel = context.autoModel
        def optionsSource = context.renewable && autoModel ? (context.optionsByVinNo.any {
            context.getVehicleOption(context, it).value == autoModel
        } ? 'byVinNo' : context.optionsByCode.any {
            context.getVehicleOption(context, it).value == autoModel
        } ? 'byCode' : null) : null
        if(optionsSource) {
            context.additionalParameters.supplementInfo.autoModel = autoModel
        }
        def options = [
            updateContext: { ctx, res, fsrv ->
                ctx.carInfo = fsrv[2]
            },
            optionsSource: optionsSource
        ]
        options
    }
}
