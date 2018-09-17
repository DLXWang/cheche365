package com.cheche365.cheche.pinganuk.flow

import static com.cheche365.cheche.pinganuk.flow.Flows._STEP_NAME_CLAZZ_MAPPINGS
import static com.cheche365.cheche.pinganuk.flow.Handlers._APPLY_POLICY_RPG
import static com.cheche365.cheche.pinganuk.flow.Handlers._QUERY_AND_QUOTE_RPG
import static com.cheche365.cheche.pinganuk.flow.Handlers._VERIFY_LOGIN_CAPTCHA_RH_370100
import static com.cheche365.cheche.pinganuk.flow.Handlers._VERIFY_LOGIN_CAPTCHA_RH_DEFAULT


/**
 * 各步骤所需的RPG和RH映射
 */
class HandlerMappings {

    //<editor-fold defaultstate="collapsed" desc="RPG Mappings">

    static final _CITY_RPG_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.报价.name): [
            default: _QUERY_AND_QUOTE_RPG,
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.核保.name): [
            default: _APPLY_POLICY_RPG,
        ]
    ]

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="RH Mappings">
    static final _CITY_RH_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.校验登陆验证码.name)   : [
            370100L: _VERIFY_LOGIN_CAPTCHA_RH_370100,
            default: _VERIFY_LOGIN_CAPTCHA_RH_DEFAULT
        ],
    ]
    //</editor-fold>

}
