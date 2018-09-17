package com.cheche365.cheche.sinosafe.flow

import static com.cheche365.cheche.sinosafe.flow.Flows._INSURE_FLOW
import static com.cheche365.cheche.sinosafe.flow.Flows._INSURE_FLOW_BJ
import static com.cheche365.cheche.sinosafe.flow.Flows._QUOTING_FLOW_DEFAULT
import static com.cheche365.cheche.sinosafe.flow.Flows._UPLOADING_FLOW
import static com.cheche365.cheche.sinosafe.util.BusinessUtils._ADVICE_POLICY_MAPPINGS



/**
 * 流程步骤所需的常量
 */
class FlowMappings {

    static final _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS = [
        default: _QUOTING_FLOW_DEFAULT,

    ]

    static final _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS = [
        110000L: _INSURE_FLOW_BJ,
        default: _INSURE_FLOW
    ]

    static final _FLOW_ADVICE_POLICY_MAPPINGS_FLOW_MAPPINGS = [
        default: _ADVICE_POLICY_MAPPINGS
    ]

    static final _FLOW_CATEGORY_UPLOADING_MAPPINGS = [
        default : _UPLOADING_FLOW
    ]
}
