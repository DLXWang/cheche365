package com.cheche365.cheche.huanong.flow

import static com.cheche365.cheche.huanong.flow.Flows._STEP_NAME_CLAZZ_MAPPINGS
import static com.cheche365.cheche.huanong.flow.Handlers._QUOTED_PRICE_RPG_DEFAULT
import static com.cheche365.cheche.huanong.flow.Handlers._QUOTED_PRICE_RPG_110000



/**
 * 华农 RPG&RH
 * Created by suyaqiang on 2017/11/17.
 */
class HandlerMappings {

    static final _CITY_RPG_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.精准报价.name):
        [
            110000L: _QUOTED_PRICE_RPG_110000,
            default: _QUOTED_PRICE_RPG_DEFAULT
        ],
    ]

}
