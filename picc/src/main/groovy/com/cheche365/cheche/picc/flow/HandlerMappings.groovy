package com.cheche365.cheche.picc.flow

import static com.cheche365.cheche.picc.flow.Flows._STEP_NAME_CLAZZ_MAPPINGS
import static com.cheche365.cheche.picc.flow.Handlers._CALCULATE_FOR_BATCH_RPG_210200
import static com.cheche365.cheche.picc.flow.Handlers._CALCULATE_FOR_BATCH_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._CALCULATE_FOR_BZ_RPG_210200
import static com.cheche365.cheche.picc.flow.Handlers._CALCULATE_FOR_BZ_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._CALCULATE_FOR_BZ_RPG_TAX_TYPE_B_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._CALCULATE_FOR_BZ_RPG_TAX_TYPE_N_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._CALCULATE_FOR_CHANGE_KIND_RPG_210200
import static com.cheche365.cheche.picc.flow.Handlers._CALCULATE_FOR_CHANGE_KIND_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._CALCULATE_FOR_XUBAO_RPG_330200
import static com.cheche365.cheche.picc.flow.Handlers._CALCULATE_FOR_XUBAO_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._CAR_BLACK_LIST_CAR_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._CAR_BLACK_LIST_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._CHECK_BZ_RPG_310000
import static com.cheche365.cheche.picc.flow.Handlers._CHECK_BZ_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._CHECK_PERIOD_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._CHECK_POLICY_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._CHECK_PRICE_FOR_CAR_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._CHECK_PROFIT_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._CHECK_PROFIT_RPG_TYPE2_AND_TYPE3 as defaultCheckProfitRpg
import static com.cheche365.cheche.picc.flow.Handlers._CHECK_REINSURANCE_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._FIND_CAR_MODEL_BY_BRAND_NAME_02_RH_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._FIND_CAR_MODEL_BY_BRAND_NAME_02_RH_TYPE2
import static com.cheche365.cheche.picc.flow.Handlers._FIND_CAR_MODEL_BY_BRAND_NAME_03_RH_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._FIND_CAR_MODEL_BY_BRAND_NAME_RPG_03
import static com.cheche365.cheche.picc.flow.Handlers._FIND_CAR_MODEL_BY_BRAND_NAME_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._FIND_CAR_MODEL_RPG_01
import static com.cheche365.cheche.picc.flow.Handlers._FIND_CAR_MODEL_RPG_02_310000
import static com.cheche365.cheche.picc.flow.Handlers._FIND_CAR_MODEL_RPG_04
import static com.cheche365.cheche.picc.flow.Handlers._FIND_CAR_MODEL_RPG_04_310000
import static com.cheche365.cheche.picc.flow.Handlers._GET_NEW_USE_YEARS_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._GET_UNIQUE_ID_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._GET_USE_YEARS_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._INSURED_BLACK_LIST_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._REGISTER_UNIQUE_ID_RH_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._REGISTER_UNIQUE_ID_RH_JY as defaultRegisterUniqueidRh
import static com.cheche365.cheche.picc.flow.Handlers._REGISTER_UNIQUE_ID_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._REGISTER_UNIQUE_ID_RPG_JY as defaultRegisterUniqueIdRpg
import static com.cheche365.cheche.picc.flow.Handlers._REGISTER_UNIQUE_ID_RPG_NON_JY
import static com.cheche365.cheche.picc.flow.Handlers._SAVE_PROPOSAL_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.Handlers._VERIFY_CAPTCHA_RPG_DEFAULT
import static com.cheche365.cheche.picc.flow.step.v2.Handlers._CALCULATE_BI_FOR_CHANGE_ITEM_KIND_RPG
import static com.cheche365.cheche.picc.flow.step.v2.Handlers._CALCULATE_CI_RH_120000
import static com.cheche365.cheche.picc.flow.step.v2.Handlers._CALCULATE_CI_RH_DEFAULT
import static com.cheche365.cheche.picc.flow.step.v2.Handlers._PRE_FOR_CAL_BI_RPG_110000
import static com.cheche365.cheche.picc.flow.step.v2.Handlers._PRE_FOR_CAL_BI_RPG_310000
import static com.cheche365.cheche.picc.flow.step.v2.Handlers._PRE_FOR_CAL_BI_RPG_DEFAULT


/**
 * PICC各步骤所需的RPG和RH映射
 */
class HandlerMappings {

    //<editor-fold defaultstate="collapsed" desc="RPG Mappings">

