package com.cheche365.cheche.cpicuk.flow

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

    static final get_VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS() {
        [
            default: [
                _SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING,
                _SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
            ]
        ]
    }


    static final _CPIC_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand         : vehicle.name,
            family        : vehicle.series,
            gearbox       : vehicle.transmission ?: vehicle.remark,
            exhaustScale  : vehicle.engineDesc,
            model         : vehicle.name,
            productionDate: vehicle.marketDate,
            seats         : vehicle.seatCount ?: vehicle.seat,
            newPrice      : vehicle.purchaseValue ?: vehicle.purchasePrice,   //新车价值
        ]
        getVehicleOption vehicle.vehicleCode ?: vehicle?.moldCharacterCode, vehicleOptionInfo
    }

    static final _CPIC_UK_SAVE_PERSISTENT_STATE = { context ->
        [
            quotationNo        : context.quotationNo, // 报价单号
            waitIdentityCaptcha: context.waitIdentityCaptcha, // 是否等待身份验证码
            proposal_status    : context.proposal_status,
            commercialInsureNo : context.commercialInsureNo,
            compulsoryInsureNo : context.compulsoryInsureNo,
            newPaymentInfo     : context.newPaymentInfo,  //支付二维码信息
            insuredTelephone   : context.insuredTelephone
        ]
    }


    static final _CPIC_UK_LOAD_PERSISTENT_STATE = { context, persistentState ->
        [
            quotationNo        : persistentState?.quotationNo, // 报价单号
            waitIdentityCaptcha: persistentState?.waitIdentityCaptcha, // 是否等待身份验证码
            proposal_status    : persistentState?.proposal_status,
            commercialInsureNo : persistentState?.commercialInsureNo,
            compulsoryInsureNo : persistentState?.compulsoryInsureNo,
            newPaymentInfo     : persistentState?.newPaymentInfo, //支付二维码信息
            insuredTelephone   : persistentState?.insuredTelephone
        ]
    }

    /**
     * 处理燃油类型  车保易  转 太平洋
     */
    static final _FUEL_TYPE_MAPPING = [
        110000L: [
            1L: 'A',  //汽油
            2L: 'C',//电
            3L: 'B',    //柴油
            4L: 'E',//天然气
        ],
        default: [
            1L: '0', //汽油
            3L: '0',    //柴油
            2L: '1', //电
            4L: '4',//天然气  -> 其他混合动力   太平洋北京外没有天燃气
        ]
    ]
    //处理所属人类型  车保易  转 太平洋

    /**
     * 证件对照   车保易  -> 太平洋
     */
    static _CERTI_TYPE_MAPPING = [
        1L : '1',//身份证
        2L : '2',//护照
        3L : '3',//军官证
        11L: '6',//组织机构代码
        9L : '11',//营业执照
        13L: '8'//税务登记证,统一社会信用代码
    ]
    //处理所属人类型  车保易  转 太平洋
    static final OWNER_PROP_MAPPING = [
        1L: '1', //个人
        //2L: '2', //机关
        2L: '3' //企业
    ]

    //车辆使用性质
    static final _USECHARACTER_MAPPINGS = [
        21L: 101L,//家庭自用汽车
        22L: 301L,// 企业
        23L: 201L,// 机关
    ]
    //车辆使用性质细分  公户车需要填写
    static final _USECHARACTER_DEETAIL_MAPPINGS = [
        101L: '',//家庭自用汽车
        301L: '23',// 企业
        201L: '13',// 机关
    ]

    //关系转换
    static final _USER_RELATIONSHIP_MAPPINGS = [
        1L: [
            true : '1',
            false: '9'
        ],
        2L: [
            true : '1',
            false: '2'
        ]
    ]

    //对应支付方式
    static final _PAYMENT_CHANNELS_MAPPINGS = [
        default: [AGENT_PARSER_ALIPAY_62, AGENT_PARSER_WECHAT_63]
    ]
}
