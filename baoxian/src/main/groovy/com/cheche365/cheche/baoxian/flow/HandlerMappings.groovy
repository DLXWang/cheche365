package com.cheche365.cheche.baoxian.flow

import static com.cheche365.cheche.baoxian.flow.Flows._STEP_NAME_CLAZZ_MAPPINGS
import static com.cheche365.cheche.baoxian.flow.Handlers._QUOTE_PRICE_2M_RPG
import static com.cheche365.cheche.baoxian.flow.Handlers._QUOTE_PRICE_RPG



/**
 * 各步骤所需的RPG和RH映射
 */
class HandlerMappings {

    static final _CITY_RPG_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.创建非续保报价.class.name): [
            default : _QUOTE_PRICE_RPG
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.创建非续保报价2.class.name): [
            default : _QUOTE_PRICE_RPG
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.创建非续保报价2M.class.name): [
            default : _QUOTE_PRICE_2M_RPG
        ]
    ]

    static final _CITY_RH_MAPPINGS = [:]

}