    static final _CITY_RPG_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.获取流程唯一标识.name): [
            default : _GET_UNIQUE_ID_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.检查车辆黑名单新接口.name): [
            default : _CAR_BLACK_LIST_CAR_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.检查车辆黑名单旧接口.name): [
            default : _CAR_BLACK_LIST_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.检查人员黑名单.name) : [
            default : _INSURED_BLACK_LIST_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.获取车辆使用年数新接口.name): [
            default : _GET_NEW_USE_YEARS_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.获取车辆使用年数.name): [
            default : _GET_USE_YEARS_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.获取车型价格.name): [
            default : _CHECK_PRICE_FOR_CAR_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.注册UniqueID到流程.name): [
            110000L : _CHECK_PROFIT_RPG_DEFAULT,             //北京
            310000L : _CHECK_PROFIT_RPG_DEFAULT,             //上海
            330200L : _CHECK_PROFIT_RPG_DEFAULT,             //宁波
            default : defaultCheckProfitRpg,
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.计算非续保全险报价.name): [
            210200L : _CALCULATE_FOR_BATCH_RPG_210200,
            default : _CALCULATE_FOR_BATCH_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.校验验证码.name): [
            default : _VERIFY_CAPTCHA_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.计算商业险.name): [
            210200L : _CALCULATE_FOR_CHANGE_KIND_RPG_210200,
            default : _CALCULATE_FOR_CHANGE_KIND_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.计算交强险.name): [
            210200L : _CALCULATE_FOR_BZ_RPG_210200,
            default : _CALCULATE_FOR_BZ_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.计算交强险补充并纳税方式.name): [
            default : _CALCULATE_FOR_BZ_RPG_TAX_TYPE_B_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.计算交强险仅纳税方式.name): [
            default : _CALCULATE_FOR_BZ_RPG_TAX_TYPE_N_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.查找车型.name): [
            110000L : _FIND_CAR_MODEL_RPG_01,
            310000L : _FIND_CAR_MODEL_RPG_02_310000,
            default : _FIND_CAR_MODEL_RPG_04
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.查找车型上海04.name): [
            default : _FIND_CAR_MODEL_RPG_04_310000
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.注册UniqueID到车型.name): [
            110000L : _REGISTER_UNIQUE_ID_RPG_DEFAULT,  //北京
            310000L : _REGISTER_UNIQUE_ID_RPG_DEFAULT,  //上海
            440300L : _REGISTER_UNIQUE_ID_RPG_DEFAULT,  //深圳
            330100L : _REGISTER_UNIQUE_ID_RPG_NON_JY,   //杭州
            320100L : _REGISTER_UNIQUE_ID_RPG_NON_JY,   //南京

            default : defaultRegisterUniqueIdRpg
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.获取再保险周期.name): [
            default : _CHECK_REINSURANCE_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.获取保险周期.name): [
            default : _CHECK_PERIOD_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.计算续保全险报价.name): [
            330200L : _CALCULATE_FOR_XUBAO_RPG_330200,
            default : _CALCULATE_FOR_XUBAO_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.检查交强险.name): [
            310000L : _CHECK_BZ_RPG_310000,
            default : _CHECK_BZ_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.检查保险周期.name): [
            default : _CHECK_PERIOD_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.检查承保政策.name): [
            default : _CHECK_POLICY_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.检查再保险周期.name): [
            default : _CHECK_REINSURANCE_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.根据品牌型号查找车型02.name): [
            default : _FIND_CAR_MODEL_BY_BRAND_NAME_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.根据品牌型号查找车型03.name): [
            default : _FIND_CAR_MODEL_BY_BRAND_NAME_RPG_03
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.保存保单信息.name): [
            default : _SAVE_PROPOSAL_RPG_DEFAULT
        ],

        // v2流程
        (_STEP_NAME_CLAZZ_MAPPINGS.商业险报价前校验V2.name): [
            110000L : _PRE_FOR_CAL_BI_RPG_110000,  //北京
            310000L : _PRE_FOR_CAL_BI_RPG_310000,  //上海
            default : _PRE_FOR_CAL_BI_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.计算自定义商业险报价V2.name): [
            default : _CALCULATE_BI_FOR_CHANGE_ITEM_KIND_RPG  // 郑州
        ]
    ]

    //</editor-fold>



    //<editor-fold defaultstate="collapsed" desc="RH Mappings">
    static final _CITY_RH_MAPPINGS = [

        (_STEP_NAME_CLAZZ_MAPPINGS.根据品牌型号查找车型02.name): [
            330100L : _FIND_CAR_MODEL_BY_BRAND_NAME_02_RH_TYPE2,   //杭州
            440300L : _FIND_CAR_MODEL_BY_BRAND_NAME_02_RH_TYPE2,   //深圳
            320100L : _FIND_CAR_MODEL_BY_BRAND_NAME_02_RH_TYPE2,   //南京
            default : _FIND_CAR_MODEL_BY_BRAND_NAME_02_RH_DEFAULT
        ],

        (_STEP_NAME_CLAZZ_MAPPINGS.根据品牌型号查找车型03.name): [
            default : _FIND_CAR_MODEL_BY_BRAND_NAME_03_RH_DEFAULT
        ],

        (_STEP_NAME_CLAZZ_MAPPINGS.注册UniqueID到车型.name): [
            110000L : _REGISTER_UNIQUE_ID_RH_DEFAULT,   //北京
            310000L : _REGISTER_UNIQUE_ID_RH_DEFAULT,   //上海
            440300L : _REGISTER_UNIQUE_ID_RH_DEFAULT,   //深圳
            330100L : _REGISTER_UNIQUE_ID_RH_DEFAULT,   //杭州
            320500L : _REGISTER_UNIQUE_ID_RH_DEFAULT,   //苏州
            320100L : _REGISTER_UNIQUE_ID_RH_DEFAULT,   //南京

            default : defaultRegisterUniqueidRh
        ],

        // v2流程
        (_STEP_NAME_CLAZZ_MAPPINGS.计算交强险V2.name): [
                120000L : _CALCULATE_CI_RH_120000,  //天津
                default : _CALCULATE_CI_RH_DEFAULT
        ],
    ]
    //</editor-fold>

}
