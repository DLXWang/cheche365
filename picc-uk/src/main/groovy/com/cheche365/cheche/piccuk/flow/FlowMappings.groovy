package com.cheche365.cheche.piccuk.flow

import static com.cheche365.cheche.piccuk.flow.Flows._CHECK_LOGIN
import static com.cheche365.cheche.piccuk.flow.Flows._CHECK_PAYMENT_STATUS_FLOW
import static com.cheche365.cheche.piccuk.flow.Flows._GET_PAYMENT_CHANNELS_FLOW
import static com.cheche365.cheche.piccuk.flow.Flows._GET_PAYMENT_INFO_FLOW_DEFAULT
import static com.cheche365.cheche.piccuk.flow.Flows._INSURANCE_BASIC_INFO_FLOW
import static com.cheche365.cheche.piccuk.flow.Flows._INSURANCE_INFO_FLOW
import static com.cheche365.cheche.piccuk.flow.Flows._INSURING_FLOW
import static com.cheche365.cheche.piccuk.flow.Flows._INSURING_FLOW_110000
import static com.cheche365.cheche.piccuk.flow.Flows._QUOTING_FLOW
import static com.cheche365.cheche.piccuk.flow.Flows._QUOTING_FLOW_110000
import static com.cheche365.cheche.piccuk.flow.Flows._QUOTING_FLOW_320100



/**
 * 流程步骤所需的常量
 */
class FlowMappings {

    /**
     * 为了支持全国报价，需要处理不同城市相同step使用不同请求参数以及响应处理器的情况。
     * 现定义了如下2个mappings,其中必须包含“default -> generator”和“default -> handler”
     * “城市 -> 请求参数生成器（Request Parameters Generator，简称RPG）”以及“城市 -> 响应处理器（Response Handler，简称RH）”
     */

    static final _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS = [
        110000L: _QUOTING_FLOW_110000,
        320100L: _QUOTING_FLOW_320100,
        default: _QUOTING_FLOW
    ]

    static final _CHECK_LOGIN_FLOW_MAPPINGS = [
        default: _CHECK_LOGIN
    ]

    static final _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS = [
        110000L: _INSURING_FLOW_110000,
        default: _INSURING_FLOW
    ]

    static final _FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS = [
        default: _INSURANCE_INFO_FLOW
    ]

    static final _FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS = [
        default: _INSURANCE_BASIC_INFO_FLOW
    ]

    static final _FLOW_CATEGORY_GET_PAYMENT_INFO_FLOW_MAPPINGS = [
        default: _GET_PAYMENT_INFO_FLOW_DEFAULT
    ]

    static final _FLOW_CATEGORY_CHECK_PAYMENT_STATUS_FLOW_MAPPINGS = [
        default: _CHECK_PAYMENT_STATUS_FLOW
    ]

    static final _FLOW_CATEGORY_GET_PAYMENT_CHANNELS_FLOW_MAPPINGS = [
        default: _GET_PAYMENT_CHANNELS_FLOW
    ]
}
