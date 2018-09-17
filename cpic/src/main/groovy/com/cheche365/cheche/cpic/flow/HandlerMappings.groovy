package com.cheche365.cheche.cpic.flow

import static com.cheche365.cheche.cpic.flow.Flows._STEP_NAME_CLAZZ_MAPPINGS
import static com.cheche365.cheche.cpic.flow.Handlers._CALC_PREMIUM_NEW
import static com.cheche365.cheche.cpic.flow.Handlers._CALC_TRAVEL_TAX_V3_RPG_DEFAULT
import static com.cheche365.cheche.cpic.flow.Handlers._INIT_TRAVEL_TAX_V3_RH_310000
import static com.cheche365.cheche.cpic.flow.Handlers._INIT_TRAVEL_TAX_V3_RH_DEFAULT
import static com.cheche365.cheche.cpic.flow.Handlers._INIT_VEHICLE_DETAIL_INFO_V3_RH_DEFAULT
import static com.cheche365.cheche.cpic.flow.Handlers._INIT_VEHICLE_DETAIL_INFO_V3_RH_NO_MODEL_CODE
import static com.cheche365.cheche.cpic.flow.Handlers._SUBMIT_VEHICLE_DETAIL_INFO_RPG_DEFAULT

/**
 * CPIC个步骤所需的PRG和RH
 */
class HandlerMappings {

    static final _CITY_RPG_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.查询车辆信息V3.name) : [
            default : _SUBMIT_VEHICLE_DETAIL_INFO_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.获取商业险报价V3.name) : [
            default : _CALC_PREMIUM_NEW
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.获取交强险报价V3.name) : [
            default : _CALC_TRAVEL_TAX_V3_RPG_DEFAULT
        ],
    ]

    /***************************************************************************************************/

    static final _CITY_RH_MAPPINGS = [

        (_STEP_NAME_CLAZZ_MAPPINGS.初始化交强险V3.name): [
            310000L : _INIT_TRAVEL_TAX_V3_RH_310000,
            default : _INIT_TRAVEL_TAX_V3_RH_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.初始化车辆详细信息V3.name): [
            110000L : _INIT_VEHICLE_DETAIL_INFO_V3_RH_NO_MODEL_CODE,
            310000L : _INIT_VEHICLE_DETAIL_INFO_V3_RH_NO_MODEL_CODE,
            default : _INIT_VEHICLE_DETAIL_INFO_V3_RH_DEFAULT
        ]

    ]

}
