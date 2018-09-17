package com.cheche365.cheche.aibao.flow

import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_COMMERCIAL_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_COMPULSORY_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.flow.business.flow.util.BusinessUtils.getVehicleOption
import static com.cheche365.cheche.parser.Constants._ENROLL_DATE
import static com.cheche365.cheche.parser.Constants._COMPULSORY_EFFECTIVE_DATE
import static com.cheche365.cheche.parser.Constants._EFFECTIVE_DATE
import static com.cheche365.cheche.common.util.ContactUtils._MALE
import static com.cheche365.cheche.common.util.ContactUtils._FEMALE



/**
 * 爱保流程步骤所需的常量
 */
class Constants {

    static final _AIBAO_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand         : vehicle.brandName, // 品牌型号
            family        : vehicle.familyName,//车系
            gearbox       : '',                // 档位
            exhaustScale  : vehicle.exhaustScale,//排量
            model         : vehicle.vehicleCode, //车型代码
            productionDate: vehicle.yearPattern,//上市年份
            seats         : vehicle.seatCount,  //座位数
            newPrice      : vehicle.purchasePrice,//新车置购价
        ]
        //厂牌型号,应该是车型编码吧，god ，please tell me ！
        getVehicleOption vehicle.vehicleCode, vehicleOptionInfo
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

    static final _AIBAO_LOAD_PERSISTENT_STATE = { context, persistentState ->
        [
            aiBaoTransactionNo  : persistentState?.aiBaoTransactionNo,   // 交易流水
//            quotationNoCI       : persistentState?.quotationNoCI,    // 交强险询价单号
//            quotationNoBI       : persistentState?.quotationNoBI,    // 商业险询价单号
//            token               : persistentState?.token,            // token令牌
            waitIdentityCaptcha : persistentState?.waitIdentityCaptcha,  // 判断北京的核保状态
            verificationCode    : persistentState?.verificationCode,   // 短信验证码
            compulsoryExpireDate: persistentState?.compulsoryExpireDate,// 交强险终保日期
            compulsoryBeginDate : persistentState?.compulsoryBeginDate,// 交强险起保日期
            commercialBeginDate : persistentState?.commercialBeginDate,
            commercialExpireDate: persistentState?.commercialExpireDate,
            formId              : persistentState?.orderNo,            // 总投保单号
            payUrl              : persistentState?.payUrl              // 支付回调地址
        ]
    }

    static final _AIBAO_SAVE_PERSISTENT_STATE = { context ->
        [
            aiBaoTransactionNo  : context.aiBaoTransactionNo,           // 交易流水
//            quotationNoCI       : context.quotationNoCI,              // 交强险询价单号
//            quotationNoBI       : context.quotationNoBI,              // 商业险询价单号
//            token               : context?.token,                     // token令牌
            waitIdentityCaptcha : context?.waitIdentityCaptcha,         // 判断北京的核保状态
            verificationCode    : context?.verificationCode,            // 短信验证码
            compulsoryExpireDate: context?.compulsoryExpireDate,        // 交强险终保日期
            compulsoryBeginDate : context?.compulsoryBeginDate,         // 交强险起保日期
            commercialBeginDate : context?.commercialBeginDate,
            commercialExpireDate: context?.commercialExpireDate,
            formId              : context?.orderNo,                     //总投保单号
            payUrl              : context?.payUrl
        ]
    }

    static final _AIBAO_PREFIELD_STATUS_MAPPINGS = { context ->
        [
            (_EFFECTIVE_DATE)           : context.precommercialStartDate,
            (_COMPULSORY_EFFECTIVE_DATE): context.precompulsoryStartDate
        ]
    }

    static final _AIBAO_POSTFIELD_STATUS_MAPPINGS = { context ->
        [
            (_EFFECTIVE_DATE)           : context.postcommercialStartDate,
            (_COMPULSORY_EFFECTIVE_DATE): context.postcompulsoryStartDate
        ]
    }

    static final COMMERCIALCHECKFLAG = 'busiVerifyCodeImg' //商业险校验平台类别标志
    static final COMPULSORYCHECKFLAG = 'bzVerifyCodeImg' //交强险校验平台类别标志

    /**
     * 假的，这个爱保报价会返回
     */
    static final _STATUS_CODE_INSURE_SUCCESS = 60001L
    static final _AIBAO_THIRD_PARTY_AMOUNT_LIST = [50000, 100000, 150000, 200000, 300000, 500000, 1000000,
                                                   1500000, 2000000, 3000000, 5000000]
    static final _AIBAO_DRIVER_AMOUNT_LIST = [10000, 20000, 30000, 40000, 50000, 100000, 200000]
    static final _AIBAO_PASSENGER_AMOUNT_LIST = [10000, 20000, 30000, 40000, 50000, 100000, 200000]
    static final _AIBAO_SCRATCH_AMOUNT_LIST = [2000, 5000, 10000, 20000]

    // 证件类型
    static final _IDENTITYTYPE_MAPPINGS = [
        1L     : '01',//身份证
        2L     : '03',//护照
        3L     : '04',//军人证件
        4L     : '02',//户口簿
        5L     : '05',//驾驶执照
        7L     : '07',//港澳身份证
        9L     : '09',//赴台通行证
        10     : '10', //港澳通行证
        15L    : '15',//士兵证
        25L    : '25',//港澳居民来往内地通行证
        26     : '26',//台湾居民来往内地通行证
        31L    : '31',//组织机构代码证
        37L    : '37',//统一社会信用代码
        99L    : '99',//其他
        default: '01'
    ]

    // 性别编码
    static final _SEX_MAPPINGS = [
        (_MALE)  : '1',//男
        (_FEMALE): '2',//女
        default  : '9'//未知
    ]

}
