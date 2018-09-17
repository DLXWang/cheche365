package com.cheche365.cheche.chinalife.flow

import static com.cheche365.cheche.chinalife.flow.Flows._STEP_NAME_CLAZZ_MAPPINGS
import static com.cheche365.cheche.chinalife.flow.Handlers._BASE_PREMIUM_DEFAULT
import static com.cheche365.cheche.chinalife.flow.Handlers._BASE_PREMIUM_TYPE2
import static com.cheche365.cheche.chinalife.flow.Handlers._CUSTOM_PREMIUM_TYPE2
import static com.cheche365.cheche.chinalife.flow.Handlers._FIND_CAR_MODEL_INFO_BASE_TYPE2
import static com.cheche365.cheche.chinalife.flow.Handlers._FIND_CAR_MODEL_INFO_RH_320100
import static com.cheche365.cheche.chinalife.flow.Handlers._FIND_CAR_MODEL_INFO_RH_DEFAULT
import static com.cheche365.cheche.chinalife.flow.Handlers._FIND_CAR_MODEL_INFO_WITHOUT_AUTOTYPE
import static com.cheche365.cheche.chinalife.flow.Handlers._RENEWAL_PREMIUM_DEFAULT
import static com.cheche365.cheche.chinalife.flow.Handlers._RENEWAL_PREMIUM_TYPE2
import static com.cheche365.cheche.chinalife.flow.Handlers._FIND_CAR_MODEL_INFO_WITHOUT_RBCODE
import static com.cheche365.cheche.chinalife.flow.Handlers._FIND_CAR_MODEL_INFO_DEFAULT
import static com.cheche365.cheche.chinalife.flow.Handlers._FIND_CAR_MODEL_INFO_WITH_AUTOTYPE
import static com.cheche365.cheche.chinalife.flow.Handlers._BZ_PREMIUM_DEFAULT
import static com.cheche365.cheche.chinalife.flow.Handlers._BZ_PREMIUM_TYPE2



/**
 * 各步骤所需的RPG和RH映射
 */
class HandlerMappings {

    //<editor-fold defaultstate="collapsed" desc="RPG Mappings">

    static final _CITY_RPG_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.获取车型.name)  : [
            110000L : _FIND_CAR_MODEL_INFO_DEFAULT,          //北京
            440300L : _FIND_CAR_MODEL_INFO_WITHOUT_RBCODE,   //深圳
            310000L : _FIND_CAR_MODEL_INFO_WITHOUT_AUTOTYPE, //上海
            500000L : _FIND_CAR_MODEL_INFO_WITHOUT_RBCODE,   //重庆
            150100L : _FIND_CAR_MODEL_INFO_WITHOUT_RBCODE,   //呼和浩特
            650100L : _FIND_CAR_MODEL_INFO_WITHOUT_RBCODE,   //乌鲁木齐
            510100L : _FIND_CAR_MODEL_INFO_WITHOUT_RBCODE,   //成都
            330200L : _FIND_CAR_MODEL_INFO_WITH_AUTOTYPE,    //宁波
            320100L : _FIND_CAR_MODEL_INFO_DEFAULT,          //南京
            411300L : _FIND_CAR_MODEL_INFO_DEFAULT,          //南阳
            default : _FIND_CAR_MODEL_INFO_WITH_AUTOTYPE
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.根据多个品牌型号获取车型.name): [
            default : _FIND_CAR_MODEL_INFO_BASE_TYPE2,
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.获取基础套餐.name): [
            450100L : _BASE_PREMIUM_TYPE2,     //南宁
            500000L : _BASE_PREMIUM_TYPE2,     //重庆
            210200L : _BASE_PREMIUM_TYPE2,     //大连
            411300L : _BASE_PREMIUM_TYPE2,     //南阳
            default : _BASE_PREMIUM_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.获取续保套餐.name): [
            370100L : _RENEWAL_PREMIUM_TYPE2,     //济南
            410100L : _RENEWAL_PREMIUM_TYPE2,     //郑州
            441200L : _RENEWAL_PREMIUM_TYPE2,     //肇庆
            440500L : _RENEWAL_PREMIUM_TYPE2,     //汕头
            440200L : _RENEWAL_PREMIUM_TYPE2,     //韶关
            441400L : _RENEWAL_PREMIUM_TYPE2,     //梅州

            default : _RENEWAL_PREMIUM_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.计算套餐.name)  : [
            default : _CUSTOM_PREMIUM_TYPE2
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.交强险报价.name)  : [
            110000L : _BZ_PREMIUM_TYPE2,   //北京
            440300L : _BZ_PREMIUM_TYPE2,   //深圳
            150100L : _BZ_PREMIUM_TYPE2,   //呼和浩特
            220100L : _BZ_PREMIUM_TYPE2,   //长春
            230100L : _BZ_PREMIUM_TYPE2,   //哈尔滨
            320100L : _BZ_PREMIUM_TYPE2,   //南京
            340100L : _BZ_PREMIUM_TYPE2,   //合肥
            410100L : _BZ_PREMIUM_TYPE2,   //郑州
            420100L : _BZ_PREMIUM_TYPE2,   //武汉
            430100L : _BZ_PREMIUM_TYPE2,   //长沙
            440100L : _BZ_PREMIUM_TYPE2,   //广州
            450100L : _BZ_PREMIUM_TYPE2,   //南宁
            500000L : _BZ_PREMIUM_TYPE2,   //重庆
            510100L : _BZ_PREMIUM_TYPE2,   //成都
            610100L : _BZ_PREMIUM_TYPE2,   //西安
            650100L : _BZ_PREMIUM_TYPE2,   //乌鲁木齐

            default : _BZ_PREMIUM_DEFAULT
        ]

    ]

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="RH Mappings">
    static final _CITY_RH_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.获取车型.name)  : [
            320100L : _FIND_CAR_MODEL_INFO_RH_320100,          //南京
            default : _FIND_CAR_MODEL_INFO_RH_DEFAULT
        ],
    ]
    //</editor-fold>

}
