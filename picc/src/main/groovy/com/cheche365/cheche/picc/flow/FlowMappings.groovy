package com.cheche365.cheche.picc.flow

import static com.cheche365.cheche.picc.flow.Flows._INSURANCE_BASIC_INFO_FLOW_110000
import static com.cheche365.cheche.picc.flow.Flows._INSURANCE_BASIC_INFO_FLOW_310000
import static com.cheche365.cheche.picc.flow.Flows._INSURANCE_BASIC_INFO_FLOW_320100
import static com.cheche365.cheche.picc.flow.Flows._INSURANCE_BASIC_INFO_FLOW_440300
import static com.cheche365.cheche.picc.flow.Flows._INSURANCE_BASIC_INFO_FLOW_TYPE3
import static com.cheche365.cheche.picc.flow.Flows._INSURANCE_INFO_FLOW_110000
import static com.cheche365.cheche.picc.flow.Flows._QUOTING_VEHICLE_DEFAULT
import static com.cheche365.cheche.picc.flow.Flows._CHECK_VEHICLE_SHANGHAI
import static com.cheche365.cheche.picc.flow.Flows._INSURANCE_INFO_FLOW_V2
import static com.cheche365.cheche.picc.flow.Flows._INSURING_FLOW_110000
import static com.cheche365.cheche.picc.flow.Flows._INSURING_FLOW_310000
import static com.cheche365.cheche.picc.flow.Flows._INSURING_FLOW_410100
import static com.cheche365.cheche.picc.flow.Flows._ORDERING_FLOW_110000
import static com.cheche365.cheche.picc.flow.Flows._ORDERING_FLOW_320100
import static com.cheche365.cheche.picc.flow.Flows._ORDERING_FLOW_440300
import static com.cheche365.cheche.picc.flow.Flows._ORDERING_FLOW_TYPE3 as defaultOrderingFlow
import static com.cheche365.cheche.picc.flow.Flows._QUOTING_FLOW_110000
import static com.cheche365.cheche.picc.flow.Flows._QUOTING_FLOW_310000
import static com.cheche365.cheche.picc.flow.Flows._QUOTING_FLOW_410100
import static com.cheche365.cheche.picc.flow.Flows.get_ORDERING_FLOW_310000



/**
 * PICC流程步骤所需的常量
 */
class FlowMappings {

    /**
     * 为了支持全国报价，需要处理不同城市相同step使用不同请求参数以及响应处理器的情况。
     * 现定义了“城市 -> 请求参数生成器（Request Parameters Generator，简称RPG）”以及“城市 -> 响应处理器（Response Handler，简称RH）”的两个mapping，其中必须包含“default -> generator”
     * 和“default -> handler”。
     */

    static final _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS = [
        110000L : _QUOTING_FLOW_110000,    //北京V2
        310000L : _QUOTING_FLOW_310000,    //上海V2

        default : _QUOTING_FLOW_410100
    ]

    static final _FLOW_CATEGORY_QUERYVECHILE_FLOW_MAPPINGS = [

        310000L : _CHECK_VEHICLE_SHANGHAI,  //上海地区

        default : _QUOTING_VEHICLE_DEFAULT   //北京V2 及其他地区
    ]

    static final _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS = [
        110000L : _INSURING_FLOW_110000,   //北京
        310000L : _INSURING_FLOW_310000,   //上海

        default : _INSURING_FLOW_410100
    ]

    static final _FLOW_CATEGORY_ORDERING_FLOW_MAPPINGS = [
        110000L : _ORDERING_FLOW_110000,   //北京
        310000L : _ORDERING_FLOW_310000,   //上海
        320100L : _ORDERING_FLOW_320100,   //南京
        440300L : _ORDERING_FLOW_440300,   //深圳

        default : defaultOrderingFlow
    ]

    static final _FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS = [
        110000L : _INSURANCE_BASIC_INFO_FLOW_110000,   //北京
        310000L : _INSURANCE_BASIC_INFO_FLOW_310000,   //上海
        320100L : _INSURANCE_BASIC_INFO_FLOW_320100,   //南京
        440300L : _INSURANCE_BASIC_INFO_FLOW_440300,   //深圳
        default : _INSURANCE_BASIC_INFO_FLOW_TYPE3
    ]

    static final _FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS = [
        110000L : _INSURANCE_INFO_FLOW_110000,   //北京

        default : _INSURANCE_INFO_FLOW_V2
    ]
}
