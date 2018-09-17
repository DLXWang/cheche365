package com.cheche365.cheche.piccuk.flow

import static com.cheche365.cheche.piccuk.flow.Flows._STEP_NAME_CLAZZ_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.Handlers._EDIT_PAY_FEE_BY_ALIPAY_OR_WECHAT_ADD_110000
import static com.cheche365.cheche.piccuk.flow.Handlers._EDIT_PAY_FEE_BY_ALIPAY_OR_WECHAT_ADD_510100
import static com.cheche365.cheche.piccuk.flow.Handlers._EDIT_PAY_FEE_BY_WECHAT_ADD_320100
import static com.cheche365.cheche.piccuk.flow.Handlers._EDIT_PAY_FEE_BY_WECHAT_ADD_RPG_BASE
import static com.cheche365.cheche.piccuk.flow.Handlers._INSERT_RPG
import static com.cheche365.cheche.piccuk.flow.Handlers._INSERT_RPG_320100
import static com.cheche365.cheche.piccuk.flow.Handlers._INSERT_RPG_510100
import static com.cheche365.cheche.piccuk.flow.Handlers._INSERT_RPG_BASE
import static com.cheche365.cheche.piccuk.flow.Handlers._QUERY_AND_QUOTE_RPG_110000
import static com.cheche365.cheche.piccuk.flow.Handlers._QUERY_AND_QUOTE_RPG_320100
import static com.cheche365.cheche.piccuk.flow.Handlers._QUERY_AND_QUOTE_RPG_410100
import static com.cheche365.cheche.piccuk.flow.Handlers._QUERY_AND_QUOTE_RPG_510100
import static com.cheche365.cheche.piccuk.flow.Handlers._QUERY_PAY_FOR_RPG
import static com.cheche365.cheche.piccuk.flow.Handlers._SAVE_JF_RPG_110000
import static com.cheche365.cheche.piccuk.flow.Handlers._SAVE_JF_RPG_320100
import static com.cheche365.cheche.piccuk.flow.Handlers._SAVE_JF_RPG_410100
import static com.cheche365.cheche.piccuk.flow.Handlers._SAVE_JF_RPG_510100


/**
 * 各步骤所需的RPG和RH映射
 */
class HandlerMappings {

    //<editor-fold defaultstate="collapsed" desc="RPG Mappings">

    static final _CITY_RPG_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.报价.name)         : [
            110000L: _QUERY_AND_QUOTE_RPG_110000,
            320100L: _QUERY_AND_QUOTE_RPG_320100,
            410100L: _QUERY_AND_QUOTE_RPG_410100,
            510100L: _QUERY_AND_QUOTE_RPG_510100,
            default: _QUERY_AND_QUOTE_RPG_110000,
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.保存报价.name)       : [
            110000L: _INSERT_RPG_BASE,
            320100L: _INSERT_RPG_320100,
            510100L: _INSERT_RPG_510100,
            default: _INSERT_RPG
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.保存收款信息.name)     : [
            410100L: _SAVE_JF_RPG_410100,
            320100L: _SAVE_JF_RPG_320100,
            110000L: _SAVE_JF_RPG_110000,
            510100L: _SAVE_JF_RPG_510100,
            default: _SAVE_JF_RPG_410100,
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.编辑微信二维码信息创建.name): [
            410100L: _EDIT_PAY_FEE_BY_WECHAT_ADD_RPG_BASE,
            320100L: _EDIT_PAY_FEE_BY_WECHAT_ADD_320100,
            110000L: _EDIT_PAY_FEE_BY_ALIPAY_OR_WECHAT_ADD_110000,
            510100L: _EDIT_PAY_FEE_BY_ALIPAY_OR_WECHAT_ADD_510100,
            default: _EDIT_PAY_FEE_BY_WECHAT_ADD_RPG_BASE,
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.查询手续费率.name)       : [
            110000L: _QUERY_PAY_FOR_RPG
        ],
    ]
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="RH Mappings">
    static final _CITY_RH_MAPPINGS = [:]
    //</editor-fold>

}
