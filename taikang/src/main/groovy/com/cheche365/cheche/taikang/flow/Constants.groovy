package com.cheche365.cheche.taikang.flow

import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMMERCIAL_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMPULSORY_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.flow.business.flow.util.BusinessUtils.getVehicleOption
import static com.cheche365.cheche.parser.Constants._COMPULSORY_EFFECTIVE_DATE
import static com.cheche365.cheche.parser.Constants._EFFECTIVE_DATE



/**
 * 泰康流程步骤所需的常量
 */
class Constants {

    static final _TaiKang_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand         : (vehicle.carModel.series ?: '').split(',')[0],
            family        : (vehicle.carModel.modelName ?: '').split(',')[0],
            gearbox       : '',
            exhaustScale  : vehicle.carModel.displacement,
            model         : vehicle.carModel.modelCode,
            productionDate: vehicle.carModel.marketYear,
            seats         : vehicle.carModel.seatCount,
            newPrice      : vehicle.carModel.replacementValue,
        ]
        getVehicleOption vehicle.carModel.vehicleCode, vehicleOptionInfo
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

    static final _TAIKANG_LOAD_PERSISTENT_STATE = { context, persistentState ->
        [
            proposalFormId      : persistentState?.proposalFormId,   //交易流水
            quoteCheckList      : persistentState?.quoteCheckList,   //报价接口checkList信息
            quotationNoCI       : persistentState?.quotationNoCI,    //交强险询价单号
            quotationNoBI       : persistentState?.quotationNoBI,    //商业险询价单号
            token               : persistentState?.token,            //token令牌
            waitIdentityCaptcha : persistentState?.waitIdentityCaptcha,  //判断北京的核保状态
            compulsoryExpireDate: persistentState?.compulsoryExpireDate,//交强险终保日期
            compulsoryBeginDate : persistentState?.compulsoryBeginDate,//交强险起保日期
            commercialBeginDate : persistentState?.commercialBeginDate,
            commercialExpireDate: persistentState?.commercialExpireDate,
            formId              : persistentState?.formId,            //总投保单号
        ]
    }

    static final _TAIKANG_SAVE_PERSISTENT_STATE = { context ->
        [
            proposalFormId      : context.proposalFormId,             //交易流水
            quoteCheckList      : context.quoteCheckList,             //报价接口checkList信息
            quotationNoCI       : context.quotationNoCI,              //交强险询价单号
            quotationNoBI       : context.quotationNoBI,              //商业险询价单号
            token               : context.token,                     //token令牌
            waitIdentityCaptcha : context.waitIdentityCaptcha,   //判断北京的核保状态
            verificationCode    : context.additionalParameters.supplementInfo?.verificationCode,       //短信验证码
            compulsoryExpireDate: context.compulsoryExpireDate,   //交强险终保日期
            compulsoryBeginDate : context.compulsoryBeginDate,//交强险起保日期
            commercialBeginDate : context.commercialBeginDate,
            commercialExpireDate: context.commercialExpireDate,
            formId              : context.formId,                  //总投保单号

        ]
    }

    static final _TAIKANG_PRE_FIELD_STATUS_MAPPING = { context ->
        [
            (_EFFECTIVE_DATE)           : context.preCommercialStartDate,
            (_COMPULSORY_EFFECTIVE_DATE): context.preCompulsoryStartDate
        ]
    }
    static final _TAIKANG_POST_FIELD_STATUS_MAPPING = {
        context ->
            [
                (_EFFECTIVE_DATE)           : context.postCommercialStartDate,
                (_COMPULSORY_EFFECTIVE_DATE): context.postCompulsoryStartDate
            ]
    }

    static final _COMMERCIAL_CHECK_FLAG = '1' //商业险校验平台类别标志
    static final _COMPULSORY_CHECK_FLAG = '0' //交强险校验平台类别标志

    /**
     * 假的，这个等泰康给
     */
    static
    final _TAIKANG_THIRD_PARTY_AMOUNT_LIST = [50000, 100000, 150000, 200000, 300000, 500000, 1000000, 1500000, 2000000, 3000000, 5000000]
    static final _TAIKANG_DRIVER_AMOUNT_LIST = [10000, 20000, 30000, 40000, 50000, 100000, 200000]
    static final _TAIKANG_PASSENGER_AMOUNT_LIST = [10000, 20000, 30000, 40000, 50000, 100000, 200000]
    static final _TAIKANG_SCRATCH_AMOUNT_LIST = [2000, 5000, 10000, 20000]
}
