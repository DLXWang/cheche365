package com.cheche365.cheche.pingan.flow

import static com.cheche365.cheche.pingan.flow.Flows._INSURANCE_BASIC_INFO_FLOW_320100
import static com.cheche365.cheche.pingan.flow.Flows._INSURANCE_BASIC_INFO_FLOW_DEFAULT
import static com.cheche365.cheche.pingan.flow.Flows._INSURANCE_INFO_FLOW_320100
import static com.cheche365.cheche.pingan.flow.Flows._INSURANCE_INFO_FLOW_DEFAULT
import static com.cheche365.cheche.pingan.flow.Flows._INSURING_FLOW_330100
import static com.cheche365.cheche.pingan.flow.Flows._INSURING_FLOW_DEFAULT
import static com.cheche365.cheche.pingan.flow.Flows._INSURING_FLOW_TYPE2
import static com.cheche365.cheche.pingan.flow.Flows._QUOTING_AND_RENEWAL_FLOW
import static com.cheche365.cheche.pingan.flow.Flows._QUOTING_AND_RENEWAL_FLOW_TYPE2



/**
 * PINGAN流程步骤所需的常量
 */
class FlowMappings {

    /**
     * 为了支持全国报价，需要处理不同城市相同step使用不同请求参数以及响应处理器的情况。
     * 现定义了“城市 -> 请求参数生成器（Request Parameters Generator，简称RPG）”以及“城市 -> 响应处理器（Response Handler，简称RH）”的两个mapping，其中必须包含“default -> generator”
     * 和“default -> handler”。
     */
    static final _FLOW_CATEGORY_QUOTING_FLOW_MAPPING = [
        320100L: _QUOTING_AND_RENEWAL_FLOW_TYPE2, // 南京
        default: _QUOTING_AND_RENEWAL_FLOW
    ]

    static final _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS = [
        320100L: _INSURING_FLOW_TYPE2,  // 南京
        330100L: _INSURING_FLOW_330100, // 杭州
        default: _INSURING_FLOW_DEFAULT
    ]

    static final _FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS = [
        320100L : _INSURANCE_BASIC_INFO_FLOW_320100,
        default : _INSURANCE_BASIC_INFO_FLOW_DEFAULT
    ]

    static final _FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS = [
        320100L : _INSURANCE_INFO_FLOW_320100,
        default : _INSURANCE_INFO_FLOW_DEFAULT
    ]

}
