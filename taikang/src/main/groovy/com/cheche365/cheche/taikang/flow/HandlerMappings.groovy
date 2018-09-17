package com.cheche365.cheche.taikang.flow

import static com.cheche365.cheche.taikang.flow.Flows._STEP_NAME_CLAZZ_MAPPINGS
import static com.cheche365.cheche.taikang.flow.Handlers._QUOTE_PROPOSAL_110000
import static com.cheche365.cheche.taikang.flow.Handlers._QUOTE_PROPOSAL_RPG_DEFAULT



/**
 * 泰康 RPG&RH
 * Created by LIUGUO on 2018/09/07
 */
class HandlerMappings {

    static final _CITY_RPG_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.下单及提交核保接口.name):
            [
                110000L: _QUOTE_PROPOSAL_110000,
                default: _QUOTE_PROPOSAL_RPG_DEFAULT
            ],
        (_STEP_NAME_CLAZZ_MAPPINGS.核保预处理接口.name)  :
            [
                110000L: _QUOTE_PROPOSAL_110000,
                default: _QUOTE_PROPOSAL_RPG_DEFAULT
            ],
    ]

}
