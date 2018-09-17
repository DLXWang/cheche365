package com.cheche365.cheche.bihu.flow

import static com.cheche365.cheche.bihu.flow.Flows._STEP_NAME_CLAZZ_MAPPINGS
import static com.cheche365.cheche.bihu.flow.Handlers._QUOTE_PRICE_RPG


/**
 * 壁虎 RPG&RH
 * Created by suyaqiang on 2017/11/17.
 */
class HandlerMappings {

    static final _CITY_RPG_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.精准报价.name): [
            default: _QUOTE_PRICE_RPG
        ]
    ]

}
