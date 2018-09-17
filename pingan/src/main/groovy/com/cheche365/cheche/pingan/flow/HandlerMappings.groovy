package com.cheche365.cheche.pingan.flow

import static com.cheche365.cheche.pingan.flow.Flows._STEP_NAME_CLAZZ_MAPPINGS
import static com.cheche365.cheche.pingan.flow.step.m.Handlers._GET_INSURANCE_CONVERT_DEFAULT
import static com.cheche365.cheche.pingan.flow.step.m.Handlers._M_QUOTE_BIZCULATE_RPG_DEFAULT
import static com.cheche365.cheche.pingan.flow.step.m.Handlers._M_SAVE_QUOTE_INFO_RPG_DEFAULT
import static com.cheche365.cheche.pingan.flow.step.m.Handlers._POPULATE_QUOTERECORD_DEFAULT
import static com.cheche365.cheche.pingan.util.BusinessUtils._M_QUOTE_RESULT_INSPECTION_RH_DEFAULT

/**
 * PINGAN流程步骤所需的常量
 */
class HandlerMappings {

    static final _CITY_RPG_MAPPINGS = [

        (_STEP_NAME_CLAZZ_MAPPINGS.M站商业险报价.name)    : [
              default: _M_QUOTE_BIZCULATE_RPG_DEFAULT,
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.商业险套餐检查.name)     : [
              default: _M_QUOTE_BIZCULATE_RPG_DEFAULT,
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.报价保存车辆信息.name)     : [
            default: _M_SAVE_QUOTE_INFO_RPG_DEFAULT//只区分上海和非上海地区
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.获取上年商业险续保套餐.name)     : [
              default: _GET_INSURANCE_CONVERT_DEFAULT,
        ],
    ]

    static final _CITY_RH_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.计算商业险报价.name): [
              default : _POPULATE_QUOTERECORD_DEFAULT,
        ],
            (_STEP_NAME_CLAZZ_MAPPINGS.商业险套餐检查.name): [
              default : _M_QUOTE_RESULT_INSPECTION_RH_DEFAULT,
        ],
    ]

}

