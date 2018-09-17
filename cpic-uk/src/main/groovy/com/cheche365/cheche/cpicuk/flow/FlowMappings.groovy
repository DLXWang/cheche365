package com.cheche365.cheche.cpicuk.flow

import static com.cheche365.cheche.cpicuk.flow.Flows._CATEGORY_CANCEL_PAY__FLOW
import static com.cheche365.cheche.cpicuk.flow.Flows._CHECK_PAYMENT_STATUS_FLOW
import static com.cheche365.cheche.cpicuk.flow.Flows._GET_PAYMENT_CHANNELS_FLOW
import static com.cheche365.cheche.cpicuk.flow.Flows._GET_PAYMENT_INFO_FLOW
import static com.cheche365.cheche.cpicuk.flow.Flows._GET_PAYMENT_INFO_FLOW_DEFAULT
import static com.cheche365.cheche.cpicuk.flow.Flows._GET_QUOTE_RECORD_STATUS_FLOW
import static com.cheche365.cheche.cpicuk.flow.Flows._INSURING_FLOW_110000
import static com.cheche365.cheche.cpicuk.flow.Flows._INSURING_FLOW_DEFAULT
import static com.cheche365.cheche.cpicuk.flow.Flows._QUOTING_FLOW
import static com.cheche365.cheche.cpicuk.flow.Flows._QUOTING_FLOW_320100
import static com.cheche365.cheche.cpicuk.flow.Flows._CHECK_LOGIN


/**
 * 流程步骤所需的常量
 */
class FlowMappings {

    static final _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS = [
        320100L : _QUOTING_FLOW_320100,
        default: _QUOTING_FLOW
    ]

    static final _CHECK_LOGIN_FLOW_MAPPINGS = [
        default: _CHECK_LOGIN
    ]

    static final _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS = [
        110000L: _INSURING_FLOW_110000,
        default: _INSURING_FLOW_DEFAULT
    ]

    static final _FLOW_CATEGORY_GET_PAYMENT_INFO_FLOW_MAPPINGS = [
        110000L: _GET_PAYMENT_INFO_FLOW,
        default: _GET_PAYMENT_INFO_FLOW_DEFAULT
    ]

    static final _FLOW_CATEGORY_GET_PAYMENT_CHANNELS_FLOW_MAPPINGS = [
        default: _GET_PAYMENT_CHANNELS_FLOW
    ]

    static final _FLOW_CATEGORY_CHECK_PAYMENT_STATUS_FLOW_MAPPINGS = [
        default: _CHECK_PAYMENT_STATUS_FLOW
    ]

    static final _FLOW_CATEGORY_GET_QUOTE_RECORD_STATUS_FLOW_MAPPINGS = [
        default: _GET_QUOTE_RECORD_STATUS_FLOW
    ]

    static final _FLOW_CATEGORY_CANCEL_PAY_FLOW_MAPPINGS = [
        default: _CATEGORY_CANCEL_PAY__FLOW
    ]
}
