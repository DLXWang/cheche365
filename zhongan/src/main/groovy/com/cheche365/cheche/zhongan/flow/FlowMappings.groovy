package com.cheche365.cheche.zhongan.flow

import static com.cheche365.cheche.zhongan.flow.Flows._INSURING_FLOW_TYPE1
import static com.cheche365.cheche.zhongan.flow.Flows._INSURING_FLOW_TYPE1_BEIJING
import static com.cheche365.cheche.zhongan.flow.Flows._INSURING_FLOW_TYPE1_SHENZHEN
import static com.cheche365.cheche.zhongan.flow.Flows._ORDERING_FLOW_TYPE1
import static com.cheche365.cheche.zhongan.flow.Flows._QUERY_SIGNSTATUS_DEFAULT
import static com.cheche365.cheche.zhongan.flow.Flows._QUOTING_FLOW_320000
import static com.cheche365.cheche.zhongan.flow.Flows._QUOTING_FLOW_DEFAULT
import static com.cheche365.cheche.zhongan.util.BusinessUtils._ADVICE_POLICY_MAPPINGS



/**
 * 流程步骤所需的常量
 */
class FlowMappings {

    static final _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS = [
        320000L: _QUOTING_FLOW_320000,
        default: _QUOTING_FLOW_DEFAULT
    ]

    static final _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS = [
        110100L: _INSURING_FLOW_TYPE1_BEIJING,
        440300L: _INSURING_FLOW_TYPE1_SHENZHEN,
        default: _INSURING_FLOW_TYPE1
    ]

    static final _FLOW_CATEGORY_ORDERING_FLOW_MAPPINGS = [
        default: _ORDERING_FLOW_TYPE1
    ]

    static final _FLOW_ADVICEPOLICY_Mappings_FLOW_MAPPINGS = [
        default: _ADVICE_POLICY_MAPPINGS
    ]

    static final _FLOW_QUERY_SIGN_STATUS_MAPPINGS = [
        default: _QUERY_SIGNSTATUS_DEFAULT
    ]
}

