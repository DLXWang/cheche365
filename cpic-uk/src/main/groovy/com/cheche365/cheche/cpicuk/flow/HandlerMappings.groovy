package com.cheche365.cheche.cpicuk.flow

import static com.cheche365.cheche.cpicuk.flow.Flows._STEP_NAME_CLAZZ_MAPPINGS
import static com.cheche365.cheche.cpicuk.flow.Handlers._CALCULATE_RH_NEED_VERIFY
import static com.cheche365.cheche.cpicuk.flow.Handlers._CALCULATE_RPG_110000
import static com.cheche365.cheche.cpicuk.flow.Handlers._CALCULATE_RPG_DEFAULT
import static com.cheche365.cheche.cpicuk.flow.Handlers._QUERY_PAYMENT_RH_110000
import static com.cheche365.cheche.cpicuk.flow.Handlers._QUERY_PAYMENT_RH_DEFAULT
import static com.cheche365.cheche.cpicuk.flow.Handlers._QUICK_SAVE_RPG_DEFAULT
import static com.cheche365.cheche.cpicuk.flow.Handlers._SAVE_CLAUSE_INFO_RPG_110000
import static com.cheche365.cheche.cpicuk.flow.Handlers._SAVE_CLAUSE_INFO_RPG_DEFAULT
import static com.cheche365.cheche.cpicuk.flow.Handlers._SUBMIT_INSURE_INFO_RPG_410100L
import static com.cheche365.cheche.cpicuk.flow.Handlers._SUBMIT_INSURE_INFO_RPG_440100L
import static com.cheche365.cheche.cpicuk.flow.Handlers._SUBMIT_INSURE_INFO_RPG_DEFAULT
import static com.cheche365.cheche.cpicuk.flow.Handlers._PAY_RPG_320100L
import static com.cheche365.cheche.cpicuk.flow.Handlers._PAY_RPG_DEFAULT


/**
 * 各步骤所需的RPG和RH映射
 */
class HandlerMappings {

    //<editor-fold defaultstate="collapsed" desc="RPG Mappings">

    static final _CITY_RPG_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.报价.name)          : [
            110000L: _CALCULATE_RPG_110000, //beijing
            default: _CALCULATE_RPG_DEFAULT,
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.保存投保信息以创建投保单.name): [
            110000L: _SAVE_CLAUSE_INFO_RPG_110000,
            default: _SAVE_CLAUSE_INFO_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.提交投保信息创建保单.name)  : [
            410100L: _SUBMIT_INSURE_INFO_RPG_410100L,
            420100L: _SUBMIT_INSURE_INFO_RPG_410100L,
            440100L: _SUBMIT_INSURE_INFO_RPG_440100L,
            default: _SUBMIT_INSURE_INFO_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.再次提交投保信息.name)    : [
            440100L: _SUBMIT_INSURE_INFO_RPG_440100L,
            default: _SUBMIT_INSURE_INFO_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.生成报价单.name)       : [
            default: _QUICK_SAVE_RPG_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.请求支付.name)       : [
            320100L: _PAY_RPG_320100L,
            default: _PAY_RPG_DEFAULT
        ],
    ]

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="RH Mappings">
    static final _CITY_RH_MAPPINGS = [
        (_STEP_NAME_CLAZZ_MAPPINGS.查询支付状态.name): [
            110000L: _QUERY_PAYMENT_RH_110000,
            default: _QUERY_PAYMENT_RH_DEFAULT
        ],
        (_STEP_NAME_CLAZZ_MAPPINGS.报价.name)    : [
            default: _CALCULATE_RH_NEED_VERIFY
        ]
    ]
    //</editor-fold>

}
