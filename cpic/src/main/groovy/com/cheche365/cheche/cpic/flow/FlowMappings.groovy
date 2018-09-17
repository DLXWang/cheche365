package com.cheche365.cheche.cpic.flow

import static com.cheche365.cheche.cpic.flow.Flows._INSURANCE_BASIC_INFO_FLOW_110000
import static com.cheche365.cheche.cpic.flow.Flows._INSURANCE_BASIC_INFO_FLOW_DEFAULT
import static com.cheche365.cheche.cpic.flow.Flows._INSURANCE_INFO_FLOW_110000
import static com.cheche365.cheche.cpic.flow.Flows._INSURANCE_INFO_FLOW_DEFAULT
import static com.cheche365.cheche.cpic.flow.Flows._INSURING_FLOW_V3
import static com.cheche365.cheche.cpic.flow.Flows._INSURING_FLOW_V3_110000
import static com.cheche365.cheche.cpic.flow.Flows._INSURING_FLOW_V3_NEW_CAPTCHA
import static com.cheche365.cheche.cpic.flow.Flows._QUOTING_FLOW_V3
import static com.cheche365.cheche.cpic.flow.Flows._QUOTING_FLOW_V3_110000
import static com.cheche365.cheche.cpic.flow.Flows._QUOTING_FLOW_V3_NEW_CAPTCHA



/**
 * CPIC流程步骤所需的常量
 */
class FlowMappings {

    static final _QUOTING_FLOW_MAPPINGS = [

        110000L: _QUOTING_FLOW_V3_110000, // 北京
        320100L: _QUOTING_FLOW_V3_NEW_CAPTCHA, // 南京
        default: _QUOTING_FLOW_V3
    ]

    static final _INSURING_FLOW_MAPPINGS = [

        110000L: _INSURING_FLOW_V3_110000,
        320100L: _INSURING_FLOW_V3_NEW_CAPTCHA,
        default: _INSURING_FLOW_V3
    ]

    static final _FLOW_INSURANCE_BASIC_INFO_FLOW_MAPPINGS = [
        110000L: _INSURANCE_BASIC_INFO_FLOW_110000,
        default: _INSURANCE_BASIC_INFO_FLOW_DEFAULT
    ]

    static final _FLOW_INSURANCE_INFO_FLOW_MAPPINGS = [
        110000L: _INSURANCE_INFO_FLOW_110000,
        default: _INSURANCE_INFO_FLOW_DEFAULT
    ]

}
